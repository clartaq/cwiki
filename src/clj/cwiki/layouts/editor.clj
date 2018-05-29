;;;
;;; This namespace lays out a page for editing with by the ClojureScript
;;; editor. It also contains functions to respond to requests from the
;;; websocket routes.
;;;

(ns cwiki.layouts.editor
  (:require [cwiki.layouts.base :as base]
            [cwiki.models.wiki-db :as db]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer [submit-button]]
            [hiccup.page :refer [html5 include-css include-js]]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

(def debugging-css true)

(defn standard-head
  "Return the standard html head section for the wiki html pages. If the var
  'debugging-css' is def'ed to true, should reload CSS everytime the page
  loads."
  [post-map]
  (let [q (if debugging-css
            (str "?" (rand-int 2147483647))
            "")]
    [:head
     [:title (base/get-tab-title post-map)]
     (include-css "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/styles/default.min.css")
     (include-css (str "css/styles.css" q))
     (include-css (str "css/editor-styles.css" q))
     (include-css (str "css/mde.css" q))]))

(defn standard-end-of-body
  "Returns a div with the standard scripts to include in the page."
  []
  [:div {:class "standard-scripts"}
   (include-js "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.2/MathJax.js?config=TeX-AMS_SVG")
   (include-js "/js/mathjax-config.js")
   (include-js "/js/highlight.pack.js")
   (include-js "/js/marked.min.js")
   (include-js "js/compiled/cwiki-mde.js")
   [:script "window.addEventListener(\"DOMContentLoaded\", cwiki_mde.core.main());"]])

;(def ^{:private true} editable-content (atom nil))

(def ^{:private true} post-map-for-editing (atom nil))

(defn get-post-map-for-editing
  "Return a copy of the post map that can be used for editing."
  []
  @post-map-for-editing)

(defn get-content-for-websocket
  "Return a copy of the editable content."
  []
  (get-post-map-for-editing))

(defn update-content-for-websocket
  "Update the editable content with new content. Might be called on
  every keystroke."
  [new-post-map]
  (reset! post-map-for-editing new-post-map))

(defn sidebar-and-editor
  "Return a sidebar and div with the given content. This is just like the
  `sidebar-and-article` in the base layout file except that the content
  of the main section is not restricted in width."
  [sidebar article]
  [:div {:class "sidebar-and-article"}
   sidebar
   article])

(defn layout-editor-page
  "Create the base page layout and plug the content into it."
  [post-map req]
  (reset! post-map-for-editing post-map)
  (debugf "(get-post-map-for-editing): %s" (get-post-map-for-editing))
  ;(reset! editable-content (db/page-map->content @post-map-for-editing))
  (let [id (db/page-map->id @post-map-for-editing)
        ;   title (db/page-map->title @post-map-for-editing)
        ;content (db/page-map->content @copy-for-editing)
        tags (vec (db/get-tag-names-for-page id))
        ]
    (reset! post-map-for-editing (assoc @post-map-for-editing :tags tags))
    (debugf "tags: %s" tags)
    (debugf "@post-map-for-editing: %s" @post-map-for-editing)
    ;(debugf "layout-editor-page: id: %s, title: %s" id title)
    (html5
      {:ng-app "CWiki" :lang "en"}
      (standard-head (get-post-map-for-editing))
      [:body {:class "page-container"}
       (base/wiki-header-component post-map req {:editing true})
       (sidebar-and-editor
         (base/sidebar-aside req)
         [:section {:class "editor-section"}
          [:div {:id "editor-container" :class "editor-container"}
           ; This should get overwritten by the running ClojureScript editor.
           [:p "The stuff from the ClojureScript editor should show up here."]]])
       (base/footer-component)
       (standard-end-of-body)])))
