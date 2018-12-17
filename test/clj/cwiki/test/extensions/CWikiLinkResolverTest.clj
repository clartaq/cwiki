(ns cwiki.test.extensions.CWikiLinkResolverTest
  (:require [clojure.test :refer :all])
  (:import (com.vladsch.flexmark.ext.gfm.strikethrough StrikethroughExtension)
           (com.vladsch.flexmark.ext.tables TablesExtension)
           (com.vladsch.flexmark.ext.footnotes FootnoteExtension)
           (com.vladsch.flexmark.ext.wikilink WikiLinkExtension)
           (com.vladsch.flexmark.html HtmlRenderer HtmlRenderer$Builder)
           (com.vladsch.flexmark.parser Parser Parser$Builder)
           (com.vladsch.flexmark.util KeepType)
           (com.vladsch.flexmark.util.options MutableDataSet)
           (cwiki.extensions CWikiLinkResolverExtension)
           (java.util ArrayList)))

;-------------------------------------------------------------------------------
; Test fixtures.
;-------------------------------------------------------------------------------

; Don't really need all these extensions for the test. Just using them so
; that the setup is a bit closer to how the extension is actually used in
; the CWiki program.

(def options (-> (MutableDataSet.)
                 (.set Parser/REFERENCES_KEEP KeepType/LAST)
                 (.set HtmlRenderer/INDENT_SIZE (Integer/valueOf 2))
                 (.set HtmlRenderer/PERCENT_ENCODE_URLS true)
                 (.set TablesExtension/COLUMN_SPANS false)
                 (.set TablesExtension/MIN_HEADER_ROWS (Integer/valueOf 1))
                 (.set TablesExtension/MAX_HEADER_ROWS (Integer/valueOf 1))
                 (.set TablesExtension/APPEND_MISSING_COLUMNS true)
                 (.set TablesExtension/DISCARD_EXTRA_COLUMNS true)
                 (.set TablesExtension/WITH_CAPTION false)
                 (.set TablesExtension/HEADER_SEPARATOR_COLUMN_MATCH true)
                 (.set WikiLinkExtension/ALLOW_ANCHOR_ESCAPE true)
                 (.set WikiLinkExtension/ALLOW_ANCHORS true)
                 (.set WikiLinkExtension/LINK_FIRST_SYNTAX true)
                 (.set WikiLinkExtension/LINK_ESCAPE_CHARS "")
                 (.set Parser/EXTENSIONS (ArrayList.
                                           [(FootnoteExtension/create)
                                            (StrikethroughExtension/create)
                                            (CWikiLinkResolverExtension/create)
                                            (WikiLinkExtension/create)
                                            (TablesExtension/create)]))))

(def parser (.build ^Parser$Builder (Parser/builder options)))
(def renderer (.build ^HtmlRenderer$Builder (HtmlRenderer/builder options)))

;-------------------------------------------------------------------------------
; Utilities
;-------------------------------------------------------------------------------

(defn- convert-markdown-to-html
  "Convert the markdown formatted input string to html
  and return it."
  [mkdn]
  (->> mkdn
       (.parse parser)
       (.render renderer)))

;-------------------------------------------------------------------------------
; Tests
;-------------------------------------------------------------------------------

(deftest wikilink-resolver-test
  (testing "Testing the get-option-value function."
    ; Assure that regular text is unaffected.
    (is (= "<p>some text</p>\n" (convert-markdown-to-html "some text")))
    ; Assure that simple links as they already existed are unaffected.
    (is (= "<p>a link to the <a href=\"Front%20Page\">Front Page</a>.</p>\n"
           (convert-markdown-to-html "a link to the [[Front Page]].")))
    ; Assure that two part links that worked before still work now.
    (is (= "<p><a href=\"link%20part\">text part</a></p>\n"
           (convert-markdown-to-html "[[link part|text part]]")))
    ; Assure that a special case used to generate a page of all the other
    ; pages containing a particular tag still work.
    (is (= "<p><a href=\"/as-tag?tag=A%20Questionable%20%28%27%3F%27%29%20Tag%21\">/as-tag?tag=A Questionable ('?') Tag!</a></p>\n"
           (convert-markdown-to-html "[[/as-tag?tag=A Questionable ('?') Tag!]]")))
    ; Assure that a special case used to generate a page of all the other
    ; pages written by a particular author still work.
    (is (= "<p><a href=\"/as-user?user=random%20user\">random user</a></p>\n"
           (convert-markdown-to-html "[[/as-user?user=random user|random user]]")))
    ;; Assure that a link to a page with some puncutation in the title works.
    (is (= "<p><a href=\"What%20is%20A%2FB%20Testing%3F\">What is A/B Testing?</a></p>\n"
           (convert-markdown-to-html "[[What is A/B Testing?]]")))))

