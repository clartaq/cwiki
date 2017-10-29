(ns cwiki.models.db
  (require [clojure.java.jdbc :as jdbc]
           [clojure.java.io :as io]
           [clj-time.core :as t]
           [clj-time.coerce :as c])
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
   (let [pm {:date     (c/to-sql-time (t/now))
             :modified (c/to-sql-time (t/now))
             :author   "CWiki"
             :title    title
             :content  content}]
     pm)))

(def initial-pages [{:title "Front Page" :file-name "Front_Page.md"}
                    {:title "About" :file-name "About.md"}
                    {:title "About CWiki" :file-name "About_CWiki.md"}
                    {:title "CWiki FAQ" :file-name "CWiki_FAQ.md"}
                    {:title "Technical Notes" :file-name "Technical_Notes.md"}
                    {:title "Features" :file-name "Features.md"}
                    {:title "Preferences" :file-name "Preferences.md"}
                    {:title "Other Wiki Software" :file-name "Other_Wiki_Software.md"}
                    {:title "Special Pages" :file-name "Special_Pages.md"}
                    {:title "Pages Primer" :file-name "Pages_Primer.md"}
                    {:title "Links Primer" :file-name "Links_Primer.md"}
                    {:title "Text Formatting" :file-name "Text_Formatting.md"}
                    {:title "CWiki Name" :file-name "CWiki_Name.md"}])

(def initial-user
  {:user "CWiki"
   :role "CWiki"
   :pwdate (c/to-sql-time (t/now))
   :mustchangepw true
   :digest "Blahblahbla"
   :front_page 0})

(defn- create-db
  "Create the database tables for the application."
  []
  (try (jdbc/db-do-commands sqlite-db
                            [(jdbc/create-table-ddl :users
                                                    [[:id :integer :primary :key]
                                                     [:user :text]
                                                     [:role :text]
                                                     [:pwdate :datetime]
                                                     [:mustchangepw :text]
                                                     [:digest :text]
                                                     [:front_page :integer]])
                             (jdbc/create-table-ddl :posts
                                                    [[:id :integer :primary :key]
                                                     [:date :datetime]
                                                     [:modified :datetime]
                                                     [:author :text]
                                                     [:title :text]
                                                     [:content :text]])])
       (catch Exception e (println e)))
  (jdbc/insert! sqlite-db :users initial-user))

(defn find-post-by-title
  ([title] (find-post-by-title title sqlite-db))
  ([title db-name]
   (first (jdbc/query db-name ["select * from posts where title=?" title]))))

(defn page-id->title
  ([id] (page-id->title id sqlite-db))
  ([id db-name]
   (:title (first (jdbc/query db-name ["select * from posts where id=?" id])))))

(defn title->page-id
  ([title] (title->page-id title sqlite-db))
  ([title db-name]
   (:id (first (jdbc/query db-name
                           ["select * from posts where title=?" title])))))

(defn page-id->content
  ([id] (page-id->content sqlite-db))
  ([id db-name]
   (:content (first (jdbc/query db-name
                                ["select * from posts where id=?" id])))))

(defn update-page-title-and-content!
  [id title content]
  (println "update-page... ")
  (jdbc/update! sqlite-db :posts {:title    title
                                  :content  content
                                  :modified (c/to-sql-time (t/now))}
                ["id=?" id]))

(defn insert-new-page!
  "Insert a new page into the database given a title and content.
  Return the post map for the new page (including id and dates)."
  [title content]
  (let [post-map (create-new-post-map title content)]
    (jdbc/insert! sqlite-db :posts post-map)
    (find-post-by-title title)))

(defn delete-page-by-id
  [page-id]
  (println "delete-page-by-id:"
           (jdbc/delete! sqlite-db :posts ["id=?" page-id])))

(defn- add-page-from-file!
  [m]
  (println "add-page-from-file!: m:" m)
  (let [resource-prefix "resources/private/md/"
        title (:title m)
        content (slurp (io/reader
                         (str resource-prefix (:file-name m))))
        post-map (create-new-post-map title content)]
    (jdbc/insert! sqlite-db :posts post-map)))

(defn- add-initial-pages!
  []
  (println "add-initial-pages!")
  (mapv add-page-from-file! initial-pages))

(defn init-db!
  "Initialize the database. Will create the database and
  tables if needed."
  []
  (when-not (.exists ^File (clojure.java.io/as-file db-file-name))
    (println "Need to create database.")
    (io/make-parents db-file-name)
    (create-db)
    (add-initial-pages!)))

