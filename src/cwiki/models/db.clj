(ns cwiki.models.db
  (require [buddy.hashers :as hashers]
           [clojure.java.io :as io]
           [clojure.java.jdbc :as jdbc]
           [clojure.string :as s]
           [clj-time.coerce :as c]
           [clj-time.core :as t]
           [clj-time.format :as f]
           [cwiki.util.files :as files]
          ; [cwiki.util.pp :as pp]
           [cwiki.util.special :as special])
  (:import (java.io File)
           (java.util UUID)
           (org.sqlite SQLiteConfig)
           (java.sql DriverManager Connection)))

;;
;; Dealing with foreign key constraints in SQLite with Clojure.
;;

; From Stackoverflow https://stackoverflow.com/questions/13348843/in-clojure-what-happens-when-you-call-sql-with-connection-within-another-sql-wi
;
; I would in general recommend using clojure.java.jdbc instead of
; clojure.contrib.sql because the latter is not supposed to work with clojure
; newer than 1.2.0. In clojure.java.jdbc with-connection uses binding to add
; the connection to a map of connections in the db var for any wrapped calls,
; so the second one will overwrite the first one.

; from: jdbc.clj
;
;(defn with-connection*
;  "Evaluates func in the context of a new connection to a database then
;  closes the connection."
;  [db-spec func]
;  (with-open [^java.sql.Connection con (get-connection db-spec)]
;    (binding [*db* (assoc *db* :connection con :level 0 :rollback (atom false))]
;      (func))))
;

; From https://dev.clojure.org/jira/browse/JDBC-38 in 2012
;(defn do-raw  [& commands]
;  (with-open [^java.sql.Statement stmt (let [^java.sql.Connection con (sql/connection)] (.createStatement con))]
;    (doseq [^String cmd commands]
;      (.addBatch stmt cmd))
;    (let [result (.executeBatch stmt)]
;      (if (and (= 1 (count result)) (= -2 (first result)))
;        (list (.getUpdateCount stmt))
;        (seq result)))))

; See https://dev.clojure.org/jira/browse/JDBC-38
;(jdbc/with-query-results res ["PRAGMA foreign_keys;"]
;                        (doall res)))

; From https://code-know-how.blogspot.ru/2011/10/how-to-enable-foreign-keys-in-sqlite3.html
; Recently I have created a database in SQLite with tables that has foreign
; keys ON DELETE CASCADE actions. To my surprise when I deleted the parent
; key each row of the child table associated with the parent key are not
; deleted. The answer is that by default in SQLite  foreign key support
; is turned off for compatibility. To enable foreign keys using Xerial
; SQLite JDBC Driver We have to enforce foreign key support every time
; we make a query.
;
; import org.sqlite.SQLiteConfig; ADDED BASED ON COMMENT IN POST.
;public static final String DB_URL = "jdbc:sqlite:database.db";
;public static final String DRIVER = "org.sqlite.JDBC";
;
;public static Connection getConnection() throws ClassNotFoundException {
;    Class.forName(DRIVER);
;    Connection connection = null;
;    try {
;        SQLiteConfig config = new SQLiteConfig();
;        config.enforceForeignKeys(true);
;        connection = DriverManager.getConnection(DB_URL,config.toProperties());
;    } catch (SQLException ex) {}
;    return connection;
;}

; Slightly paraphrased from https://dev.clojure.org/jira/browse/JDBC-38 in 2012
;(defn do-raw [db & commands]
;  (with-open [^java.sql.Statement stmt
;              (let [^java.sql.Connection con (jdbc/db-connection db)] ;(sql/connection)]
;                (.createStatement con))]
;    (doseq [^String cmd commands]
;      (.addBatch stmt cmd))
;    (let [result (.executeBatch stmt)]
;      (if (and (= 1 (count result)) (= -2 (first result)))
;        (list (.getUpdateCount stmt))
;        (seq result)))))

