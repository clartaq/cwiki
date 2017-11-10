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
            [cwiki.models.db :as db]
            [clojure.string :as s])
  (:import (com.vladsch.flexmark.ext.gfm.strikethrough StrikethroughExtension)
           (com.vladsch.flexmark.ext.tables TablesExtension)
           (com.vladsch.flexmark.html HtmlRenderer HtmlRenderer$Builder)
           (com.vladsch.flexmark.parser Parser Parser$Builder
                                        ParserEmulationProfile)
           (com.vladsch.flexmark.util.options MutableDataSet)
           (java.util ArrayList)
           (com.vladsch.flexmark.util KeepType)))

(def program-name-and-version "CWiki v0.0.3-SNAPSHOT")

;;------------------------------------------------------------------------------
;; Markdown translation functions.
;;------------------------------------------------------------------------------

(def options (-> (MutableDataSet.)
                 (.set Parser/REFERENCES_KEEP KeepType/LAST)
                 (.set HtmlRenderer/INDENT_SIZE (Integer. 2))
                 (.set HtmlRenderer/PERCENT_ENCODE_URLS true)
                 (.set TablesExtension/COLUMN_SPANS false)
                 (.set TablesExtension/MIN_HEADER_ROWS (Integer. 1))
                 (.set TablesExtension/MAX_HEADER_ROWS (Integer. 1))
                 (.set TablesExtension/APPEND_MISSING_COLUMNS true)
                 (.set TablesExtension/DISCARD_EXTRA_COLUMNS true)
                 (.set TablesExtension/WITH_CAPTION false)
                 (.set TablesExtension/HEADER_SEPARATOR_COLUMN_MATCH true)
                 (.set Parser/EXTENSIONS (ArrayList.
                                           [(StrikethroughExtension/create)
                                            (TablesExtension/create)]))))

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
  ([post-map]
   (wiki-hmenu-component post-map {}))
  ([post-map options]
   (let [allow-editing (not (:editing options))
         edit-link (and post-map
                        allow-editing
                        (get-edit-link-for-existing-page post-map))
         delete-link (and post-map
                          allow-editing
                          (get-delete-link-for-existing-page post-map))]
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
       (menu-item-span [:a {:href "/search"} "Search"])]])))

(defn wiki-header-component
  ([post-map]
   (wiki-header-component post-map {}))
  ([post-map options]
   [:header {:class "header"}
    [:div {:class "header-wrapper"}
     [:hgroup {:class "left-header-wrapper"}
      [:h1 {:class "brand-title"} "CWiki"]
      [:p {:class "brand-sub-title"}
       "A Simple " [:a {:href "https://en.wikipedia.org/wiki/Wiki"}
                    "Wiki"]]]
     (wiki-hmenu-component post-map options)]]))

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
        sidebar-content (:content (db/find-post-by-title "Sidebar"))]
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
         (limited-width-content-component sidebar-content)]
        [:article {:class "page-content"}
         (limited-width-title-component post-map)
         (limited-width-content-component content)]]
       (footer-component)])))

(defn process-title-set
  "Process a sorted set of page titles into a Markdown-formatted
  unordered list and return it"
  [titles]
  (loop [t titles
         st ""]
    (if (empty? t)
      (str st "\n")
      (recur (rest t) (str st "\n- [[" (first t) "]]")))))

(defn compose-all-pages-page
  "Return a page listing all of the pages in the wiki."
  []
  (let [all-pages-query (db/get-all-page-names)
        processed-titles (process-title-set all-pages-query)
        content (s/join ["All Pages Content:\n"
                         processed-titles])
        post-map (db/create-new-post-map "All Pages" content)]
    (view-wiki-page post-map)))

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
  "Will compose a page to create or edit a page in the wiki. The
  difference is based on whether or not the post-map passed as
  argument has a nil entry for the :id key in the map -- nil causes
  creation, non-nil is an edit."
  [post-map]
  (let [id (:id post-map)
        title (:title post-map)
        content (:content post-map)
        sidebar-content (:content (db/find-post-by-title "Sidebar"))
        t-title (get-tab-title post-map)
        tab-title (if id
                    (str "Editing " t-title)
                    (str "Creating " t-title))]
    (html5
      [:head
       [:title tab-title]
       (include-css "/css/styles.css")]
      [:body {:class "page"}
       (wiki-header-component post-map {:editing true})
       [:div {:class "sidebar-and-article"}
        [:aside {:class "left-aside"}
         (limited-width-content-component sidebar-content)]
        [:article {:class "page-content"}
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
                             :onclick "window.history.back();"}]])]]]
       (footer-component)])))

