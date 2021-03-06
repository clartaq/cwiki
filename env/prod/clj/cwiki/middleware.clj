(ns cwiki.middleware
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [clojure.java.io :as io]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

;(println "wrapping production middleware")

(defn wrap-middleware [handler]
  ; Have to have this for the uberjar to start on a virgin system.
  ; Just makes sure the "resources" directory exists so that the
  ; "wrap-file" middleware below doesn't blow up.
  (io/make-parents "resources/random-file.txt")
  (-> handler
      (wrap-authentication (backends/session))
      (wrap-defaults site-defaults)
      ; Makes static assets in $PROJECT_DIR/resources/public/ available.
      ; If you take this out, editor icons won't load.
      (wrap-file "resources")
      ; Content-Type, Content-Length, and Last Modified headers for files in body.
      (wrap-content-type)
      (wrap-not-modified)))

