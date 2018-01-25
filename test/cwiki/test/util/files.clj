(ns cwiki.test.util.files
  (:require [clojure.test :refer :all]
            [cwiki.util.files :refer :all]
            [clojure.string :as s]))

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
      (is (= (not (nil? (:date res)))))
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
      (is (= "\nThis is part of a post with front matter." (:body res)))
      (is (not (nil? (:meta res))))
      (is (= 3 (count tags)))
      (is (= "Serving WordPress over HTTPS with Caddy Server"
             (:title (:meta res))))
      (is (= "david" (:author (:meta res))))
      (is (contains? tags "caddy"))
      (is (contains? tags "wordpress"))
      (is (contains? tags "blogging")))))
