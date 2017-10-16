(ns cwiki.views.layout
  (:require [clj-time.format :as f]
            [clj-time.coerce :as c]
            [cwiki.util.wikilinks :refer [replace-wikilinks]]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form]
            [hiccup.element :refer [link-to]]
            [clj-time.core :as t])
  (:import (com.vladsch.flexmark.ext.gfm.strikethrough StrikethroughExtension)
           (com.vladsch.flexmark.html HtmlRenderer HtmlRenderer$Builder)
           (com.vladsch.flexmark.parser Parser Parser$Builder
                                        ParserEmulationProfile)
           (com.vladsch.flexmark.util.options MutableDataSet)
           (java.util ArrayList)))

(def program-name-and-version "cwiki v0.1.0")

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

(defn hmenu-component
  "Return the standard menu component for the application."
  []
  [:menu {:class "hmenu"}
   [:p {:class "menu-item"}
    [:a {:href "/"} "Home"] " "
    [:a {:href "/about"} "About"] " "
    [:a {:href "/subscriptions"} "Subscriptions"] " "
    [:a {:href "/now"} "Now"] " "
    [:a {:href "/stats"} "Stats"] " "
    [:a {:href "/config"} "Config"]]])

(defn header-component
  "Return the standard page header for the application."
  []
  [:header {:class "header"}
   [:div {:class "header-wrapper"}
    [:div {:class "left-header-wrapper"}
     [:h1 {:class "brand-title"} "cwiki"]
     [:p {:class "brand-sub-title"}
      "A Simple " [:a {:href "https://en.wikipedia.org/wiki/Wiki"}
                   "Wiki"]]]
    (hmenu-component)]])

; A span element with a bold, red "Error:" in it.
(def error-span [:span {:style {:color "red"}} [:strong "Error: "]])

(defn content-component
  "Put the content in and return it."
  [& content]
  [:content {:class "content"}
   (if content
     (first content)
     [:p error-span "There is no content for this page."])])

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
    [:content {:class "centered-content"}
      [:h1 title]
      [:p {:class "author-line"} [:span {:class "author-header"} "Author: "] author]
      [:p [:span {:class "date-header"} "Created: "]
       [:span {:class "date-text"} (get-formatted-time created) ", "]
       [:span {:class "date-header"} "Last Modified: "]
       [:span {:class "date-text"} (get-formatted-time modified)]]]))

(defn limited-width-content-component
  "Center the content in a centered element and return it."
  [& content]
  [:content {:class "centered-content"}
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

(defonce unique-key (atom 0))
(defn gen-key
  "Generate a unique key for use with list items and things that
  require them."
  []
  (swap! unique-key inc))

(defn map->two-col-list
  "A component that creates a two column list from a single-level map."
  [m]
  (fn []
    (let [key-column [:ul.mp-list
                      (for [[k v] m]
                        ^{:key (gen-key)} [:li.mp-key-list-item (str k)])]
          val-column [:ul.mp-list
                      (for [[k v] m]
                        ^{:key (gen-key)} [:li.mp-val-list-item (if v
                                                                  (str v)
                                                                  " ")])]]
      [:div.map-pair key-column val-column])))

(defn single-layer-map-component
  "Return a component consisting of a two columns showing the
  keys and values in the input map (only one level allowed, no
  nesting). Nil values are blank. Will blow up with nested lists."
  [m]
  (map->two-col-list m))

(defn compose-page
  "Compose a standard page with the given content and
  return it."
  [content]
  (html5
    [:head
     [:title "Welcome to cwiki"]
     (include-css "/css/styles.css")]
    [:body {:class "page"}
     (header-component)
     (content-component content)
     (footer-component)]))

(defn compose-centered-page
  "Compose a standard page with the given content
  centered and return it."
  [content]
  (html5
    [:head
     [:title "Welcome to cwiki"]
     (include-css "/css/styles.css")]
    [:body {:class "page"}
     (header-component)
     (centered-content-component content)
     (footer-component)]))

(defn view-wiki-page
  [post-map]
  (let [content (:content post-map)]
    (html5
      [:head
       [:title "Welcome to cwiki"]
       (include-css "/css/styles.css")
       (include-js "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-AMS-MML_HTMLorMML")
       (include-js "/js/mathjax-config.js")
       (include-js "https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js")]

      [:body {:class "page"}
       (header-component)
       (limited-width-title-component post-map)
       (limited-width-content-component content)
       (footer-component)])))

(defn compose-wiki-page
  [content]
  (html5
    [:head
     [:title "Welcome to cwiki"]
     (include-css "/css/styles.css")
     (include-js "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-AMS-MML_HTMLorMML")
     (include-js "/js/mathjax-config.js")
     (include-js "https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js")]

    [:body {:class "page"}
     (header-component)
     (limited-width-content-component content)
     (footer-component)]))

(defn compose-404-page
  "Build and return a 'Not Found' page."
  []
  (html5
    [:head
     [:title "Welcome to cwiki"]
     (include-css "/css/styles.css")]
    [:body {:class "page"}
     (header-component)
     (centered-content-component
       [:div
        [:h1 {:class "info-warning"} "Page Not Found"]
        [:p "The requested page does not exist."]
        (link-to {:class "btn btn-primary"} "/" "Take me Home")])
     (footer-component)]))


