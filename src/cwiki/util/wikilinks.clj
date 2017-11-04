(ns cwiki.util.wikilinks
  (:require [cemerick.url :as u]
            [clojure.string :as s]
            [cwiki.models.db :as db]
            [cwiki.util.special :as special]
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
    {:title-part (first splits) :display-part (second splits)}))

(defn get-wikilink-parts
  "Return a map of the various parts in the wikilink."
  [wikilink]
  (let [clean-link (strip-link-brackets wikilink)]
    (if (s/includes? clean-link "|")
      (get-different-parts clean-link)
      {:title-part clean-link :display-part clean-link})))

(defn get-view-link-for-existing-page
  "Return a link to be displayed in an existing page. As such, it
  may have separate display and link parts that must be handled."
  [link-parts]
  (let [uri (u/url-encode (str "/" (:title-part link-parts)))
        h (hc/html (link-to uri (:display-part link-parts)))]
    h))

(defn get-edit-link-for-existing-page
  "Return a link to be used with a button or menu."
  [post-map]
  (let [page-title (:title post-map)]
    (when (special/is-editable? page-title)
      (let [uri (u/url-encode (str "/" page-title "/edit"))
            h (hc/html (link-to uri "Edit"))]
        h))))

(defn get-delete-link-for-existing-page
  "Return a link to be used with a button or menu. If the page
  is special and cannot be deleted, return nil."
  [post-map]
  (let [page-title (:title post-map)]
    (when (special/is-deletable? page-title)
      (let [uri (u/url-encode (str "/" page-title "/delete"))
            h (hc/html (link-to uri "Delete"))]
        h))))

(defn get-creation-link-for-new-page
  "Return a link to a non-existent page to be display in a page.
  As such, it may have separate display and link parts that must
  be handled."
  [link-parts]
  (let [page-title (:title-part link-parts)
        uri (u/url-encode (str "/" page-title))
        h (hc/html (link-to {:style "color:red"}
                            uri (:display-part link-parts)))]
    h))

(defn link-parts->html-link
  "Given a map containing a (possibly identical) page title for
  a link and some text to be displayed for the link,
  return the html for a link."
  [link-parts]
  (if (article-is-present? (:title-part link-parts))
    (get-view-link-for-existing-page link-parts)
    (get-creation-link-for-new-page link-parts)))

(defn do-one-substitution
  "Substitute an html-style link for a single
  wikilink and return the text with the substitution."
  [wikilink txt]
  (let [link-parts (get-wikilink-parts wikilink)
        html (link-parts->html-link link-parts)]
    (let [rt (s/replace txt wikilink html)]
      rt)))

(defn replace-wikilinks
  "Replace all wikilinks in the input text with html-style
  links and return the text with the replacements."
  [md-txt]
  (let [link-coll (find-wikilinks md-txt)]
    (loop [lc link-coll txt md-txt]
      (if (empty? lc)
        txt
        (recur (rest lc) (do-one-substitution (first lc) txt))))))

