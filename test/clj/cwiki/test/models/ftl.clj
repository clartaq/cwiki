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

;(defn int-at-end
;  "Return the (base 10) integer at the end of the string or nil if there isn't one."
;  [s]
;  (when s
;    (let [dig-str (re-find #"-?\d*$" s)]
;      (when (and dig-str (seq dig-str))
;        (Integer/parseInt dig-str)))))
;
(deftest int-at-end-test
  (testing "The int-at-end function."
    (is (nil? (int-at-end nil)))
    (is (nil? (int-at-end "")))
    (is (nil? (int-at-end " ")))
    (is (nil? (int-at-end "abc")))
    (is (= 45 (int-at-end "abc45")))
    (is (nil? (int-at-end "abc45def")))
    (is (= -37 (int-at-end "abc-37")))))

;(defn rs->result-vector
;  "Take a result set list and turn it into a vector."
;  [rs]
;  (let [result (vec rs)]
;    result))
;
;(defn rm->id-score-map
;  "Take a result map {:query string :id string} and turn it into a score map
;  where the id has been converted to an integer."
;  [result-map]
;  (let [query (:query result-map)
;        id (int-at-end query)]
;    {:id id :score (:score result-map)}))
;
;(defn rv->id-score-vector
;  "Take a vector of result maps and return a vector of id/score maps where
;  the id is an integer."
;  [rv]
;  (mapv rm->id-score-map rv))
;
;(defn case-insensitive-title-comparator
;  "Case-insensitive title comparator."
;  [m1 m2]
;  (.compareToIgnoreCase (:title m1) (:title m2)))
;
;(defn reverse-case-insensitive-title-comparator
;  "Reverse order case-insensitive title comparator"
;  [m1 m2]
;  (case-insensitive-title-comparator m2 m1))
;
;(defn lo-to-hi-score-comparator
;  "Numeric comparator for low to high sorting."
;  [s1 s2]
;  (compare (:score s1) (:score s2)))
;
;(defn hi-to-lo-score-comparator
;  "Numeric comparator for high to low sorting."
;  [s1 s2]
;  (compare (:score s2) (:score s1)))
;
;(defn sort-search-results
;  "Sort the vector of results according to the type of sort requested and
;  return a sorted vector."
;  [v sort-type]
;  (when (and v sort-type)
;    (cond
;      (= sort-type :none) v
;      (= sort-type :alphanum-natural) (sort case-insensitive-title-comparator v)
;      (= sort-type :alphanum-reverse) (sort reverse-case-insensitive-title-comparator v)
;      (= sort-type :score-hi-to-lo) (sort hi-to-lo-score-comparator v)
;      (= sort-type :score-lo-to-hi) (sort lo-to-hi-score-comparator v)
;      :else (errorf "ACK! Don't know how to sort by %s " sort-type))))
;
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

;(defn id-score-map->title-score-map
;  [m db-spec]
;  "Take a map containing a page id under the :id key and return a map
;  containing the page title under the key :title."
;  (let [title (page-id->title (:id m) db-spec)]
;    (merge {:title title} m)))
;
;(defn iv->title-score-vector
;  "Take a vector of maps containing the keys :id and :score, and return a
;  vector of maps where the page titles have been added under the key :title."
;  [id-score-vector db-spec]
;  (mapv #(id-score-map->title-score-map % db-spec) id-score-vector))
;
;(defn search-results->title-score-vector
;  "Take the result set for a search, along with a map of options, and return
;  a vector of maps containing :title :id and :scores, with the vector sorted
;  according to the options. Sorting defaults to :score-hi-to-lo so that the
;  most relevant results are first."
;  [srv options]
;  (let [db-spec (or (:db-spec options) (get-test-db-spec))
;        sort-type (or (:sort-type options) :score-hi-to-lo)
;        tsv (-> srv
;                rs->result-vector
;                rv->id-score-vector
;                (iv->title-score-vector db-spec)
;                (sort-search-results sort-type))]
;    tsv))
;
;(defn search-content
;  "Search the content of all pages in the database for the given text.
;  Return a vector of maps. Each map has a page title, page id, and relevance
;  score for a page that matches the search criteria. A map of options can
;  be used to specify the maximum number of results to return (:max-results,
;  defaults to 1000), the offset to use (:offset, defaults to 0), the
;  database to search (:db-spec, defaults to the production database),
;  and how the output vector should be sorted (:sort-type, defaults to
;  :score-hi-to-low)."
;  [txt options]
;  (let [max-results (or (:max-results options) 1000)
;        offset (or (:offset options) 0)
;        db-spec (or (:db-spec options) (get-h2-db-spec))
;        search-str (str "SELECT * FROM FTL_SEARCH('" txt "', " max-results ", " offset ")")
;        rs (jdbc/query db-spec [search-str])
;        tsv (search-results->title-score-vector rs options)]
;    tsv))
;
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
