(ns cwiki.models.db
  (:require [buddy.hashers :as hashers]
            [com.stuartsierra.component :as component]
            [cwiki.models.config :as config]
            [cwiki.util.datetime :as dt]
            [cwiki.util.files :as files]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as s]
            [clojure.set :as set]
            [clojure.pprint :as pp]
            [taoensso.timbre :refer [trace debug info warn error
                                     tracef debugf infof warnf errorf]])
  (:import (java.io File)
           (java.util UUID)
           (cwiki.models.config Config)))

(defn get-valid-roles
  []
  ["cwiki" "admin" "editor" "writer" "reader"])

;(def valid-roles ["cwiki" "admin" "editor" "writer" "reader"])

(defn get-initial-users
  []
  ([{:user_name              "CWiki"
                     :user_role              "cwiki"
                     :user_password          (hashers/derive (str (UUID/randomUUID)))
                     :user_new_password      nil
                     :user_new_password_time nil
                     :user_email             ""
                     :user_email_token       0
                     :user_email_expires     nil
                     :user_touched           (dt/sql-now)
                     :user_registration      (dt/sql-now)}
                    {:user_name              "admin"
                     :user_role              "admin"
                     :user_password          (hashers/derive "admin")
                     :user_new_password      nil
                     :user_new_password_time nil
                     :user_email             ""
                     :user_email_token       0
                     :user_email_expires     nil
                     :user_touched           (dt/sql-now)
                     :user_registration      (dt/sql-now)}
                    {:user_name              "guest"
                     :user_role              "reader"
                     :user_password          (hashers/derive "guest")
                     :user_new_password      nil
                     :user_new_password_time nil
                     :user_email             ""
                     :user_email_token       0
                     :user_email_expires     nil
                     :user_touched           (dt/sql-now)
                     :user_registration      (dt/sql-now)}]))

;;------------------------------------------------------------------------------
;; Utility functions that exist outside of the defrecord.

; Database/file-related functions.

(defn- db-exists?
  [path]
  (let [retval (.exists ^File (clojure.java.io/as-file path))]
    retval))

(defn- h2-spec->long-db-file-name
  "Return the file name on disk for an H2 database."
  [db-spec]
  (let [retval (str (:subname db-spec) ".mv.db")]
    retval))

(defn- file-name->h2-spec
  "Create and return a database spec based on the file path. The database
  spec is specific for the H2 database."
  [file-path]
  (let [h2-file-path (str (-> (File. ".")
                              .getAbsolutePath
                              (files/remove-from-end "."))
                          file-path)]
    (let [retval {:classname   "org.h2.Driver"
                  :subprotocol "h2:file"
                  :subname     h2-file-path
                  :make-pool?  true}]
      retval)))

(defn- get-row-id
  "Return the row id returned as the result of a single insert operation.
  It's buried in an odd map, hence this function."
  [result]
  (first (vals (first result))))

; String-related functions.

