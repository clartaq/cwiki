(ns cwiki.handler
  (:require [cemerick.url :as u]
            [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.util.response :refer [status]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cwiki.views.layout :as layout]
            [cwiki.routes.home :refer [home-routes]]
            [compojure.response :as response]
            [compojure.response :as response]
            [clojure.string :as s]
            [cwiki.models.db :as db]))

(defn init []
  (println "cwiki is starting"))

(defn destroy []
  (println "cwiki is shutting down"))

(defn page-finder-route
  [body]
  (fn [request]
    (let [raw-title (u/url-decode (:uri request))
          title (s/replace raw-title "//" "")
          raw-post (db/find-post-by-title title)]
     ; (println "raw-title:" raw-title ", title:" title ", raw-post:" raw-post)
      (cond
        raw-post (let [new-body (:content raw-post)
                       new-page (layout/view-wiki-page raw-post)] ;(layout/compose-wiki-page new-body)]
                   (println "Found title:" title)
                   (-> (response/render new-page request)
                       (status 200)
                       (assoc :body new-page)))

        (s/ends-with? title "/edit") (let [title-only (s/replace title "/edit" "")
                                          ; _ (println "saw edit request for:" title-only)
                                           new-body (layout/compose-edit-page
                                                      (db/find-post-by-title title-only))]
                                       (-> (response/render new-body request)
                                           (status 200)
                                           (assoc :body new-body)))
        (s/ends-with? title "/create") (let [title-only (s/replace title "/create" "")
                                         ;  _ (println "saw create request for:" title-only)
                                           new-body (layout/compose-create-page
                                                      (db/create-new-post-map title-only))]
                                         ;(println "new-body:" new-body)
                                       (-> (response/render new-body request)
                                           (status 200)
                                           (assoc :body new-body)))

        :else nil))))

(defroutes app-routes
           (route/resources "/")
           (page-finder-route (layout/compose-404-page))
           (route/not-found (layout/compose-404-page)))

(def app
  (-> (routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)))
