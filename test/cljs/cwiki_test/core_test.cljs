(ns cwiki-test.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [cwiki-mde.core :as core]))

;; These tests don't do anything useful. They are just here to test the
;; testing process.

;; Uncomment this to see behavior with a failing test. It usually does
;; all of the testing correctly but then crashes figwheel (I think).
;(deftest fake-failing-test
;  (testing "fake failing description"
;    (is (= 1 2))))

(deftest fake-passing-test
  (testing "fake passing description"
    (is (= 3 (+ 1 2)))))