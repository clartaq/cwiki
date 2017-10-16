(ns cwiki.models.db
  (require [clojure.java.jdbc :as jdbc]
           [clojure.java.io :as io]
           [clj-time.core :as t]
           [clj-time.coerce :as c])
  (:import (java.io File)))

(def db-file-name "resources/public/db/database.db")

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "resources/public/db/database.db"
   })

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
  (try (jdbc/db-do-commands db
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
  (let [result (first (jdbc/insert! db :posts initial-post))
        the-key (first (keys result))
        rowid (the-key result)
        new-user (assoc initial-user :front_page rowid)]
    (jdbc/insert! db :posts initial-post)
    (jdbc/insert! db :posts other-wiki-software-post)
    (jdbc/insert! db :posts preferences-post)
    (jdbc/insert! db :users initial-user)
    new-user))

(defn find-post-by-title
  [title]
  (first (jdbc/query db ["select * from posts where title=?" title])))

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
          content (:content (first (jdbc/query db ["select * from posts where id=?" fp])))]
     ; (println "user:" user)
     ; (println "  front page:" content)
      )
    (let [result (jdbc/query db "select * from users")]
          (println "(keys (first result)):" (keys (first result)))
          (println "(:user (first result)):" (:user (first result))))
    (let [result (jdbc/query db "select * from posts")]
          (println "(keys (first result)):" (keys (first result)))
          (println "(:content (first result)):" (:content (first result))))
    ))

