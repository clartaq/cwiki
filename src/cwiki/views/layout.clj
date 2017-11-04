(ns cwiki.views.layout
  (:require [clj-time.format :as f]
            [clj-time.coerce :as c]
            [cwiki.util.special :refer [is-special?]]
            [cwiki.util.wikilinks :refer [replace-wikilinks
                                          get-edit-link-for-existing-page
                                          get-delete-link-for-existing-page]]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :refer [form-to hidden-field submit-button text-area
                                 text-field]]
            [hiccup.element :refer [link-to]]
            [clj-time.core :as t]
            [compojure.response :as response]
            [cwiki.models.db :as db])
  (:import (com.vladsch.flexmark.ext.gfm.strikethrough StrikethroughExtension)
           (com.vladsch.flexmark.html HtmlRenderer HtmlRenderer$Builder)
           (com.vladsch.flexmark.parser Parser Parser$Builder
                                        ParserEmulationProfile)
           (com.vladsch.flexmark.util.options MutableDataSet)
           (java.util ArrayList)))

(def program-name-and-version "CWiki v0.0.1")

;;------------------------------------------------------------------------------
;; Markdown translation functions.
;;------------------------------------------------------------------------------

(def options (.set (MutableDataSet.)
                   Parser/EXTENSIONS
                   (ArrayList. [(StrikethroughExtension/create)])))
(def parser (.build (Parser/builder options)))
(def renderer (.build (HtmlRenderer/builder options)))

(defn convert-markdown-to-html
  "Convert the markdown formatted input string to html
  and return it."
  [mkdn]
  (let [out (->> mkdn
                 (.parse parser)
                 (.render renderer))]
    out))

; Format a DateTime object nicely in the current time zone.
(def custom-formatter (f/with-zone
                        (f/formatter "dd MMM yyyy, hh:mm:ss aa")
                        (t/default-time-zone)))

(defn get-formatted-time
  "Return a string containing the input time (represented as a long)
  nicely formatted in the current time zone."
  [time-as-long]
  (f/unparse custom-formatter (c/from-long time-as-long)))

(defn get-tab-title
  "Return a string to be displayed in the browser tab."
  [post-map]
  (if-let [junk (and post-map
                     (:title post-map))]
    (str "CWiki: " (:title post-map))
    "Welcome to CWiki"))

(defn- menu-item-span
  "Return a span with CSS class 'menu-item' around the given content."
  [content]
  [:span {:class "menu-item"} content])

(defn wiki-hmenu-component
  "Return the standard menu component for the application."
  [post-map]
  (let [edit-link (and post-map (get-edit-link-for-existing-page post-map))
        delete-link (and post-map (get-delete-link-for-existing-page post-map))]
    [:nav {:class "hmenu"}
     [:p
      (when edit-link
        (menu-item-span edit-link))
      (when delete-link
        (menu-item-span delete-link))
      (menu-item-span [:a {:href "/"} "Home"])
      (when (and (db/db-exists?)
                 (db/find-post-by-title "About"))
        (menu-item-span [:a {:href "/about"} "About"]))
      (menu-item-span [:a {:href "/search"} "Search"])]]))

(defn wiki-header-component
  [post-map]
  [:header {:class "header"}
   [:div {:class "header-wrapper"}
    [:hgroup {:class "left-header-wrapper"}
     [:h1 {:class "brand-title"} "CWiki"]
     [:p {:class "brand-sub-title"}
      "A Simple " [:a {:href "https://en.wikipedia.org/wiki/Wiki"}
                   "Wiki"]]]
    (wiki-hmenu-component post-map)]])

; A span element with a bold, red "Error:" in it.
(def error-span [:span {:style {:color "red"}} [:strong "Error: "]])

(defn centered-content-component
  "Put the content in a centered element and return it."
  [& content]
  [:div {:class "centered-content"}
   (if content
     (first content)
     [:p error-span "There is not centered content for this page."])])

