(ns cwiki.layouts.editor
  (:require [cwiki.models.wiki-db :as db]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer [submit-button]]
            [hiccup.page :refer [html5 include-css include-js]]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

;(defn embed-editor
;  "Return a piece of layout that will embed the MDE editor in the page."
;  []
;  [:section {:class "editor-section"}
;   [:div {:class "title-edit-section"}
;    [:p "Title Edit Section"]]
;   [:div {:class "tag-edit-secton"}
;    [:p "Tag Edit Section"]]
;   [:div {:id "editor-container" :class "editor-container"}
;    ; This should get overwritten by the running ClojureScript editor.
;    [:p "The stuff from the ClojureScript editor should show up here."]]
;   [:div {:class "button-bar-container"}
;    [:input {:type    "button"
;             :id      "Save Button"
;             :name    "save-button"
;             :value   "Save Changes"
;             :class   "form-button button-bar-item"
;             :onclick "console.log(\"Saw click on save button!\");"}]
;    [:input {:type    "button"
;             :id      "Cancel Button"
;             :name    "cancel-button"
;             :value   "Cancel"
;             :class   "form-button button-bar-item"
;             :onclick "window.history.back();"}]]])

; Try to force changes in CSS to be re-loaded each time that browser
; page is reloaded.
(def debugging-css true)

(defn standard-head
  "Return the standard html head section for the wiki html pages. If the var
  'debugging-css' is def'ed to true, should reload CSS everytime the page
  loads."
  [title]
  (let [q (if debugging-css
            (str "?" (rand-int 2147483647))
            "")]
    [:head
     [:title title]
     (include-css "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/styles/default.min.css")
     (include-css (str "css/styles.css" q))
     (include-css (str "css/editor-styles.css" q))
     (include-css (str "css/mde.css" q))]))

(defn standard-end-of-body
  "Returns a div with the standard scripts to include in the page."
  []
  [:div {:class "standard-scripts"}
   (include-js "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/highlight.min.js")
   (include-js "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/languages/clojure.min.js")
   (include-js "//cdnjs.cloudflare.com/ajax/libs/marked/0.3.19/marked.min.js")
   (include-js "js/compiled/cwiki-mde.js")
   [:script "window.addEventListener(\"DOMContentLoaded\", cwiki_mde.core.main());"]])

;[post-map req]
;(println "mde-template")
;(let [id (db/page-map->id post-map)
;      title (db/page-map->title post-map)
;      content (db/page-map->content post-map)
;      tags (db/get-tag-names-for-page id)]
;
;  (editor-layout/layout-editor title content)))

(defn layout-editor-page
  "Create the base page layout and plug the content into it."
  [post-map req]
  (let [id (db/page-map->id post-map)
        title (db/page-map->title post-map)
        content (db/page-map->content post-map)
        tags (db/get-tag-names-for-page id)]
    (infof "layout-editor-page: id: %s, title: %s" id title)
    (html5
      {:ng-app "MDE Test" :lang "en"}
      (standard-head title)
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
         [:div {:class "button-bar-container"}
          [:input {:type    "button"
                   :id      "Save Button"
                   :name    "save-button"
                   :value   "Save Changes"
                   :class   "form-button button-bar-item"
                   :onclick "console.log(\"Saw click on save button!\");"}]
          [:input {:type    "button"
                   :id      "Cancel Button"
                   :name    "cancel-button"
                   :value   "Cancel"
                   :class   "form-button button-bar-item"
                   :onclick "window.history.back();"}]]]]
       [:footer {:class "test-footer"}
        [:p "This is a fixed footer, just like the page where I intend to use
       the editor."]]]
      (standard-end-of-body))))
