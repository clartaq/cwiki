(ns cwiki.models.wiki-db
  (require [buddy.hashers :as hashers]
           [clojure.java.io :as io]
           [clojure.java.jdbc :as jdbc]
           [clojure.string :as s]
           [clj-time.coerce :as c]
           [clj-time.core :as t]
           [clj-time.format :as f]
           [cwiki.util.files :as files]
           [cwiki.util.pp :as pp]
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

; Need "Front Page" to be first in list since some tests depend on it
; being in that position. Kinda fragile.

(def initial-pages-with-front-matter
  ["Front_Page.md"
   "About.md"
   "About_Admin_Pages.md"
   "About_Backup_and_Restore.md"
   "About_Compressing_the_Database.md"
   "About_CWiki.md"
   "About_Front_Matter.md"
   "About_Images.md"
   "About_Import_Export.md"
   "About_Roles.md"
   "About_TeX.md"
   "About_the_Sidebar.md"
   "Admin.md"
   "CWiki_FAQ.md"
   "CWiki_Name.md"
   "Features.md"
   "How_to_Make_a_Table_of_Contents.md"
   "Limits.md"
   "Links_Primer.md"
   "Motivation.md"
   "Other_Wiki_Software.md"
   "Pages_Primer.md"
   "Path_to_Release.md"
   "Preferences.md"
   "Sidebar.md"
   "Special_Pages.md"
   "Text_Formatting.md"
   "Technical_Notes.md"
   "todo.md"
   "Wikilinks.md"])

(def valid-roles ["cwiki" "admin" "editor" "writer" "reader"])

(def initial-tags ["help" "wiki" "cwiki" "linking"])

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
     (into (special/get-all-special-page-names)
           (mapv #(:page_title %) title-array)))))

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

(defn get-all-namespaces
  "Return a sorted set of all of the namespaces in the wiki."
  ([]
   (get-all-namespaces h2-db))
  ([db-name]
   (when-let [namespace-array (jdbc/query db-name ["select namespace_name from namespaces"])]
     (into (sorted-set-by case-insensitive-comparator)
           (mapv #(:namespace_name %) namespace-array)))))

(defn get-all-tags
  "Return a sorted set of all of the tags in the wiki."
  ([]
   (get-all-tags h2-db))
  ([db-name]
   (when-let [tag-array (jdbc/query db-name ["select tag_name from tags"])]
     (into (sorted-set-by case-insensitive-comparator)
           (mapv #(:tag_name %) tag-array)))))

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

(defn delete-page-by-id
  [page-id]
  (jdbc/delete! h2-db :pages ["page_id=?" page-id]))

(defn- get-tags-from-meta
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

(defn- update-tag-tables
  "Update the tag tables with tags and associate them with the
  page-id."
  [tags page-id]
  (let [row-ids (reduce
                  #(conj %1
                         (get-row-id
                           (jdbc/insert!
                             h2-db :tags {:tag_name %2})))
                  [] tags)]
    ; add to cross-reference table
    (println "row-ids (tag ids):" row-ids ", page-id:" page-id)
    (mapv #(jdbc/insert! h2-db :tags_x_pages {:tag_id  %
                                              :page_id page-id}) row-ids)))

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
            tags (get-tags-from-meta meta)
            pm (merge (create-new-post-map title content author-id)
                      {:page_created creation-date}
                      {:page_modified update-date}
                      {:page_tags tags})
            page-id (get-row-id (jdbc/insert! h2-db :pages pm))]
        (update-tag-tables (get-tag-vector-from-meta meta) page-id)))))

(defn- add-page-from-file!
  [m id]
  (println "add-page-from-file!: m:" m ", id:" id)
  (let [resource-prefix "private/md/"
        title (:title m)
        content (slurp (io/resource
                         (str resource-prefix (:file-name m))))
        post-map (create-new-post-map title content id)]
    (jdbc/insert! h2-db :pages post-map)))

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
  (println "Done."))

(defn- add-initial-pages!
  []
  (mapv #(add-page-with-meta-from-file! %) initial-pages-with-front-matter))

(defn- add-initial-tags!
  []
  (println "adding tags")
  (mapv (fn [%] (jdbc/insert! h2-db :tags {:tag_name %})) initial-tags)
  (println "done"))

(defn- add-initial-roles!
  []
  (println "adding roles")
  (mapv (fn [%] (jdbc/insert! h2-db :roles {:role_name %})) valid-roles)
  (println "done"))

(defn- create-tables
  "Create the database tables for the application."
  []
  (println "creating tables")
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
                                  [:page_tags :varchar]
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
                                  [:page_id :integer]
                                  ;["foreign key (tag_id) references public.tags(tag_id)"]
                                  ;["foreign key (page_id) references public.pages(page_id"]
                                  ;foreign key (TOURISTINFO_ID) references touristinfo(TOURISTINFO_ID)
                                  ;["FOREIGN KEY(tag_id) REFERENCES tags(tag_id)"]
                                  ;["FOREIGN KEY(page_id) REFERENCES pages(page_id)"]
                                  ])
          (jdbc/create-table-ddl :user_x_pages
                                 [[:x_ref_id :integer :auto_increment :primary :key]
                                  [:user_id :integer]
                                  [:page_id :integer]
                                  ])])
       (catch Exception e (println e)))
  (println "done"))

(defn- create-db
  "Create the database tables and initialize them with content for
  first-time use."
  []
  (println "h2-db:" (pp/pp-map h2-db))
  (create-tables)
  (init-admin-table)
  (add-initial-users!)
  (add-initial-pages!)
  (add-initial-roles!)
  (add-initial-tags!))

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

