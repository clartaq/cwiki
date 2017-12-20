(ns cwiki.test.util.cwiki.util.parse-yaml
  (:require [clojure.test :refer :all]
            [cwiki.util.parse-yaml :refer :all]))

(deftest simple-yaml-test
  (testing "process-lines with a simple vector of text lines
   containing YAML front matter."

    (let [test-lines ["---"
                      "key1: value1"
                      "key2: value2"
                      "---"
                      ""
                      "Body text paragraph 1"
                      ""
                      "Body text paragraph 2"
                      ""
                      "Body text paragraph 3"]
          result (process-lines test-lines)]
      (is (seq (:front result)))
      (is (= (:front result) ["key1: value1" "key2: value2"]))
      (is (seq (:body result)))
      (is (= (:body result) ["Body text paragraph 1" "Body text paragraph 2"
                             "Body text paragraph 3"])))

    (let [test-lines ["---"
                      "title: First Light"
                      "date: 2017-08-15 14:03:02"
                      "tags: blogging"
                      "---"
                      ""
                      "First light with a new blogging platform."]
          result (process-lines test-lines)]
      (is (seq (:front result)))
      (is (= (:front result) ["title: First Light" "date: 2017-08-15 14:03:02"
                              "tags: blogging"]))
      (is (seq (:body result)))
      (is (= (:body result) ["First light with a new blogging platform."])))

    (let [test-lines ["---"
                      "title: First Light"
                      "date: 2017-08-15 14:03:02"
                      "tags: blogging"
                      "---"]
          result (process-lines test-lines)]
      (is (seq (:front result)))
      (is (= (:front result) ["title: First Light" "date: 2017-08-15 14:03:02"
                              "tags: blogging"]))
      (is (empty? (:body result))))

    (let [test-lines ["First light with a new blogging platform."]
          result (process-lines test-lines)]
      (is (empty? (:front result)))
      (is (seq (:body result)))
      (is (= (:body result) ["First light with a new blogging platform."])))))
