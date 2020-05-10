;;;;
;;;; This namespace includes utility functions to count the number of words
;;;; in a file of Markdown text.

;; See https://github.com/gandreadis/markdown-word-count/blob/master/mwc.py

(ns cwiki.util.wc
  (:require [clojure.string :as s]))

(defn strip-html-comments
  "Return a version of the input text with HTML comments removed."
  [text]
  (s/replace text #"<!--(.+)-->" ""))

(defn replace-tabs-with-spaces
  "Return a version of the input text with any tab characters
  replaced with a single space."
  [text]
  (s/replace text #"\t+" " "))

(defn strip-images
  "Return a version of the input text where all image links have
  been removed."
  [text]
  (let [no-images (s/replace text #"!\[[^\]]*\]\([^)]*\)" "")]
    no-images))

(defn extract-markdown-headers
  "Strip header markers and return only the text portion of the header
  surrounded with newlines. The newlines prevent erroneously merging words
  in consecutive header lines.

  Note that this function expects there to be a space between the last '#'
  marking a header and the text content in the header. The original perl
  version of Markdown did not require this, but I have never seen a
  Markdown rendered that worked without it so..."
  [text]
  (let [no-atx (s/replace text #"^\#{1,6}[ \t]+(.+?)[ \t]+\#*[ \t]*\n+" "\n$1\n")
        no-setex-1 (s/replace no-atx #"^(.+)[ \t]*\n=+[ \t]*\n+" "\n$1\n")
        no-setex-2 (s/replace no-setex-1 #"^(.+)[ \t]*\n-+[ \t]*\n+" "\n$1\n")]
    no-setex-2))

(defn extract-link-text
  "Return a version of the input text where the link indicators are replaced
  with the text in the brackets. The punctuation and url are deleted."
  [text]
  (let [no-links (s/replace text #"\[(.+?)\]\(.+?\)" " $1 ")]
    no-links))

(defn extract-wikilink-text
  "Return the displayable content of the link. In the case of tw-part links
  with the link text and the display text separated by a pipe character,
  return only the content of the display text part."
  [text]
  (let [no-wikilinks (s/replace text #"\[\[(?:[^|\]]*\|)?([^\]]*)\]\]" " $1 ")]
    no-wikilinks))

(defn replace-html-tags-with-spaces
  "Return a version of the input text where html tags have been replaced
  with a single space."
  [text]
  (s/replace text #"<\/?[^>]*>" " "))

(defn strip-footnote-references
  "Return a version of the input text where MultiMarkdown-style footnote
  references have been removed. Note that this leaves the text of the
  footnote in place."
  [text]
  (let [no-footnote-references (s/replace text #"\[\^[0-9\w]*\]:?" "")]
    no-footnote-references))

(defn strip-mathjax
  "Remove mathematics to be formatted by MathJax. This will also remove
  all the text between two dollar signs on a line, such as when listing
  prices."
  [text]
  (let [;; Need to enable the Java Pattern.DOTALL flag using (?s) or it won't
        ;; catch and remove newlines in the display equation. Also matches
        ;; and removes Unicode characters with similar function.
        no-display-math (s/replace text #"(?s)\$\$.*\$\$" "")
        no-inline-math (s/replace no-display-math #"\$.*\$" "")]
    no-inline-math))

(defn strip-special-characters
  "Return a version of the text where the 'special' characters have been
  removed. This takes care of block quotes, unordered lists, rules, tables
  and others."
  [text]
  (s/replace text #"[#*`~\-–^=<>+|/:]" ""))

(defn strip-standalone-punctuation
  "Return a version of the text where any punctuation surrounded by
  whitespace has been replaced by a space."
  [text]
  (let [no-standalone (s/replace text #"\s\p{Punct}\s" " ")]
    no-standalone))

(defn strip-standalone-numbers
  "Return a version of the text where standalone numbers have been removed.
  This also eliminates the numbers in front of numbered lists."
  [text]
  (let [no-standalone (s/replace text #"\s[+-]?((\d+\.?\d*)|(\.\d+))([Ee]?[+-]?(\d+))?\s" " ")]
    no-standalone))

(defn replace-newlines-with-spaces
  "Return a verion of the text where newlines have been replaced
  with a single space."
  [text]
  (s/replace text #"\n" " "))

(defn strip-repeated-spaces
  "Return a version of the text where consecutive spaces have been
  replaced with a single space."
  [text]
  (s/trim (s/replace text #" +" " ")))

(defn count-words-in-markdown
  "Return a map containg the number of characters in a piece of
  Markdown text and an estimate of the number of words in the text."
  [md-text]
  (let [chars (count md-text)
        words (-> md-text
                  strip-html-comments
                  replace-tabs-with-spaces
                  strip-images
                  extract-markdown-headers
                  extract-link-text
                  extract-wikilink-text
                  replace-html-tags-with-spaces
                  strip-footnote-references
                  strip-mathjax
                  strip-special-characters
                  strip-standalone-punctuation
                  strip-standalone-numbers
                  replace-newlines-with-spaces
                  strip-repeated-spaces
                  (s/split #" ")
                  count)]
    {:words words :chars chars}))
