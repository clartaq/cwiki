(ns cwiki-test.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            ;[cwiki-mde.core :as core]
            ))

(deftest fake-failing-test
  (testing "fake failing description"
    (is (= 1 2))))

(deftest fake-passing-test
  (testing "fake passing description"
    (is (= 3 (+ 1 2)))))