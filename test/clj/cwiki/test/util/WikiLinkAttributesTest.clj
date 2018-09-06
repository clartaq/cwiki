(ns cwiki.test.util.WikiLinkAttributesTest
  (:require [clojure.test :refer :all]
            [cwiki.util.wikilink-attributes])
  (:import (com.vladsch.flexmark.util.options MutableDataSet)
           (com.vladsch.flexmark.parser Parser Parser$Builder)
           (com.vladsch.flexmark.html HtmlRenderer HtmlRenderer$Builder)
           (com.vladsch.flexmark.ext.wikilink WikiLinkExtension)
           (cwiki.util WikiLinkAttributeExtension)
           (java.util ArrayList)))

;-------------------------------------------------------------------------------
; Helpers
;-------------------------------------------------------------------------------

(defn wiki-markdown->html
  [markdown]
  (let [options (-> (MutableDataSet.)
                    (.set WikiLinkExtension/LINK_FIRST_SYNTAX true)
                    (.set WikiLinkExtension/LINK_ESCAPE_CHARS "")
                    (.set Parser/EXTENSIONS
                          (ArrayList. [(WikiLinkExtension/create)
                                       (WikiLinkAttributeExtension/create)])))
        parser (.build ^Parser$Builder (Parser/builder options))
        document (.parse ^Parser parser ^String markdown)
        renderer (.build ^HtmlRenderer$Builder (HtmlRenderer/builder options))]
    (.render renderer document)))

;-------------------------------------------------------------------------------
; Tests
;-------------------------------------------------------------------------------

(deftest cwiki-attribute-provider-test
  (testing "The ability to parse and attributize wikilinks."
    (let [test-output (wiki-markdown->html "A [[wikilink|WIKILINK]] here.")]
      (is (= test-output
             "<p>A <a href=\"wikilink\" style=\"color:red;\">WIKILINK</a> here.</p>\n")))

    (let [test-output (wiki-markdown->html "A [[missing link]] here.")]
      (is (= test-output
             "<p>A <a href=\"missing link\" style=\"color:red;\">missing link</a> here.</p>\n")))

    (let [test-output (wiki-markdown->html "The [[All Pages]] page is special.")]
      (is (= test-output
             "<p>The <a href=\"All Pages\">All Pages</a> page is special.</p>\n")))

    (let [test-output (wiki-markdown->html "As is the page that lists [[Orphans|orphan pages]].")]
      (is (= test-output
             "<p>As is the page that lists <a href=\"Orphans\">orphan pages</a>.</p>\n")))

    (let [test-output (wiki-markdown->html "This is the [[Front Page]] of our wiki.")]
      (is (= test-output
             "<p>This is the <a href=\"Front Page\">Front Page</a> of our wiki.</p>\n")))

    (let [test-output (wiki-markdown->html "The [[Front Page|front page]] appears first.")]
      (is (= test-output
             "<p>The <a href=\"Front Page\">front page</a> appears first.</p>\n")))))

(deftest cwiki-skip-parsing-embedded-wikilinks-test
  (testing "Assuring that wikilinks embedded in code blocks are ignored."

  (let [test-output (wiki-markdown->html "Should `ignore [[wikilinks]] in in-line code` sections.")]
    (is (= test-output
           "<p>Should <code>ignore [[wikilinks]] in in-line code</code> sections.</p>\n")))

  (let [test-output (wiki-markdown->html "    Should ignore\n    the [[wikilinks]] in\n    code blocks too.")]
    (is (= test-output
           "<pre><code>Should ignore\nthe [[wikilinks]] in\ncode blocks too.\n</code></pre>\n")))

  (let [test-output (wiki-markdown->html "```\nAnd\n    the [[wikilinks]] in\n   fenced code blocks too.")]
    (is (= test-output
           "<pre><code>And\n    the [[wikilinks]] in\n   fenced code blocks too.</code></pre>\n")))))
