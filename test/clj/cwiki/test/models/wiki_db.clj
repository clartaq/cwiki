(ns cwiki.test.models.wiki-db
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [clojure.string :as s]
            [cwiki.models.wiki-db :as db]
            [cwiki.util.files :as files]
            [taoensso.timbre :refer [trace debug info warn error
                                     tracef debugf infof warnf errorf]])
  (:import (java.io File)))

;-------------------------------------------------------------------------------
; Functions to set up the test database.
;-------------------------------------------------------------------------------

; The kerfuffle here is to get the directory from which the program
; is running and create an absolute path as required for the H2 database.

(defn get-test-db-file-name []
  (str (-> (File. ".")
           .getAbsolutePath
           (files/remove-from-end "."))
       "test/data/db/testdatabase.db"))

; Because H2 seems to append this to the name above.
(defn get-test-db-file-name-long []
  (str (get-test-db-file-name) ".mv.db"))

(defn get-test-db-spec []
  {:classname   "org.h2.Driver"
   :subprotocol "h2:file"
   :subname     (get-test-db-file-name)
   :make-pool?  true})

(defn- add-test-page-from-file!
  "Add a page to the database based on the information in a Markdown file
  containing YAML front matter."
  [file-name db]
  (let [file (File. (str "test/data/" file-name))
        file-name-only (first (s/split file-name #"\."))
        m (files/load-markdown-from-file file)
        enhanced-map (assoc m :file-name file-name-only)]
    (db/add-page-from-map enhanced-map "CWiki" db)))

(defn add-test-pages!
  "Read the pages used for testing from files and add them to the test database."
  [db]
  (info "Adding test pages.")
  (mapv #(add-test-page-from-file! % db) ["A_Dummy_Test_Page.md"
                                          "NoContentHere.md"
                                          "NoDatesOrTags.md"
                                          "NoDatesOrTagsBlankTitle.md"
                                          "NoDatesOrTagsOrTitle.md"
                                          "NoMeta.md"
                                          "Test_Page_for_Tags.md"])
  (info "Done!"))

(defn- create-test-db
  "Create the database tables and initialize them with content for
  first-time use."
  [db]
  (db/create-tables db)
  (db/init-admin-table! db)
  (db/add-initial-users! db)
  (add-test-pages! db)
  (db/add-initial-roles! db)
  (db/add-initial-options! db)
  (db/set-admin-has-logged-in true db))

(defn- init-test-db!
  "Initialize the database. Will create the database and
  tables."
  [db short-db-file-name long-db-file-name]
  (info "Creating initial test database.")
  (io/delete-file (io/file long-db-file-name) true)
  (io/make-parents short-db-file-name)
  (create-test-db db))

;-------------------------------------------------------------------------------
; Test fixtures.
;-------------------------------------------------------------------------------

(defn one-time-setup []
  (info "one time setup")
  (init-test-db! (get-test-db-spec) (get-test-db-file-name) (get-test-db-file-name-long))
  (info "setup complete"))

(defn one-time-teardown []
  (info "one time teardown")
  ;(io/delete-file (io/file test-db-file-name-long) true)
  (info "teardown complete")
  )

(defn once-fixture [f]
  (one-time-setup)
  ;(println (type f))
  (f)
  (one-time-teardown))


(defn setup []
  ;(println "setup")
  )

(defn teardown []
  ;(println "teardown")
  )

(defn each-fixture [f]
  (setup)
  ;(println (type f))
  (f)
  (teardown))

(use-fixtures :once once-fixture)
(use-fixtures :each each-fixture)

;-------------------------------------------------------------------------------
; Tests
;-------------------------------------------------------------------------------

; Assure that test files were added with expected titles.

(deftest add-page-from-map-test
  (testing "That the add-page-from-map function created the expected page titles."
    (is (some? (db/title->page-id "NoDatesOrTagsBlankTitle" (get-test-db-spec))))
    (is (some? (db/title->page-id "NoDatesOrTagsOrTitle" (get-test-db-spec))))
    (is (some? (db/title->page-id "NoMeta" (get-test-db-spec))))
    (is (nil? (db/title->page-id "NoMetaD" (get-test-db-spec))))))

(deftest find-user-by-name-test
  (testing "find-user-by-name"
    (is (nil? (db/find-user-by-name "blofeld" (get-test-db-spec))))
    (is (not (nil? (db/find-user-by-name "guest" (get-test-db-spec)))))))

(deftest lookup-user-test
  (testing "lookup-user"
    (is (nil? (db/lookup-user "guest" "xyxyxyxyx" (get-test-db-spec))))
    (is (nil? (db/lookup-user "glofeld" "xyxyxyx" (get-test-db-spec))))
    (is (not (nil? (db/lookup-user "guest" "guest" (get-test-db-spec)))))))

(deftest user-name->user-role-test
  (testing "user-name->user-role"
    (is (= :cwiki (db/user-name->user-role "CWiki" (get-test-db-spec))))
    (is (= :admin (db/user-name->user-role "admin" (get-test-db-spec))))
    (is (= :reader (db/user-name->user-role "guest" (get-test-db-spec))))
    (is (nil? (db/user-name->user-role "non-existent-user" (get-test-db-spec))))))

(deftest page-id->title-test
  (testing "page-id->title"
    (is (nil? (db/page-id->title 1000039 (get-test-db-spec))))
    (is (= "A Dummy Test Page" (db/page-id->title 1 (get-test-db-spec))))))

(deftest page-id->content-test
  (testing "page-id->content"
    (is (not (nil? (db/page-id->content 1 (get-test-db-spec)))))
    (is (nil? (db/page-id->content 10000039 (get-test-db-spec))))))

(deftest title->page-id-test
  (testing "title->page-id"
    (is (nil? (db/title->page-id ";lkjasdfl;kjasdf;lkjasdfkljasdfj" (get-test-db-spec))))
    (is (= 1 (db/title->page-id "A Dummy Test Page" (get-test-db-spec))))))

(deftest find-post-by-title-test
  (testing "find-post-by-title"
    (is (nil? (db/find-post-by-title "l;kjasfd9084325lkjal;kjasdfohi" (get-test-db-spec))))
    (is (= "A Dummy Test Page" (:page_title (db/find-post-by-title "A Dummy Test Page" (get-test-db-spec)))))))

(defn insert-test-data
  [title content tags]
  (let [new-id (:page_id (db/insert-new-page! title content tags
                                              (db/get-cwiki-user-id)
                                              (get-test-db-spec)))]
    new-id))

(deftest delete-page-by-id-test
  (testing "delete-page-by-id"
    (let [new-title "A Title"
          new-content "Some new content."
          new-tags #{"flubar" "grubar" "about"}
          new-id (insert-test-data new-title new-content new-tags)]
      (is (= new-title (db/page-id->title new-id (get-test-db-spec))))
      (is (= new-content (db/page-id->content new-id (get-test-db-spec))))
      (let [res (db/delete-page-by-id! new-id (get-test-db-spec))]
        (is (seq res))
        (is (= 1 (first res))))
      (is (nil? (db/page-id->title new-id (get-test-db-spec)))))
    (let [new-title "A New Title"
          new-content "Two lines\nof content."
          new-tags #{"wangdant" "clangdang" "help"}
          new-id (insert-test-data new-title new-content new-tags)]
      (is (= new-title (db/page-id->title new-id (get-test-db-spec))))
      (is (= new-content (db/page-id->content new-id (get-test-db-spec))))
      (let [res (db/delete-page-by-id! new-id (get-test-db-spec))]
        (is (seq res))
        (is (= 1 (first res))))
      (is (nil? (db/page-id->title new-id (get-test-db-spec)))))
    (let [new-title "Another New Title"
          new-content "Three lines\nof content\n\nwith a double linefeed."
          new-tags #{"sloodo" "TeX" "voodoo"}
          new-id (insert-test-data new-title new-content new-tags)]
      (is (= new-title (db/page-id->title new-id (get-test-db-spec))))
      (is (= new-content (db/page-id->content new-id (get-test-db-spec))))
      (let [res (db/delete-page-by-id! new-id (get-test-db-spec))]
        (is (seq res))
        (is (= 1 (first res))))
      (is (nil? (db/page-id->title new-id (get-test-db-spec)))))))

(deftest page-map->content-test
  (testing "page-map->content function"
    (let [new-title "A Title"
          new-content "Some new content."
          new-tags #{"cwiki" "bufar" "clufar"}
          new-id (insert-test-data new-title new-content new-tags)
          new-map (db/find-post-by-title new-title (get-test-db-spec))]
      (is (= new-title (db/page-id->title new-id (get-test-db-spec))))
      (is (= new-content (db/page-map->content new-map)))
      (let [res (db/delete-page-by-id! new-id (get-test-db-spec))]
        (is (seq res))
        (is (= 1 (first res))))
      (is (nil? (db/page-id->title new-id (get-test-db-spec)))))
    (let [new-title "A New Title"
          new-content "Two lines\nof content."
          new-tags #{"arvar" "snarvar" "wiki"}
          new-id (insert-test-data new-title new-content new-tags)
          new-map (db/find-post-by-title new-title (get-test-db-spec))]
      (is (= new-title (db/page-id->title new-id (get-test-db-spec))))
      (is (= new-content (db/page-map->content new-map)))
      (let [res (db/delete-page-by-id! new-id (get-test-db-spec))]
        (is (seq res))
        (is (= 1 (first res))))
      (is (nil? (db/page-id->title new-id (get-test-db-spec)))))
    (let [new-title "Another New Title"
          new-content "Three lines\nof content\n\nwith a double linefeed."
          new-tags #{"hoodoo" "yoodoo" "so well"}
          new-id (insert-test-data new-title new-content new-tags)
          new-map (db/find-post-by-title new-title (get-test-db-spec))]
      (is (= new-title (db/page-id->title new-id (get-test-db-spec))))
      (is (= new-content (db/page-map->content new-map)))
      (let [res (db/delete-page-by-id! new-id (get-test-db-spec))]
        (is (seq res))
        (is (= 1 (first res))))
      (is (nil? (db/page-id->title new-id (get-test-db-spec)))))))

