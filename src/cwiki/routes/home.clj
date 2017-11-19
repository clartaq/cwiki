(ns cwiki.routes.home
  (:require [compojure.core :refer :all]
            [cwiki.views.layout :as layout]
            [cwiki.models.db :as db]
            [ring.util.response :refer [response redirect]]))

(defn read-front-page
  "Read the complete post for the front page."
  []
  (db/find-post-by-title "Front Page"))

(defn read-about-page
  []
  (db/find-post-by-title "About"))

(defn save-edits
  [page-id new-title new-content req]
  (let [actual-id (Integer. ^String page-id)]
    (db/update-page-title-and-content! actual-id new-title new-content)
    (layout/view-wiki-page (db/find-post-by-title new-title) req)))

(defn save-new-page
  [title content req]
  (db/insert-new-page! title content)
  (layout/view-wiki-page (db/find-post-by-title title) req))

(defn home [req]
  (layout/view-wiki-page (read-front-page) req))

(defn about [req]
  (layout/view-wiki-page (read-about-page) req))

(defroutes home-routes
           (GET "/" request (home request))
           (GET "/about" request (about request))
           (POST "/save-edits" request
             (let [params (request :multipart-params)]
               (save-edits (get params "page-id")
                           (get params "title")
                           (get params "content") request)))
           (POST "/save-new-page" request
             (let [params (request :multipart-params)]
               (save-new-page (get params "title")
                              (get params "content") request))))
