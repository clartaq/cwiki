(ns cwiki.views.layout
  (:require [clj-time.format :as f]
            [clj-time.coerce :as c]
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

(defn wiki-hmenu-component
  "Return the standard menu component for the application."
  [post-map]
  (let [edit-link (and post-map (get-edit-link-for-existing-page post-map))
        delete-link (and post-map (get-delete-link-for-existing-page post-map))]
    [:menu {:class "hmenu"}
     [:p {:class "menu-item"}
      (when edit-link
        (str edit-link " "))
      (when delete-link
        (str delete-link " "))
      [:a {:href "/"} "Home"] " "
      [:a {:href "/about"} "About"] " "
      [:a {:href "/search"} "Search"]]]))

(defn wiki-header-component
  [post-map]
  [:header {:class "header"}
   [:div {:class "header-wrapper"}
    [:div {:class "left-header-wrapper"}
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
  [:content {:class "centered-content"}
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
  (let [content (:content post-map)]
    (html5
      [:head
       [:title "Welcome to CWiki"]
       (include-css "/css/styles.css")
       (include-js "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-AMS-MML_HTMLorMML")
       (include-js "/js/mathjax-config.js")
       (include-js "https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js")]
      [:body {:class "page"}
       (wiki-header-component post-map)
       [:content {:class "centered-content"}
        (limited-width-title-component post-map)
        (limited-width-content-component content)]
       (footer-component)])))

(defn compose-404-page
  "Build and return a 'Not Found' page."
  []
  (html5
    [:head
     [:title "Welcome to CWiki"]
     (include-css "/css/styles.css")]
    [:body {:class "page"}
     (wiki-header-component nil)
     (centered-content-component
       [:div
        [:h1 {:class "info-warning"} "Page Not Found"]
        [:p "The requested page does not exist."]
        (link-to {:class "btn btn-primary"} "/" "Take me Home")])
     (footer-component)]))

(defn compose-edit-page
  [post-map]
  (let [id (:id post-map)
        title (:title post-map)
        content (:content post-map)]
    (html5
      [:head
       [:title "Welcome to CWiki"]
       (include-css "/css/styles.css")]
      [:body {:class "page"}
       (wiki-header-component post-map)
       (centered-content-component
         [:div
          (form-to {:enctype "multipart/form-data"}
                   [:post "save-edits"]
                   (hidden-field :page-id id)
                   (text-field "title" title)

                   (text-area "content" content)
                   [:br]
                   (submit-button {:id id} "Save Changes")
                   " "
                   (submit-button {:id id} "Cancel")
                   " "
                   (link-to {:class "btn btn-primary"} "/" "Take me Home"))])
       (footer-component)])))

(defn compose-create-page
  [post-map]
  (let [id (:id post-map)
        title (:title post-map)
        content (:content post-map)]
    (html5
      [:head
       [:title "Welcome to CWiki"]
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
                   (submit-button {:id "Save Button"} "Save Changes")
                   " "
                   (submit-button {:id "Cancel Button"} "Cancel")
                   " "
                   (link-to {:class "btn btn-primary"} "/" "Take me Home"))])
       (footer-component)])))
