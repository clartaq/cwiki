;;;
;;; This namespace contains definitions for basic layouts used
;;; in the application. It also contains the program name and
;;; version. It controls the type of Markdown understood by the
;;; application.

(ns cwiki.layouts.base
  (:require [cemerick.url :as u]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clojure.string :as s]
            [cwiki.models.wiki-db :as db]
            [cwiki.util.authorization :as ath]
            [cwiki.util.files :refer [is-seed-page?]]
            [cwiki.util.req-info :as ri]
            [cwiki.util.special :as special]
            [environ.core :refer [env]]
            [hiccup.core :as hc]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer [form-to hidden-field submit-button text-area
                                 text-field]]
            [hiccup.page :refer [html5 include-css include-js]])
  (:import (cwiki.extensions CWikiLinkAttributeExtension)
           (cwiki.extensions CWikiLinkResolverExtension)
           (com.vladsch.flexmark.ext.gfm.strikethrough StrikethroughExtension)
           (com.vladsch.flexmark.ext.tables TablesExtension)
           (com.vladsch.flexmark.ext.footnotes FootnoteExtension)
           (com.vladsch.flexmark.ext.wikilink WikiLinkExtension)
           (com.vladsch.flexmark.html HtmlRenderer HtmlRenderer$Builder)
           (com.vladsch.flexmark.parser Parser Parser$Builder)
           (com.vladsch.flexmark.util KeepType)
           (com.vladsch.flexmark.util.options MutableDataSet)
           (java.net URL URLDecoder)
           (java.util ArrayList)))

(def program-name-and-version "CWiki v0.1.3-SNAPSHOT")

;;------------------------------------------------------------------------------
;; Markdown translation functions.
;;------------------------------------------------------------------------------

(def options (-> (MutableDataSet.)
                 (.set Parser/REFERENCES_KEEP KeepType/LAST)
                 (.set HtmlRenderer/INDENT_SIZE (Integer/valueOf 2))
                 (.set HtmlRenderer/PERCENT_ENCODE_URLS true)
                 (.set TablesExtension/COLUMN_SPANS false)
                 (.set TablesExtension/MIN_HEADER_ROWS (Integer/valueOf 1))
                 (.set TablesExtension/MAX_HEADER_ROWS (Integer/valueOf 1))
                 (.set TablesExtension/APPEND_MISSING_COLUMNS true)
                 (.set TablesExtension/DISCARD_EXTRA_COLUMNS true)
                 (.set TablesExtension/WITH_CAPTION false)
                 (.set TablesExtension/HEADER_SEPARATOR_COLUMN_MATCH true)
                 (.set WikiLinkExtension/LINK_FIRST_SYNTAX true)
                 (.set WikiLinkExtension/LINK_ESCAPE_CHARS "")
                 (.set Parser/EXTENSIONS (ArrayList.
                                           [(FootnoteExtension/create)
                                            (StrikethroughExtension/create)
                                            ; Order is important here.
                                            ; Our custom link resolver must
                                            ; preceed the default resolver.
                                            (CWikiLinkResolverExtension/create)
                                            (WikiLinkExtension/create)
                                            (CWikiLinkAttributeExtension/create)
                                            (TablesExtension/create)]))))

(def parser (.build ^Parser$Builder (Parser/builder options)))
(def renderer (.build ^HtmlRenderer$Builder (HtmlRenderer/builder options)))

(defn- convert-markdown-to-html
  "Convert the markdown formatted input string to html
  and return it."
  [mkdn]
  (->> mkdn
       (.parse parser)
       (.render renderer)))

; Format a DateTime object nicely in the current time zone.
(def custom-formatter (f/with-zone
                        (f/formatter "dd MMM yyyy, hh:mm:ss aa")
                        (t/default-time-zone)))

(defn- get-formatted-time
  "Return a string containing the input time (represented as a long)
  nicely formatted in the current time zone."
  [time-as-long]
  (f/unparse custom-formatter (c/from-long time-as-long)))

(defn get-tab-title
  "Return a string to be displayed in the browser tab."
  [post-map]
  (if (and post-map
           (db/page-map->title post-map))
    (str "CWiki: " (db/page-map->title post-map))
    "CWiki"))

