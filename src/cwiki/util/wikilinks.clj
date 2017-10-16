(ns cwiki.util.wikilinks
  (:require [cemerick.url :as u]
            [clojure.string :as s]
            [cwiki.models.db :as db]
            [hiccup.core :as hc]
            [hiccup.element :refer [link-to]]))

; A regular expression to match MediaWiki-style internal
; wikilinks.
(def pattern #"\[\[.+?\]\]")

(defn find-wikilinks
  "Return a collection of all of the wikilinks in the text."
  [txt]
  (re-seq pattern txt))

(defn article-is-present?
  "Return true if an article with the given title
  exists in the database."
  [article-name]
  (not (nil? (db/find-post-by-title article-name))))

(defn- strip-link-brackets
  "Return a copy of the wikilink with the brackets
  removed."
  [wikilink]
  (-> wikilink
      (s/replace "[[" "")
      (s/replace "]]" "")))

(defn- get-different-parts
  [clean-link]
  (let [splits (s/split clean-link #"\|")]
    (println "splits:" splits)
    {:title-part (first splits) :display-part (second splits)}))

(defn get-wikilink-parts
  "Return a map of the various parts in the wikilink."
  [wikilink]
  (let [clean-link (strip-link-brackets wikilink)]
    (if (s/includes? clean-link "|")
      (get-different-parts clean-link)
      ; else both parts are the same
      {:title-part clean-link :display-part clean-link})))

(defn wikilink->title
  "Return an article title based on the input wikilink."
  [wikilink]
  (:title-part (get-wikilink-parts wikilink)))

(defn link-parts->html-link
  "Given a map containing a (possibly identical) page title for
  a link and some text to be displayed for the link,
  return the html for a link."
  [link-parts]
  ;(println "input: link-parts:" link-parts)
  (let [style (if (article-is-present? (:title-part link-parts))
                "present-button-style"
                "absent-button-style")
        uri (u/url-encode (str "/" (:title-part link-parts)))
        ;_ (println "uri:" uri)
        html (hc/html (link-to {:class style} uri (:display-part link-parts)))]
    html))

(defn do-one-subsitution
  "Substitute an html-style link for a single
  wikilink and return the text with the substitution."
  [wikilink txt]
  ;(println "do-one-subsitution: wikilink:" wikilink ", txt:" txt)
  (let [link-parts (get-wikilink-parts wikilink)
        ;_ (println "link-parts:" link-parts)
        html (link-parts->html-link link-parts)]
    (let [rt (s/replace txt wikilink html)]
      ;(println "rt:" rt)
      rt)))

(defn replace-wikilinks
  "Replace and wikilinks in the input text with html-style
  links and return the text with the replacements."
  [md-txt]
  (let [link-coll (find-wikilinks md-txt)]
    ;(println "link-coll:" link-coll)
    (loop [lc link-coll txt md-txt]
      ;(println "inside loop: lc:" lc ", txt:" txt)
      (if (empty? lc)
        txt
        (recur (rest lc) (do-one-subsitution (first lc) txt))))))