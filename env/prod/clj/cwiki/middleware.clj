(ns cwiki.middleware
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            ;[clojure.java.io :as io]
            ;[ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]))

(println "wrapping production middleware")

(defn wrap-middleware [handler]
  ; Have to have this for the uberjar to start on a virgin system.
  ; Just makes sure the "resources" directory exists so that the
  ; "wrap-file" middleware below doesn't blow up.
  ;(io/make-parents "resources/random-file.txt")
  (-> handler
      (wrap-authentication (backends/session))
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      ; *** THIS DOES NOT ACTUALLY SEEM TO BE REQUIRED ***
      ; Makes static assets in $PROJECT_DIR/resources/public/ available.
      ;(wrap-file "resources")
      ; Content-Type, Content-Length, and Last Modified headers for files in body.
      (wrap-file-info)
      (wrap-gzip)))

