(ns cwiki.models.wiki-db
  (require [buddy.hashers :as hashers]
           [clojure.java.io :as io]
           [clojure.java.jdbc :as jdbc]
           [clojure.string :as s]
           [clj-time.coerce :as c]
           [clj-time.core :as t]
           [clj-time.format :as f]
           [cwiki.util.files :as files]
    ;[cwiki.util.pp :as pp]
           [cwiki.util.special :as special])
  (:import (java.io File)
           (java.util UUID)
           (org.h2.jdbc JdbcClob)))

;; Things that deal with the database file and connection.

; The kerfuffle here is to get the directory from which the program
; is running and create an absolute path as required for the H2 database.
(def db-file-name (str (-> (File. ".")
                           .getAbsolutePath
                           (files/remove-from-end "."))
                       "resources/public/db/database.db"))
; Because H2 seems to append this to the name above.
(def db-file-name-long (str db-file-name ".mv.db"))

(def h2-db {:classname   "org.h2.Driver"
            :subprotocol "h2:file"
            :subname     db-file-name
            :make-pool?  true})

;; Things related to time formatting.

(def markdown-pad-format (f/formatter-local "MM/dd/yyy h:mm:ss a"))

(defn create-new-post-map
  ([title]
   (create-new-post-map title ""))
  ([title content]
   (create-new-post-map title content 1))
  ([title content author-id]
   {:page_created  (c/to-sql-time (t/now))
    :page_modified (c/to-sql-time (t/now))
    :page_author   author-id
    :page_title    title
    :page_content  content}))

(def valid-roles ["cwiki" "admin" "editor" "writer" "reader"])

(def initial-users [{:user_name              "CWiki"
                     :user_role              "cwiki"
                     :user_password          (hashers/derive (str (UUID/randomUUID)))
                     :user_new_password      nil
                     :user_new_password_time nil
                     :user_email             nil
                     :user_email_token       0
                     :user_email_expires     nil
                     :user_touched           (c/to-sql-time (t/now))
                     :user_registration      (c/to-sql-time (t/now))}
                    {:user_name              "admin"
                     :user_role              "admin"
                     :user_password          (hashers/derive "admin")
                     :user_new_password      nil
                     :user_new_password_time nil
                     :user_email             nil
                     :user_email_token       0
                     :user_email_expires     nil
                     :user_touched           (c/to-sql-time (t/now))
                     :user_registration      (c/to-sql-time (t/now))}
                    {:user_name              "guest"
                     :user_role              "reader"
                     :user_password          (hashers/derive "guest")
                     :user_new_password      nil
                     :user_new_password_time nil
                     :user_email             nil
                     :user_email_token       0
                     :user_email_expires     nil
                     :user_touched           (c/to-sql-time (t/now))
                     :user_registration      (c/to-sql-time (t/now))}])

(defn user-name->user-id
  ([name]
   (user-name->user-id name h2-db))
  ([name db-name]
   (:user_id (first (jdbc/query
                      db-name
                      ["select user_id from users where user_name=?" name])))))

(defn user-name->user-role
  "Return the user role (a keyword) assigned to the named user. If the
  user does not exist, return nil."
  ([name]
   (user-name->user-role name h2-db))
  ([name db-name]
   (let [string-role (:user_role
                       (first
                         (jdbc/query
                           db-name
                           ["select user_role from users where user_name=?" name])))]
     (when string-role
       (let [colon-stripped (s/replace-first string-role ":" "")]
         (keyword colon-stripped))))))

(defn get-cwiki-user-id
  []
  (user-name->user-id "CWiki"))

(defn user-id->user-name
  "Given a user id, return a human-readable user name."
  ([id]
   (user-id->user-name id h2-db))
  ([id db-name]
   (:user_name (first (jdbc/query
                        db-name
                        ["select user_name from users where user_id=?" id])))))

(defn find-user-by-name
  "Look up the user in the database and return the map of user attributes
  for a matching entry. If no match, return nil."
  ([name]
   (find-user-by-name name h2-db))
  ([name db-name]
   (let [user-map (first (jdbc/query
                           db-name
                           ["select * from users where user_name=?" name]))]
     user-map)))

(defn find-user-by-case-insensitive-name
  "Look up the user in the database using a case-insensitive search for the
  username. Return the matching entry, if any. Otherwise, return nil."
  ([name]
   (find-user-by-case-insensitive-name name h2-db))
  ([name db-name]
   (let [user-map (first (jdbc/query
                           db-name
                           [(str "select * from users where user_name like '"
                                 name "'")]))]
     user-map)))