;(exec-foreign-keys-pragma-statement sqlite-db)
;(with-db-connection [db-con db-spec]
;                    (let [;; fetch some rows using this connection
;                          rows (jdbc/query db-con ["SELECT * FROM table WHERE id = ?" 42])]
;                      ;; insert a copy of the first row using the same connection
;                      (jdbc/insert! db-con :table (dissoc (first rows) :id)))

;(println "jdbc/query:" (jdbc/query sqlite-db ["PRAGMA foreign_keys;"]))
;(let [res (jdbc/query sqlite-db ["PRAGMA foreign_keys;"])]
;  (println "res:" res)
;  (println "(count res):" (count res))
;  (println "(first res):" (first res)))
; (println "with-db-connection:" (jdbc/with-db-connection sqlite-db
;  (jdbc/db-do-commands sqlite-db false "PRAGMA foreign_keys = 1;")))
;
; This doesn't work
;(do-raw sqlite-db (jdbc/execute! sqlite-db "PRAGMA foreign_keys = ON;"))
;(println "jdbc/execute!:" (jdbc/execute! sqlite-db "PRAGMA foreign_keys = ON;"))
;(jdbc/db-do-commands sqlite-db false "PRAGMA foreign_keys = ON;")
;(println "jdbc/query check:" (jdbc/query sqlite-db ["PRAGMA foreign_keys;"]))
;(println "jdbc/execute:" (jdbc/execute! sqlite-db
;                                        ["PRAGMA foreign_keys = ON;"]
;                                        {:transaction? false}))
;(jdbc/db-do-commands sqlite-db false [(jdbc/query sqlite-db "PRAGMA foreign_keys;")
;(jdbc/execute! sqlite-db ["PRAGMA foreight_keys = ON;"])
;(jdbc/query sqlite-db "PRAGMA foreign_keys;")])
;(println "jdbc/query check:" (jdbc/query sqlite-db ["PRAGMA foreign_keys;"]))
;(println "(jdbc/db-connection sqlite-db):" (jdbc/db-connection sqlite-db))
;(println "PRAGMA foreign_keys;" (jdbc/db-do-commands sqlite-db false "PRAGMA foreign_keys;"))

; This returns the expected results.
;
;(defn exec-foreign-keys-pragma-statement
;  [db]
;  (let [con ^Connection (get-connection db)
;        statement (.createStatement con)]
;    (println "exec-foreign-keys-pragma-statement:"
;             (.execute statement "PRAGMA foreign_keys;"))))
;
; The way someone did it in Java that I used to create the version below.
;
;import org.sqlite.SQLiteConfig;
;public static final String DB_URL = "jdbc:sqlite:database.db";
;public static final String DRIVER = "org.sqlite.JDBC";

;public static Connection getConnection() throws ClassNotFoundException {
;    Class.forName(DRIVER);
;    Connection connection = null;
;    try {
;        SQLiteConfig config = new SQLiteConfig();
;        config.enforceForeignKeys(true);
;        connection = DriverManager.getConnection(DB_URL,config.toProperties());
;    } catch (SQLException ex) {}
;    return connection;
;}

; Here's some example code that I used to get things working with an
; experiment and presented in a Stackoverflow question.
;
;(def the-db-name "the.db")
;(def the-db {:classname   "org.sqlite.JDBC"
;             :subprotocol "sqlite"
;             :subname     the-db-name})
;
;(defn create-some-tables
;  "Create some tables and a cross-reference table with foreign key constraints."
;  []
;  (when-let [conn (get-connection the-db)]
;    (try
;      (jdbc/with-db-connection
;        [conn the-db]
;        (println "the-db:" the-db)
;        (try (jdbc/db-do-commands
;               the-db false
;               [(jdbc/create-table-ddl :pages
;                                       [[:page_id :integer :primary :key]
;                                        [:page_content :text]])
;                (jdbc/create-table-ddl :tags
;                                       [[:tag_id :integer :primary :key]
;                                        [:tag_name :text "NOT NULL"]])
;                (jdbc/create-table-ddl :tags_x_pages
;                                       [[:x_ref_id :integer :primary :key]
;                                        [:tag_id :integer]
;                                        [:page_id :integer]
;                                        ["FOREIGN KEY(tag_id) REFERENCES tags(tag_id)"]
;                                        ["FOREIGN KEY(page_id) REFERENCES pages(page_id)"]])])
;
;             ; This still doesn't work.
;             (println "After table creation:"
;                      (jdbc/query the-db "PRAGMA foreign_keys;"))
;
;             (catch Exception e (println e))))
;
;      ; This returns the expected results.
;      (when-let [statement (.createStatement conn)]
;        (try
;          (println "After creating some tables: PRAGMA foreign_keys =>"
;                   (.execute statement "PRAGMA foreign_keys;"))
;          (catch Exception e (println e))
;          (finally (when statement
;                     (.close statement)))))
;      (catch Exception e (println e))
;      (finally (when conn
;                 (.close conn))))))
;


;; Things that deal with the database file and connection.

(def db-file-name "resources/public/db/database.db")

(def sqlite-db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     db-file-name
   })

