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
  ;@editable-content
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
  (reset! editable-content (db/page-map->content @post-map-for-editing))
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
          ;[:div {:class "title-edit-section"}
          ; [:p "Title Edit Section"]]
          ;[:div {:class "tag-edit-secton"}
          ; [:p "Tag Edit Section"]]
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
          ])
       (base/footer-component)
       (standard-end-of-body)])))

#_(defn- tag-editor-list-component
    [tags]
    (let [tv (vec tags)]
      [:div {:class "tag-edit-container"}
       [:label {:class "tag-edit-label"} "Tags"]
       [:div {:class "tag-edit-tag-list"}
        (text-field {:class "tag-text-field"} "tag0" (nth tv 0 ""))
        (text-field {:class "tag-text-field"} "tag1" (nth tv 1 ""))
        (text-field {:class "tag-text-field"} "tag2" (nth tv 2 ""))
        (text-field {:class "tag-text-field"} "tag3" (nth tv 3 ""))
        (text-field {:class "tag-text-field"} "tag4" (nth tv 4 ""))
        (text-field {:class "tag-text-field"} "tag5" (nth tv 5 ""))
        (text-field {:class "tag-text-field"} "tag6" (nth tv 6 ""))
        (text-field {:class "tag-text-field"} "tag7" (nth tv 7 ""))
        (text-field {:class "tag-text-field"} "tag8" (nth tv 8 ""))
        (text-field {:class "tag-text-field"} "tag9" (nth tv 9 ""))]]))

#_(defn compose-create-or-edit-page
    "Will compose a page to create or edit a page in the wiki. The
    difference is based on whether or not the post-map passed as
    argument has a nil entry for the :post_id key in the map -- nil causes
    creation, non-nil is an edit."
    [post-map req]
    (let [id (db/page-map->id post-map)
          title (db/page-map->title post-map)
          content (db/page-map->content post-map)
          tags (db/get-tag-names-for-page id)]
      (html5
        {:lang "en"}
        (standard-head post-map)
        [:body {:class "page"}
         (wiki-header-component post-map req {:editing true})
         (sidebar-and-article
           (sidebar-aside req)
           [:div {:class "editor-container"}
            (form-to {:enctype "multipart/form-data" :class "editor-form"}
                     (if id
                       [:post "save-edits"]
                       [:post "save-new-page"])
                     (when id
                       (hidden-field :page-id id))
                     [:div {:class "form-group"}
                      [:div {:class "form-label-div"}
                       [:label {:class "form-label required"
                                :for   "title"} "Page Title"]]
                      (text-field {:class     "form-title-field"
                                   :autofocus "autofocus"} "title" title)]
                     (tag-editor-list-component tags)
                     ; KEEP THIS FOR NOW
                     ;[:div {:class "form-group"}
                     ; [:div {:class "form-label-div"}
                     ;  [:label {:class "form-label"
                     ;           :for   "tags"} "Tags"]]
                     ; [:input {:type "submit" :id "new-tag-button"}]]
                     [:div {:class "form-group"}
                      [:div {:class "form-label-div"}
                       [:label {:class "form-label"
                                :for   "content"} "Page Content"]]
                      (text-area {:class "form-text-area"} "content" content)]
                     [:div {:class "button-bar-container"}
                      (submit-button {:id    "Save Button"
                                      :class "form-button button-bar-item"}
                                     "Save Changes")
                      [:input {:type    "button" :name "cancel-button"
                               :value   "Cancel"
                               :class   "form-button button-bar-item"
                               :onclick "window.history.back();"}]])])
         (footer-component)])))

