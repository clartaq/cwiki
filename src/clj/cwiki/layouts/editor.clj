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
            [ring.middleware.anti-forgery :as anti-forgery]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

(defn standard-end-of-body
  "Returns a div with the standard scripts to include in the page."
  []
  [:div {:class "standard-scripts"}
   (include-js "/js/mathjax-config.js")
   (include-js "/js/highlight.pack.js")
   (include-js "/js/marked-4.0.18-min.js")
   (include-js "/js/compiled/cwiki-mde.js")
   [:script "window.addEventListener(\"DOMContentLoaded\", cwiki_mde.core.main());"]])

(def ^{:private true} post-map-for-editing (atom nil))

(defn get-post-map-for-editing
  "Return a copy of the post map that can be used for editing."
  []
  @post-map-for-editing)

(defn update-content-for-websocket
  "Update the editable content with new content. Might be called on
  every keystroke."
  [new-post-map]
  (debugf "update-content-for-websocket: new-post-map: %s" new-post-map)
  (reset! post-map-for-editing new-post-map))

(defn sidebar-and-editor
  "Return a sidebar and div with the given content. This is just like the
  `sidebar-and-article` in the base layout file except that the content
  of the main section is not restricted in width."
  [sidebar article]
  [:div {:class "sidebar-and-article"}
   sidebar
   [:div {:class "vertical-page-divider"}]
   [:div {:class       "vertical-page-splitter"
          :id          "splitter"
          ; Don't forget to translate the hyphen to an underscore. The false
          ; return is required for correct behavior on Safari.
          :onmousedown "cwiki_mde.dragger.onclick_handler(); return false;"}]
   article])

(defn get-markdown-help-html
  "Return the content of the 'Markdown Help' page as Markdown for use by the
  editor. The title of the help page is hardcoded here."
  []
  (when-let [page-id (db/title->page-id "Markdown Help")]
    (db/page-id->content page-id)))

(defn layout-editor-page
  "Create the base page layout and plug the content into it."
  [post-map req]
  (reset! post-map-for-editing post-map)
  (let [id (db/page-map->id @post-map-for-editing)
        tags (vec (db/get-tag-names-for-page id))
        options (db/get-option-map)
        help-html (get-markdown-help-html)
        csrf-token (force anti-forgery/*anti-forgery-token*)]
    ;; The tags and options are attached to the page map here.
    (swap! post-map-for-editing assoc :tags tags)
    (swap! post-map-for-editing assoc :options options)
    (swap! post-map-for-editing assoc :markdown-help help-html)
    (html5
      {:ng-app "CWiki" :lang "en"}
      (base/standard-head (get-post-map-for-editing) :editor-highlighter)
      [:body {:class "page"}
       [:div#sente-csrf-token {:data-csrf-token csrf-token}]
       (base/wiki-header-component post-map req {:editing true})
       (sidebar-and-editor
         (base/sidebar-aside)
         [:section {:id "outer-editor-container" :class "outer-editor-container"}
          [:p "One moment please. Loading the editor."]])
       (standard-end-of-body)])))
