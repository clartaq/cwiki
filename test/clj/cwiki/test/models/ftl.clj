(ns cwiki.test.models.ftl
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all]
            [cwiki.models.wiki-db :refer :all]
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

(defn get-test-db-trace-file-name []
  (str (get-test-db-file-name) ".trace.db"))

(defn get-test-db-spec []
  {:classname   "org.h2.Driver"
   :subprotocol "h2:file"
   :subname     (get-test-db-file-name)
   :make-pool?  true})

(defn- add-test-page-from-file!
  "Add a page to the database based on the information in a Markdown file
  containing YAML front matter."
  [file-name db-spec]
  (let [file (File. (str "test/data/" file-name))
        m (files/load-markdown-from-file file)]
    (add-page-from-map m "CWiki" db-spec)))

(defn add-test-pages!
  "Read the pages used for testing from files and add them to the test database."
  [db-spec]
  (info "Adding test pages.")
  (mapv #(add-test-page-from-file! % db-spec) ["A_Dummy_Test_Page.md"
                                               "NoContentHere.md"
                                               "NoDatesOrTags.md"
                                               ; No title in this one, so, it
                                               ; gets a unique one generated
                                               ; each time the database is
                                               ; built.
                                               "NoMeta.md"
                                               "Test_Page_for_Tags.md"])
  (info "Done!"))

(defn- create-test-db
  "Create the database tables and initialize them with content for
  first-time use."
  [db-spec]
  (create-tables db-spec)
  ; The magic incanations to get full text search to work.
  (jdbc/execute! db-spec
                 ["CREATE ALIAS IF NOT EXISTS FTL_INIT FOR \"org.h2.fulltext.FullTextLucene.init\""])
  (jdbc/execute! db-spec ["CALL FTL_INIT()"])
  (jdbc/execute! db-spec ["CALL FTL_CREATE_INDEX('PUBLIC', 'PAGES', 'PAGE_CONTENT')"])
  ; End incantations.
  (init-admin-table! db-spec)
  (add-initial-users! db-spec)
  (add-test-pages! db-spec)
  (add-initial-roles! db-spec)
  (add-initial-options! db-spec)
  (set-admin-has-logged-in true db-spec))

(defn- init-test-db!
  "Initialize the database. Will create the database and tables."
  [db-spec short-db-file-name long-db-file-name]
  (info "Creating initial test database.")
  (io/delete-file (io/file long-db-file-name) true)
  (io/make-parents short-db-file-name)
  (create-test-db db-spec))

;-------------------------------------------------------------------------------
; Test fixtures.
;-------------------------------------------------------------------------------

(defn one-time-setup []
  (info "one time setup")
  (init-test-db! (get-test-db-spec) (get-test-db-file-name) (get-test-db-file-name-long))
  (info "setup complete"))

(defn one-time-teardown []
  (info "one time teardown")
  (jdbc/execute! (get-test-db-spec) ["CALL FTL_DROP_ALL()"])
  (io/delete-file (io/file (get-test-db-file-name-long)))
  (io/delete-file (io/file (get-test-db-trace-file-name)) true)
  (info "teardown complete"))

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
; The tests.
;-------------------------------------------------------------------------------

(deftest int-at-end-test
  (testing "The int-at-end function."
    (is (nil? (int-at-end nil)))
    (is (nil? (int-at-end "")))
    (is (nil? (int-at-end " ")))
    (is (nil? (int-at-end "abc")))
    (is (= 45 (int-at-end "abc45")))
    (is (nil? (int-at-end "abc45def")))
    (is (= -37 (int-at-end "abc-37")))))

(def sort-test-data
  [{:score 0.753 :title "Bogus Title"}
   {:title "Zimbabwe Title" :score 1.294}
   {:title "A New Title" :score 0.537}
   {:title "Crazy Title" :score 0.0}
   {:title "aardvark Title" :score 32.0}
   {:title "Wamsutta Towels" :score 1.897}])

(deftest sort-results-test
  (testing "The sort-results function"
    (is (nil? (sort-search-results nil :none)))
    (is (nil? (sort-search-results [] nil)))
    (is (nil? (sort-search-results sort-test-data :boogie-sort)))
    (is (= ["A New Title" "aardvark Title" "Bogus Title" "Crazy Title" "Wamsutta Towels" "Zimbabwe Title"]
           (reduce #(conj %1 (:title %2)) [] (sort-search-results sort-test-data :alphanum-natural))))
    (is (= ["Zimbabwe Title" "Wamsutta Towels" "Crazy Title" "Bogus Title" "aardvark Title" "A New Title"]
           (reduce #(conj %1 (:title %2)) [] (sort-search-results sort-test-data :alphanum-reverse))))
    (is (= ["aardvark Title" "Wamsutta Towels" "Zimbabwe Title" "Bogus Title" "A New Title" "Crazy Title"]
           (reduce #(conj %1 (:title %2)) [] (sort-search-results sort-test-data :score-hi-to-lo))))
    (is (= ["Crazy Title" "A New Title" "Bogus Title" "Zimbabwe Title" "Wamsutta Towels" "aardvark Title"]
           (reduce #(conj %1 (:title %2)) [] (sort-search-results sort-test-data :score-lo-to-hi))))))

(deftest find-word-missing-test
  (testing "Searching for docs containing the word 'missing'."
    (let [tsv (search-content "missing" {:db-spec (get-test-db-spec)})]
      (is (= (count tsv) 0)))))

(deftest find-word-formatting-test
  (testing "Searching for docs containing the word 'formatting'."
    (let [tsv (search-content "formatting" {:max-results 100
                                            :db-spec     (get-test-db-spec)
                                            :sort-type   :score-hi-to-lo})]
      (is (= (count tsv) 1))
      (is (= (:title (first tsv)) "A Dummy Test Page")))))

(deftest find-word-markdown-test
  (testing "Searching for docs containing the word 'Markdown'."
    (let [tsv (search-content "Markdown" {:db-spec (get-test-db-spec)})]
      (is (= (count tsv) 2))
      (is (= (:title (first tsv)) "A Remarkable Title")))))

(deftest find-word-markdown-limit-results-test
  (testing "Searching for docs containing the word 'Markdown'
  returning at most 2 results and sorting low to high."
    (let [tsv (search-content "Markdown" {:max-results 2
                                          :db-spec     (get-test-db-spec)
                                          :sort-type   :score-lo-to-hi})]
      (is (= (count tsv) 2))
      (is (= (:title (first tsv)) "A Dummy Test Page")))))

(deftest find-word-remarkable-limit-results-test
  (testing "Searching for docs containing the word 'remarkable'
  returning at most 5 results and sorting high to low."
    (let [tsv (search-content "remarkable" {:max-results 5
                                            :db-spec     (get-test-db-spec)
                                            :sort-type   :score-hi-to-lo})]
      (is (= (count tsv) 2))
      (is (= (:title (first tsv)) "A Remarkable Title")))))
