(ns cwiki.test.util.wikilinks
  (:require [clojure.test :refer :all]
            [cwiki.util.wikilinks :refer :all]))

(deftest find-wikilinks-test
  (testing "find-wikilinks"
    (is (nil? (find-wikilinks "")))
    (is (nil? (find-wikilinks "some text, no links")))
    (is (nil? (find-wikilinks "some text\non three\r\nlines")))
    (is (nil? (find-wikilinks "[[]]")))
    (is (nil? (find-wikilinks "[page]")))
    (is (nil? (find-wikilinks "[[page")))
    (is (nil? (find-wikilinks "[page]]")))
    (is (nil? (find-wikilinks "[[page]")))
    (let [links (find-wikilinks "[[page]]")]
      (is (and (= 1 (count links))
               (= "[[page]]" (first links)))))
    (let [links (find-wikilinks "[[some page]]")]
      (is (and (= 1 (count links))
               (= "[[some page]]" (first links)))))
    (let [links (find-wikilinks "some text with\n[[some page]] linked")]
      (is (and (= 1 (count links))
               (= "[[some page]]" (first links)))))
    (let [links (find-wikilinks "some text with\n[[some page]] linked and a [[piped|link]] too.")]
      (is (and (= 2 (count links))
               (= "[[some page]]" (first links))
               (= "[[piped|link]]" (second links)))))))

(deftest article-is-present?-test
  (testing "article-is-present"
    (is (false? (article-is-present? "wooboo")))
    (is (article-is-present? "Front Page"))))

(deftest get-wikilink-parts-test
  (testing "get-wikilink-parts"
    (is (thrown? NullPointerException (get-wikilink-parts nil)))
    (is (= (get-wikilink-parts "")
           {:title-part "" :display-part ""}))
    (is (= (get-wikilink-parts "[[]]")
           {:title-part "" :display-part ""}))
    (is (= (get-wikilink-parts "[[The Link]]")
           {:title-part "The Link" :display-part "The Link"}))
    (is (= (get-wikilink-parts "[[A|B]]")
           {:title-part "A" :display-part "B"}))
    (is (= (get-wikilink-parts "[[The Title Link|To Be Displayed]]")
           {:title-part "The Title Link" :display-part "To Be Displayed"}))))

(deftest link-parts->html-link-test
  (testing "link-parts->html-link"
    (println "about to do first test")
    (is (= (link-parts->html-link {:title-part "Front Page"
                                   :display-part "Front Page"} nil)
           "<a href=\"Front%20Page\" style=\"any-old-style\">Front Page</a>"))
    (println "about to do second test")
    (is (= (link-parts->html-link {:title-part "Other Wiki Software"
                                   :display-part "Other Wiki Software"} nil)
           "<a href=\"Other%20Wiki%20Software\" style=\"any-old-style\">Other Wiki Software</a>"))
    (println "about to do third test")
    (is (= (link-parts->html-link {:title-part "Some Random Page"
                                   :display-part "Some Random Page"} nil)
           "<a href=\"Some%20Random%20Page\" style=\"color:red\">Some Random Page</a>"))
    (is (= (link-parts->html-link {:title-part "Some Random Title"
                                   :display-part "Some Random Display"} nil)
           "<a href=\"Some%20Random%20Title\" style=\"color:red\">Some Random Display</a>"))
    (is (= (link-parts->html-link {:title-part "Front Page"
                                   :display-part "Root Page"} nil)
           "<a href=\"Front%20Page\" style=\"any-old-style\">Root Page</a>"))))

(deftest replace-wikilinks-test
  (testing "replace-wikilinks"
    (is (= "" (replace-wikilinks "" nil)))
    (is (= "No links here" (replace-wikilinks "No links here" nil)))
    (is (= "<a href=\"only%20a%20link\" style=\"color:red\">only a link</a>"
           (replace-wikilinks "[[only a link]]" nil)))
    (is (= "The <a href=\"Front%20Page\" style=\"any-old-style\">Front Page</a> and a <a href=\"non-existent\" style=\"color:red\">non-existent</a> post"
           (replace-wikilinks "The [[Front Page]] and a [[non-existent]] post" nil)))
    (is (= "The <a href=\"Front%20Page\" style=\"any-old-style\">Root Page</a> and a <a href=\"non-existent\" style=\"color:red\">non-existent</a> post"
           (replace-wikilinks "The [[Front Page|Root Page]] and a [[non-existent]] post" nil)))))


