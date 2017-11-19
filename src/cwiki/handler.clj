(ns cwiki.handler
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [cemerick.url :as u]
            [clojure.string :as s]
            [compojure.core :refer [defroutes routes]]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [compojure.route :as route]
            [cwiki.models.db :as db]
            [cwiki.views.layout :as layout]
            [cwiki.routes.home :refer [home-routes]]
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [status]]))

(def backend (backends/session))

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
        raw-post (let [new-body (db/page-map->content raw-post)
                       new-page (layout/view-wiki-page raw-post)]
                   (build-response new-page request))

        (= title "All Pages") (let [new-body (layout/compose-all-pages-page)]
                                (build-response new-body request))

        (= title "All Users") (let [new-body (layout/compose-all-users-page)]
                                (build-response new-body request))

        (= title "All Namespaces") (let [new-body (layout/compose-all-namespaces-page)]
                                     (build-response new-body request))

        (= title "All Tags") (let [new-body (layout/compose-all-tags-page)]
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
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-base-url)
      (wrap-authentication backend)))
