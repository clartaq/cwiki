;;;
;;; This namespace contains definitions for basic layouts used
;;; in the application. It also contains the program name and
;;; version. It controls the type of Markdown understood by the
;;; application.

(ns cwiki.layouts.base
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clojure.string :as s]
            [compojure.response :as response]
            [cwiki.models.db :as db]
            [cwiki.util.authorization :as ath]
            [cwiki.util.req-info :as ri]
            [cwiki.util.wikilinks :refer [replace-wikilinks
                                          get-edit-link-for-existing-page
                                          get-delete-link-for-existing-page]]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :refer [drop-down email-field form-to hidden-field
                                 password-field select-options
                                 submit-button text-area text-field]]
            [hiccup.element :refer [link-to]])
  (:import (com.vladsch.flexmark.ext.gfm.strikethrough StrikethroughExtension)
           (com.vladsch.flexmark.ext.tables TablesExtension)
           (com.vladsch.flexmark.html HtmlRenderer HtmlRenderer$Builder)
           (com.vladsch.flexmark.parser Parser Parser$Builder
                                        ParserEmulationProfile)
           (com.vladsch.flexmark.util KeepType)
           (com.vladsch.flexmark.util.options MutableDataSet)
           (java.util ArrayList)))

(def program-name-and-version "CWiki v0.0.6-SNAPSHOT")

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