(defn get-edit-link-for-page
  "Return a link to be used with a button or menu."
  [post-map req]
  (let [page-title (db/page-map->title post-map)]
    (when (special/is-editable? page-title)
      (let [uri (str (u/url-encode page-title) "?edit=true")
            h (hc/html (link-to uri "Edit"))]
        h))))

(defn get-delete-link-for-existing-page
  "Return a link to be used with a button or menu. If the page
  is special and cannot be deleted, return nil."
  [post-map req]
  (let [page-title (db/page-map->title post-map)]
    (when (special/is-deletable? page-title)
      (let [uri (str (u/url-encode page-title) "?delete=true")
            h (hc/html (link-to uri "Delete"))]
        h))))

(defn get-development-css-path
  "Return the path to the development css file."
  []
  (let [debugging-css (env :debugging-css)
        q (if debugging-css
            (str "?" (rand-int 2147483647))
            "")
        cssp (str "/css/styles.css" q)]
    cssp))

(defn get-production-css-path
  "Return the path to the minimized, production css file."
  []
  "/css/styles.min.css")

(defn standard-head
  "Return the standard html head section for the wiki html pages. If the var
  'debugging-css' is def'ed to true, should reload CSS every time the page
  loads. which-highlighter must be one of :no-highlighter, (for forms and such)
  :editor-highlighter (for the highlighter used in the editor preview pane)
  or :page-highlighter for all page views for reading."
  [post-map which-highlighter]
  [:head
   [:title (get-tab-title post-map)]
   [:link {:rel "shortcut icon" :href "/img/favicon/favicon.ico"}]
   [:link {:rel "apple-touch-icon" :sizes "180x180" :href "/img/favicon/apple-touch-icon.png"}]
   [:link {:rel "icon" :type "image/png" :sizes "32x32" :href "/img/favicon/favicon-32x32.png"}]
   [:link {:rel "icon" :type "image/png" :sizes "16x16" :href "/img/favicon/favicon-16x16.png"}]
   [:link {:rel "manifest" :href "/img/favicon/site.webmanifest"}]
   [:link {:rel "mask-icon" :href "/img/favicon/safari-pinned-tab.svg" :color "#5bbad5"}]
   [:meta {:name "msapplication-TileColor" :content "#da532c"}]
   [:meta {:name "msapplication-config" :content "/img/favicon/browserconfig.xml"}]
   [:meta {:name "theme-color" :content "#ffffff"}]
   (if (or (= (env :profile-type) "development")
           (= (env :profile-type) "test"))
     (include-css (get-development-css-path))
     (include-css (get-production-css-path)))
   (when (= which-highlighter :editor-highlighter)
     (include-css "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/styles/default.min.css"))
   (when (= which-highlighter :page-highlighter)
     (include-css (str "/js/styles/default.css")))])        ;)

(defn standard-end-of-body
  "Returns a div with the standard scripts to include in the page."
  []
  [:div {:class "standard-scripts"}
   (include-js "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.2/MathJax.js?config=TeX-AMS_SVG")
   (include-js "/js/mathjax-config.js")
   (include-js "/js/highlight.pack.js")])

(defn- drop-menu
  "Return the drop-down menu for use in the page header."
  [req post-map options]
  (let [page-title (db/page-map->title post-map)]
    [:div {:class "menu-item"}
     [:ul
      [:li {:class "subNav"}
       [:a "More  ▾"]
       [:ul
        (when (db/find-post-by-title "About")
          [:li [:a {:href "/About"} "About"]])
        (when-not (ri/is-reader-user? req)
          [:li [:a {:href "/import"} "Import"]])
        (when-not (or (special/is-generated? page-title)
                      (:editing options))
          [:li [:a {:href "/export"} "Export"]])
        [:li [:a {:href "/export-all"} "Export All"]]
        (when (and (ri/is-admin-user? req)
                   (is-seed-page? (:page_title post-map)))
          [:li [:a {:href "/save-seed-page"} "Save Seed"]])
        (when (ri/is-admin-user? req)
          [:li [:a {:href "/Admin"} "Admin"]])
        [:li [:a {:href "/logout"} "Sign Out"]]]]]]))

