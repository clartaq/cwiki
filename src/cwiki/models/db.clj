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
   :subname     "resources/public/db/database.db"
   })

(defn create-new-post-map
  ([title]
   (create-new-post-map title ""))
  ([title content]
   (let [pm {:date     (c/to-sql-time (t/now))
             :modified (c/to-sql-time (t/now))
             :author   "admin"
             :title    title
             :content  content}]
     pm)))

(def preferences-post
  {:date     (c/to-sql-time (t/now))
   :modified (c/to-sql-time (t/now))
   :author   "admin"
   :title    "Preferences"
   :content  (slurp (io/reader "resources/public/md/Preferences.md"))})


(def initial-post
  {:date     (c/to-sql-time (t/now))
   :modified (c/to-sql-time (t/now))
   :author   "admin"
   :title    "Front Page"
   :content  (slurp (io/reader "resources/public/md/Front_Page.md"))})

(def other-wiki-software-post
  {:date     (c/to-sql-time (t/now))
   :modified (c/to-sql-time (t/now))
   :author   "admin"
   :title    "Other Wiki Software"
   :content  (slurp (io/reader "resources/public/md/Other_Wiki_Software.md"))})

(def initial-user
  {:user "admin"
   :role "admin"
   :pwdate (c/to-sql-time (t/now))
   :mustchangepw true
   :digest "Blahblahbla"
   :front_page 0})

(defn create-db
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
  (let [result (first (jdbc/insert! sqlite-db :posts initial-post))
        the-key (first (keys result))
        rowid (the-key result)
        new-user (assoc initial-user :front_page rowid)]
    (jdbc/insert! sqlite-db :posts initial-post)
    (jdbc/insert! sqlite-db :posts other-wiki-software-post)
    (jdbc/insert! sqlite-db :posts preferences-post)
    (jdbc/insert! sqlite-db :users initial-user)
    new-user))

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

  ;(jdbc/insert! db-spec :table {:col1 42 :col2 "123"})               ;; Create
  ;(jdbc/query   db-spec ["SELECT * FROM table WHERE id = ?" 13])     ;; Read
  ;(jdbc/update! db-spec :table {:col1 77 :col2 "456"} ["id = ?" 13]) ;; Update
  ;(jdbc/delete! db-spec :table ["id = ?" 13])                        ;; Delete

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

(defn init-db
  "Initialize the database. Will create the database and
  tables if needed."
  []
  (when-not (.exists ^File (clojure.java.io/as-file db-file-name))
    (println "Need to create database.")
    (io/make-parents db-file-name)
    (let [user (create-db)
          fp (:front_page user)
          _ (println "fp:" fp)
          content (:content (first (jdbc/query sqlite-db ["select * from posts where id=?" fp])))]
     ; (println "user:" user)
     ; (println "  front page:" content)
      )
    (let [result (jdbc/query sqlite-db "select * from users")]
          (println "(keys (first result)):" (keys (first result)))
          (println "(:user (first result)):" (:user (first result))))
    (let [result (jdbc/query sqlite-db "select * from posts")]
          (println "(keys (first result)):" (keys (first result)))
          (println "(:content (first result)):" (:content (first result))))
    ))

