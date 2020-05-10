;;;
;;; This namespace provides tests for the functions used to count
;;; words in a piece of Markdown text.
;;;
;;; NOTE: These tests have special knowledge of how things are replaced
;;; by spaces or not. Changes in the implementation of the counting
;;; functions may require alteration of the expected test results.
;;;

(ns cwiki.test.util.wc
  (:require [clojure.test :refer :all]
            [cwiki.util.wc :refer :all]))

(deftest strip-html-comments-test
  (testing "The function that strips HTML comments from strings."
    (is (= "Bob" (strip-html-comments "Bob")))
    (is (empty? (strip-html-comments "")))
    (is (= "Bob Alice" (strip-html-comments "Bob Alice")))
    (is (= "Bob Alice" (strip-html-comments "Bob <!--No End Space-->Alice")))
    (is (= "Bob Alice" (strip-html-comments "Bob <!-- -->Alice")))
    (is (= "Bob Alice" (strip-html-comments "Bob <!-- A comment with a <tag>tag</tag> in it -->Alice")))))

(deftest replace-tabs-with-spaces-test
  (testing "An empty string"
    (is (empty? (replace-tabs-with-spaces ""))))
  (testing "A single tab"
    (is (= " " (replace-tabs-with-spaces "\t"))))
  (testing "Tabs between words"
    (is (= "Bob Carol Ted" (replace-tabs-with-spaces "Bob\tCarol\tTed"))))
  (testing "Works across newlines"
    (is (= "Bob Carol\nTed Alice" (replace-tabs-with-spaces "Bob\tCarol\nTed\tAlice")))))

(deftest strip-images-test
  (testing "Removing images"
    (is (= "Links to images like these:\n\n\n\n\n\nShould go away."
           (strip-images "Links to images like these:\n\n![Alt text](/path/to/img.jpg)\n\n![Alt text](/path/to/img.jpg \"Optional title\")\n\nShould go away.")))))

;; Note that the headers actually contain a leading a trailing newline when
;; the header indicators are removed.
(deftest extract-markdown-headers-test
  ;; atx style headers
  (testing "Header 1"
    (is (= "\nHeader 1\n" (extract-markdown-headers "# Header 1 #\n"))))
  (testing "Header 2 with mismatched trailing markers"
    (is (= "\nHeader 2\n" (extract-markdown-headers "## Header 2 ####\n"))))
  (testing "Header 6"
    (is (= "\nHeader 6\n" (extract-markdown-headers "###### Header 6 ######  \n"))))
  (testing "Only markers before title"
    (is (= "\nHeader 3\n" (extract-markdown-headers "### Header 3   \n"))))
  (testing "Header without space after marker is not really a Header"
    (is (= "#Header 1#\n" (extract-markdown-headers "#Header 1#\n"))))
  (testing "Trailing markers should be left alone"
    (is (= "Header 1#\n" (extract-markdown-headers "Header 1#\n")))
    (is (= "Header 1 #\n" (extract-markdown-headers "Header 1 #\n"))))

  ;;; setext style headers
  (testing "Setext style headers"
    (is (= "\nAn H1 header\n" (extract-markdown-headers "An H1 header\n=============\n")))
    (is (= "\nAn H2 header\n" (extract-markdown-headers "An H2 header\n-------------\n")))))

(deftest extract-link-text-test
  (testing "A plain ole link"
    (is (= "A  link  here" (extract-link-text "A [link](http://example.com) here"))))
  (testing "Two links"
    ;; Note the double spaces around "first" and "second".
    (is (= "A  first  link and a  second  one"
           (extract-link-text "A [first](http://example.com) link and a [second](https://example.com) one")))))

(deftest extract-wikilink-text-test
  (testing "Empty wikilink"
    (is (= "  " (extract-wikilink-text "[[]]"))))
  (testing "Malformed links"
    (is (= "Some [text]] here" (extract-wikilink-text "Some [text]] here")))
    (is (= "Some [[text] here" (extract-wikilink-text "Some [[text] here"))))
  (testing "Simple, single word wikilink"
    (is (= " wikilink " (extract-wikilink-text "[[wikilink]]"))))
  (testing "Multi-word wikilink"
    (is (= " Some Random Text " (extract-wikilink-text "[[Some Random Text]]"))))
  (testing "A two-part link"
    ; Note the _two_ spaces before "link"
    (is (= "A  link ?" (extract-wikilink-text "A [[two part|link]]?")))))

(deftest replace-html-tags-with-spaces-test
  (testing "A regular string with no tag"
    (is (= "A regular string" (replace-html-tags-with-spaces "A regular string"))))
  (testing "A single tag element"
    ;; Note three spaces after break
    (is (= "A break   here" (replace-html-tags-with-spaces "A break </ br> here"))))
  (testing "Multiple tags"
    (is (= "Some  bold  and  underlined  elements" (replace-html-tags-with-spaces "Some <b>bold</b> and <u>underlined</u> elements")))))

