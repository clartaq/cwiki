;;;
;;; This namespace contains tests related to the use of apostrophes in
;;; various parts of the database like page titles, tags, etc.
;;;

(ns cwiki.test.models.apostrophe-in-sql
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [cwiki.models.wiki-db :refer :all]
            [cwiki.util.files :as files])
  (:import (java.io File)))

;-------------------------------------------------------------------------------
; Functions to set up the test database.
;-------------------------------------------------------------------------------

; The kerfuffle here is to get the directory from which the program
; is running and create an absolute path as required for the H2 database.
(def test-db-file-name (str (-> (File. ".")
                                .getAbsolutePath
                                (files/remove-from-end "."))
                            "test/data/db/testdatabase.db"))
; Because H2 seems to append this to the name above.
(def test-db-file-name-long (str test-db-file-name ".mv.db"))

(def test-db {:classname   "org.h2.Driver"
              :subprotocol "h2:file"
              :subname     test-db-file-name
              :make-pool?  true})

(defn- add-test-page-from-file!
  "Add a page to the database based on the information in a Markdown file
  containing YAML front matter."
  [file-name db]
  ;(println "add-page-with-meta-from-file!: file-name: " file-name)
  (let [file (File. (str "test/data/" file-name))
        m (files/load-markdown-from-file file)]
    ;(println "  m: " m)
    (add-page-from-map m "CWiki" db)))

(defn add-test-pages!
  "Read the pages used for testing from files and add them to the test database."
  [db]
  (println "Adding test pages.")
  (letfn [(with-db [x] (add-test-page-from-file! x db))]
    (mapv #(add-test-page-from-file! % db) ["A_Dummy_Test_Page.md"
                                            "NoContentHere.md"
                                            "NoDatesOrTags.md"
                                            "NoMeta.md"
                                            "Test_Page_for_Tags.md"]))
  (println "Done!"))

(defn- create-test-db
  "Create the database tables and initialize them with content for
  first-time use."
  [db]
  (create-tables db)
  (init-admin-table! db)
  (add-initial-users! db)
  (add-test-pages! db)
  (add-initial-roles! db)
  (set-admin-has-logged-in true db))

(defn- init-test-db!
  "Initialize the database. Will create the database and
  tables."
  [db short-db-file-name long-db-file-name]
  (println "Creating initial test database.")
  (io/delete-file (io/file long-db-file-name) true)
  (io/make-parents short-db-file-name)
  (create-test-db db))

;-------------------------------------------------------------------------------
; Test fixtures.
;-------------------------------------------------------------------------------

(defn one-time-setup []
  (println "one time setup")
  (init-test-db! test-db test-db-file-name test-db-file-name-long)
  (println "setup complete"))

(defn one-time-teardown []
  (println "one time teardown")
  ;(io/delete-file (io/file test-db-file-name-long) true)
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

; Some of these tests just assure that we have built the test database
; correctly.

(deftest find-user-by-name-test
  (testing "Whether the expected users were added to the database."
    (is (nil? (find-user-by-name "blofeld" test-db)))
    (is (not (nil? (find-user-by-name "guest" test-db))))))

(deftest find-expected-article
  (testing "Whether the test data page was added to the database."
    (let [res (find-post-by-title "No Content Here" test-db)]
      (is (seq res)))
    (is (nil? (find-post-by-title "frambooly title")))))

(deftest escape-apostrophes-test
  (testing "The escape-apostrophes function"
    (is (nil? (escape-apostrophes nil)))
    (is (empty? (escape-apostrophes "")))
    (is (= "A String" (escape-apostrophes "A String")))
    (is (= "A String''s String" (escape-apostrophes "A String's String")))
    (is (= "A''s and B''s String" (escape-apostrophes "A's and B's String")))))

;(deftest unescape-apostrophes-test
;  (testing "The unescape-apostrophes function"
;    (is (nil? (unescape-apostrophes nil)))
;    (is (empty? (unescape-apostrophes "")))
;    (is (= "A String" (unescape-apostrophes "A String")))
;    (is (= "A String's String" (unescape-apostrophes "A String''s String")))
;    (is (= "A's and B's String" (unescape-apostrophes "A''s and B''s String")))))

(deftest apostrophe-in-title-test
  (testing "That changes to the title including invalid characters don't crash"
    (let [new-title "Joe's Title"
          m (find-post-by-title "A Dummy Test Page" test-db)
          page-id (page-map->id m)
          content (page-map->content m)
          tag-set (get-tag-names-for-page page-id test-db)]
      (update-page-title-and-content!
        page-id
        new-title
        tag-set
        content
        test-db)
      (is (= new-title (:page_title (find-post-by-title "Joe's Title" test-db)))))))

(deftest apostrophe-in-tag-test
  (testing "The apostropes can be added to tags and the tags can be retrieved."
    (let [test-page-name "Test Page for Tags"
          test-tag "Joe's Tag"
          m (find-post-by-title test-page-name test-db)
          page-id (page-map->id m)
          original-tag-set (get-tag-names-for-page page-id test-db)
          new-tag-set (set/union original-tag-set #{test-tag})]

      ; Add the funny tag.
      (update-tags-for-page new-tag-set page-id test-db)
      (is (seq (get-tag-names-for-page page-id test-db)))
      (is (= (count (get-tag-names-for-page page-id test-db))
             (+ 1 (count original-tag-set))))
      (is (seq (get-all-tag-names test-db)))
      (is (contains? (get-all-tag-names test-db) test-tag))
      (is (contains? (get-titles-of-all-pages-with-tag test-tag test-db) test-page-name))

      ; Now remove it.
      (update-tags-for-page original-tag-set page-id test-db)
      (is (= original-tag-set (get-tag-names-for-page page-id test-db)))
      (is (empty? (get-titles-of-all-pages-with-tag test-tag test-db))))))
