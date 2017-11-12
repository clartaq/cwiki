(ns cwiki.test.models.db
  (:require [clojure.test :refer :all]
            [cwiki.models.db :refer :all]))

(deftest page-id->title-test
  (testing "page-id->title"
    (is (nil? (page-id->title 1039)))
    (println "title->page-id:" (title->page-id "Front Page"))
    (is (= "Front Page" (page-id->title 1)))))

(deftest page-id->content-test
  (testing "page-id->content"
    (is (nil? (page-id->content 1039)))
    (is (= "Some fake content." (page-id->content 11)))))

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

