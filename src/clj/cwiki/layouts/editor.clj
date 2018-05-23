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

(def ^{:private true} editable-content (atom nil))

(def ^{:private true} post-map-for-editing (atom nil))

(defn get-post-map-for-editing
  "Return a copy of the post map that can be used for editing."
  []
  @post-map-for-editing)

(defn get-content-for-websocket
  "Return a copy of the editable content."
  []
  @editable-content)

(defn update-content-for-websocket
  "Update the editable content with new content. Might be called on
  every keystroke."
  [new-content]
  (reset! editable-content new-content))

(defn layout-editor-page
  "Create the base page layout and plug the content into it."
  [post-map req]
  (reset! post-map-for-editing post-map)
  (reset! editable-content (db/page-map->content @post-map-for-editing))
  (let [id (db/page-map->id @post-map-for-editing)
        title (db/page-map->title @post-map-for-editing)
        ;content (db/page-map->content @copy-for-editing)
        tags (db/get-tag-names-for-page id)
        ]
    (debugf "layout-editor-page: id: %s, title: %s" id title)
    (html5
      {:ng-app "CWiki" :lang "en"}
      (standard-head (get-post-map-for-editing))
      [:body {:class "page-container"}
       [:header {:class "test-header"}
        [:h1 "MDE Test"]
        [:p "This program provides a test fixture for the MDE Markdown editor."]]
       [:main {:class "main-area"}
        [:aside {:class "sidebar"}
         [:p "This is a sidebar. Just here because I want the test page to
       look similar to the page where I intend to use the editor."]]
        [:section {:class "editor-section"}
         [:div {:class "title-edit-section"}
          [:p "Title Edit Section"]]
         [:div {:class "tag-edit-secton"}
          [:p "Tag Edit Section"]]
         [:div {:id "editor-container" :class "editor-container"}
          ; This should get overwritten by the running ClojureScript editor.
          [:p "The stuff from the ClojureScript editor should show up here."]]
         ;[:div {:class "button-bar-container"}
         ; [:input {:type    "button"
         ;          :id      "Save Button"
         ;          :name    "save-button"
         ;          :value   "Save Changes"
         ;          :class   "form-button button-bar-item"
         ;          :onclick "console.log(\"Saw click on save button!\");"}]
         ; [:input {:type    "button"
         ;          :id      "Cancel Button"
         ;          :name    "cancel-button"
         ;          :value   "Cancel"
         ;          :class   "form-button button-bar-item"
         ;          :onclick "window.history.back();"}]]
         ]]
       (base/footer-component)]
      (standard-end-of-body))))