(defn ^Connection get-connection
  "Return a connection to a SQLite database that
  enforces foreign key constraints."
  [db]
  (Class/forName (:classname db))
  (let [config (SQLiteConfig.)]
    (.enforceForeignKeys config true)
    (let [connection (DriverManager/getConnection
                       (str "jdbc:sqlite:" (:subname db))
                       (.toProperties config))]
      connection)))

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
   "About_Front_Matter.md"
   "Other_Wiki_Software.md"
   "Path_to_Release.md"
   "Text_Formatting.md"
   "Technical_Notes.md"])

(def initial-pages [;{:title "Front Page" :file-name "Front_Page.md"}
                    {:title "About" :file-name "About.md"}
                    {:title "About Admin Pages" :file-name "About_Admin_Pages.md"}
                    {:title "About Backup and Restore" :file-name "About_Backup_and_Restore.md"}
                    {:title "About Compressing the Database" :file-name "About_Compressing_the_Database.md"}
                    {:title "About CWiki" :file-name "About_CWiki.md"}
                    {:title "About Images" :file-name "About_Images.md"}
                    {:title "About Import/Export" :file-name "About_Import_Export.md"}
                    {:title "About Roles" :file-name "About_Roles.md"}
                    {:title "About the Sidebar" :file-name "About_the_Sidebar.md"}
                    {:title "About TeX" :file-name "About_TeX.md"}
                    {:title "Admin" :file-name "Admin.md"}
                    {:title "CWiki FAQ" :file-name "CWiki_FAQ.md"}
                    {:title "CWiki Name" :file-name "CWiki_Name.md"}
                    {:title "Features" :file-name "Features.md"}
                    {:title "How to Make a Table of Contents" :file-name "How_to_Make_a_Table_of_Contents.md"}
                    {:title "Links Primer" :file-name "Links_Primer.md"}
                    ;{:title "Other Wiki Software" :file-name "Other_Wiki_Software.md"}
                    {:title "Pages Primer" :file-name "Pages_Primer.md"}
                    {:title "Preferences" :file-name "Preferences.md"}
                    {:title "Sidebar" :file-name "Sidebar.md"}
                    {:title "Special Pages" :file-name "Special_Pages.md"}
                    ;{:title "Text Formatting" :file-name "Text_Formatting.md"}
                    ;{:title "Technical Notes" :file-name "Technical_Notes.md"}
                    {:title "To Do" :file-name "todo.md"}
                    {:title "Wikilinks" :file-name "Wikilinks.md"}])

(def valid-roles [:cwiki :admin :editor :writer :reader])

(def initial-namespaces (atom ["cwiki" "default" "help"]))

(def initial-tags ["help" "wiki" "cwiki" "linking"])

