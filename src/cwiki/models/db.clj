(ns cwiki.models.db
  (require [buddy.hashers :as hashers]
           [clojure.java.jdbc :as jdbc]
           [clojure.java.io :as io]
           [clj-time.core :as t]
           [clj-time.coerce :as c]
           [cwiki.util.special :as special])
  (:import (java.io File)))

;; Things that deal with the database file and connection.

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
   (create-new-post-map title content 1))
  ([title content author-id]
   {:page_created  (c/to-sql-time (t/now))
    :page_modified (c/to-sql-time (t/now))
    :page_author   author-id
    :page_title    title
    :page_content  content}))

; Need "Front Page" to be first in list since some tests depend on it
; being in that position. Kinda fragile.

(def initial-pages [{:title "Front Page" :file-name "Front_Page.md"}
                    {:title "About" :file-name "About.md"}
                    {:title "About CWiki" :file-name "About_CWiki.md"}
                    {:title "About Roles" :file-name "About_Roles.md"}
                    {:title "About the Sidebar" :file-name "About_the_Sidebar.md"}
                    {:title "Admin" :file-name "Admin.md"}
                    {:title "Admin Pages" :file-name "Admin_Pages.md"}
                    {:title "CWiki FAQ" :file-name "CWiki_FAQ.md"}
                    {:title "CWiki Name" :file-name "CWiki_Name.md"}
                    {:title "Features" :file-name "Features.md"}
                    {:title "Links Primer" :file-name "Links_Primer.md"}
                    {:title "Other Wiki Software" :file-name "Other_Wiki_Software.md"}
                    {:title "Pages Primer" :file-name "Pages_Primer.md"}
                    {:title "Preferences" :file-name "Preferences.md"}
                    {:title "Sidebar" :file-name "Sidebar.md"}
                    {:title "Special Pages" :file-name "Special_Pages.md"}
                    {:title "Text Formatting" :file-name "Text_Formatting.md"}
                    {:title "Technical Notes" :file-name "Technical_Notes.md"}
                    {:title "To Do" :file-name "todo.md"}])

(def valid-roles [:cwiki :admin :editor :writer :reader])

(def initial-namespaces (atom ["cwiki" "default" "help"]))

(def initial-tags ["help" "wiki" "cwiki" "linking"])

(def initial-users [{:user_name              "CWiki"
                     :user_role              :cwiki
                     :user_password          (hashers/derive "BlahBlahBlah")
                     :user_new_password      nil
                     :user_new_password_time nil
                     :user_email             nil
                     :user_email_token       0
                     :user_email_expires     nil
                     :user_touched           (c/to-sql-time (t/now))
                     :user_registration      (c/to-sql-time (t/now))}
                    {:user_name              "admin"
                     :user_role              :admin
                     :user_password          (hashers/derive "admin")
                     :user_new_password      nil
                     :user_new_password_time nil
                     :user_email             nil
                     :user_email_token       0
                     :user_email_expires     nil
                     :user_touched           (c/to-sql-time (t/now))
                     :user_registration      (c/to-sql-time (t/now))}
                    {:user_name              "editor"
                     :user_role              :editor
                     :user_password          (hashers/derive "editor")
                     :user_new_password      nil
                     :user_new_password_time nil
                     :user_email             nil
                     :user_email_token       0
                     :user_email_expires     nil
                     :user_touched           (c/to-sql-time (t/now))
                     :user_registration      (c/to-sql-time (t/now))}
                    {:user_name              "writer"
                     :user_role              :writer
                     :user_password          (hashers/derive "writer")
                     :user_new_password      nil
                     :user_new_password_time nil
                     :user_email             nil
                     :user_email_token       0
                     :user_email_expires     nil
                     :user_touched           (c/to-sql-time (t/now))
                     :user_registration      (c/to-sql-time (t/now))}
                    {:user_name              "guest"
                     :user_role              :reader
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
   (user-name->user-id name sqlite-db))
  ([name db-name]
   (:user_id (first (jdbc/query
                      db-name
                      ["select user_id from users where user_name=?" name])))))

(defn get-cwiki-user-id
  []
  (user-name->user-id "CWiki"))

(defn user-id->user-name
  "Given a user id, return a human-readable user name."
  ([id]
   (user-id->user-name id sqlite-db))
  ([id db-name]
   (:user_name (first (jdbc/query
                        db-name
                        ["select user_name from users where user_id=?" id])))))

(defn find-user-by-name
  "Look up the user in the database and return the map of user attributes
  for a matching entry. If no match, return nil."
  ([name]
   (find-user-by-name name sqlite-db))
  ([name db-name]
   (let [user-map (first (jdbc/query
                           db-name
                           ["select * from users where user_name=?" name]))]
     user-map)))

(defn get-user-by-username-and-password [username password]
  (prn username password)
  (let [result (find-user-by-name username)
        pw-hash (:user_password result)]
    (when (and (= (:user-name result))
               (hashers/check password pw-hash))
      result)))

(defn lookup-user
  "Look up a user an verify that the password is a match. If the user
  cannot be found or the password doesn't match, return nil."
  [username password]
  (when-let [user (find-user-by-name username)]
    (let [pw (get user :user_password)]
      (when (hashers/check password pw)
        (dissoc user :user_password)))))

(defn find-post-by-title
  ([title] (find-post-by-title title sqlite-db))
  ([title db-name]
   (first (jdbc/query db-name ["select * from pages where page_title=?" title]))))

(defn title->user-id
  ([title] (title->user-id title sqlite-db))
  ([title db-name]
   (:page_author
     (first (jdbc/query db-name
                        ["select page_author from pages where page_title=?" title])))))

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
  (let [author-id (:page_author m)
        result (jdbc/query sqlite-db ["select user_name from users where user_id=?" author-id])
        name (:user_name (first result))]
    name))

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