(defn- convert-seq-to-comma-separated-string
  "Return a string containing the members of the sequence separated by commas."
  [the-seq]
  (let [but-last-comma-mapped (map #(str % ", ") (butlast the-seq))]
    (s/join (concat but-last-comma-mapped (str (last the-seq))))))

(defn- escape-apostrophes
  [bad-string]
  (when bad-string
    (s/replace bad-string "'" "''")))

(defn- case-insensitive-comparator
  "Case-insensitive string comparator."
  [^String s1 ^String s2]
  (.compareToIgnoreCase s1 s2))

; Tag-related utilities.

(defn- get-tag-name-set-from-meta
  "Return a case-insensitive sorted-set of tag names contained the meta data."
  [meta]
  (reduce conj (sorted-set-by case-insensitive-comparator) (:tags meta)))

(defn- is-tag-id-NOT-in-xref-table?
  "Return true if the tag id is recorded anywhere in the xref table,
  false otherwise."
  [tag-id db]
  (let [sql (str "select * from tags_x_pages where tag_id=" tag-id ";")
        res (jdbc/query db [sql])]
    (empty? res)))

(defn- get-tag-id-from-name
  "Given a string containing a tag name, return the id from the tags table
  or nil if a tag with the name has not been recorded."
  [name db]
  (let [escaped-name (escape-apostrophes name)
        sql (str "select tag_id from tags where tag_name='" escaped-name "';")
        rs (jdbc/query db [sql])]
    (when (seq rs)
      (:tag_id (first rs)))))

(defn- get-tag-ids-for-page
  "Return a seq of the tag ids associated with the page-id. Returns
  nil if the page id is nil."
  [page-id db]
  (when page-id
    (let [sql (str "select tag_id from tags_x_pages where page_id=" page-id)
          rs (jdbc/query db [sql])]
      (reduce #(conj %1 (:tag_id %2)) [] rs))))

(defn- remove-deleted-tags!
  "Remove deleted tags for the page, and if there are no more pages
  with the tag, remove them from the tags tables too."
  [tag-id-vector page-id db]
  ; Remove from the cross-reference table.
  (let [sql (str "page_id=" page-id " and tag_id in ("
                 (convert-seq-to-comma-separated-string tag-id-vector) ");")]
    (jdbc/delete! db :tags_x_pages [sql]))
  ; Check if any need to be removed from the tags table because
  ; they are not referred to by any other page.
  (let [not-used (filterv #(is-tag-id-NOT-in-xref-table? % db)
                          tag-id-vector)]
    (when (seq not-used)
      (let [sql (str "tag_id in ("
                     (convert-seq-to-comma-separated-string not-used) ");")]
        (jdbc/delete! db :tags [sql])))))

(defn- delete-unused-tags!
  "Given a set of tag name strings, remove them from the cross-reference
  entries for the page and, possibly, the tag table if they are used
  nowhere else."
  [tag-name-set page-id db]
  (let [tag-ids (reduce #(conj %1 (get-tag-id-from-name %2 db)) [] tag-name-set)]
    (remove-deleted-tags! tag-ids page-id db)))

(defn- is-tag-name-NOT-in-tag-table?
  "Return true if the tag name is not already recorded in the tags
  table, nil otherwise."
  [tag-name db]
  (let [escaped-tag (escape-apostrophes tag-name)
        sql (str "select tag_id from tags where tag_name='" escaped-tag "';")
        res (jdbc/query db [sql])]
    (empty? res)))

(defn- add-new-tags-for-page!
  "Add new tags for the page to the cross-reference table and, possibly,
  the tag table."
  [new-tag-set page-id db]
  ; Add tags that are new to the database to the tabs table.
  (let [completely-new (filterv #(is-tag-name-NOT-in-tag-table? % db)
                                new-tag-set)]
    (when (seq completely-new)
      (mapv #(jdbc/insert! db :tags {:tag_name %}) completely-new)))
  ; Add tags to the xref table.
  (let [tag-ids (reduce #(conj %1 (get-tag-id-from-name %2 db)) [] new-tag-set)]
    (mapv #(jdbc/insert! db :tags_x_pages {:tag_id % :page_id page-id}) tag-ids)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Get information from page maps.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn page-map->id
  [m]
  (:page_id m))

(defn page-map->title
  [m]
  (:page_title m))

(defn page-map->content
  [m]
  (:page_content m))

;(defn page-map->author
;  ([m] (page-map->author m h2-db))
;  ([m db]
;   (let [author-id (:page_author m)
;         result (jdbc/query db ["select user_name from users where user_id=?" author-id])
;         name (:user_name (first result))]
;     (if (and result name)
;       name
;       "Unknown"))))

(defn page-map->tags
  [m]
  (:page_tags m))

(defn page-map->created-date
  [m]
  (:page_created m))

(defn page-map->modified-date
  [m]
  (:page_modified m))

;;------------------------------------------------------------------------------
;; The protocol that the defrecord implements.

(defprotocol Db-Protocol
  (user-name->user-id [this user-name])
  (user-name->user-role [this user-name])
  (get-author-from-import-meta-data [this meta default-author-id])
  (create-new-page-map [this title content author-id])
  (get-tag-names-for-page [this page-id])
  (update-tags-for-page! [this desired-tag-set page-id])
  (add-page-from-map! [this page-map default-author])
  (add-page-with-meta-from-file! [this file-name])
  (insert-user! [this user-map])
  (add-user! [this user-name user-password user-role user-email])
  (delete-user! [this user-id])
  (init-admin-table! [this])
  (add-initial-users! [this])
  (add-initial-pages! [this])
  (add-initial-roles! [this])
  (create-tables! [this])
  (seed-database! [this])
  (init-database! [this]))

(defrecord Db [config-map db-spec]
  component/Lifecycle

  (start [this]
    (info "Starting database.")
    (assoc this :config-map config-map)
    (when-let [file-path (.get-config-item config-map :db-file-path)]
      (let [my-spec (file-name->h2-spec file-path)
            db-component (assoc this :db-spec (file-name->h2-spec file-path))]
        (init-database! db-component)
        db-component)))

  (stop [this]
    (info "Stopping database.")
    (assoc this :db-spec nil))

  Db-Protocol

  (user-name->user-id
    [this user-name]
    (:user_id (first (jdbc/query
                       db-spec
                       ["select user_id from users where user_name=?" user-name]))))

  #_"Return the user role (a keyword) assigned to the named user. If the
  user does not exist, return nil."
  (user-name->user-role
    [this user-name]
    (let [string-role (:user_role
                        (first
                          (jdbc/query
                            db-spec
                            ["select user_role from users where user_name=?" user-name])))]
      (when string-role
        (let [colon-stripped (s/replace-first string-role ":" "")]
          (keyword colon-stripped)))))

  #_"Return the author id based on the content of the meta-data. If there is no
  author or they do not have the appropriate role, return the default id."
  (get-author-from-import-meta-data
    [this meta default-author-id]
    (let [author-name (:author meta)
          author-id (user-name->user-id this author-name)]
      (if (or (nil? author-id)
              (= "reader" (user-name->user-role this author-name)))
        (user-name->user-id this default-author-id)
        author-id)))

  #_"Return a new post map with the information provided."
  (create-new-page-map
    [_ title content author-id]
    {:page_created  (dt/sql-now)
     :page_modified (dt/sql-now)
     :page_author   author-id
     :page_title    title
     :page_content  content})

  #_"Returns a case-insensitive sorted-set of tag names associated with the page.
  If there are no such tags (it's nil or an empty seq), returns an empty set."
  (get-tag-names-for-page
    [_ page-id]
    (let [tag-ids (filterv (complement nil?) (get-tag-ids-for-page page-id db-spec))]
      (if (or (nil? tag-ids) (empty? tag-ids))
        (sorted-set-by case-insensitive-comparator)
        (let [tag-ids-as-string (convert-seq-to-comma-separated-string tag-ids)
              sql (str "select tag_name from tags where tag_id in ("
                       tag-ids-as-string ");")
              rs (jdbc/query db-spec [sql])]
          (reduce #(conj %1 (:tag_name %2))
                  (sorted-set-by case-insensitive-comparator) rs)))))

  #_"Update the tag tables with tags and associate them with the
  page-id."
  (update-tags-for-page!
    [this desired-tag-set page-id]
    (let [existing-tags (get-tag-names-for-page this page-id)
          tags-to-remove (set/difference existing-tags desired-tag-set)
          tags-to-add (set/difference desired-tag-set existing-tags)]
      (add-new-tags-for-page! tags-to-add page-id db-spec)
      (when (seq tags-to-remove)
        (delete-unused-tags! tags-to-remove page-id db-spec))))

  #_"Add a new page to the wiki using the information in a map. The map must
  have two keys, :meta and :body. Meta contains things like the author name,
  tags, etc. The body contains the Markdown content. If the author cannot be
  determined or is not recognized, the contents of the 'default-author'
  argument is used. If there is no title, a random title is created.
  Return the title of the imported page."

  (add-page-from-map!
    [this page-map default-author]
    (let [meta (:meta page-map)
          author-id (get-author-from-import-meta-data this meta default-author)
          title (or (:title meta) (str "Title - " (UUID/randomUUID)))]
      (when (and author-id title)
        (let [content (:body page-map)
              creation-date-str (or (:date meta)
                                    (:created meta))
              creation-date (if creation-date-str
                              (dt/meta-datetime-to-sql-datetime creation-date-str)
                              (dt/sql-now)
                              )
              update-date-str (or (:updated meta)
                                  (:changed meta)
                                  (:modified meta))
              update-date (if update-date-str
                            (dt/meta-datetime-to-sql-datetime update-date-str)
                            creation-date)
              pm (merge (create-new-page-map this title content author-id)
                        {:page_created  creation-date
                         :page_modified update-date})
              page-id (get-row-id (jdbc/insert! db-spec :pages pm))
              tv (get-tag-name-set-from-meta meta)]
          (update-tags-for-page! this tv page-id)
          title))))

  (add-page-with-meta-from-file!
    [this file-name]
    (let [resource-prefix "private/md/"
          m (files/load-markdown-from-resource (str resource-prefix file-name))]
      (add-page-from-map! this m "CWiki")))

  ; User addition, insertion, and deletion.

  (insert-user!
    [this user-map]
    (jdbc/insert! db-spec :users user-map))

  (add-user!
    [this user-name user-password user-role user-email]
    (let [role user-role
          user-map {:user_name              user-name
                    :user_role              role
                    :user_password          (hashers/derive user-password)
                    :user_new_password      nil
                    :user_new_password_time nil
                    :user_email             user-email
                    :user_email_token       0
                    :user_email_expires     nil
                    :user_touched           (dt/sql-now)
                    :user_registration      (dt/sql-now)}]
      (insert-user! this user-map)))

  (delete-user!
    [this user-id]
    (jdbc/delete! db-spec :users ["user_id=?" user-id]))

  ; Database initialization.

  (init-admin-table! [this]
    (jdbc/insert! db-spec :admin {:admin_id            1
                                  :admin_has_logged_in nil}))

  (add-initial-users!
    [this]
    (info "Adding initial users.")
    (mapv #(insert-user! this %) (get-initial-users))
    (info "Done!"))

  (add-initial-pages!
    [this]
    (info "Adding initial pages.")
    (mapv #(add-page-with-meta-from-file! this %) (files/load-initial-page-list))
    (info "Done!"))

  (add-initial-roles!
    [this]
    (info "Adding roles.")
    (mapv #(jdbc/insert! db-spec :roles {:role_name %}) (get-valid-roles))
    (info "Done!"))

  (create-tables!
    [this]
    (info "Creating the tables.")
    (try (jdbc/db-do-commands
           db-spec false
           [(jdbc/create-table-ddl :users
                                   [[:user_id :integer :auto_increment :primary :key]
                                    [:user_name :varchar]
                                    [:user_role :varchar]
                                    [:user_password :varchar]
                                    [:user_new_password :varchar]
                                    [:user_new_password_time :datetime]
                                    [:user_email "VARCHAR(254)"]
                                    [:user_email_token :int]
                                    [:user_email_expires :datetime]
                                    [:user_touched :datetime]
                                    [:user_registration :datetime]])
            (jdbc/create-table-ddl :admin
                                   [[:admin_id :integer :auto_increment :primary :key]
                                    [:admin_has_logged_in :boolean]])
            (jdbc/create-table-ddl :pages
                                   [[:page_id :integer :auto_increment :primary :key]
                                    [:page_created :datetime]
                                    [:page_modified :datetime]
                                    ;[:page_tags :varchar]
                                    [:page_author :integer]
                                    [:page_title :varchar]
                                    [:page_content :text]])
            (jdbc/create-table-ddl :namespaces
                                   [[:namespace_id :integer :auto_increment :primary :key]
                                    [:namespace_name :varchar]])
            (jdbc/create-table-ddl :roles
                                   [[:role_id :integer :auto_increment :primary :key]
                                    [:role_name :varchar]])
            (jdbc/create-table-ddl :tags
                                   [[:tag_id :integer :auto_increment :primary :key]
                                    [:tag_name :varchar "NOT NULL"]])
            (jdbc/create-table-ddl :tags_x_pages
                                   [[:x_ref_id :integer :auto_increment :primary :key]
                                    [:tag_id :integer]
                                    [:page_id :integer]])
            (jdbc/create-table-ddl :user_x_pages
                                   [[:x_ref_id :integer :auto_increment :primary :key]
                                    [:user_id :integer]
                                    [:page_id :integer]])])
         (jdbc/execute! db-spec "alter table tags_x_pages add foreign key (tag_id) references public.tags(tag_id);")
         (catch Exception e (println e)))
    (info "Done!"))

  (seed-database! [this]
    (create-tables! this)
    (init-admin-table! this)
    (add-initial-users! this)
    (add-initial-pages! this)
    (add-initial-roles! this))

  (init-database! [this]
    (when-not (db-exists? (h2-spec->long-db-file-name db-spec))
      (info "Creating initial database.")
      (io/make-parents (:subname db-spec))
      (seed-database! this)))

  )                                                         ; End of defrecord Db.

(defn new-database
  "Create and return a new Db record."
  []
  (map->Db {}))

;(do
;  (require '[com.stuartsierra.component :as component])
;  (require '[cwiki.models.config :as config] :reload)
;  (require '[cwiki.models.db :as db] :reload)
;  (def my-system (component/system-map
;                   :config-map (config/new-config)
;                   :db (component/using
;                         (db/new-database)
;                         {:config-map :config-map})))
;  (def my-started-system (component/start-system my-system)))