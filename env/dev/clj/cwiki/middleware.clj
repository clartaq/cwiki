(ns cwiki.middleware
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.reload :refer [wrap-reload]]))

;(println "wrapping dev middleware")

(def backend (backends/session))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-authentication backend)
      (wrap-defaults site-defaults)
      ; Makes static assets in $PROJECT_DIR/resources/public/ available.
      ; If you take this out, editor icons won't load.
      (wrap-file "resources")
      ; Content-Type, Content-Length, and Last Modified headers for files in body.
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-exceptions)
      (wrap-reload)))