(defn get-all-users
  "Return a sorted set of all of the user names known to the wiki."
  ([]
   (get-all-users sqlite-db))
  ([db-name]
   (when-let [user-array (jdbc/query db-name ["select user_name from users"])]
     (into (sorted-set) (mapv #(:user_name %) user-array)))))

(defn get-all-namespaces
  "Return a sorted set of all of the namespaces in the wiki."
  ([]
   (get-all-namespaces sqlite-db))
  ([db-name]
   (when-let [namespace-array (jdbc/query db-name ["select namespace_name from namespaces"])]
     (into (sorted-set) (mapv #(:namespace_name %) namespace-array)))))

(defn get-all-tags
  "Return a sorted set of all of the tags in the wiki."
  ([]
   (get-all-tags sqlite-db))
  ([db-name]
   (when-let [tag-array (jdbc/query db-name ["select tag_name from tags"])]
     (into (sorted-set) (mapv #(:tag_name %) tag-array)))))

(defn update-page-title-and-content!
  [id title content]
  (jdbc/update! sqlite-db :pages {:page_title    title
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
     (jdbc/insert! sqlite-db :pages post-map)
     (find-post-by-title title))))

(defn delete-page-by-id
  [page-id]
  (jdbc/delete! sqlite-db :pages ["page_id=?" page-id]))

(defn- add-page-from-file!
  [m id]
  (println "add-page-from-file!: m:" m ", id:" id)
  (let [resource-prefix "resources/private/md/"
        title (:title m)
        content (slurp (io/reader
                         (str resource-prefix (:file-name m))))
        post-map (create-new-post-map title content id)]
    (jdbc/insert! sqlite-db :pages post-map)))

(defn add-user
  ([user-name user-password user-role]
   (add-user user-name user-password user-role nil))
  ([user-name user-password user-role user-email]
   (let [role-as-keyword (keyword user-role)
         usr {:user_name              user-name
              :user_role              role-as-keyword
              :user_password          (hashers/derive user-password)
              :user_new_password      nil
              :user_new_password_time nil
              :user_email             user-email
              :user_email_token       0
              :user_email_expires     nil
              :user_touched           (c/to-sql-time (t/now))
              :user_registration      (c/to-sql-time (t/now))}]
     (jdbc/insert! sqlite-db :users usr))))

(defn- add-initial-users!
  []
  (println "Adding initial users.")
  (mapv #(jdbc/insert! sqlite-db :users %) initial-users)
  (println "Done."))

(defn get-initial-user-id
  "Get the user id for the initial user (CWiki). Probably always 1."
  []
  (println "Getting initial user id.")
  (let [initial-id (jdbc/query sqlite-db ["select user_id from users where user_name=?" "CWiki"])
        id (:user_id (first initial-id))]
    (println "Returning id:" id)
    id))

(defn- add-initial-pages!
  [user-id]
  (mapv #(add-page-from-file! % user-id) initial-pages))

(defn- add-initial-namespaces!
  []
  (println "adding namespaces")
  (mapv (fn [%] (jdbc/insert! sqlite-db :namespaces {:namespace_name %})) @initial-namespaces)
  (println "done"))

(defn- add-initial-tags!
  []
  (println "adding tags")
  (mapv (fn [%] (jdbc/insert! sqlite-db :tags {:tag_name %})) initial-tags))

(defn- add-initial-roles!
  []
  (println "adding roles")
  (mapv (fn [%] (jdbc/insert! sqlite-db :roles {:role_name %})) valid-roles)
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
                                                     [:page_author :integer]
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
                                                     [:tag_name :text]])
                             (jdbc/create-table-ddl :user_x_pages
                                                    [[:x_ref_id :integer :primary :key]
                                                     [:user_id :integer]
                                                     [:page_id :integer]])])
       (catch Exception e (println e)))
  (println "done"))

(defn- create-db
  []
  (create-tables)
  (add-initial-users!)
  (add-initial-pages! (get-initial-user-id))
  (add-initial-roles!)
  (add-initial-namespaces!)
  (add-initial-tags!))

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