(def footnote-test-text "Here's a simple footnote,[^1] and here's a longer one.[^bignote]

[^1]: This is the first footnote.

[^bignote]: Here's one with multiple paragraphs and code.")

(def footnote-result-text
  "Here's a simple footnote, and here's a longer one.

 This is the first footnote.

 Here's one with multiple paragraphs and code.")

(deftest strip-footnote-references-test
  (testing "Footnote removal"
    (is (= footnote-result-text
           (strip-footnote-references footnote-test-text)))))

(deftest strip-mathjax-test
  (testing "Stripping inline LaTeX"
    ;; Note double space before "and".
    (is (= "Some math inline  and"
           (strip-mathjax "Some math inline $\\sum_{i=0}^n i^2 = \\frac{(n^2+n)(2n+1)}{6}$ and"))))
  (testing "Stripping inline LaTeX symbol"
    ;; Note double space before "symbol".
    (is (= "The  symbol inline" (strip-mathjax "The $\\rm\\LaTeX$ symbol inline"))))
  (testing "Display equation with no content"
    (is (empty? (strip-mathjax "$$$$"))))
  (testing "Display equation with only spaces for content"
    (is (empty? (strip-mathjax "$$   $$"))))
  (testing "The LaTeX symbol"
    (is (= "LaTeX symbol\n\n\n\n" (strip-mathjax "LaTeX symbol\n\n$$\\rm\\LaTeX$$\n\n"))))
  (testing "Display equation with internal newlines (not the LaTeX \\newline)"
    (is (empty? (strip-mathjax "$$\n\\sigma = \\sqrt{ \\frac{1}{N} \\sum_{i=1}^N (x_i -\\mu)^2}\n$$"))))
  (testing "Display equation with internal Unicode 'nextline' character \u0085"
    (is (empty? (strip-mathjax "$$\u0085\\sigma = \\sqrt{ \\frac{1}{N} \\sum_{i=1}^N (x_i -\\mu)^2}\u0085$$"))))
  (testing "Display equation with internal Unicode 'line-separatpr' character \u2028"
    (is (empty? (strip-mathjax "$$\u2028\\sigma = \\sqrt{ \\frac{1}{N} \\sum_{i=1}^N (x_i -\\mu)^2}\u2028$$"))))
  (testing "Display equation with internal Unicode 'nex-paragraph' character \u2029"
    (is (empty? (strip-mathjax "$$\u2029\\sigma = \\sqrt{ \\frac{1}{N} \\sum_{i=1}^N (x_i -\\mu)^2}\u2029$$"))))
  (testing "Display equation with internal Unicode Greek 'Omega' character \u03A9"
    (is (empty? (strip-mathjax "$$\u0349\\sigma = \\sqrt{ \\frac{1}{N} \\sum_{i=1}^N (x_i -\\mu)^2}\u0349$$"))))
  (testing "Stripping standalone LaTeX equation with internal newlines"
    (is (= "on it's own line:\n\n\n\nLooks good, huh?"
           (strip-mathjax "on it's own line:\n\n$$\\n\\sigma = \\sqrt{ \\frac{1}{N} \\sum_{i=1}^N (x_i -\\mu)^2}\\n$$\n\nLooks good, huh?")))))

(deftest strip-special-characters-test
  (testing "An empty string"
    (is (empty? (strip-special-characters ""))))
  (testing "Just the special characters"
    (is (empty? (strip-special-characters "#*`~–^=<>+|/:"))))
  (testing "Characters and words"
    (is (= "AWordInTheMiddleOfTheCharsThatShouldBeRemovedNow"
          (strip-special-characters "#A*Word`In~The–Middle^Of=The<Chars>That+Should|Be/Removed:Now")))))

(deftest strip-standalone-punctuation-test
  (testing "No punctuation string"
    (is (= "A string" (strip-standalone-punctuation "A string")))
    (is (= " Another String " (strip-standalone-punctuation " Another String "))))
  (testing "Interposed punctuation"
    (is (= "A comma period qmark" (strip-standalone-punctuation "A , comma . period ? qmark"))))
  (testing "At the beginning of a line"
    (is (= " At beginning" (strip-standalone-punctuation "\n! At beginning"))))
  (testing "At the end of a line"
    (is (= "At end " (strip-standalone-punctuation "At end ;\n"))))
  (testing "At both ends"
    (is (= " At both ends " (strip-standalone-punctuation "\t! At both / ends\t/\n")))))

(deftest strip-standalone-numbers-test
  (testing "Multi-line, no numbers"
    (is (= "First item\nSecond Item\nThird item\n"
           (strip-standalone-numbers "First item\nSecond Item\nThird item\n"))))
  (testing "Numbered list"
    (is (= "A Numbered List First item Second Item Third item\n"
           (strip-standalone-numbers "A Numbered List\n1. First item\n2. Second Item\n3. Third item\n"))))
  (testing "Numbers in words"
    (is (= "An identifier is an_id_21c."
           (strip-standalone-numbers "An identifier is an_id_21c."))))
  (testing "Things with prices"
    (is (= "Pears cost $1.34 per pound."
           (strip-standalone-numbers "Pears cost $1.34 per pound.")))))