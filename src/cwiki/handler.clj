(ns cwiki.handler
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [cemerick.url :as u]
            [clojure.string :as s]
            [compojure.core :refer [defroutes routes]]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [compojure.route :as route]
            [cwiki.models.db :as db]
            [cwiki.routes.admin :refer [admin-routes]]
            [cwiki.routes.home :refer [home-routes]]
            [cwiki.routes.login :refer [login-routes]]
            [cwiki.util.authorization :as ath]
            [cwiki.util.req-info :as ri]
            [cwiki.layouts.base :as layout]
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [redirect status]]))

(def backend (backends/session))

(defn init []
  (println "CWiki is starting"))

(defn destroy []
  (println "CWiki is shutting down"))

(defn- build-response
  ([body req]
   (build-response body req 200))
  ([body req stat]
   (-> (response/render body req)
       (status stat)
       (assoc :body body))))

(defn respond-to-page-request
  [request]
  (let [raw-title (u/url-decode (:uri request))
        title (s/replace-first raw-title "/" "")
        raw-post (db/find-post-by-title title)]
    (cond
      raw-post (let [new-body (db/page-map->content raw-post)
                     new-page (layout/view-wiki-page raw-post request)]
                 (build-response new-page request))

      (= title "All Pages") (let [new-body (layout/compose-all-pages-page request)]
                              (build-response new-body request))

      (= title "All Users") (let [new-body (layout/compose-all-users-page request)]
                              (build-response new-body request))

      (= title "All Namespaces") (let [new-body (layout/compose-all-namespaces-page request)]
                                   (build-response new-body request))

      (= title "All Tags") (let [new-body (layout/compose-all-tags-page request)]
                             (build-response new-body request))

      (s/ends-with? title "/edit") (let [title-only (s/replace title "/edit" "")]
                                     (if (ath/can-edit-and-delete? request title-only)
                                       (let [new-body (layout/compose-create-or-edit-page
                                                        (db/find-post-by-title title-only) request)]
                                         (build-response new-body request))
                                       ;else
                                       (build-response (layout/compose-403-page) request 403)))
      (s/ends-with? title "/delete") (let [title-only (s/replace title "/delete" "")]
                                       (if (ath/can-edit-and-delete? request title-only)
                                         (let [new-body (layout/view-wiki-page
                                                          (db/find-post-by-title "Front Page") request)]
                                           (db/delete-page-by-id (db/title->page-id title-only))
                                           (build-response new-body request))
                                         ;else
                                         (build-response (layout/compose-403-page) request 403)))
      :else (if (ath/can-create? request)
              (let [title-only (s/replace title "/create" "")
                    new-body (layout/compose-create-or-edit-page
                               (db/create-new-post-map
                                 title-only
                                 ""
                                 (ri/req->user-id request)) request)]
                (build-response new-body request))
              ;else
              (build-response (layout/compose-403-page) request 403)))))

(defn page-finder-route
  [body]
  (fn [request]
    (if (ri/is-authenticated-user? request)
      (respond-to-page-request request)
      (redirect "/login"))))

(defroutes app-routes
           (route/resources "/")
           (page-finder-route (layout/compose-404-page))
           (route/not-found (layout/compose-404-page)))

(def app
  (-> (routes admin-routes home-routes login-routes app-routes)
      (wrap-authentication backend)
      (handler/site)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-base-url)))

