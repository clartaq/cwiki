(ns cwiki.test.util.files
  (:require [clojure.test :refer :all]
            [cwiki.util.files :refer :all]
            [clojure.string :as s])
  (:import (java.io File)))

(deftest drop-lines-while-test
  (testing "drop-lines-while function"
    (is (nil? (drop-lines-while #(s/starts-with? % "A") "")))
    (is (nil? (drop-lines-while #(s/starts-with? % "A") [])))
    (let [res (drop-lines-while #(s/starts-with? % "A") ["A Test"])]
      (is (empty? res)))
    (let [res (drop-lines-while #(s/starts-with? % "A") ["Some Line"])]
      (is (= res ["Some Line"])))
    (let [res (drop-lines-while #(s/starts-with? % "A") ["A Line" "Some Line"])]
      (is (= res ["Some Line"])))
    (let [res (drop-lines-while #(s/starts-with? % "A") ["A Test" "Another Line"])]
      (is (empty? res)))))

(deftest drop-lines-while-blank-test
  (testing "drop-lines-while-blank-function"
    (is (nil? (drop-lines-while-blank "")))
    (is (nil? (drop-lines-while-blank [])))
    (is (= ["A Line"] (drop-lines-while-blank ["A Line"])))
    (is (= ["A Line"] (drop-lines-while-blank ["" "   " "A Line"])))
    (is (= ["A Line" " " "Another"]
           (drop-lines-while-blank ["A Line" " " "Another"])))
    (is (= ["A Line" " " "Another"]
           (drop-lines-while-blank ["" "\n" "   " "\n\n\t" "A Line" " " "Another"])))))

(deftest split-front-matter-from-body-test
  (testing "split-front-matter-from-body function"
    (let [res (split-front-matter-from-body nil)]
      (is (= {:body []} res)))
    (let [res (split-front-matter-from-body "")]
      (is (= {:body []} res)))
    (let [res (split-front-matter-from-body [" "])]
      (is (= {:body []} res)))

    (let [res (split-front-matter-from-body ["# Test #\n\n"
                                             "This is a **test** of the emergency data"])]
      (is (= {:body ["# Test #\n\n"
                     "This is a **test** of the emergency data"]} res)))

    (let [res (split-front-matter-from-body ["---"
                                             "---"
                                             "# Test #\n\n"
                                             "This is a **test** of the emergency data"])]
      (is (= {:front []
              :body  ["# Test #\n\n"
                      "This is a **test** of the emergency data"]} res)))

    (let [res (split-front-matter-from-body ["---"
                                             "author: boogledy"
                                             "date: 21 July 2025"
                                             "---"
                                             "# Test #\n\n"
                                             "This is a **test** of the emergency data"])]
      (is (= {:front ["author: boogledy"
                      "date: 21 July 2025"]
              :body  ["# Test #\n\n"
                      "This is a **test** of the emergency data"]} res)))

    (let [res (split-front-matter-from-body ["\n\n\n"
                                             "   "
                                             "---"
                                             "author: boogledy"
                                             "date: 21 July 2025"
                                             "---"
                                             "# Test #\n\n"
                                             "   "
                                             "This is a **test** of the emergency data"])]
      (is (= {:front ["author: boogledy"
                      "date: 21 July 2025"]
              :body  ["# Test #\n\n"
                      "   "
                      "This is a **test** of the emergency data"]} res)))))

(deftest yaml->map-test
  (testing "yaml->map function"
    (is (nil? (yaml->map "")))
    (let [res (yaml->map (s/join "\n" ["author: boogledy"
                                       "date: 21 July 2025"]))]
      (is (= res {:author "boogledy"
                  :date   "21 July 2025"})))

    (let [res (yaml->map (s/join "\n" ["title: First Light"
                                       "date: 2017-08-15 14:03:02"
                                       "tags: blogging"
                                       ]))]
      (is (= (:title res) "First Light"))
      (is (not (nil? (:date res))))
      (is (= (:tags res) "blogging"))

      (let [res (yaml->map (s/join "\n" ["title: Serving HTTPS with Caddy Server"
                                         "tags:"
                                         "- wordpress"
                                         "- blogging"
                                         "- caddy"
                                         "date: 2016-08-19 18:02:38"]))]
        (is (= (:title res) "Serving HTTPS with Caddy Server"))
        (is (= (count (:tags res)) 3))
        (is (not (nil? (:date res))))))))

(deftest no-front-matter-test
  (testing "The load-markdown-resource function on a file with no front matter."
    (is (= {:meta {} :body nil} (load-markdown-from-resource "no-such-file.md")))
    (is (not (nil? (load-markdown-from-resource "test.md"))))
    (is (= {:meta {} :body (str "# Test #\n\n"
                                "This is a **test** of the emergency data"
                                " acquisition system.")}
           (load-markdown-from-resource "private/test_data/test.md")))))

(deftest with-front-matter-test
  (testing "The load-markdown-resource function on a file with front matter."
    (let [res (load-markdown-from-resource
                "private/test_data/post-with-front-matter.md")
          tags (into (sorted-set) (:tags (:meta res)))]
      (is (= "This is part of a post with front matter." (:body res)))
      (is (not (nil? (:meta res))))
      (is (= 3 (count tags)))
      (is (= "Serving WordPress over HTTPS with Caddy Server"
             (:title (:meta res))))
      (is (= "david" (:author (:meta res))))
      (is (contains? tags "caddy"))
      (is (contains? tags "wordpress"))
      (is (contains? tags "blogging")))))

(deftest incomplete-content-test
  (testing "The load-markdown-from-file function on a file with parts missing."
    (is (nil? (load-markdown-from-file (File. "Non-existent file"))))
    (let [file (File. "test/data/NoContentHere.md")
          m (load-markdown-from-file file)]
      (is (empty? (:body m)))
      (is (= "Someone" (:author (:meta m))))
      (is (= "No Content Here" (:title (:meta m)))))
    (let [file (File. "test/data/NoMeta.md")
          m (load-markdown-from-file file)]
      (is (empty? (:meta m)))
      (is (= "A plain text file without any metadata." (:body m))))
    (let [file (File. "test/data/NoDatesOrTags.md")
          m (load-markdown-from-file file)
          meta (:meta m)]
      (is (= "Someone" (:author meta)))
      (is (= "A Remarkable Title" (:title meta)))
      (is (nil? (and (:date meta)
                     (:created meta))))
      (is (nil? (and (:updated meta)
                     (:changed meta)
                     (:modified meta))))
      (is (nil? (:tags meta)))
      (is (= "A Markdown file without dates or tags in the metadata.\nBut it does have a remarkable title. It is remarkable\nin that is uses the word remarkable several times.\nRemarkable!" (:body m))))))

(deftest trim-leading-and-trailing-underscores-test
  (testing "The trim-leading-and-trailing-underscores function."
    (is (nil? (trim-leading-and-trailing-underscores nil)))
    (is (= "" (trim-leading-and-trailing-underscores "")))
    (is (= "" (trim-leading-and-trailing-underscores "_")))
    (is (= "" (trim-leading-and-trailing-underscores "____")))
    (is (= "word" (trim-leading-and-trailing-underscores "word")))
    (is (= "word" (trim-leading-and-trailing-underscores "_word")))
    (is (= "word" (trim-leading-and-trailing-underscores "word_")))
    (is (= "word" (trim-leading-and-trailing-underscores "_word_")))
    (is (= "word" (trim-leading-and-trailing-underscores "___word__")))
    (is (= "hyphenated-word" (trim-leading-and-trailing-underscores
                               "__hyphenated-word_")))))

(deftest in?-test
  (testing "The in? function."
    (is (nil? (in? nil "x")))
    (is (nil? (in? 32 nil)))
    (is (nil? (in? 32 [12 15 40 56])))
    (is (true? (in? 32 [13 939 32 909485])))
    (is (true? (in? \g "finger")))
    (is (nil? (in? \z "finger")))
    (is (nil? (in? 32 '(45 67 9883))))
    (is (true? (in? 32 '(1 3 5 32 95 891234789))))
    (is (true? (in? \n "finger")))
    (is (nil? (in? \z "finger")))
    (is (true? (in? \newline "a line\n")))
    (is (true? (in? \tab "a\tab haha")))
    (is (true? (in? 32 #{1 3 5 32 95 891234789})))
    (is (nil? (in? 32 #{1 3 5 432 95 891234789})))))

(deftest is-seed-page?-test
  (testing "The is-seed-page? function."
    (is (nil? (is-seed-page? "bufar floqwar")))
    (is (true? (is-seed-page? "About")))
    (is (true? (is-seed-page? "Front Page")))
    (is (true? (is-seed-page? "Roadmap")))))