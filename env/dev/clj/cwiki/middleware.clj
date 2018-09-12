(ns cwiki.middleware
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.reload :refer [wrap-reload]]))

(println "wrapping dev middleware")

(def backend (backends/session))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-authentication backend)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      ; Makes static assets in $PROJECT_DIR/resources/public/ available.
      (wrap-file "resources")
      ; Content-Type, Content-Length, and Last Modified headers for files in body.
      (wrap-file-info)
      (wrap-exceptions)
      (wrap-reload)))
