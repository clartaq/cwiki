(ns cwiki.main
  (:gen-class)
  (:require [cwiki.handler :refer [app init destroy]]
            [cwiki.models.wiki-db :as db]
            [cwiki.routes.ws :as ws]
            [environ.core :refer [env]]
            [org.httpkit.server :as http-kit]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.reload :refer [wrap-reload]]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf]]))

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

(defonce ^{:private true} web-server_ (atom nil))

(defn- stop-web-server! []
  (when-let [stop-fn @web-server_]
    (stop-fn)))

(defn- start-web-server! [& [port]]
  (stop-web-server!)
  (let [port (or port 1350)
        ring-handler (if (true? (env :dev))
                       (wrap-reload (get-handler))
                       (get-handler))
        [port-used stop-fn] (let [stop-fn (http-kit/run-server ring-handler {:port port})]
                         [(:local-port (meta stop-fn)) (fn [] (stop-fn :timeout 100))])
        uri (format "http://localhost:%s/" port-used)]

    (infof "Web server is running at `%s`" uri)
    (try
      (.browse (java.awt.Desktop/getDesktop) (java.net.URI. uri))
      (catch java.awt.HeadlessException _))

    (reset! web-server_ stop-fn)))

(defn start-app
  "Used to initialize the database, if needed, and start the
  server in development mode from the REPL."
  [& [port]]
  (db/init-db!)
  (ws/start-router!)
  (start-web-server! 1350))

(defn stop-app
  "Shut down the application."
  []
  (ws/stop-router!)
  (stop-web-server!))

(defn -main [& args]
  (start-app))
