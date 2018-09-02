;;;
;;; This namespace provides two classes used to extend the flexmark wikilink
;;; extension -- WikiLinkAttributeProvider and WikiLinkAttributeExtension.
;;; These two classes are intended to replace the ad hoc methods used to
;;; parse wikilinks in Markdown files. They have the advantage that
;;; wikilinks inside of code blocks and preformatted text is not converted
;;; to a link because we are using a parset that actually parses the
;;; Markdown file rather than searching through the file for matching
;;; regular expressions.
;;;
;;; The class compilation is annoyingly touchy. Also, Leiningen seems to
;;; have some issues with figuring out that it needs to compile this file
;;; at all, so a :prep-tasks task is included in the project file.
;;;

(ns cwiki.util.wikilink-attributes
  (:require [clojure.string :as s]
            [cwiki.models.wiki-db :as db]
            [cwiki.util.req-info :as ri]
            [cwiki.util.special :refer [is-special?]]
            [cwiki.util.special :as special])
  (:import (com.vladsch.flexmark.ast Node)
           (com.vladsch.flexmark.ext.wikilink WikiLink)
           (com.vladsch.flexmark.html AttributeProviderFactory
                                      HtmlRenderer$Builder
                                      IndependentAttributeProviderFactory)
           (com.vladsch.flexmark.html.renderer AttributablePart
                                               LinkResolverContext)
           (com.vladsch.flexmark.util.html Attributes)
           (com.vladsch.flexmark.util.options MutableDataHolder)))

;-------------------------------------------------------------------------------
; Private data and helper functions.
;-------------------------------------------------------------------------------

; The style used for "normal" links.
(def ok-to-link-style nil)                                  ;"color:green;")
; The style used for links to non-existent pages. Triggers page creation.
(def non-existent-link-style "color:red;")
; This is the style used for disabled links in wikilinks.clj
(def disabled-link-style "pointer-events:none;cursor:default;color:lightgray;")

(defn- article-is-present?
  "Return true if an article with the given title
  exists in the database."
  [article-name]
  (not (nil? (db/find-post-by-title article-name))))

(defn- get-link-style-for-existing-page
  "Return a link to be displayed in an existing page. As such, it
  may have separate display and link parts that must be handled."
  [page-title req]
  (let [is-admin-only (special/is-admin-only? page-title)
        is-admin-user (ri/is-admin-user? req)
        ok-to-link (or is-admin-user
                       (not is-admin-only))
        style-to-use (if ok-to-link
                       ok-to-link-style
                       disabled-link-style)]
    style-to-use))

(defn- get-link-style-for-new-page
  "Return a link to a non-existent page to be display in a page.
  As such, it may have separate display and link parts that must
  be handled."
  [page-title req]
  (let [is-reader-user (ri/is-reader-user? req)
        ok-to-link (not is-reader-user)
        style-to-use (if ok-to-link
                       non-existent-link-style
                       disabled-link-style)]
    style-to-use))

(defn- as-tag?
  "Handle the special case when a link points to a tag. Used to build
  the All Tags page."
  [title]
  (s/ends-with? title "/as-tag"))

(defn- as-user?
  "Handle the special case when a link points to a user. Used to build
  the All Users page."
  [title]
  (s/ends-with? title "/as-user"))

(defn- title->link-style
  "Given a map containing a (possibly identical) page title for
  a link and some text to be displayed for the link,
  return the html for a link."
  [title req]
  (if (or (as-tag? title)
          (as-user? title)
          (special/is-generated? title)
          (article-is-present? title))
    (get-link-style-for-existing-page title req)
    (get-link-style-for-new-page title req)))

;-------------------------------------------------------------------------------
; The WikiLinkAttributeProvider is responsible for styling the links
; depending on whether the page exists (normal link), doesn't exist (red link),
; and whether the user is even authorized to edit/create a new page (disabled
; link, depending on user permissions.)
;-------------------------------------------------------------------------------

(gen-class
  :name cwiki.util.WikiLinkAttributeProvider
  :implements [com.vladsch.flexmark.html.AttributeProvider]
  :methods [^{:static true} [Factory [] com.vladsch.flexmark.html.AttributeProviderFactory]])

(defn -setAttributes
  "Style the wikilink dependent on the user role and page existence."
  [this ^Node node ^AttributablePart part ^Attributes attributes]
  (when (= (type node) WikiLink)
    (let [title (str (.getLink ^WikiLink node))
          style-to-use (title->link-style title (ri/retrieve-session-info))]
      (when style-to-use
        (.replaceValue attributes "style" style-to-use)))))

(defn ^AttributeProviderFactory -Factory []
  (proxy [IndependentAttributeProviderFactory] []
    (create [^LinkResolverContext context]
      ;; noinspection ReturnOfInnerClass
      (cwiki.util.WikiLinkAttributeProvider.))))

;-------------------------------------------------------------------------------
; The WikiLinkAttributeExtension provides the glue that plugs the
; WikiLinkProvider into the flexmark wikilink extension.
;-------------------------------------------------------------------------------

; First provide a forward declaration.
(gen-class
  :name cwiki.util.WikiLinkAttributeExtension
  :implements [com.vladsch.flexmark.html.HtmlRenderer$HtmlRendererExtension])

; Then expand it with the declaration of the method that returns an
; instance of the class.
(gen-class
  :name cwiki.util.WikiLinkAttributeExtension
  :implements [com.vladsch.flexmark.html.HtmlRenderer$HtmlRendererExtension]
  :methods [^{:static true} [create [] cwiki.util.WikiLinkAttributeExtension]])

(defn -rendererOptions
  [this ^MutableDataHolder options]
  ; Add any configuration option that you want to apply to everything here.
  )

(defn -extend
  [this ^HtmlRenderer$Builder rendererBuilder ^String rendererType]
  (.attributeProviderFactory rendererBuilder (cwiki.util.WikiLinkAttributeProvider/Factory)))

(defn ^cwiki.util.WikiLinkAttributeExtension -create []
  (cwiki.util.WikiLinkAttributeExtension.))