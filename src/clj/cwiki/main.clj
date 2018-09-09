;;;
;;; Main entry point to the application.
;;;

(ns cwiki.main
  (:gen-class)
  (:require [cwiki.handler :refer [app]]
            [cwiki.models.wiki-db :as db]
            [cwiki.routes.ws :as ws]
            [org.httpkit.server :as http-kit]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [taoensso.timbre :refer [trace debug info warn error
                                     tracef debugf infof warnf errorf]]))

(defonce ^{:private true} web-server_ (atom nil))

(defn- stop-web-server! []
  (info "Stopping web server.")
  (when-let [stop-fn @web-server_]
    (stop-fn)))

(defn- start-web-server! [& [port]]
  (stop-web-server!)
  (info "Starting web server.")
  (let [port (or port 1350)
        ;; #'app expands to (var app) so that when we reload our code,
        ;; the server is forced to re-resolve the symbol in the var
        ;; rather than having its own copy. When the root binding
        ;; changes, the server picks it up without having to restart.
        ring-handler #'app
        [port-used stop-fn] (let [stop-fn (http-kit/run-server ring-handler {:port port})]
                         [(:local-port (meta stop-fn)) (fn [] (stop-fn :timeout 100))])
        uri (format "http://localhost:%s/" port-used)]

    (infof "Web server is running at `%s`" uri)
    (try
      (.browse (java.awt.Desktop/getDesktop) (java.net.URI. uri))
      (catch java.awt.HeadlessException _))

    (reset! web-server_ stop-fn)))

(defn start-app!
  "Used to initialize the database, if needed, and start the
  server in development mode from the REPL."
  [& [port]]
  (info "Starting CWiki.")
  (db/start-db!)
  (ws/start-ws-router!)
  (start-web-server! 1350))

(defn stop-app!
  "Shut down the application."
  []
  (info "Stopping CWiki.")
  (stop-web-server!)
  (ws/stop-ws-router!)
  (db/stop-db!))

(defn -main [& args]
  (start-app!))
