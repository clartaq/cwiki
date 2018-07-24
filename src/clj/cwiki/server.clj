(ns cwiki.server
  (:require [com.stuartsierra.component :as component]
            [cwiki.handlers.handler :refer [app]]
            [org.httpkit.server :as http-kit]
            [taoensso.timbre :refer [trace debug info warn error
                                     tracef debugf infof warnf errorf]]))

(defrecord WebServer [config server handler]
  component/Lifecycle

  (start [this]
    (info "Starting server.")
    (let [port (get-in config [:config-map :server-port] 55557)
          build-type (get-in config [:config-map :build-type])
          server-options {:port port}
          server (http-kit/run-server handler server-options)
          uri (format "http://localhost:%s/" port)]
      (when (= "dev" build-type)
        (try
          (.browse (java.awt.Desktop/getDesktop) (java.net.URI. uri))
          (infof "Web server is running at `%s`" uri)
          (catch java.awt.HeadlessException _)))
      (assoc this :server server)))

  (stop [this]
    (info "Stopping server.")
    (when server
      (server))
    this)

  ; WebServer-Protocol

  )

(defn new-web-server []
  ;; #'app expands to (var app) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (let [ring-handler #'app]
    (map->WebServer {:handler ring-handler})))
