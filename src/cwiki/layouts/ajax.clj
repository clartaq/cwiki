(ns cwiki.layouts.ajax
  (:require [clojure.string :as s]
            [cwiki.layouts.base :as base]
            [cwiki.util.req-info :as ri]
            [cwiki.models.wiki-db :as db]
            [hiccup.page :refer [html5 include-css include-js]]))

(defn mde-startup-scripts
  []
  (include-js "/js/mathjax-config.js")
  (include-js "/js/highlight.pack.js"))

(defn mde-template
  "A page template for short messages, no sidebar content, no nav."
  [post-map req]
  (println "mde-template")
  (let [id (db/page-map->id post-map)
        title (db/page-map->title post-map)
        content (db/page-map->content post-map)
        tags (db/get-tag-names-for-page id)]

    (let [ret-val (html5
                  {:lang "en"}

                  (include-css "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/styles/default.min.css")
                  (include-css "css/demo.css")
                  (include-css "css/mde.css")
                  [:div {:id "app"}]
                  ; (base/standard-head nil)
                  ; [:body {:class "page"}
                  ;  (base/no-nav-header-component)
                  ;  (base/sidebar-and-article
                  ;    (base/no-content-aside)
                  ;    content)
                  ;  (base/footer-component)]

                  (include-js "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/highlight.min.js")
                  (include-js "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/languages/clojure.min.js")
                  (include-js "//cdnjs.cloudflare.com/ajax/libs/marked/0.3.19/marked.min.js")
                  (include-js "js/mde.js")
                  [:script "window.addEventListener(\"DOMContentLoaded\", mde.core.main());"])]
    (println "ret-val: " ret-val)
    ret-val)))
