(ns cwiki.test.routes.admin
  (:require [clojure.test :refer :all]
            [cwiki.routes.admin :refer :all]))

(deftest get-new-name-test
  (testing "get-new-name"
    (is (nil? (get-new-name "old-name" nil)))
    (is (nil? (get-new-name "old-name" "")))
    (is (nil? (get-new-name "old-name" "CWiki")))
    (is (nil? (get-new-name "old-name" "cwiki")))
    (is (nil? (get-new-name "old-name" "guest")))
    (is (nil? (get-new-name "old-name" "old-name")))
    (is (= "yekdorb" (get-new-name "old-name" "yekdorb")))))