(defn- convert-markdown-to-html
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

(defn- get-formatted-time
  "Return a string containing the input time (represented as a long)
  nicely formatted in the current time zone."
  [time-as-long]
  (f/unparse custom-formatter (c/from-long time-as-long)))

(defn- get-tab-title
  "Return a string to be displayed in the browser tab."
  [post-map]
  (if-let [junk (and post-map
                     (db/page-map->title post-map))]
    (str "CWiki: " (db/page-map->title post-map))
    "CWiki"))

(defn standard-head
  "Return the standard html head section for the wiki html pages."
  [post-map]
  [:head
   [:title (get-tab-title post-map)]
   (include-css "/css/styles.css")
   (include-js "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-AMS-MML_HTMLorMML")
   (include-js "/js/mathjax-config.js")
   (include-js "https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js")])

(defn- menu-item-span
  "Return a span with CSS class 'menu-item' around the given content."
  [content]
  [:span {:class "menu-item"} content])

(defn- wiki-hmenu-component
  "Return the standard navigation menu component for the application.
  The options argument can make the menu context specific, as for when
  editing or not (the only option now)."
  ([post-map req]
   (wiki-hmenu-component post-map req {}))
  ([post-map req options]
   (let [allow-editing (not (:editing options))
         title (db/page-map->title post-map)
         can-edit-and-delete (ath/can-edit-and-delete? req title)
         edit-link (and post-map
                        allow-editing
                        can-edit-and-delete
                        (get-edit-link-for-existing-page post-map req))
         delete-link (and post-map
                          allow-editing
                          can-edit-and-delete
                          (get-delete-link-for-existing-page post-map req))]
     [:nav {:class "hmenu"}
      [:p
       (when (ath/can-create? req)
         (menu-item-span [:a {:href "/New Page/create"} "New"]))
       (when edit-link
         (menu-item-span edit-link))
       (when delete-link
         (menu-item-span delete-link))
       (menu-item-span [:a {:href "/"} "Home"])
       (when (and (db/db-exists?)
                  (db/find-post-by-title "About"))
         (menu-item-span [:a {:href "/about"} "About"]))
       (when (ri/is-admin-user? req)
         (menu-item-span [:a {:href "/Admin"} "Admin"]))
       (menu-item-span [:a {:href "/logout"} "Sign Off"])
       (menu-item-span [:a {:href "/search"} "Search"])]])))

(defn wiki-header-component
  "Return the standard wiki page header."
  ([post-map req]
   (wiki-header-component post-map req {}))
  ([post-map req options]
   [:header {:class "header"}
    [:div {:class "header-wrapper"}
     [:hgroup {:class "left-header-wrapper"}
      [:h1 {:class "brand-title"} "CWiki"]
      [:p {:class "brand-sub-title"}
       "A Simple " [:a {:href "https://en.wikipedia.org/wiki/Wiki"}
                    "Wiki"]]]
     (wiki-hmenu-component post-map req options)]]))

(defn no-nav-header-component
  "Return the wiki page header without the nav menu items."
  []
  [:header {:class "header"}
   [:div {:class "header-wrapper"}
    [:hgroup {:class "left-header-wrapper"}
     [:h1 {:class "brand-title"} "CWiki"]
     [:p {:class "brand-sub-title"}
      "A Simple " [:a {:href "https://en.wikipedia.org/wiki/Wiki"}
                   "Wiki"]]]]])

; A span element with a bold, red "Error:" in it.
(def error-span [:span {:style {:color "red"}} [:strong "Error: "]])

; A span element with a bold, red "Warning:" at the beginning.
(def warning-span [:span {:style {:color "red"}} [:strong "Warning: "]])

(def required-field-hint [:p {:class "required-field-hint"} "Required fields are marked with a"])

(defn- centered-content-component
  "Put the content in a centered element and return it."
  [& content]
  [:div {:class "centered-content"}
   (if content
     (first content)
     [:p error-span "There is not centered content for this page."])])

(defn- limited-width-title-component
  [post-map]
  (let [title (db/page-map->title post-map)
        author (db/page-map->author post-map)
        created (db/page-map->created-date post-map)
        modified (db/page-map->modified-date post-map)]
    [:div {:class "page-title-div"}
     [:h1 {:class "page-title-header"} title]
     [:p {:class "author-line"}
      [:span {:class "author-header"} "Author: "] author]
     [:p {:class "date-line"}
      [:span {:class "date-header"} "Created: "]
      [:span {:class "date-text"} (get-formatted-time created) ", "]
      [:span {:class "date-header"} "Last Modified: "]
      [:span {:class "date-text"} (get-formatted-time modified)]]]))

(defn- limited-width-content-component
  "Center the content in a centered element and return it."
  [req & content]
  [:div
   (if content
     (let [txt-with-links (replace-wikilinks (first content) req)]
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

(defn- no-content-aside
  "Return an aside section with no content."
  []
  [:aside {:class "left-aside"} ""])

(defn- sidebar-aside
  "Return an aside with the content of the sidebar page."
  [req]
  (let [sidebar-content (db/page-map->content (db/find-post-by-title "Sidebar"))]
    [:aside {:class "left-aside"}
     (limited-width-content-component req sidebar-content)]))

(defn- sidebar-and-article
  "Return a sidebar and article div with the given content."
  [sidebar article]
  [:div {:class "sidebar-and-article"}
   sidebar
   [:article {:class "page-content"}
    [:div article]]])

;;
;; Pages that show no sidebar information.
;;

(defn short-form-template
  "A page template for short messages, no sidebar content, no nav."
  [content]
  (html5
    (standard-head nil)
    [:body {:class "page"}
     (no-nav-header-component)
     (sidebar-and-article
       (no-content-aside)
       content)
     (footer-component)]))

(defn short-message
  "Return a page with a title, message and 'Ok' button."
  [title message]
  (short-form-template
  [:div {:class "cwiki-form"}
   [:p {:class "form-title"} title]
   [:p message]
   [:div {:class "button-bar-container"}
    [:input {:type    "button" :name "ok-button"
             :value   "Ok"
             :class   "form-button"
             :onclick "window.history.back();"}]]]))

(defn compose-not-yet-view
  "Return a page stating that the requested feature
  is not available yet."
  [name]
  (short-message "That's Not Ready"
                 (str "There is no \"" name \"" route yet.")))

(defn compose-404-page
  "Return a 'Not Found' page."
  []
  (short-message "Page Not Found" "The page requested does not exist."))

(defn compose-403-page
  "Return a page stating that the requested action is forbidden (403)."
  []
  (short-message "Forbidden" "You are not allowed to perform that action."))

;;
;; Functions related to viewing or editing wiki pages.
;;

(defn compose-create-or-edit-page
  "Will compose a page to create or edit a page in the wiki. The
  difference is based on whether or not the post-map passed as
  argument has a nil entry for the :post_id key in the map -- nil causes
  creation, non-nil is an edit."
  [post-map req]
  (let [id (db/page-map->id post-map)
        title (db/page-map->title post-map)
        content (db/page-map->content post-map)
        t-title (get-tab-title post-map)
        tab-title (if id
                    (str "Editing " t-title)
                    (str "Creating " t-title))]
    (html5
      (standard-head post-map)
      [:body {:class "page"}
       (wiki-header-component post-map req {:editing true})
       (sidebar-and-article
         (sidebar-aside req)
         [:div
          (form-to {:enctype "multipart/form-data"}
                   (if id
                     [:post "save-edits"]
                     [:post "save-new-page"])
                   (when id
                     (hidden-field :page-id id))
                   (text-field {:autofocus "autofocus"} "title" title)
                   (text-area "content" content)
                   [:br]
                   [:div {:class "button-bar-container"}
                    (submit-button {:id    "Save Button"
                                    :class "form-button button-bar-item"} "Save Changes")
                    [:input {:type    "button" :name "cancel-button"
                             :value   "Cancel"
                             :class   "form-button button-bar-item"
                             :onclick "window.history.back();"}]])])
       (footer-component)])))

(defn view-wiki-page
  "Return a 'regular' wiki page view."
  [post-map req]
  (let [content (db/page-map->content post-map)]
    (html5
      (standard-head post-map)
      [:body {:class "page"}
       (wiki-header-component post-map req)
       (sidebar-and-article
         (sidebar-aside req)
         [:div (limited-width-title-component post-map)
          (limited-width-content-component req content)])
       (footer-component)])))

;;
;; Pages and utilities that show all there are of something, like
;; page names or users.
;;

(defn- process-title-set
  "Process a sorted set of page titles into a Markdown-formatted
  unordered list and return it"
  [titles]
  (loop [t titles
         st ""]
    (if (empty? t)
      (str st "\n")
      (recur (rest t) (str st "\n- [[" (first t) "]]")))))

(defn- process-name-set
  "Process a sorted set of names into a Markdown-formatted
  unordered list and return it. If the set of names is empty,
  return an empty string."
  [names]
  (if (zero? (count names))
    ""
    (loop [t names
           st ""]
      (if (empty? t)
        (str st "\n")
        (recur (rest t) (str st "\n- " (first t)))))))

(defn compose-all-pages-page
  "Return a page listing all of the pages in the wiki."
  [req]
  (let [all-pages-query (db/get-all-page-names)
        processed-titles (process-title-set all-pages-query)
        content (s/join ["Pages:\n"
                         processed-titles])
        post-map (db/create-new-post-map "All Pages" content)]
    (view-wiki-page post-map req)))

(defn compose-all-users-page
  "Return a page listing all of the users known to the wiki."
  [req]
  (let [all-users-query (db/get-all-users)
        processed-names (process-name-set all-users-query)
        content (s/join ["Users:\n"
                         processed-names])
        post-map (db/create-new-post-map "All Users" content)]
    (view-wiki-page post-map req)))

(defn compose-all-namespaces-page
  "Return a page listing of all of the namespaces in the wiki."
  [req]
  (let [all-namespaces-query (db/get-all-namespaces)
        processed-names (process-name-set all-namespaces-query)
        content (s/join ["Namespaces:\n"
                         processed-names])
        post-map (db/create-new-post-map "All Namespaces" content)]
    (view-wiki-page post-map req)))

(defn compose-all-tags-page
  "Return a page listing all of the tags in the wiki."
  [req]
  (let [all-tags-query (db/get-all-tags)
        processed-names (process-name-set all-tags-query)
        page-start (if (zero? (count processed-names))
                     "Tags: None"
                     "Tags:")
        content (s/join [page-start
                         processed-names])
        post-map (db/create-new-post-map "All Tags" content)]
    (view-wiki-page post-map req)))