(defn get-user-by-username-and-password
  "Look up a user and verify that the password is a match. If so,
  return the user record, otherwise return nil."
  [username password]
  (let [result (find-user-by-name username)
        pw-hash (:user_password result)]
    (when (and (= (:user-name result))
               (hashers/check password pw-hash))
      result)))

(defn lookup-user
  "Look up a user an verify that the password is a match. If the user
  cannot be found or the password doesn't match, return nil. Otherwise,
  return a copy of the user record with the password digest dissociated
  from it."
  [username password]
  (when-let [user (find-user-by-name username)]
    (let [pw (get user :user_password)]
      (when (hashers/check password pw)
        (dissoc user :user_password)))))

(defn update-user
  "Update the database entry for the user with the given id to the
  information included in the map."
  [user_id user-map]
  (jdbc/update! h2-db :users user-map ["user_id=?" user_id]))

(defn- clob->string
  "Return a string created by translating and H2 Clob."
  [clob]
  (with-open [rdr (.getCharacterStream clob)]
    (let [lseq (line-seq rdr)
          butlast-line (butlast lseq)
          butlast-line-mapped (map #(str % "\n") butlast-line)
          last-line (last lseq)
          all-lines-with-newline (concat butlast-line-mapped last-line)]
      (apply str all-lines-with-newline))))

(defn- to-map-helper
  "Return a post map created from the result set."
  [rs]
  (let [frs (first rs)
        raw-content (:page_content frs)]
    (if raw-content
      (assoc frs :page_content (clob->string raw-content))
      frs)))

(defn find-post-by-title
  "Look up a post in the database with the given title. Return the page
  map for the post if found; nil otherwise. The lookup is case-insensitive."
  ([title] (find-post-by-title title h2-db))
  ([title db-name]
   (let [sql-str (str "select * from pages where page_title like '" title "'")
         res (jdbc/query db-name sql-str {:result-set-fn to-map-helper})]
     res)))

(defn title->user-id
  ([title] (title->user-id title h2-db))
  ([title db-name]
   (:page_author
     (first (jdbc/query db-name
                        ["select page_author from pages where page_title=?" title])))))

(defn page-id->title
  ([id] (page-id->title id h2-db))
  ([id db-name]
   (let [sql-str (str "select page_title from pages where page_id=" id)]
     (:page_title (first (jdbc/query db-name sql-str))))))

(defn title->page-id
  ([title] (title->page-id title h2-db))
  ([title db-name]
   (:page_id (first (jdbc/query
                      db-name
                      ["select page_id from pages where page_title=?" title])))))

(defn- to-content-helper
  [rs]
  (let [raw-content (:page_content (first rs))]
    (when raw-content
      (clob->string raw-content))))

(defn page-id->content
  ([id] (page-id->content id h2-db))
  ([id db-name]
   (let [sql-str (str "select page_content from pages where page_id=" id)
         res (jdbc/query db-name sql-str {:result-set-fn to-content-helper})]
     res)))

(defn page-map->id
  [m]
  (:page_id m))

(defn page-map->title
  [m]
  (:page_title m))

(defn page-map->content
  [m]
  (:page_content m))

(defn page-map->author
  [m]
  (let [author-id (:page_author m)
        result (jdbc/query h2-db ["select user_name from users where user_id=?" author-id])
        name (:user_name (first result))]
    (if (and result name)
      name
      "Unknown")))

(defn page-map->tags
  [m]
  (:page_tags m))

(defn page-map->created-date
  [m]
  (:page_created m))

(defn page-map->modified-date
  [m]
  (:page_modified m))

(defn get-all-page-names
  "Return a sorted set of all of the page titles in the wiki,
  including all of the 'special' pages."
  ([]
   (get-all-page-names h2-db))
  ([db-name]
   (when-let [title-array (jdbc/query db-name ["select page_title from pages"])]
     (reduce #(conj %1 (:page_title %2))
             (special/get-all-special-page-names) title-array))))

(defn- case-insensitive-comparator
  "Case-insensitive string comparator."
  [^String s1 ^String s2]
  (.compareToIgnoreCase s1 s2))

(defn get-all-users
  "Return a sorted set of all of the user names known to the wiki."
  ([]
   (get-all-users h2-db))
  ([db-name]
   (when-let [user-array (jdbc/query db-name ["select user_name from users"])]
     (into (sorted-set-by case-insensitive-comparator)
           (mapv #(:user_name %) user-array)))))

(defn update-page-title-and-content!
  [id title content]
  (jdbc/update! h2-db :pages {:page_title    title
                              :page_content  content
                              :page_modified (c/to-sql-time (t/now))}
                ["page_id=?" id]))

(defn insert-new-page!
  "Insert a new page into the database given a title and content.
  Return the post map for the new page (including id and dates).
  If the arguments do not include an author id, use the CWiki
  author id (same as CWiki user id)."
  ([title content]
   (insert-new-page! title content (get-cwiki-user-id)))
  ([title content author-id]
   (let [post-map (create-new-post-map title content author-id)]
     (jdbc/insert! h2-db :pages post-map)
     (find-post-by-title title))))

;;
;; The functions related to tags follow.
;;

(defn get-all-tags
  "Return a sorted set of all of the tags in the wiki."
  ([]
   (get-all-tags h2-db))
  ([db-name]
   (when-let [tag-array (jdbc/query db-name ["select tag_name from tags"])]
     (reduce
       #(conj %1 (:tag_name %2))
       (sorted-set-by case-insensitive-comparator)
       tag-array))))

(defn get-tag-ids-for-page
  "Return a seq of the tag ids associated with the page-id. Returns
  nil if the page id is nil."
  ([page-id]
   (get-tag-ids-for-page page-id h2-db))
  ([page-id db]
   (when page-id
     (let [sql (str "select tag_id from tags_x_pages where page_id=" page-id)
           rs (jdbc/query db [sql])]
       (reduce #(conj %1 (:tag_id %2)) [] rs)))))

(defn convert-seq-to-comma-separated-string
  "Return a string containing the members of the sequence separated by commas."
  [the-seq]
  (let [but-last-comma-mapped (map #(str % ", ") (butlast the-seq))]
        (apply str (concat but-last-comma-mapped (str (last the-seq))))))

(defn get-tag-names-for-page
  "Returns a case-insensitive sorted-set of tag name associated with the page.
  If ther are no such tags (it's nil or and empy seq), returns a sorted set
  containing the word 'None'."
  ([page-id]
   (get-tag-names-for-page page-id h2-db))
  ([page-id db]
   (let [tag-ids (get-tag-ids-for-page page-id)]
     (if (or (nil? tag-ids) (empty? tag-ids))
       (into (sorted-set-by case-insensitive-comparator) ["None"])
       (let [tag-ids-as-string (convert-seq-to-comma-separated-string tag-ids)
             sql (str "select tag_name from tags where tag_id in ("
                      tag-ids-as-string ");")
             rs (jdbc/query db [sql])]
         (reduce #(conj %1 (:tag_name %2))
                 (sorted-set-by case-insensitive-comparator) rs))))))

(defn- get-tags-from-meta
  "Return a string of tags, nicely separated with commas, from
  some parsed YAML metadata."
  [meta]
  (let [tags (:tags meta)]
    (when tags
      (if (seq tags)
        (s/join ", " tags)
        tags))))

(defn- get-tag-vector-from-meta
  "Return a vector of tags contained the the meta data."
  [meta]
  (reduce conj [] (:tags meta)))

(defn- get-row-id
  "Return the row id returned as the result of a single insert operation.
  It's buried in an odd map, hence this function."
  [result]
  (first (vals (first result))))

(defn- classify-tags
  "Return a map containing two vectors. The :new vector contains maps for
  each new tag with the :id of nil and :tag_name of all of the tags that
  are not already in the tags table.
  The :old vector contains maps with the :id of the existing tag as well as
  it's :tag_name. Input is a vector of tag names."
  ([tags]
   (classify-tags tags h2-db))
  ([tags db]
   (loop [t tags new-t [] old-t []]
     (if (empty? t)
       {:new new-t :old old-t}
       (let [rs (jdbc/query
                  db
                  ["select * from tags where tag_name=?" (first t)])]
         (if (empty? rs)
           (recur (rest t) (conj new-t {:id nil :tag_name (first t)}) old-t)
           (recur (rest t)
                  new-t
                  (conj
                    old-t
                    {:id (:tag_id (first rs)) :tag_name (first t)}))))))))

(defn- insert-new-tags
  "Insert the new tags contained in the map and return a new copy of the
  map with ids of the newly inserted tags too."
  ([new-tag-map]
   (insert-new-tags new-tag-map h2-db))
  ([new-tag-map db]
   (let [map-vec (reduce
                   #(conj
                      %1
                      (let [tn (:tag_name %2)]
                        {:id       (get-row-id (jdbc/insert! db :tags
                                                             {:tag_name tn}))
                         :tag_name tn}))
                   [] new-tag-map)]
     map-vec)))

(defn- tag-is-used?
  "Return a result set if the tag is used, nil otherwise."
  ([tag-id]
   (tag-is-used? tag-id h2-db))
  ([tag-id db]
   (println "tag-is-used? tag-id:" tag-id)
   (let [sql (str "select * from tags_x_pages where tag_id=" tag-id)
         _ (println "sql:" sql)
         res (jdbc/query db [sql])]
     (println "res:" res)
     (not (empty? res)))))

(defn- remove-deleted-tags
  "Remove deleted tags for the page, and if there are no more pages
  with the tag, remove them from the cross ref and tags tables too."
  ([tag-map-vector page-id]
   (remove-deleted-tags tag-map-vector page-id h2-db))
  ([tag-map-vector page-id db]
   (println "remove-deleted-tags: tag-map-vector:" tag-map-vector
            ", page-id:" page-id)
    ; Remove from the cross-reference table.
   (let [sql (str "select xref_id from tags_x_pages where tag_id in('"
                  (convert-seq-to-comma-separated-string tag-map-vector)
                  "');")
         _ (println "deletion sql:" sql)
         res (jdbc/delete! db :tags_x_pages ["page_id=?" page-id])
         _ (println "res:" res)]
     ; Check if any need to be removed from the tags table because
     ; they are not referred to by any page.
     (let [not-used (filterv (complement tag-is-used?) tag-map-vector)
           _ (println "not-used:" not-used)]
       (when (not (empty? not-used))
         (let [sql (str "tag_id in ('"
                        (convert-seq-to-comma-separated-string not-used) "');")
               _ (println "sql:" sql)
               res (jdbc/delete! db :tags [sql])
               _ (println "result of deletion: res:" res)]))))))

(defn- add-tags-and-page-id-to-cross-reference-table
  ([map-vec page-id]
   (add-tags-and-page-id-to-cross-reference-table map-vec page-id h2-db))
  ([map-vec page-id db]
   (mapv #(jdbc/insert! db :tags_x_pages {:tag_id  (:id %)
                                          :page_id page-id}) map-vec)))

(defn- update-tags-for-page
  "Update the tag tables with tags and associate them with the
  page-id."
  [desired-tags page-id]
  (let [classified-tags (classify-tags desired-tags)
        nt (insert-new-tags (:new classified-tags))
        ot (:old classified-tags)]
    ; Shouldn't we update the tags table with new tags here?
    (add-tags-and-page-id-to-cross-reference-table nt page-id)
    (add-tags-and-page-id-to-cross-reference-table ot page-id)))

(defn- rs->page-ids-as-set
  "Take a result set consisting of a sequence of maps with the key
  :page_id and the page id as an integer. Return a set of page ids.
  If the result set is nil, returns nil."
  [rs]
  (when rs
    (reduce #(conj %1 (:page_id %2)) #{} rs)))

(defn- get-ids-of-all-pages-with-tag
  "Return a set of all the page ids for pages with the given tag name."
  ([tag-name]
   (get-ids-of-all-pages-with-tag tag-name h2-db))
  ([tag-name db-name]
   (let [rs (jdbc/query
              db-name
              ["select tag_id from tags where tag_name=?" tag-name])
         tag-id (:tag_id (first rs))]
     (jdbc/query db-name
                 ["select page_id from tags_x_pages where tag_id=?" tag-id]
                 {:result-set-fn rs->page-ids-as-set}))))

(defn get-titles-of-all-pages-with-tag
  "Return a case-insensitive sorted-set of all of the titles of all
  of the pages that have this tag."
  [tag-name]
  (let [page-ids (get-ids-of-all-pages-with-tag tag-name)]
    (reduce #(conj %1 (page-id->title %2))
            (sorted-set-by case-insensitive-comparator) page-ids)))

(defn delete-page-by-id!
  "Remove the page and related data from the database."
  [page-id]
  (println "delete-page-by-id!:" page-id)
  (when page-id
    (let [tags (get-tag-ids-for-page page-id)]
      (remove-deleted-tags tags page-id)
      (jdbc/delete! h2-db :pages ["page_id=?" page-id]))))

(defn- add-page-with-meta-from-file!
  "Add a page to the database based on the information in a Markdown file
  containing YAML front matter."
  [file-name]
  (let [resource-prefix "private/md/"
        m (files/load-markdown-resource (str resource-prefix file-name))
        meta (:meta m)
        author-id (user-name->user-id (:author meta))
        title (:title meta)]
    (when (and author-id title)
      (let [content (:body m)
            creation-date-str (or (:date meta)
                                  (:created meta))
            creation-date (if creation-date-str
                            (c/to-sql-time (f/parse markdown-pad-format
                                                    creation-date-str))
                            (c/to-sql-time (t/now)))
            update-date-str (or (:updated meta)
                                (:changed meta)
                                (:modified meta))
            update-date (if update-date-str
                          (c/to-sql-time (f/parse markdown-pad-format
                                                  update-date-str))
                          creation-date)
            ;   tags (get-tags-from-meta meta)
            pm (merge (create-new-post-map title content author-id)
                      {:page_created creation-date}
                      {:page_modified update-date}
                      ;{:page_tags tags}
                      )
            page-id (get-row-id (jdbc/insert! h2-db :pages pm))]
        (update-tags-for-page (get-tag-vector-from-meta meta) page-id)))))

;(defn- add-page-from-file!
;  [m id]
;  (println "add-page-from-file!: m:" m ", id:" id)
;  (let [resource-prefix "private/md/"
;        title (:title m)
;        content (slurp (io/resource
;                         (str resource-prefix (:file-name m))))
;        post-map (create-new-post-map title content id)]
;    (jdbc/insert! h2-db :pages post-map)))

(defn add-user
  ([user-name user-password user-role]
   (add-user user-name user-password user-role nil))
  ([user-name user-password user-role user-email]
   (let [role user-role
         usr {:user_name              user-name
              :user_role              role
              :user_password          (hashers/derive user-password)
              :user_new_password      nil
              :user_new_password_time nil
              :user_email             user-email
              :user_email_token       0
              :user_email_expires     nil
              :user_touched           (c/to-sql-time (t/now))
              :user_registration      (c/to-sql-time (t/now))}]
     (jdbc/insert! h2-db :users usr))))

(defn delete-user
  ([user-id]
   (delete-user user-id h2-db))
  ([user-id db-name]
   (jdbc/delete! db-name :users ["user_id=?" user-id])))

(defn has-admin-logged-in?
  "Return true if the admin has ever logged in; nil otherwise."
  []
  (let [result (jdbc/query
                 h2-db
                 ["select admin_has_logged_in from admin where admin_id=?" 1])]
    (:admin_has_logged_in (first result))))

(defn set-admin-has-logged-in
  [b]
  (jdbc/update! h2-db :admin {:admin_has_logged_in b}
                ["admin_id=?" 1]))

(defn- init-admin-table
  []
  (jdbc/insert! h2-db :admin {:admin_id            1
                              :admin_has_logged_in nil}))

(defn- add-initial-users!
  []
  (println "Adding initial users.")
  (mapv #(jdbc/insert! h2-db :users %) initial-users)
  (println "Done!"))

(defn- add-initial-pages!
  []
  (mapv #(add-page-with-meta-from-file! %) (files/load-initial-page-list)))

(defn- add-initial-roles!
  []
  (println "Adding roles.")
  (mapv (fn [%] (jdbc/insert! h2-db :roles {:role_name %})) valid-roles)
  (println "Done!"))

(defn- create-tables
  "Create the database tables for the application."
  []
  (println "Creating the tables.")
  (try (jdbc/db-do-commands
         h2-db false
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
       (jdbc/execute! h2-db "alter table tags_x_pages add foreign key (tag_id) references public.tags(tag_id);")
       ; (println "unique tags:" (jdbc/execute! h2-db "alter table tags add unique (tag_name);"))
       (catch Exception e (println e)))
  (println "Done!"))

(defn- create-db
  "Create the database tables and initialize them with content for
  first-time use."
  []
  (create-tables)
  (init-admin-table)
  (add-initial-users!)
  (add-initial-pages!)
  (add-initial-roles!))

(defn db-exists?
  "Return true if the wiki database already exists."
  []
  (.exists ^File (clojure.java.io/as-file db-file-name-long)))

(defn init-db!
  "Initialize the database. Will create the database and
  tables if needed."
  []
  (when-not (db-exists?)
    (println "Creating initial database.")
    (io/make-parents db-file-name)
    (create-db)))

