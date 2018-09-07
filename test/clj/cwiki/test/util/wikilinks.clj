(ns cwiki.test.util.wikilinks
  (:require [clojure.test :refer :all]
            [cwiki.util.wikilinks :refer :all]))

;(deftest find-wikilinks-test
;  (testing "find-wikilinks"
;    (is (nil? (find-wikilinks "")))
;    (is (nil? (find-wikilinks "some text, no links")))
;    (is (nil? (find-wikilinks "some text\non three\r\nlines")))
;    (is (nil? (find-wikilinks "[[]]")))
;    (is (nil? (find-wikilinks "[page]")))
;    (is (nil? (find-wikilinks "[[page")))
;    (is (nil? (find-wikilinks "[page]]")))
;    (is (nil? (find-wikilinks "[[page]")))
;    (let [links (find-wikilinks "[[page]]")]
;      (is (and (= 1 (count links))
;               (= "[[page]]" (first links)))))
;    (let [links (find-wikilinks "[[some page]]")]
;      (is (and (= 1 (count links))
;               (= "[[some page]]" (first links)))))
;    (let [links (find-wikilinks "some text with\n[[some page]] linked")]
;      (is (and (= 1 (count links))
;               (= "[[some page]]" (first links)))))
;    (let [links (find-wikilinks "some text with\n[[some page]] linked and a [[piped|link]] too.")]
;      (is (and (= 2 (count links))
;               (= "[[some page]]" (first links))
;               (= "[[piped|link]]" (second links)))))))

;(deftest article-is-present?-test
;  (testing "article-is-present"
;    (is (false? (article-is-present? "wooboo")))
;    (is (article-is-present? "Front Page"))))

;(deftest get-wikilink-parts-test
;  (testing "get-wikilink-parts"
;    (is (thrown? NullPointerException (get-wikilink-parts nil)))
;    (is (= (get-wikilink-parts "")
;           {:title-part "" :display-part ""}))
;    (is (= (get-wikilink-parts "[[]]")
;           {:title-part "" :display-part ""}))
;    (is (= (get-wikilink-parts "[[The Link]]")
;           {:title-part "The Link" :display-part "The Link"}))
;    (is (= (get-wikilink-parts "[[A|B]]")
;           {:title-part "A" :display-part "B"}))
;    (is (= (get-wikilink-parts "[[The Title Link|To Be Displayed]]")
;           {:title-part "The Title Link" :display-part "To Be Displayed"}))))

;(deftest link-parts->html-link-test
;  (testing "link-parts->html-link"
;    (is (= (link-parts->html-link {:title-part   "Front Page"
;                                   :display-part "Front Page"} nil)
;           (str "<a href=\"Front%20Page\" style=\"" ok-to-link-style
;                "\">Front Page</a>")))
;    (is (= (link-parts->html-link {:title-part   "Other Wiki Software"
;                                   :display-part "Other Wiki Software"} nil)
;           (str "<a href=\"Other%20Wiki%20Software\" style=\"" ok-to-link-style
;                "\">Other Wiki Software</a>")))
;    (is (= (link-parts->html-link {:title-part   "Some Random Page"
;                                   :display-part "Some Random Page"} nil)
;           (str "<a href=\"Some%20Random%20Page\" style=\""
;                non-existent-link-style "\">Some Random Page</a>")))
;    (is (= (link-parts->html-link {:title-part   "Some Random Title"
;                                   :display-part "Some Random Display"} nil)
;           (str "<a href=\"Some%20Random%20Title\" style=\""
;                non-existent-link-style "\">Some Random Display</a>")))
;    (is (= (link-parts->html-link {:title-part   "Front Page"
;                                   :display-part "Root Page"} nil)
;           (str "<a href=\"Front%20Page\" style=\"" ok-to-link-style
;                "\">Root Page</a>")))))

;(deftest replace-wikilinks-test
;  (testing "replace-wikilinks"
;    (is (= "" (replace-wikilinks "" nil)))
;    (is (= "No links here" (replace-wikilinks "No links here" nil)))
;    (is (= (str "<a href=\"only%20a%20link\" style=\"" non-existent-link-style
;                "\">only a link</a>")
;           (replace-wikilinks "[[only a link]]" nil)))
;    (is (= (str "The <a href=\"Front%20Page\" style=\"" ok-to-link-style
;                "\">Front Page</a> and a <a href=\"non-existent\" style=\""
;                non-existent-link-style "\">non-existent</a> post")
;           (replace-wikilinks "The [[Front Page]] and a [[non-existent]] post" nil)))
;    (is (= (str "The <a href=\"Front%20Page\" style=\"" ok-to-link-style
;                "\">Root Page</a> and a <a href=\"non-existent\" style=\""
;                non-existent-link-style "\">non-existent</a> post")
;           (replace-wikilinks "The [[Front Page|Root Page]] and a [[non-existent]] post" nil)))))

;;
;; Some data and tests to assure that the link replacer applies the correct
;; special styles for admins and readers when needed.
;;

;(def fake-admin-user-session
;  {:session {:identity {:user_role "admin"}}})
;
;(def fake-reader-user-session
;  {:session {:identity {:user_role "reader"}}})
;
;(deftest replace-links-for-admin-test
;  (testing "wikilink replacement for admin pages."
;    ; Link to a non-existent page should use the "non-existent-link-style".
;    (is (= (str "<a href=\"only%20a%20link\" style=\"" non-existent-link-style
;                "\">only a link</a>")
;           (replace-wikilinks "[[only a link]]" fake-admin-user-session)))
;    ; Link to an existing page should use the "ok-to-link-style".
;    (is (= (str "The <a href=\"Front%20Page\" style=\"" ok-to-link-style
;                "\">Front Page</a> is here.")
;           (replace-wikilinks "The [[Front Page]] is here."
;                              fake-admin-user-session)))
;    ; Link to an admin page should use "ok-to-link-style" when user is admin.
;    (is (= (str "<a href=\"Admin\" style=\"" ok-to-link-style "\">Admin</a>")
;           (replace-wikilinks "[[Admin]]" fake-admin-user-session)))))
;
;(deftest replace-links-for-reader-test
;  (testing "wikilink replacement for user with reader role"
;    ; Link to admin page should be disabled.
;    (is (= (str "<a href=\"Admin\" style=\"" disabled-link-style "\">Admin</a>")
;           (replace-wikilinks "[[Admin]]" fake-reader-user-session)))
;    ; Link to existing page should use normal link.
;    (is (= (str "The <a href=\"Front%20Page\" style=\"" ok-to-link-style
;                "\">Front Page</a> is here.")
;           (replace-wikilinks "The [[Front Page]] is here."
;                              fake-reader-user-session)))
;    ; Link to non-existent page should be disabled.
;    (is (= (str "<a href=\"only%20a%20link\" style=\"" disabled-link-style
;                "\">only a link</a>")
;           (replace-wikilinks "[[only a link]]" fake-reader-user-session)))))