(defn- searchbox
  "Return the search box element for use in the page header."
  []
  [:div {:class "search-container"}
   [:form {:id      "searchbox" :action "/search" :method "post"
           :enctype "multipart/form-data"}
    [:input {:type        "text" :id "search-text" :name "search-text"
             :aria-label  "Search" :class "searchbox"
             :placeholder "Enter search terms ..."}]]])

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
         new-link (and post-map
                       allow-editing
                       (ath/can-create? req)
                       [:a {:href "/New Page"} "New"])
         can-edit-and-delete (ath/can-edit-and-delete? req title)
         edit-link (and post-map
                        allow-editing
                        can-edit-and-delete
                        (get-edit-link-for-page post-map req))
         delete-link (and post-map
                          allow-editing
                          can-edit-and-delete
                          (get-delete-link-for-existing-page post-map req))]
     [:nav {:class "hmenu"}
      (when new-link
        (menu-item-span new-link))
      (when edit-link
        (menu-item-span edit-link))
      (when delete-link
        (menu-item-span delete-link))
      (menu-item-span [:a {:href "/"} "Home"])
      (drop-menu req post-map options)
      (searchbox)])))

(defn wiki-header-component
  "Return the standard wiki page header."
  ([post-map req]
   (wiki-header-component post-map req {}))
  ([post-map req options]
   [:header {:class "page-header"}
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
  [:header {:class "page-header"}
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

(defn- limited-width-title-component
  [post-map]
  (let [title (db/page-map->title post-map)
        author (db/page-map->author post-map)
        tags (db/convert-seq-to-comma-separated-string
               (db/get-tag-names-for-page (db/page-map->id post-map)))
        tag-str (if (seq tags)
                  tags
                  "None")
        created (db/page-map->created-date post-map)
        modified (db/page-map->modified-date post-map)]
    [:div {:class "page-title-div"}
     [:h1 {:class "page-title-header"} title]
     [:p {:class "author-line"}
      [:span {:class "author-header"} "Author: "] author]
     [:p {:class "tag-line"}
      [:span {:class "tag-header"} "Tags: "]
      [:span {:class "tag-text"} tag-str]]
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
     (convert-markdown-to-html (first content))
     [:p error-span "There is not centered content for this page."])])

(defn footer-component
  "Return the standard footer for the program pages. If
  needed, retrieve the program name and version from the server."
  []
  [:footer {:class "footer"}
   [:div {:class "footer-wrapper"}
    [:p "Copyright \u00A9 2017-2018, David D. Clark"]
    [:p program-name-and-version]]])

(defn- no-content-aside
  "Return an aside section with no content."
  []
  [:aside {:class "left-aside"} ""])

(defn sidebar-aside
  "Return an aside with the content of the sidebar page."
  [req]
  (let [sidebar-content (db/page-map->content (db/find-post-by-title "Sidebar"))]
    [:aside {:class "left-aside"}
     (limited-width-content-component req sidebar-content)]))

(defn sidebar-and-article
  "Return a sidebar and article div with the given content."
  [sidebar article]
  [:div {:class "sidebar-and-article"}
   sidebar
   [:article {:class "page-content"}
    article]])

;;
;; Pages that show no sidebar information.
;;

(defn short-form-template
  "A page template for short messages, no sidebar content, no nav."
  [content]
  (html5
    {:lang "en"}
    (standard-head nil :no-highlighter)
    [:body {:class "page"}
     (no-nav-header-component)
     (sidebar-and-article
       (no-content-aside)
       content)
     (standard-end-of-body)]))

(defn short-message
  "Return a page with a title, message and 'Ok' button."
  [title message]
  (short-form-template
    [:div {:class "cwiki-form"}
     [:p {:class "form-title"} title]
     [:p message]
     [:div {:class "button-bar-container"}
      [:input {:type      "button" :name "ok-button"
               :value     "Ok"
               :class     "form-button"
               :autofocus "autofocus"
               :onclick   "window.history.back();"}]]]))

(defn short-message-return-to-referer
  "Return a page with a title, message and 'Ok' button. After the
  user clicks the button, go to the page given by referer argument."
  [title message referer]
  (short-form-template
    [:div {:class "cwiki-form"}
     [:p {:class "form-title"} title]
     [:p message]
     [:div {:class "button-bar-container"}
      [:input {:type      "button"
               :name      "ok-button"
               :value     "Ok"
               :class     "form-button"
               :autofocus "autofocus"
               :onclick   (str "window.location.replace('" referer "');")}]]]))

(defn inform-admin-of-first-use
  "Return a page with a title, message and 'Ok' button.
  The button press will take the user to the login page."
  [title message]
  (short-form-template
    [:div {:class "cwiki-form"}
     [:p {:class "form-title"} title]
     [:div message]
     [:div {:class "button-bar-container"}
      [:input {:type      "button" :name "ok-button"
               :value     "Ok"
               :class     "form-button"
               :autofocus "autofocus"
               :onclick   "window.location.href='/login'"}]]]))

(defn compose-not-yet-view
  "Return a page stating that the requested feature
  is not available yet."
  [the-name]
  (short-message "That's Not Ready"
                 (str "There is no \"" the-name \" " route yet.")))

(defn compose-404-page
  "Return a 'Not Found' page."
  []
  (short-message "Page Not Found" "The page requested does not exist."))

(defn compose-403-page
  "Return a page stating that the requested action is forbidden (403)."
  []
  (short-message "Forbidden" "You are not allowed to perform that action."))

;;
;; Import/Export related pages.
;;

(defn no-files-to-import-page
  "Create a page stating that there are no files to import."
  [referer]
  (short-message-return-to-referer
    "Nothing to Do" "There are no files to import." referer))

(defn confirm-import-page
  "Return a page stating that the file has been imported."
  [file-name title referer]
  (short-message-return-to-referer
    "Import Complete"
    (str "File \"" file-name "\" has been imported as \"" title "\".") referer))

(defn compose-import-existing-page-warning
  "Return a page stating that a page with the same title
  already exists in the wik."
  [import-map file-name referer]
  (short-form-template
    [:div {:class "cwiki-form"}
     (form-to {:enctype      "multipart/form-data"
               :autocomplete "off"}
              [:post "proceed-with-import"]
              (hidden-field "import-map" import-map)
              (hidden-field "file-name" file-name)
              (hidden-field "referer" referer)
              [:p {:class "form-title"} "Page Already Exists"]
              [:div {:class "form-group"}
               [:p (str "A page with the title \"" (get-in import-map [:meta :title])
                        "\" already exists in the wiki.")]
               [:p (str "Click \"Proceed\" to delete the existing page and "
                        "replace it with the contents of the imported file.")]
               [:div {:class "button-bar-container"}
                (submit-button {:id    "proceed-with-import-button"
                                :class "form-button button-bar-item"}
                               "Proceed")
                [:input {:type      "button" :name "cancel-button"
                         :value     "Cancel"
                         :class     "form-button button-bar-item"
                         :autofocus "autofocus"
                         :onclick   "window.history.back();"}]]])]))

(defn compose-import-file-page
  "Compose and return a page that allows the user to choose a file to import."
  [req]
  (short-form-template
    [:div {:class "cwiki-form"}
     (form-to {:enctype      "multipart/form-data"
               :autocomplete "off"}
              [:post "import"]
              (hidden-field "referer" (get (:headers req) "referer"))
              [:p {:class "form-title"} "Import a File"]
              [:div {:class "form-group"}
               [:div {:class "form-label-div"}
                [:label {:class "form-label"
                         :for   "filename"} "Select the file to Import"]]
               [:p "First select a file to import, then press the \"Import\" button."]
               [:label
                [:input {:type   "file"
                         :id     "file-info"
                         :name   "file-info"
                         :accept ".txt,.md"}]]]
              [:div {:class "button-bar-container"}
               (submit-button {:id    "import-button"
                               :class "form-button button-bar-item"}
                              "Import")
               [:input {:type      "button" :name "cancel-button"
                        :value     "Cancel"
                        :class     "form-button button-bar-item"
                        :autofocus "autofocus"
                        :onclick   "window.history.back();"}]])]))

(defn confirm-export-page
  "Return a page stating that the file has been exported."
  [page-name file-name referer]
  (short-message-return-to-referer
    "Export Complete"
    (str "Page \"" page-name "\" has been exported to \"" file-name "\".") referer))

(defn compose-export-file-page
  "Compose and return a page that allows the user to choose a directory
  to export a page to."
  [req]
  (let [referer (get (:headers req) "referer")
        ; First figure out if they are trying to export the Front Page or
        ; a 'regular' page.
        file-name (.getFile (URL. referer))
        page-title (if (= "/" file-name)
                     "Front Page"
                     (let [snip (.substring ^String referer
                                            (inc (s/last-index-of referer "/")))]
                       (URLDecoder/decode snip "UTF-8")))
        page-id (db/title->page-id page-title)]
    (if (nil? page-id)
      (short-message-return-to-referer
        "Page Name Translation Error"
        (str "There was a problem getting the page name from the referring URL: \""
             referer "\".")
        referer)
      (short-form-template
        [:div {:class "cwiki-form"}
         (form-to {:enctype      "multipart/form-data"
                   :autocomplete "off"}
                  [:post "export"]
                  (hidden-field "page-id" page-id)
                  (hidden-field "referer" referer)
                  [:p {:class "form-title"} "Export a Page"]
                  [:div {:class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label"
                             :for   "filename"} (str "Export page \"" page-title "\"?")]]]
                  [:div {:class "button-bar-container"}
                   (submit-button {:id    "export-button"
                                   :class "form-button button-bar-item"}
                                  "Export")
                   [:input {:type      "button" :name "cancel-button"
                            :value     "Cancel"
                            :class     "form-button button-bar-item"
                            :autofocus "autofocus"
                            :onclick   "window.history.back();"}]])]))))

(defn confirm-export-all-pages
  "Return a page stating that the file has been exported."
  [dir-name referer]
  (short-message-return-to-referer
    "Export Complete"
    (str "All pages have been exported to the directory " dir-name ".") referer))

(defn compose-export-all-pages
  [req]
  (let [referer (get (:headers req) "referer")]
    (short-form-template
      [:div {:class "cwiki-form"}
       (form-to {:enctype      "multipart/form-data"
                 :autocomplete "off"}
                [:post "export-all"]
                (hidden-field "referer" referer)
                [:p {:class "form-title"} "Export All Pages"]
                [:div {:class "form-group"}
                 [:div {:class "form-label-div"}
                  [:label {:class "form-label"
                           :for   "filename"} "Export all pages?"]]]
                [:div {:class "button-bar-container"}
                 (submit-button {:id    "export-all-button"
                                 :class "form-button button-bar-item"}
                                "Export All")
                 [:input {:type      "button" :name "cancel-button"
                          :value     "Cancel"
                          :class     "form-button button-bar-item"
                          :autofocus "autofocus"
                          :onclick   "window.history.back();"}]])])))

;;
;; Functions related to viewing or editing wiki pages.
;;

(defn view-wiki-page
  "Return a 'regular' wiki page view."
  [post-map req]
  (let [content (db/page-map->content post-map)]
    (html5
      {:lang "en"}
      (standard-head post-map :page-highlighter)
      [:body {:class "page"}
       (wiki-header-component post-map req)
       (sidebar-and-article
         (sidebar-aside req)
         [:div (limited-width-title-component post-map)
          (limited-width-content-component req content)])
       (standard-end-of-body)]
      (include-js "/js/onload.js"))))

(defn view-list-page
  "Return a one or two column layout of list items. Number
  of columns depends on number of items."
  [post-map query-results req]
  (let [content (db/page-map->content post-map)
        class-to-use (if (> (count query-results) 10)
                       "two-column-list"
                       "one-column-list")]
    (html5
      {:lang "en"}
      (standard-head post-map :no-highlighter)
      [:body {:class "page"}
       (wiki-header-component post-map req)
       (sidebar-and-article
         (sidebar-aside req)
         [:div (limited-width-title-component post-map)
          [:div {:class class-to-use}
           (limited-width-content-component req content)]])
       (standard-end-of-body)])))

;;
;; Pages and utilities that show all there are of something, like
;; page names or users.
;;

(defn- process-item-set-to-list-of-wikilinks
  "Process a set of items into a Markdown-formatted list of items and return it."
  [items uri-and-query]
  (if (zero? (count items))
    ""
    (let [leadin-str (str "\n- [[" uri-and-query)]
      (loop [t items
             sb (StringBuilder.)]
        (if (empty? t)
          (-> sb
              (.append "\n")
              (.toString))
          (let [item (first t)]
            (recur (rest t) (-> sb
                                (.append leadin-str)
                                (.append item)
                                (.append "|")
                                (.append item)
                                (.append "]]")))))))))

(defn- process-title-set
  "Process a sorted set of page titles into a Markdown-formatted
  unordered list and return it"
  [titles]
  ; This builds bigger links than strictly necessary since it duplicates
  ; the page name in the link, but it uses a well-tested function to do it.
  (process-item-set-to-list-of-wikilinks titles ""))

(defn- process-tag-set
  "Process a sorted set of tag names into a Markdown-formatted
  unordered list and return it. Individual tag names are placed
  in links that will generate a list of all pages containing
  the tag."
  [tags]
  (process-item-set-to-list-of-wikilinks tags "/as-tag?tag="))

(defn- process-name-set
  "Process a sorted set of names into a Markdown-formatted
  unordered list and return it. If the set of names is empty,
  return an empty string."
  [names]
  (process-item-set-to-list-of-wikilinks names "/as-user?user="))

(defn compose-all-pages-page
  "Return a page listing all of the pages in the wiki."
  [req]
  (let [query-results (db/get-all-page-names)
        content (process-title-set query-results)
        post-map (db/create-new-post-map "All Pages" content)]
    (view-list-page post-map query-results req)))

(defn compose-search-results-page
  "Return a page listing the page titles in the search results as links."
  [search-results req]
  (let [titles (reduce #(conj %1 (:title %2)) [] search-results)
        content (process-title-set titles)
        post-map (db/create-new-post-map "Search Results" content)]
    (view-list-page post-map titles req)))

(defn compose-all-users-page
  "Return a page listing all of the users known to the wiki."
  [req]
  (let [query-results (db/get-all-users)
        content (process-name-set query-results)
        post-map (db/create-new-post-map "All Users" content)]
    (view-list-page post-map query-results req)))

(defn compose-all-pages-with-user
  "Return a page listing the titles of all of the pages attributed to the user."
  [user-name req]
  (let [query-results (db/get-titles-of-all-pages-with-user user-name)
        content (process-title-set query-results)
        post-map (db/create-new-post-map (str "All Pages Attributed to User \""
                                              user-name "\"")
                                         content)]
    (view-list-page post-map query-results req)))

(defn compose-all-tags-page
  "Return a page listing all of the tags in the wiki."
  [req]
  (let [query-results (db/get-all-tag-names)
        content (process-tag-set query-results)
        post-map (db/create-new-post-map "All Tags" content)]
    (view-list-page post-map query-results req)))

(defn compose-all-pages-with-tag
  "Return a page listing all of the pages with the tag."
  [tag req]
  (let [query-results (db/get-titles-of-all-pages-with-tag tag)
        content (process-title-set query-results)
        post-map (db/create-new-post-map
                   (str "All Pages with Tag \"" tag "\"") content)]
    (view-list-page post-map query-results req)))

;;
;; Pages and utilities that allow the user to change option/preference settings.
;;

(defn compose-get-options-age
  [req]
  (let [delay (db/get-option-value :editor_autosave_interval)]
    (short-form-template
      [:div {:class "cwiki-form"}
       (form-to {:enctype      "multipart/form-data"
                 :autocomplete "off"}
                [:post "preferences"]
                (hidden-field "referer" (get (:headers req) "referer"))
                [:p {:class "form-title"} "Change Preferences"]
                [:div {:class "form-group"}
                 [:div {:class "form-label-div"}
                  [:label {:class "form-label"
                           :for   "autosave-interval"}
                   "Autosave Interval (seconds)"]]
                 (text-field {:class       "form-text-field"
                              :autofocus   "autofocus"
                              :placeholder "Enter the autosave interval in seconds"
                              :value       (str delay)}
                             "autosave-interval")
                 [:p {:class "hint-field"}
                  "Enter an integer representing the number of seconds after
                  the last keypress before saving the edits to a document. The
                  default value of zero (0) indicates that there should not be
                  any automatic saving."]
                 [:p {:class "hint-field"}
                  "A setting of zero is recommended if you want to retain the
                  ability to revert to the unaltered version of a page. Once an
                  autosave occurs, you cannot easily recover the unaltered page."]]
                [:div {:class "button-bar-container"}
                 (submit-button {:id    "save-options-button"
                                 :class "form-button button-bar-item"} "Save")
                 [:input {:type    "button" :name "cancel-button"
                          :value   "Cancel"
                          :class   "form-button button-bar-item"
                          :onclick "window.history.back();"}]])])))

(defn confirm-saved-options
  "Return a page stating that the preferences have been saved."
  [referer]
  (short-message-return-to-referer
    "Preferences Saved"
    "All changes to the preferences have been saved." referer))
