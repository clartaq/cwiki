(ns cwiki.util.wikilinks
  (:require [cemerick.url :as u]
            [clojure.string :as s]
            [cwiki.models.wiki-db :as db]
            [cwiki.util.special :as special]
            [hiccup.core :as hc]
            [hiccup.element :refer [link-to]]
            [cwiki.util.req-info :as ri]))

; A regular expression to match MediaWiki-style internal
; wikilinks.
(def pattern #"\[\[.+?\]\]")

; The style used for "normal" links.
(def ok-to-link-style "ok-to-link-style")
; The style used for links to non-existent pages. Triggers page creation.
(def non-existent-link-style "color:red")
; This is the style used for disabled links in wikilinks.clj
(def disabled-link-style "pointer-events:none;cursor:default;color:lightgray;")

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

(defn get-edit-link-for-existing-page
  "Return a link to be used with a button or menu."
  [post-map req]
  (let [page-title (db/page-map->title post-map)]
    (when (special/is-editable? page-title)
      (let [uri (u/url-encode (str page-title "/edit"))
            h (hc/html (link-to uri "Edit"))]
        h))))

(defn get-delete-link-for-existing-page
  "Return a link to be used with a button or menu. If the page
  is special and cannot be deleted, return nil."
  [post-map req]
  (let [page-title (db/page-map->title post-map)]
    (when (special/is-deletable? page-title)
      (let [uri (u/url-encode (str page-title "/delete"))
            h (hc/html (link-to uri "Delete"))]
        h))))

(defn- get-view-link-for-existing-page
  "Return a link to be displayed in an existing page. As such, it
  may have separate display and link parts that must be handled."
  [link-parts req]
  (let [title (:title-part link-parts)
        is-admin-only (special/is-admin-only? title)
        is-admin-user (ri/is-admin-user? req)
        ok-to-link (or is-admin-user
                       (not is-admin-only))
        style-to-use (if ok-to-link
                       ok-to-link-style
                       disabled-link-style)
        uri (u/url-encode title)
        h (hc/html (link-to {:style style-to-use}
                            uri (:display-part link-parts)))]
    h))

(defn- get-creation-link-for-new-page
  "Return a link to a non-existent page to be display in a page.
  As such, it may have separate display and link parts that must
  be handled."
  [link-parts req]
  (let [page-title (:title-part link-parts)
        is-reader-user (ri/is-reader-user? req)
        ok-to-link (not is-reader-user)
        style-to-use (if ok-to-link
                       non-existent-link-style
                       disabled-link-style)
        uri (u/url-encode (str page-title))
        h (hc/html (link-to {:style style-to-use}
                            uri (:display-part link-parts)))]
    h))

(defn- as-tag?
  "Handle the special case when a link points to a tag. Used to build
  the All Tags page."
  [title]
  (s/ends-with? title "/as-tag"))

(defn link-parts->html-link
  "Given a map containing a (possibly identical) page title for
  a link and some text to be displayed for the link,
  return the html for a link."
  [link-parts req]
  (let [title (:title-part link-parts)]
    (if (or (as-tag? title)
            (special/is-generated? title)
            (article-is-present? title))
      (get-view-link-for-existing-page link-parts req)
      (get-creation-link-for-new-page link-parts req))))

(defn- do-one-substitution
  "Substitute an html-style link for a single
  wikilink and return the text with the substitution."
  [wikilink txt req]
  (let [link-parts (get-wikilink-parts wikilink)
        html (link-parts->html-link link-parts req)]
    (let [rt (s/replace txt wikilink html)]
      rt)))

(defn replace-wikilinks
  "Replace all wikilinks in the input text with html-style
  links and return the text with the replacements."
  [md-txt req]
  (let [link-coll (find-wikilinks md-txt)]
    (loop [lc link-coll txt md-txt]
      (if (empty? lc)
        txt
        (recur (rest lc) (do-one-substitution (first lc) txt req))))))

