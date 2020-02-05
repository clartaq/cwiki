;;;;
;;;; This file provides a test to assure that the upgrade of the H2 database
;;;; from version 1.4.197 to 1.4.199, along with the concomitant upgrade of
;;;; Lucene from version 3.6.2 to 5.5.5, works correctly.
;;;;

(ns cwiki.test.models.h2-upgrade
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all])
  (:import (java.io File)))

(defn remove-from-end
  "Remove any instance of 'end' from the end of string s
  and return the result."
  [s end]
  (if (.endsWith s end)
    (.substring s 0 (- (count s)
                       (count end)))
    s))

(defn get-test-db-file-name []
  (str (-> (File. ".")
           .getAbsolutePath
           (remove-from-end "."))
       "test/data/db/h2_update_test.db"))

(defn get-test-db-spec []
  {:classname   "org.h2.Driver"
   :subprotocol "h2:file"
   :subname     (get-test-db-file-name)
   :make-pool?  true})

;-------------------------------------------------------------------------------
; A few useful file utilities to set up / take down the database.
;-------------------------------------------------------------------------------

(defn get-parent
  "Return the parent directory of the file."
  [file-name-and-path]
  (File. (.getParent ^File (File. ^String file-name-and-path))))

(defn make-parents
  "Create all of the parent directories of the file."
  [file-name-and-path]
  (when-let [parent (get-parent file-name-and-path)]
    (.mkdirs parent)))

(defn delete-files-recursively
  "Delete the given file. If the file is a directory, all files in the
  directory and all subdirectories are also removed. Raises an exception
  on failure. Otherwise, returns nil."
  [file-name-and-path]
  (letfn [(delete-function [file-or-dir-name]
            (when (.isDirectory file-or-dir-name)
              (doseq [child-file-or-dir (.listFiles file-or-dir-name)]
                (delete-function child-file-or-dir)))
            (io/delete-file file-or-dir-name))]
    (delete-function (io/file file-name-and-path))))

(defn delete-parent
  "Delete the parent directory of the file, including all other children
  that may exist"
  [file-name-and-path]
  (when-let [parent (get-parent file-name-and-path)]
    (when (and (.isDirectory parent)
               (.exists parent))
      (delete-files-recursively parent))))

;-------------------------------------------------------------------------------
; The test.
;-------------------------------------------------------------------------------

(deftest hello-ftl-test
  (testing "The simple 'Hello World' Lucene full text search demo from the H2 documentation."

    ;; Delete any pre-existing versions of the test database files and
    ;; directories. Then make sure the needed parent directories are present.
    (delete-parent (get-test-db-file-name))
    (make-parents (get-test-db-file-name))

    (let [db-spec (get-test-db-spec)]
      (jdbc/execute! db-spec ["CREATE ALIAS IF NOT EXISTS FTL_INIT FOR \"org.h2.fulltext.FullTextLucene.init\";"])
      (jdbc/execute! db-spec ["CALL FTL_INIT();"])
      (jdbc/execute! db-spec ["CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR);"])
      (jdbc/execute! db-spec ["INSERT INTO TEST VALUES(1, 'Hello World');"])
      (jdbc/execute! db-spec ["CALL FTL_CREATE_INDEX('PUBLIC', 'TEST', NULL);"])
      (is (= 1 (count (jdbc/query db-spec ["SELECT * FROM FTL_SEARCH('Hello', 0, 0);"]))))
      (jdbc/execute! db-spec ["CALL FTL_DROP_ALL()"]))))

(deftest ftl-setup-test
  (testing "The ability to setup and use full text search with the H2 database 1.4.198 and later")

  ;; Delete any pre-existing versions of the test database files and
  ;; directories. Then make sure the needed parent directories are present.
  (delete-parent (get-test-db-file-name))
  (make-parents (get-test-db-file-name))

  (let [db-spec (get-test-db-spec)]
    (jdbc/execute! db-spec ["CREATE ALIAS IF NOT EXISTS FTL_INIT FOR \"org.h2.fulltext.FullTextLucene.init\""])
    (jdbc/execute! db-spec ["CALL FTL_INIT()"])
    (jdbc/execute! db-spec ["DROP TABLE IF EXISTS TEST"])
    (jdbc/execute! db-spec ["CREATE TABLE TEST(ID INT PRIMARY KEY, FIRST_NAME VARCHAR, LAST_NAME VARCHAR)"])
    (jdbc/execute! db-spec ["CALL FTL_CREATE_INDEX('PUBLIC', 'TEST', NULL)"])
    (jdbc/execute! db-spec ["INSERT INTO TEST VALUES(1, 'John', 'Wayne')"])
    (jdbc/execute! db-spec ["INSERT INTO TEST VALUES(2, 'Elton', 'John')"])
    (is (= 2 (count (jdbc/query db-spec ["SELECT * FROM FTL_SEARCH_DATA('John', 0, 0)"]))))
    (is (= 1 (count (jdbc/query db-spec ["SELECT * FROM FTL_SEARCH_DATA('LAST_NAME:John', 0, 0)"]))))
    (jdbc/execute! db-spec ["CALL FTL_DROP_ALL()"])))

