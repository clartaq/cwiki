(ns cwiki.test.models.db
  (:require [clojure.test :refer :all]
            [cwiki.models.db :refer :all]))

(deftest find-user-by-name-test
  (testing "find-user-by-name"
    (is (nil? (find-user-by-name "blofeld")))
    (is (not (nil? (find-user-by-name "guest"))))))

(deftest lookup-user-test
  (testing "lookup-user"
    (is (nil? (lookup-user "guest" "xyxyxyxyx")))
    (is (nil? (lookup-user "glofeld" "xyxyxyx")))
    (is (not (nil? (lookup-user "guest" "guest"))))))

(deftest user-name->user-role-test
  (testing "user-name->user-role"
    (is (= :cwiki (user-name->user-role "CWiki")))
    (is (= :reader (user-name->user-role "guest")))
    (is (nil? (user-name->user-role "non-existent-user")))))

(deftest page-id->title-test
  (testing "page-id->title"
    (is (nil? (page-id->title 1039)))
    (println "title->page-id:" (title->page-id "Front Page"))
    (is (= "Front Page" (page-id->title 1)))))

(deftest page-id->content-test
  (testing "page-id->content"
    (is (nil? (page-id->content 1039)))))

(deftest title->page-id-test
  (testing "title->page-id"
    (is (nil? (title->page-id ";lkjasdfl;kjasdf;lkjasdfkljasdfj")))
    (is (= 1 (title->page-id "Front Page")))))

(deftest find-post-by-title-test
  (testing "find-post-by-title"
    (is (nil? (find-post-by-title "l;kjasfd9084325lkjal;kjasdfohi")))
    (is (= "Front Page" (:page_title (find-post-by-title "Front Page"))))))

(defn insert-test-data
  [title content]
  (let [new-id (:page_id (insert-new-page! title content))]
    new-id))

(deftest delete-page-by-id-test
  (testing "delete-page-by-id"
    (let [new-title "A Title"
          new-content "Some new content."
          new-id (insert-test-data new-title new-content)]
      (is (= new-title (page-id->title new-id)))
      (is (= new-content (page-id->content new-id)))
      (delete-page-by-id new-id)
      (is (nil? (page-id->title new-id))))))

