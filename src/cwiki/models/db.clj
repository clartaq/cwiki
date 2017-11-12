(ns cwiki.models.db
  (require [clojure.java.jdbc :as jdbc]
           [clojure.java.io :as io]
           [clj-time.core :as t]
           [clj-time.coerce :as c]
           [cwiki.util.special :as special])
  (:import (java.io File)))

(def db-file-name "resources/public/db/database.db")

(def sqlite-db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     db-file-name
   })

(defn create-new-post-map
  ([title]
   (create-new-post-map title ""))
  ([title content]
   (let [pm {:page_created  (c/to-sql-time (t/now))
             :page_modified (c/to-sql-time (t/now))
             :page_author        "CWiki"
             :page_title         title
             :page_content       content}]
     pm)))

(def initial-pages [{:title "Front Page" :file-name "Front_Page.md"}
                    {:title "About" :file-name "About.md"}
                    {:title "About CWiki" :file-name "About_CWiki.md"}
                    {:title "About the Sidebar" :file-name "About_the_Sidebar.md"}
                    {:title "CWiki FAQ" :file-name "CWiki_FAQ.md"}
                    {:title "Technical Notes" :file-name "Technical_Notes.md"}
                    {:title "Features" :file-name "Features.md"}
                    {:title "Preferences" :file-name "Preferences.md"}
                    {:title "Other Wiki Software" :file-name "Other_Wiki_Software.md"}
                    {:title "Special Pages" :file-name "Special_Pages.md"}
                    {:title "Pages Primer" :file-name "Pages_Primer.md"}
                    {:title "Links Primer" :file-name "Links_Primer.md"}
                    {:title "Text Formatting" :file-name "Text_Formatting.md"}
                    {:title "Sidebar" :file-name "Sidebar.md"}
                    {:title "To Do" :file-name "todo.md"}
                    {:title "CWiki Name" :file-name "CWiki_Name.md"}])

(def valid-roles [:cwiki :admin :editor :writer :reader])

(def initial-namespaces (atom ["cwiki" "default" "help"]))

(def initial-user
  {:user_name              "CWiki"
   :user_role              :cwiki
   :user_password          "BlahBlahBlah"
   :user_new_password      nil
   :user_new_password_time nil
   :user_email             nil
   :user_email_token       0
   :user_email_expires     nil
   :user_touched           (c/to-sql-time (t/now))
   :user_registration      (c/to-sql-time (t/now))})

(defn user-name->user-id
  ([name]
   (user-name->user-id name sqlite-db))
  ([name db-name]
   (:user_id (first (jdbc/query
                      db-name
                      ["select user_id from users where user_name=?" name])))))

(defn find-post-by-title
  ([title] (find-post-by-title title sqlite-db))
  ([title db-name]
   (first (jdbc/query db-name ["select * from pages where page_title=?" title]))))

(defn page-id->title
  ([id] (page-id->title id sqlite-db))
  ([id db-name]
   (:page_title (first (jdbc/query
                    db-name
                    ["select page_title from pages where page_id=?" id])))))

(defn title->page-id
  ([title] (title->page-id title sqlite-db))
  ([title db-name]
   (:page_id (first (jdbc/query db-name
                                ["select page_id from pages where page_title=?" title])))))

(defn page-id->content
  ([id] (page-id->content id sqlite-db))
  ([id db-name]
   (:page_content (first (jdbc/query
                      db-name
                      ["select page_content from pages where page_id=?" id])))))

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
  (:page_author m))

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
   (get-all-page-names sqlite-db))
  ([db-name]
   (when-let [title-array (jdbc/query db-name ["select page_title from pages"])]
     (into (special/get-all-special-page-names)
           (mapv #(:page_title %) title-array)))))

(defn update-page-title-and-content!
  [id title content]
  (jdbc/update! sqlite-db :pages {:page_title    title
                                  :page_content  content
                                  :page_modified (c/to-sql-time (t/now))}
                ["page_id=?" id]))

(defn insert-new-page!
  "Insert a new page into the database given a title and content.
  Return the post map for the new page (including id and dates)."
  [title content]
  (let [post-map (create-new-post-map title content)]
    (jdbc/insert! sqlite-db :pages post-map)
    (find-post-by-title title)))

(defn delete-page-by-id
  [page-id]
  (jdbc/delete! sqlite-db :pages ["page_id=?" page-id]))

(defn- add-page-from-file!
  [m]
  (println "add-page-from-file!: m:" m)
  (let [resource-prefix "resources/private/md/"
        title (:title m)
        content (slurp (io/reader
                         (str resource-prefix (:file-name m))))
        post-map (create-new-post-map title content)]
    (jdbc/insert! sqlite-db :pages post-map)))

(defn- add-initial-user!
  []
  (jdbc/insert! sqlite-db :users initial-user))

(defn- add-initial-pages!
  []
  (mapv add-page-from-file! initial-pages))

(defn- add-initial-namespaces!
  []
  (println "adding namespaces")
  (mapv (fn[%] (jdbc/insert! sqlite-db :namespaces {:namespace_name %})) @initial-namespaces)
  (println "done"))

(defn- add-initial-roles!
  []
  (println "adding roles")
  (mapv (fn[%] (jdbc/insert! sqlite-db :roles {:role_name %})) valid-roles)
  (println "done"))

(defn- create-tables
  "Create the database tables for the application."
  []
  (println "creating tables")
  (try (jdbc/db-do-commands sqlite-db
                            [(jdbc/create-table-ddl :users
                                                    [[:user_id :integer :primary :key]
                                                     [:user_name :text]
                                                     [:user_role :text]
                                                     [:user_password :text]
                                                     [:user_new_password :text]
                                                     [:user_new_password_time :datetime]
                                                     [:user_email :text]
                                                     [:user_email_token :int]
                                                     [:user_email_expires :datetime]
                                                     [:user_touched :datetime]
                                                     [:user_registration :datetime]])
                             (jdbc/create-table-ddl :pages
                                                    [[:page_id :integer :primary :key]
                                                     [:page_created :datetime]
                                                     [:page_modified :datetime]
                                                     [:page_author :text]
                                                     [:page_title :text]
                                                     [:page_content :text]])
                             (jdbc/create-table-ddl :namespaces
                                                    [[:namespace_id :integer :primary :key]
                                                     [:namespace_name :text]])
                             (jdbc/create-table-ddl :roles
                                                    [[:role_id :integer :primary :key]
                                                     [:role_name :text]])
                             (jdbc/create-table-ddl :tags
                                                    [[:tag_id :integer :primary :key]
                                                     [:tag_name :text]])])
       (catch Exception e (println e)))
  (println "done"))

(defn- create-db
  []
  (create-tables)
  (add-initial-pages!)
  (add-initial-user!)
  (add-initial-roles!)
  (add-initial-namespaces!))

(defn db-exists?
  "Return true if the wiki database already exists."
  []
  (.exists ^File (clojure.java.io/as-file db-file-name)))

(defn init-db!
  "Initialize the database. Will create the database and
  tables if needed."
  []
  (when-not (db-exists?)
    (println "Creating initial database.")
    (io/make-parents db-file-name)
    (create-db)))

