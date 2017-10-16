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

(defn read-front-page-content
  "Read in the front page for a new wiki."
  []
  (:content (read-front-page))) ;find-post-by-title "Front Page"))]
    ;(println "fp:" fp)
    ;fp))



(defn home []
  (layout/view-wiki-page (read-front-page))) ;compose-wiki-page (read-front-page-content)))

(defroutes home-routes
           (GET "/" [] (home)))