(defn limited-width-title-component
  [post-map]
  (let [title (:title post-map)
        author (:author post-map)
        created (:date post-map)
        modified (:modified post-map)]
    [:div {:class "page-title-div"}
     [:h1 {:class "page-title-header"} title]
     [:p {:class "author-line"}
      [:span {:class "author-header"} "Author: "] author]
     [:p {:class "date-line"}
      [:span {:class "date-header"} "Created: "]
      [:span {:class "date-text"} (get-formatted-time created) ", "]
      [:span {:class "date-header"} "Last Modified: "]
      [:span {:class "date-text"} (get-formatted-time modified)]]]))

(defn limited-width-content-component
  "Center the content in a centered element and return it."
  [& content]
  [:div
   (if content
     (let [txt-with-links (replace-wikilinks (first content))]
       (convert-markdown-to-html txt-with-links))
     [:p error-span "There is not centered content for this page."])])

(defn footer-component
  "Return the standard footer for the program pages. If
  needed, retrieve the program name and version from the server."
  []
  [:footer {:class "footer"}
   [:div {:class "footer-wrapper"}
    [:p "Copyright \u00A9 2017, David D. Clark"]
    [:p program-name-and-version]]])

(defn view-wiki-page
  [post-map]
  (let [content (:content post-map)
        title (:title post-map)]
    (html5
      [:head
       [:title (get-tab-title post-map)]
       (include-css "/css/styles.css")
       (include-js "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-AMS-MML_HTMLorMML")
       (include-js "/js/mathjax-config.js")
       (include-js "https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js")]
      [:body {:class "page"}
       (wiki-header-component post-map)
       [:div {:class "sidebar-and-article"}
        [:aside {:class "left-aside"}
         [:p "Here's the sidebar"]]
        [:article {:class "page-content"}
         (limited-width-title-component post-map)
         (limited-width-content-component content)]]
       (footer-component)])))

(defn compose-404-page
  "Build and return a 'Not Found' page."
  []
  (html5
    [:head
     [:title (get-tab-title nil)]
     (include-css "/css/styles.css")]
    [:body {:class "page"}
     (wiki-header-component nil)
     (centered-content-component
       [:div
        [:h1 {:class "info-warning"} "Page Not Found"]
        [:p "The requested page does not exist."]
        (link-to {:class "btn btn-primary"} "/" "Take me Home")])
     (footer-component)]))

(defn compose-create-or-edit-page
  [post-map]
  (let [id (:id post-map)
        title (:title post-map)
        content (:content post-map)]
    (println "title:" title)
    (html5
      [:head
       [:title (get-tab-title post-map)]
       (include-css "/css/styles.css")]
      [:body {:class "page"}
       (wiki-header-component post-map)
       (centered-content-component
         [:div
          (form-to {:enctype "multipart/form-data"}
                   (if id
                     [:post "save-edits"]
                     [:post "save-new-page"])
                   (when id
                     (hiccup.form/hidden-field :page-id id))
                   (text-field "title" title)
                   (text-area "content" content)
                   [:br]
                   [:div {:class "button-bar-container"}
                    (submit-button {:id    "Save Button"
                                    :class "topcoat-button--large"} "Save Changes")
                    [:input {:type    "button" :name "cancel-button"
                             :value   "Cancel"
                             :class   "topcoat-button--large"
                             :onclick "window.history.back();"}]])])
       (footer-component)])))

(defn compose-edit-page
  [post-map]
  (compose-create-or-edit-page post-map))

(defn compose-create-page
  [post-map]
  ;(let [pg (compose-create-or-edit-page post-map)]
  ;  (println "pg:" (pretty-print-html pg))
  ;  pg))

(let [title (:title post-map)
        content (:content post-map)]
    (html5
      [:head
       [:title (get-tab-title post-map)]
       (include-css "/css/styles.css")]
      [:body {:class "page"}
       (wiki-header-component post-map)
       (centered-content-component
         [:div
          (form-to {:enctype "multipart/form-data"}
                   [:post "/save-new-page"]
                   (text-field "title" title)
                   (text-area "content" content)
                   [:br]
                   [:div {:class "button-bar-container"}
                    (submit-button {:id    "Save Button"
                                    :class "topcoat-button--large"} "Save Changes")
                    [:input {:type    "button" :name "cancel-button"
                             :value   "Cancel"
                             :class   "topcoat-button--large"
                             :onclick "window.history.back();"}]])])
       (footer-component)])))
