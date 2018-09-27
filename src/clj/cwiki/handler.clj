;;;
;;; The main handler namespace. All of the smaller route structures are
;;; consolidated here and wrapped with middleware.
;;;

(ns cwiki.handler
  (:require [cemerick.url :as u]
            [clojure.string :as s]
            [compojure.core :refer [defroutes routes]]
            [compojure.response :as response]
            [compojure.route :as route]
            [cwiki.layouts.base :as layout]
            [cwiki.layouts.editor :as layout-editor]
            [cwiki.middleware :as middleware]
            [cwiki.models.wiki-db :as db]
            [cwiki.routes.admin :refer [admin-routes]]
            [cwiki.routes.home :refer [home-routes]]
            [cwiki.routes.ws :refer [websocket-routes]]
            [cwiki.routes.login :refer [login-routes]]
            [cwiki.util.authorization :as ath]
            [cwiki.util.req-info :as ri]
            [ring.util.response :refer [redirect status]]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

(defn- wanna? [request the-key]
  (= "true" (get-in request [:params the-key])))

(defn- wanna-delete? [request]
  (wanna? request :delete))

(defn- wanna-edit? [request]
  (wanna? request :edit))

(defn- build-response
  "A helper to build and return a response to a request."
  ([body req]
   (build-response body req 200))
  ([body req stat]
   (-> (response/render body req)
       (status stat)
       (assoc :body body))))

(defn- return-403-page [request]
  (build-response (layout/compose-403-page) request 403))

(defn- edit-existing-post [post-map request]
  (-> post-map
      (layout-editor/layout-editor-page request)
      (build-response request)))

(defn- edit-new-post [title request]
  (-> title
      (db/create-new-post-map "" (ri/req->user-id request))
      (edit-existing-post request)))

(defn respond-to-page-request
  "Deal with a request for a page in the wiki, including some special
  pages and pages that may not even exist yet."
  [request]
  (let [title (s/replace-first (u/url-decode (:uri request)) "/" "")
        raw-post (db/find-post-by-title title)]
    (cond
      (wanna-delete? request) (if (ath/can-edit-and-delete? request title)
                                (let [new-body (layout/view-wiki-page
                                                 (db/find-post-by-title "Front Page")
                                                 request)]
                                  (db/delete-page-by-id! (db/title->page-id title))
                                  (build-response new-body request))
                                ;else
                                (return-403-page request))

      (wanna-edit? request) (if (ath/can-edit-and-delete? request title)
                              (edit-existing-post raw-post request)
                              (return-403-page request))

      (= title "All Pages") (let [new-body (layout/compose-all-pages-page request)]
                              (build-response new-body request))

      (= title "All Users") (let [new-body (layout/compose-all-users-page request)]
                              (build-response new-body request))

      (= title "All Tags") (let [new-body (layout/compose-all-tags-page request)]
                             (build-response new-body request))

      (= title "Orphan Pages") (let [new-body (layout/compose-not-yet-view "Orphan Pages")]
                            (build-response new-body request))

      (= title "Dead Links") (let [new-body (layout/compose-not-yet-view "Dead Links")]
                                 (build-response new-body request))

      (= title "as-user") (let [author (get-in request [:params :user])
                                new-body (layout/compose-all-pages-with-user author request)]
                            (build-response new-body request))
      (= title "as-tag") (let [tag (get-in request [:params :tag])
                               new-body (layout/compose-all-pages-with-tag tag request)]
                           (build-response new-body request))

      raw-post (let [new-page (layout/view-wiki-page raw-post request)]
                 (build-response new-page request))

      ; This is the fall through case. We make the assumption that if the page
      ; doesn't already exist, the user wants to create it.
      :else (if (ath/can-edit-and-delete? request title)
              (edit-new-post title request)
              (return-403-page request)))))

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

(def all-routes (routes admin-routes home-routes login-routes websocket-routes app-routes))

(def app (middleware/wrap-middleware all-routes))

