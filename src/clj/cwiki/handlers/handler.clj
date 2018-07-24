(ns cwiki.handlers.handler
  (:require [cemerick.url :as u]
            [clojure.string :as s]
            [compojure.core :refer [defroutes routes]]
            [compojure.response :as response]
            [compojure.route :as route]
            [cwiki.components.socket-server :refer [sente-routes]]
            [cwiki.layouts.base :as layout]
            [cwiki.layouts.editor :as layout-editor]
            [cwiki.middleware :as middleware]
            [cwiki.models.wiki-db :as db]
            [cwiki.routes.admin :refer [admin-routes]]
            [cwiki.routes.home :refer [home-routes]]
            [cwiki.routes.ws :refer [websocket-routes]]
            [cwiki.routes.login :refer [login-routes]]
            [cwiki.util.authorization :as ath]
            [cwiki.util.pp :as pp]
            [cwiki.util.req-info :as ri]
            [ring.util.response :refer [redirect status]]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

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
      raw-post (let [new-page (layout/view-wiki-page raw-post request)]
                 (build-response new-page request))

      (= title "All Pages") (let [new-body (layout/compose-all-pages-page request)]
                              (build-response new-body request))

      (= title "All Users") (let [new-body (layout/compose-all-users-page request)]
                              (build-response new-body request))

      (= title "All Tags") (let [new-body (layout/compose-all-tags-page request)]
                             (build-response new-body request))

      (= title "Orphans") (let [new-body (layout/compose-not-yet-view "Orphans")]
                            (build-response new-body request))

      (s/ends-with? title "/edit") (let [title-only (s/replace title "/edit" "")]
                                     (if (ath/can-edit-and-delete? request title-only)
                                       (let [post (db/find-post-by-title title-only)
                                             ;_ (println "existing post: \n" (pp/pp-map post))
                                             new-body (layout-editor/layout-editor-page
                                                        post
                                                        request)
                                             response (build-response new-body request)]
                                         response)
                                       ;else
                                       (build-response (layout/compose-403-page) request 403)))

      (s/ends-with? title "/delete") (let [title-only (s/replace title "/delete" "")]
                                       (if (ath/can-edit-and-delete? request title-only)
                                         (let [new-body (layout/view-wiki-page
                                                          (db/find-post-by-title "Front Page") request)]
                                           (db/delete-page-by-id! (db/title->page-id title-only))
                                           (build-response new-body request))
                                         ;else
                                         (build-response (layout/compose-403-page) request 403)))
      (s/ends-with? title "/as-user") (let [author-only (s/replace title "/as-user" "")
                                            new-body (layout/compose-all-pages-with-user author-only request)]
                                        (build-response new-body request))
      (s/ends-with? title "/as-tag") (let [tag-only (s/replace title "/as-tag" "")
                                           new-body (layout/compose-all-pages-with-tag tag-only request)]
                                       (build-response new-body request))
      ; This is the fall through case. We make the assumption that if the page
      ; doesn't already exist, the user wants to create it.
      :else (let [title-only (s/replace title "/edit" "")]
              (if (ath/can-edit-and-delete? request title-only)
                (let [new-post (db/create-new-post-map
                                 title-only
                                 ""
                                 (ri/req->user-id request))
                      ;_ (println "new post: \n" (pp/pp-map new-post))
                      new-body (layout-editor/layout-editor-page
                                 new-post
                                 request)
                      response (build-response new-body request)]
                  response)
                ;else
                (build-response (layout/compose-403-page) request 403))))))

(defn page-finder-route
  []
  (fn [request]
    (if (ri/is-authenticated-user? request)
      (respond-to-page-request request)
      (redirect "/login"))))

(defroutes app-routes
           (route/resources "/")
           (page-finder-route)
           (route/not-found (layout/compose-404-page)))

(def all-routes (routes admin-routes home-routes login-routes
                        (sente-routes)
                        ;websocket-routes
                        app-routes))

(def app (middleware/wrap-middleware all-routes))

