(ns cwiki.routes.home
  (:require [compojure.core :refer :all]
            [cwiki.layouts.base :as layout]
            [cwiki.models.db :as db]
            [cwiki.util.req-info :as ri]
            [ring.util.response :refer [redirect]]))

(defn read-front-page
  "Read the complete post for the front page."
  []
  (db/find-post-by-title "Front Page"))

(defn read-about-page
  "Read the 'About' page from the database."
  []
  (db/find-post-by-title "About"))

(defn save-edits
  "Save any edits to the page back to the database."
  [page-id new-title new-content req]
  (let [actual-id (Integer. ^String page-id)]
    (db/update-page-title-and-content! actual-id new-title new-content)
    (layout/view-wiki-page (db/find-post-by-title new-title) req)))

(defn- save-and-view-page
  "Do the actual saving then retrieve and view the page."
  [title content req]
  (db/insert-new-page! title content (ri/req->user-id req))
  (layout/view-wiki-page (db/find-post-by-title title) req))

(defn save-new-page
  "Save a new page to the database."
  [title content req]
  (if (db/find-post-by-title title)
    (layout/short-message "Can't Do That"
                          "A post with that title already exists.")
    (save-and-view-page title content req)))

(defn home
  "Handle a request to view the 'Home' page if there is an
  authenticated user. Otherwise, force them to log in first."
  [req]
  (if (ri/is-authenticated-user? req)
    (layout/view-wiki-page (read-front-page) req)
    (redirect "/login")))

(defn about
  "Handle a request for the 'About' route if there is
  an authenticated user for the session. Otherwise,
  force them to log in."
  [req]
  (if (ri/is-authenticated-user? req)
    (layout/view-wiki-page (read-about-page) req)
    (redirect "/login")))

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
