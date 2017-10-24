(ns cwiki.routes.home
  (:require [compojure.core :refer :all]
            [cwiki.views.layout :as layout]
            [clojure.java.jdbc :as jdbc]
            [cwiki.models.db :as db])
  (:import (java.io FileReader)))

(defn read-front-page
  "Read the complete post for the front page."
  []
  (db/find-post-by-title "Front Page"))

(defn read-about-page
  []
  (db/find-post-by-title "About"))

(defn save-edits
  [page-id new-title new-content]
  ;(println "page-id:" page-id ", (type page-id):" (type page-id))
  (let [actual-id (Integer. ^String page-id)]
   ; (println "save-edits: actual-id:" actual-id ", new-title:" new-title ",
   ; new-content:" new-content)
    (db/update-page-title-and-content! actual-id new-title new-content)
    (layout/view-wiki-page (db/find-post-by-title new-title))))

(defn save-new-page
  [title content]
  ;(println "save-new-page: title:" title ", content:" content)
 ; (println "insert:"
  (db/insert-new-page! title content)
  ;)
  (let [new-view (layout/view-wiki-page (db/find-post-by-title title))]
  ;  (println "new-view:" new-view)
    new-view))

(defn home []
  (layout/view-wiki-page (read-front-page)))

(defn about []
  (layout/view-wiki-page (read-about-page)))

(defroutes home-routes
           (GET "/" [] (home))
           (GET "/about" [] (about))
           (POST "/save-edits" request
             (let [params (request :multipart-params)]
               (save-edits (get params "page-id")
                           (get params "title")
                           (get params "content"))))
           (POST "/save-new-page" request
             (println "request:" request)
             (let [params (request :multipart-params)]
               (save-new-page (get params "title")
                              (get params "content")))))
