(ns cwiki.models.wiki-db
  (require [buddy.hashers :as hashers]
           [clojure.java.io :as io]
           [clojure.java.jdbc :as jdbc]
           [clojure.set :as set]
           [clojure.string :as s]
           [clj-time.coerce :as c]
           [clj-time.core :as t]
           [clj-time.format :as f]
           [cwiki.util.files :as files]
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
      (s/join all-lines-with-newline))))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-all-page-names-in-db
  "Return a collection of page name maps with all of the page names
  in the db (as opposed to any generated pages.)"
  ([]
   (get-all-page-names-in-db h2-db))
  ([db-name]
   (let [title-array (jdbc/query db-name ["select page_title from pages"])]
     title-array)))

(defn get-all-page-names
  "Return a sorted set of all of the page titles in the wiki,
  including all of the 'special' pages."
  ([]
   (get-all-page-names h2-db))
  ([db-name]
   (when-let [title-array (get-all-page-names-in-db db-name)]
     (reduce #(conj %1 (:page_title %2))
             (special/get-all-special-page-names) title-array))))

(defn case-insensitive-comparator
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
           (mapv :user_name user-array)))))

(defn convert-seq-to-comma-separated-string
  "Return a string containing the members of the sequence separated by commas."
  [the-seq]
  (let [but-last-comma-mapped (map #(str % ", ") (butlast the-seq))]
    (s/join (concat but-last-comma-mapped (str (last the-seq))))))

;;
;; The functions related to tags follow.
;;

(defn get-all-tag-names
  "Return a case-insensitive sorted set of all of the tags in the wiki."
  ([]
   (get-all-tag-names h2-db))
  ([db-name]
   (when-let [tag-array (jdbc/query db-name ["select tag_name from tags"])]
     (reduce
       #(conj %1 (:tag_name %2))
       (sorted-set-by case-insensitive-comparator)
       tag-array))))

(defn- get-tag-id-from-name
  "Given a string containing a tag name, return the id from the tags table
  or nil if a tag with the name has not been recorded."
  ([name]
   (get-tag-id-from-name name h2-db))
  ([name db]
   (let [sql (str "select tag_id from tags where tag_name='" name "';")
         rs (jdbc/query db [sql])]
     (when (seq rs)
       (:tag_id (first rs))))))

(defn- get-tag-ids-for-page
  "Return a seq of the tag ids associated with the page-id. Returns
  nil if the page id is nil."
  ([page-id]
   (get-tag-ids-for-page page-id h2-db))
  ([page-id db]
   (when page-id
     (let [sql (str "select tag_id from tags_x_pages where page_id=" page-id)
           rs (jdbc/query db [sql])]
       (reduce #(conj %1 (:tag_id %2)) [] rs)))))

(defn get-tag-names-for-page
  "Returns a case-insensitive sorted-set of tag names associated with the page.
  If there are no such tags (it's nil or an empty seq), returns an empty set."
  ([page-id]
   (get-tag-names-for-page page-id h2-db))
  ([page-id db]
   (let [tag-ids (filterv (complement nil?) (get-tag-ids-for-page page-id))]
     (if (or (nil? tag-ids) (empty? tag-ids))
       (sorted-set-by case-insensitive-comparator)
       (let [tag-ids-as-string (convert-seq-to-comma-separated-string tag-ids)
             sql (str "select tag_name from tags where tag_id in ("
                      tag-ids-as-string ");")
             rs (jdbc/query db [sql])]
         (reduce #(conj %1 (:tag_name %2))
                 (sorted-set-by case-insensitive-comparator) rs))))))

(defn- get-tag-name-set-from-meta
  "Return a case-insensitive sorted-set of tag names contained the meta data."
  [meta]
  (reduce conj (sorted-set-by case-insensitive-comparator) (:tags meta)))

(defn- get-row-id
  "Return the row id returned as the result of a single insert operation.
  It's buried in an odd map, hence this function."
  [result]
  (first (vals (first result))))

(defn- is-tag-id-in-xref-table?
  "Return true if the tag id is recorded anywhere in the xref table,
  false otherwise."
  ([tag-id]
   (is-tag-id-in-xref-table? tag-id h2-db))
  ([tag-id db]
   (let [sql (str "select * from tags_x_pages where tag_id=" tag-id ";")
         res (jdbc/query db [sql])]
     (seq res))))

(defn- remove-deleted-tags
  "Remove deleted tags for the page, and if there are no more pages
  with the tag, remove them from the tags tables too."
  ([tag-id-vector page-id]
   (remove-deleted-tags tag-id-vector page-id h2-db))
  ([tag-id-vector page-id db]
    ; Remove from the cross-reference table.
   (let [sql (str "page_id=" page-id " and tag_id in ("
                  (convert-seq-to-comma-separated-string tag-id-vector) ");")]
     (jdbc/delete! db :tags_x_pages [sql]))
    ; Check if any need to be removed from the tags table because
    ; they are not referred to by any other page.
   (let [not-used (filterv (complement is-tag-id-in-xref-table?)
                           tag-id-vector)]
     (when (seq not-used)
       (let [sql (str "tag_id in ("
                      (convert-seq-to-comma-separated-string not-used) ");")]
         (jdbc/delete! db :tags [sql]))))))

(defn- delete-unused-tags
  "Given a set of tag name strings, remove them from the cross-reference
  entries for the page and, possibly, the tag table if they are used
  nowhere else."
  ([tag-name-set page-id]
   (delete-unused-tags tag-name-set page-id h2-db))
  ([tag-name-set page-id db]
   (let [tag-ids (reduce #(conj %1 (get-tag-id-from-name %2)) [] tag-name-set)]
     (remove-deleted-tags tag-ids page-id db))))

(defn- is-tag-name-in-tag-table?
  "Return true if the tag name is already recorded in the tags
  table, nil otherwise."
  ([tag-name]
   (is-tag-name-in-tag-table? tag-name h2-db))
  ([tag-name db]
   (let [sql (str "select tag_id from tags where tag_name='" tag-name "';")
         res (jdbc/query db [sql])]
     (seq res))))

(defn- add-new-tags-for-page
  "Add new tags for the page to the cross-reference table and, possibly,
  the tag table."
  ([new-tag-set page-id]
   (add-new-tags-for-page new-tag-set page-id h2-db))
  ([new-tag-set page-id db]
    ; Add tags that are new to the database to the tabs table.
   (let [completely-new (filterv (complement is-tag-name-in-tag-table?)
                                 new-tag-set)]
     (when (seq completely-new)
       (mapv #(jdbc/insert! db :tags {:tag_name %}) completely-new)))
    ; Add tags to the xref table.
   (let [tag-ids (reduce #(conj %1 (get-tag-id-from-name %2)) [] new-tag-set)]
     (mapv #(jdbc/insert! db :tags_x_pages {:tag_id % :page_id page-id}) tag-ids))))

(defn- update-tags-for-page
  "Update the tag tables with tags and associate them with the
  page-id."
  [desired-tag-set page-id]
  (let [existing-tags (get-tag-names-for-page page-id)
        tags-to-remove (set/difference existing-tags desired-tag-set)
        tags-to-add (set/difference desired-tag-set existing-tags)]
    (add-new-tags-for-page tags-to-add page-id)
    (when (seq tags-to-remove)
      (delete-unused-tags tags-to-remove page-id))))

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

;;
;; End of tag functions.
;;

(defn get-ids-of-all-pages-with-user
  "Return a set of all the page ids for pages with the given user name."
  ([user-name]
   (get-ids-of-all-pages-with-user user-name h2-db))
  ([user-name db]
   (let [user-id (user-name->user-id user-name)]
     (jdbc/query
       db
       ["select page_id from pages where page_author=?" user-id]
       {:result-set-fn rs->page-ids-as-set}))))

(defn get-titles-of-all-pages-with-user
  "Return a case-insensitive sorted-set of all of the titles of all
  of the pages that have this user name."
  [user-name]
  (let [page-ids (get-ids-of-all-pages-with-user user-name)]
    (reduce #(conj %1 (page-id->title %2))
            (sorted-set-by case-insensitive-comparator) page-ids)))

(defn update-page-title-and-content!
  [id title tag-set content]
  (jdbc/update! h2-db :pages {:page_title    title
                              :page_content  content
                              :page_modified (c/to-sql-time (t/now))}
                ["page_id=?" id])
  (update-tags-for-page tag-set id))

(defn insert-new-page!
  "Insert a new page into the database given a title, tags and content.
  Return the post map for the new page (including id and dates).
  If the arguments do not include an author id, use the CWiki
  author id (same as CWiki user id)."
  ([title content tags]
   (insert-new-page! title content tags (get-cwiki-user-id)))
  ([title content tags author-id]
   (let [post-map (create-new-post-map title content author-id)]
     (jdbc/insert! h2-db :pages post-map)
     (let [pm (find-post-by-title title)
           id (page-map->id pm)]
       (update-tags-for-page tags id)
       pm))))

(defn delete-page-by-id!
  "Remove the page and related data from the database."
  [page-id]
  (when page-id
    (let [tags (get-tag-ids-for-page page-id)]
      (remove-deleted-tags tags page-id)
      (jdbc/delete! h2-db :pages ["page_id=?" page-id]))))

(defn- get-author-from-import-meta-data
  "Return the author id based on the content of the meta-data. If there is no
  author or they do not have the appropriate role, return the default id."
  [meta default-author-id]
  (let [author-name (:author meta)
        author-id (user-name->user-id author-name)]
    (if (or (nil? author-id)
            (= "reader" (user-name->user-role author-name)))
      (user-name->user-id default-author-id)
      author-id)))

(defn add-page-from-map
  "Add a new page to the wiki using the information in a map. The map must
  have two keys, :meta and :body. Meta contains things like the author name,
  tags, etc. The body contains the Markdown content. If the author cannot be
  determined or is not recognized, the contents of the 'default-author'
  argument is used. If there is no title, a random title is created.
  Return the title of the imported page."
  [m default-author]
  (let [meta (:meta m)
        author-id (get-author-from-import-meta-data meta default-author)
        title (or (:title meta) (str "Title - " (UUID/randomUUID)))]
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
            pm (merge (create-new-post-map title content author-id)
                      {:page_created creation-date}
                      {:page_modified update-date})
            page-id (get-row-id (jdbc/insert! h2-db :pages pm))
            tv (get-tag-name-set-from-meta meta)]
        (update-tags-for-page tv page-id)
        title))))

(defn- add-page-with-meta-from-file!
  "Add a page to the database based on the information in a Markdown file
  containing YAML front matter. The file name is appended to the path for
  the initial pages in the uberjar."
  [file-name]
  (let [resource-prefix "private/md/"
        m (files/load-markdown-from-resource (str resource-prefix file-name))]
    (add-page-from-map m "CWiki")))

(defn add-user
  "Add a user to the system with the given user name and password. Optionally,
  add an email address for password recovery."
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
  "Delete the user with the give id."
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
  "Note that the admin user has logged in at least once and record it
  in the database."
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
  (mapv add-page-with-meta-from-file! (files/load-initial-page-list)))

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

