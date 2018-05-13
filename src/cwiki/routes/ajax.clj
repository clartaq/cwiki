(ns cwiki.routes.ajax
  (:require [cemerick.url :as url]
            [compojure.core :refer :all]
            [compojure.response :as response]
    ;[cwiki.layouts.base :as layout]
            [cwiki.layouts.ajax :as layout]
            [cwiki.models.wiki-db :as db]
            [cwiki.util.files :as files]
            [cwiki.util.req-info :as ri]
            [ring.util.response :refer [redirect status]]))

(defn launch-mde
  [req]
  (println "launch-mde")
  (layout/mde-template req
    [:div
     [:h1 "Saw request for mde editor."]
     [:p (str "Request: \n" req)]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes ajax-routes
           (GET "/mde" request (launch-mde request)))
