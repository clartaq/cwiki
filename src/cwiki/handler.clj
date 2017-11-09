(ns cwiki.handler
  (:require [cemerick.url :as u]
            [compojure.core :refer [defroutes routes]]
            [ring.util.response :refer [status]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cwiki.views.layout :as layout]
            [cwiki.routes.home :refer [home-routes]]
            [compojure.response :as response]
            [clojure.string :as s]
            [cwiki.models.db :as db]))

(defn init []
  (println "CWiki is starting"))

(defn destroy []
  (println "CWiki is shutting down"))

(defn- build-response
  [body req]
  (-> (response/render body req)
      (status 200)
      (assoc :body body)))

(defn page-finder-route
  [body]
  (fn [request]
    (let [raw-title (u/url-decode (:uri request))
          title (s/replace raw-title "//" "")
          raw-post (db/find-post-by-title title)]
     (cond
       raw-post (let [new-body (:content raw-post)
                      new-page (layout/view-wiki-page raw-post)]
                  (build-response new-page request))

       (= title "All Pages") (let [new-body (layout/compose-all-pages-page)]
                               (build-response new-body request))

       (s/ends-with? title "/edit") (let [title-only (s/replace title "/edit" "")
                                          new-body (layout/compose-create-or-edit-page
                                                     (db/find-post-by-title title-only))]
                                      (build-response new-body request))
       (s/ends-with? title "/delete") (let [title-only (s/replace title "/delete" "")
                                            new-body (layout/view-wiki-page
                                                       (db/find-post-by-title "Front Page"))]
                                        (db/delete-page-by-id (db/title->page-id title-only))
                                        (build-response new-body request))
       :else (let [title-only (s/replace title "/create" "")
                   new-body (layout/compose-create-or-edit-page
                              (db/create-new-post-map title-only))]
               (build-response new-body request))))))

(defroutes app-routes
           (route/resources "/")
           (page-finder-route (layout/compose-404-page))
           (route/not-found (layout/compose-404-page)))

(def app
  (-> (routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)))
