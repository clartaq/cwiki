;;;
;;; Some repl convenience functions. The application should be
;;; launched from here.
;;;

(ns cwiki.repl
  (:require [cwiki.handler :refer [app init destroy]]
            [cwiki.models.db :as db]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.server.standalone :refer [serve]]))

(defonce server (atom nil))

(defn get-handler []
  ;; #'app expands to (var app) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (-> #'app
    ; Makes static assets in $PROJECT_DIR/resources/public/ available.
    (wrap-file "resources")
    ; Content-Type, Content-Length, and Last Modified headers for files in body.
    (wrap-file-info)))

(defn start-server
  "Used for starting the server in development mode from REPL."
  [& [port]]
  (let [port (if port (Integer/parseInt port) 1350)]
    (reset! server
            (serve (get-handler)
                   {:port port
                    :init init
                    :auto-reload? true
                    :destroy destroy
                    :join true}))
    (println (str "You can view the site at http://localhost:" port))))

(defn start-app
  "Used to initialize the database, if needed, and start the
  server in development mode from the REPL."
  [& [port]]
  (db/init-db!)
  (start-server port))

(defn stop-server []
  (.stop @server)
  (reset! server nil))

(defn stop-app
  "Shut down the application."
  []
  (stop-server))

