(ns cwiki.views.layout
  (:require [clj-time.format :as f]
            [clj-time.coerce :as c]
            [cwiki.util.wikilinks :refer [replace-wikilinks
                                          get-edit-link-for-existing-page]]
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

(defn wiki-hmenu-component
  "Return the standard menu component for the application."
  [post-map]
  (let [edit-link (get-edit-link-for-existing-page post-map)]
    [:menu {:class "hmenu"}
     [:p {:class "menu-item"}
      edit-link " "
      ; [:a {:href "/edit"} "Edit"] " "
      [:a {:href "/"} "Home"] " "
      [:a {:href "/about"} "About"] " "
      [:a {:href "/search"} "Search"]]]))

(defn wiki-header-component
  [post-map]
  [:header {:class "header"}
   [:div {:class "header-wrapper"}
    [:div {:class "left-header-wrapper"}
     [:h1 {:class "brand-title"} "cwiki"]
     [:p {:class "brand-sub-title"}
      "A Simple " [:a {:href "https://en.wikipedia.org/wiki/Wiki"}
                   "Wiki"]]]
    (wiki-hmenu-component post-map)]])

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
    [:div
      [:h1 title]
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
  ;(println "view-wiki-page")
  (let [content (:content post-map)]
    (html5
      [:head
       [:title "Welcome to cwiki"]
       (include-css "/css/styles.css")
       (include-js "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-AMS-MML_HTMLorMML")
       (include-js "/js/mathjax-config.js")
       (include-js "https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js")]

      [:body {:class "page"}
       (wiki-header-component post-map)
       ;(header-component)
       [:content {:class "centered-content"}
       (limited-width-title-component post-map)
        (limited-width-content-component content)]
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

(defn compose-edit-page
  [post-map]
  (let [id (:id post-map)
        title (:title post-map)
        content (:content post-map)]
    (html5
      [:head
       [:title "Welcome to cwiki"]
       (include-css "/css/styles.css")]
      [:body {:class "page"}
       (header-component)
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
       ; _ (println "id:" id)
        title (:title post-map)
        ;_ (println "title:" title)
        content (:content post-map)
       ; _ (println "content:" content)
       ]
    (html5
      [:head
       [:title "Welcome to cwiki"]
       (include-css "/css/styles.css")]
      [:body {:class "page"}
       (header-component)
       (centered-content-component
         [:div
          (form-to {:enctype "multipart/form-data"}
                   [:post "/save-new-page"]
                   ;(hidden-field :page-id id)
                   (text-field "title" title)
                   (text-area "content" content)
                   [:br]
                   (submit-button {:id "Save Button"} "Save Changes")
                   " "
                   (submit-button {:id "Cancel Button"} "Cancel")
                   " "
                   (link-to {:class "btn btn-primary"} "/" "Take me Home"))])
       (footer-component)])))



;
;A complete example looks like:
;
;(ns myapp.routes.home
;  (:use [hiccup core form])
;  (:require [compojure.core :refer :all]))
;
;(defn quick-form [& [name message error]]
;  (html
;   (form-to {:enctype "multipart/form-data"}
;    [:post "/form-out"]
;   (text-field "Hello")
;   (submit-button {:class "btn" :name "submit"} "Save")
;   (submit-button {:class "btn" :name "submit"} "Clone"))))
;
;Note that using the same name for both submit buttons allows you to do a simple lookup of the "submit" key in the result map.
;
;(defroutes home-routes
; (GET "/form-in" [] (quick-form))
; (POST "/form-out" [:as request] (str (request :multipart-params))))
;
;When opening the following page:
;
; http://localhost:3000/form-in
;
;And filling the form, the result out of the POST route is:
;
; {"submit" "Save", "Hello" "hello2"}
;
;By the way, I found an old useful post about the way the request map is structured in Compojure, so it makes it easier to destructure it in the Clojure code.
;shareeditflag
;
;edited Jan 13 '15 at 2:26
;
;answered Jan 13 '15 at 1:00
;Nicolas Modrzyk
;10.1k12134
;
;
;
;
;Thanks! This is what I was looking for. – sakh1979 Jan 13 '15 at 1:41
;
;add a comment
;up vote
;1
;down vote
;
;
;submit-button generate HTML <input type="text" ...> element. You can add "name" and "value" attributes to them:
;
;(submit-button {:name "button" :value "save" :class "btn"} "Save")
;(submit-button {:name "button" :value "clone" :class "btn"} "Clone")
;
;and find it out in your server side code. In your case lib-noir is used. But recent version of lib-noir no longer provide utils for destructuring requests and encourage people to utilize other libraries like Compojure or bare Ring.
;
;Basically you need: - ensure your server side app use wrap-params Ring middleware - in case the above "Save" button is click, your server side handler for [:post "/add-data"] should receive a hash map like this:
;
;{:http-method :post
; :uri "/add-data"
; :form-params {"button" "save"
;               ;; other form data as key/value pairs
;               ;; where: key is input element's "name" attribute and value is input element's "value" attribute
;               ...
;               }
; ...}
;
;I hope you can figure out yourself how to find the value you need in such a map.
;
;More in-depth reading:
;
;https://github.com/mmcgrana/ring/wiki/Parameters
