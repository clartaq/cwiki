(ns cwiki.views.layout
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clojure.string :as s]
            [compojure.response :as response]
            [cwiki.models.db :as db]
            [cwiki.util.special :refer [is-special?]]
            [cwiki.util.wikilinks :refer [replace-wikilinks
                                          get-edit-link-for-existing-page
                                          get-delete-link-for-existing-page]]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :refer [form-to hidden-field submit-button text-area
                                 text-field password-field]]
            [hiccup.element :refer [link-to]])
  (:import (com.vladsch.flexmark.ext.gfm.strikethrough StrikethroughExtension)
           (com.vladsch.flexmark.ext.tables TablesExtension)
           (com.vladsch.flexmark.html HtmlRenderer HtmlRenderer$Builder)
           (com.vladsch.flexmark.parser Parser Parser$Builder
                                        ParserEmulationProfile)
           (com.vladsch.flexmark.util KeepType)
           (com.vladsch.flexmark.util.options MutableDataSet)
           (java.util ArrayList)))

(def program-name-and-version "CWiki v0.0.4-SNAPSHOT")

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

(defn- standard-head
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
         is-admin (= ":admin" (get-in req [:session :identity :user_role]))
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
       (when is-admin
         (menu-item-span [:a {:href "/admin"} "Admin"]))
       (menu-item-span [:a {:href "/logout"} "Sign Off"])
       (menu-item-span [:a {:href "/search"} "Search"])]])))

(defn- wiki-header-component
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

(defn- no-nav-header-component
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
  [& content]
  [:div
   (if content
     (let [txt-with-links (replace-wikilinks (first content))]
       (convert-markdown-to-html txt-with-links))
     [:p error-span "There is not centered content for this page."])])

(defn- footer-component
  "Return the standard footer for the program pages. If
  needed, retrieve the program name and version from the server."
  []
  [:footer {:class "footer"}
   [:div {:class "footer-wrapper"}
    [:p "Copyright \u00A9 2017, David D. Clark"]
    [:p program-name-and-version]]])

(defn- sidebar-aside
  [req]
  (let [sidebar-content (db/page-map->content (db/find-post-by-title "Sidebar"))]
    [:aside {:class "left-aside"}
     (limited-width-content-component sidebar-content req)]))

(defn- sidebar-and-article
  "Return a sidebar and article div with the given content."
  [sidebar article]
  [:div {:class "sidebar-and-article"}
   sidebar
   [:article {:class "page-content"}
    [:div article]]])

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
                     (hiccup.form/hidden-field :page-id id))
                   (text-field {:autofocus "autofocus"} "title" title)
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
          (limited-width-content-component content)])
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

;;
;; Pages that show no sidebar information.
;;

(defn- no-content-aside
  "Return an aside section with no content."
  []
  [:aside {:class "left-aside"} ""])

(defn compose-404-page
  "Build and return a 'Not Found' page."
  []
  (html5
    (standard-head nil)
    [:body {:class "page"}
     (no-nav-header-component)
     (sidebar-and-article
       (no-content-aside)
       (centered-content-component
         [:div
          [:h1 {:class "info-warning"} "Page Not Found"]
          [:p "The requested page does not exist."]
          (link-to {:class "btn btn-primary"} "/" "Take me Home")]))
     (footer-component)]))

(defn compose-403-page
  []
  (html5
    (standard-head nil)
    [:body {:class "page"}
     (no-nav-header-component)
     (sidebar-and-article
       (no-content-aside)
       (centered-content-component
         [:div
          [:h1 {:class "info-warning"} "403 - Forbidden"]
          [:p "You are not allowed to perform this action."]
          [:div {:class "button-bar-container"}
           [:input {:type    "button" :name "cancel-button"
                    :value   "Cancel"
                    :class   "topcoat-button--large"
                    :onclick "window.history.back();"}]]]))]))

(defn view-login-page
  "Display a login page and gather the user name and password to log in."
  []
  (html5
    (standard-head nil)
    [:body {:class "page"}
     (no-nav-header-component)
     (sidebar-and-article
       (no-content-aside)
       [:div
        (form-to {:enctype "multipart/form-data"}
                 [:post "login"]
                 [:h1 "Sign In"]
                 [:p "You must be logged in to use this wiki."]
                 [:h5 "User Name"]
                 [:p (text-field {:autofocus   "autofocus"
                                  :placeholder "User Name"} "user-name")]
                 [:h5 "Password"]
                 [:p (password-field "password")]
                 [:div {:class "button-bar-container"}
                  (submit-button {:id    "login-button"
                                  :class "topcoat-button--large"} "Sign In")])])
     (footer-component)]))

(defn no-user-to-logout-page
  "Display a page stating that there is no logged in user to sign out.
  Should never happen in production. Only used during development."
  []
  (html5
    (standard-head nil)
    [:body {:class "page"}
     (no-nav-header-component)
     (sidebar-and-article
       (no-content-aside)
       [:div
        [:h1 "That's a Problem"]
        [:p "There is no user to sign off."]
        [:div {:class "button-bar-container"}
         [:input {:type    "button" :name "cancel-button"
                  :value   "Cancel"
                  :class   "topcoat-button--large"
                  :onclick "window.history.back();"}]]])
     (footer-component)]))

(defn post-logout-page
  "Ask the user if they really want to log out."
  [user-name]
  (html5
    (standard-head nil)
    [:body {:class "page"}
     (no-nav-header-component)
     (sidebar-and-article
       (no-content-aside)
       [:div
        (form-to {:enctype "multipart/form-data"}
                 [:post "logout"]
                 [:h1 (str "Sign Out " user-name)]
                 [:p "Are you sure?"]
                 [:div {:class "button-bar-container"}
                  (submit-button {:id    "sign-out-button"
                                  :class "topcoat-button--large"} "Sign Out")
                  [:input {:type    "button" :name "cancel-button"
                           :value   "Cancel"
                           :class   "topcoat-button--large"
                           :onclick "window.history.back();"}]])])
     (footer-component)]))

(defn view-logout-page
  [{session :session}]
  (if-let [user-name (:user_name (:identity session))]
    (post-logout-page user-name)
    (no-user-to-logout-page)))

(defn compose-not-yet-view [name]
  (html5
    (standard-head nil)
    [:body {:class "page"}
     (no-nav-header-component)
     (sidebar-and-article
       (no-content-aside)
       [:div
        [:h1 "That's Not Ready"]
        [:p "There is no \"" name "\"route yet."]
        [:div {:class "button-bar-container"}
         [:input {:type    "button" :name "cancel-button"
                  :value   "Cancel"
                  :class   "topcoat-button--large"
                  :onclick "window.history.back();"}]]])
     (footer-component)]))