(def initial-users [{:user_name              "CWiki"
                     :user_role              :cwiki
                     :user_password          (hashers/derive (str (UUID/randomUUID)))
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

(defn user-name->user-role
  "Return the user role (a keyword) assigned to the named user. If the
  user does not exist, return nil."
  ([name]
   (user-name->user-role name sqlite-db))
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

(defn find-user-by-case-insensitive-name
  "Look up the user in the database using a case-insensitive search for the
  username. Return the matching entry, if any. Otherwise, return nil."
  ([name]
   (find-user-by-case-insensitive-name name sqlite-db))
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
  (jdbc/update! sqlite-db :users user-map ["user_id=?" user_id]))

(defn find-post-by-title
  "Look up a post in the database with the given title. Return the page
  map for the post if found; nil otherwise. The lookup is case-insensitive."
  ([title] (find-post-by-title title sqlite-db))
  ([title db-name]
   (first (jdbc/query
            db-name
            [(str "select * from pages where page_title like '" title "'")]))))

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
   (get-all-page-names sqlite-db))
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
   (get-all-users sqlite-db))
  ([db-name]
   (when-let [user-array (jdbc/query db-name ["select user_name from users"])]
     (into (sorted-set-by case-insensitive-comparator)
           (mapv #(:user_name %) user-array)))))

(defn get-all-namespaces
  "Return a sorted set of all of the namespaces in the wiki."
  ([]
   (get-all-namespaces sqlite-db))
  ([db-name]
   (when-let [namespace-array (jdbc/query db-name ["select namespace_name from namespaces"])]
     (into (sorted-set-by case-insensitive-comparator)
           (mapv #(:namespace_name %) namespace-array)))))

(defn get-all-tags
  "Return a sorted set of all of the tags in the wiki."
  ([]
   (get-all-tags sqlite-db))
  ([db-name]
   (when-let [tag-array (jdbc/query db-name ["select tag_name from tags"])]
     (into (sorted-set-by case-insensitive-comparator)
           (mapv #(:tag_name %) tag-array)))))

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
                             sqlite-db :tags {:tag_name %2})))
                  [] tags)]
    ; add to cross-reference table
    (println "row-ids (tag ids):" row-ids ", page-id:" page-id)
    (mapv #(jdbc/insert! sqlite-db :tags_x_pages {:tag_id %
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
            page-id (get-row-id (jdbc/insert! sqlite-db :pages pm))]
        (update-tag-tables (get-tag-vector-from-meta meta) page-id)))))

(defn- add-page-from-file!
  [m id]
  (println "add-page-from-file!: m:" m ", id:" id)
  (let [resource-prefix "private/md/"
        title (:title m)
        content (slurp (io/resource
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

(defn delete-user
  ([user-id]
   (delete-user user-id sqlite-db))
  ([user-id db-name]
   (jdbc/delete! db-name :users ["user_id=?" user-id])))

(defn has-admin-logged-in?
  "Return true if the admin has ever logged in; nil otherwise."
  []
  (let [result (jdbc/query
                 sqlite-db
                 ["select admin_has_logged_in from admin where admin_id=?" 1])]
    (:admin_has_logged_in (first result))))

(defn set-admin-has-logged-in
  [b]
  (jdbc/update! sqlite-db :admin {:admin_has_logged_in b}
                ["admin_id=?" 1]))

(defn- init-admin-table
  []
  (jdbc/insert! sqlite-db :admin {:admin_id            1
                                  :admin_has_logged_in nil}))

(defn- add-initial-users!
  []
  (println "Adding initial users.")
  (mapv #(jdbc/insert! sqlite-db :users %) initial-users)
  (println "Done."))

(defn- add-initial-pages!
  [user-id]
  (mapv #(add-page-with-meta-from-file! %) initial-pages-with-front-matter)
  (mapv #(add-page-from-file! % user-id) initial-pages))

(defn- add-initial-namespaces!
  []
  (println "adding namespaces")
  (mapv (fn [%] (jdbc/insert! sqlite-db :namespaces {:namespace_name %}))
        @initial-namespaces)
  (println "done"))

(defn- add-initial-tags!
  []
  (println "adding tags")
  (mapv (fn [%] (jdbc/insert! sqlite-db :tags {:tag_name %})) initial-tags)
  (println "done"))

(defn- add-initial-roles!
  []
  (println "adding roles")
  (mapv (fn [%] (jdbc/insert! sqlite-db :roles {:role_name %})) valid-roles)
  (println "done"))

(defn- create-tables
  "Create the database tables for the application."
  []
  (println "creating tables")
  (when-let [conn (get-connection sqlite-db)]
    (try
      (jdbc/with-db-connection
        [conn sqlite-db]
        (try (jdbc/db-do-commands
               sqlite-db false
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
                (jdbc/create-table-ddl :admin
                                       [[:admin_id :integer :primary :key]
                                        [:admin_has_logged_in :boolean]])
                (jdbc/create-table-ddl :pages
                                       [[:page_id :integer :primary :key]
                                        [:page_created :datetime]
                                        [:page_modified :datetime]
                                        [:page_tags :text]
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
                                        [:tag_name :text "NOT NULL"]])
                (jdbc/create-table-ddl :tags_x_pages
                                       [[:x_ref_id :integer :primary :key]
                                        [:tag_id :integer]
                                        [:page_id :integer]
                                        ["FOREIGN KEY(tag_id) REFERENCES tags(tag_id)"]
                                        ["FOREIGN KEY(page_id) REFERENCES pages(page_id)"]])
                (jdbc/create-table-ddl :user_x_pages
                                       [[:x_ref_id :integer :primary :key]
                                        [:user_id :integer]
                                        [:page_id :integer]
                                        ])])
             (catch Exception e (println e))))
      (catch Exception e (println e))
      (finally (when conn
                 (.close conn)))))
  (println "done"))

(defn- create-db
  "Create the database tables and initialize them with content for
  first-time use."
  []
  (create-tables)
  (init-admin-table)
  (add-initial-users!)
  (add-initial-pages! (get-cwiki-user-id))
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

