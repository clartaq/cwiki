(ns cwiki-test.tag-editor-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [cwiki-mde.tag-editor :as te]))

;(deftest add-tag-to-set-test
;  (testing "Correct operation of the 'add-new-tag-to-set' function."
;    (let [base-set-atom (atom #{})
;          a-tag "A Tag"]
;      (is (zero? (count @base-set-atom)))
;      (is (= 1 (count (te/add-new-tag-to-set base-set-atom a-tag))))
;      (is (= #{a-tag} (te/add-new-tag-to-set base-set-atom a-tag))))))
;
;(deftest remove-tag-from-set-test
;  (testing "Correct operation of the 'remove-tag-from-set' function."
;    (let [a-tag "A Tag"
;          b-tag "B Tag"
;          base-set-atom (atom #{a-tag b-tag})
;          b-tag-only-set-atom (atom #{b-tag})
;          ]
;      (is (= 2 (count @base-set-atom)))
;      (is (= 1 (count (te/remove-tag-from-set base-set-atom a-tag))))
;      (is (= #{b-tag} (te/remove-tag-from-set base-set-atom a-tag)))
;      (is (zero? (count (te/remove-tag-from-set b-tag-only-set-atom b-tag))))
;      (is (= #{} (te/remove-tag-from-set b-tag-only-set-atom b-tag)))
;      (is (= #{} (te/remove-tag-from-set (atom #{}) a-tag)))
;      (is (= @base-set-atom (te/remove-tag-from-set base-set-atom "No Such Tag"))))))
;
;(deftest replace-tag-in-set-test
;  (testing "Correct operation of the 'replace-tag-in-set' function."
;    (let [a-tag "A Tag"
;          b-tag "B Tag"
;          a-new-tag "A New Tag"
;          base-set-atom (atom #{a-tag b-tag})]
;      (is (nil? (te/replace-tag-in-set base-set-atom "No Such Tag" a-new-tag)))
;      (is (= #{a-tag a-new-tag} (te/replace-tag-in-set base-set-atom b-tag a-new-tag))))))

(deftest delete-existing-tag-test
  (testing "Correct operation of the 'delete-existing-tag' function.")
  (let [v ["a" "b" "c"]]
    (is (= ["a" "b"] (te/delete-existing-tag (atom v) 2)))))