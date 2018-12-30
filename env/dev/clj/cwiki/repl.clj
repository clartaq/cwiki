(ns cwiki.repl
  (:require [cwiki.main :as main]
    ; Just so all the functions are available in the repl.
            [figwheel-sidecar.repl-api :refer :all]))

(defn start
  "A convenience function to start the app from the repl."
  []
  (let [port 1350
        uri (format "http://localhost:%s/" port)]
    (start-figwheel!)
    (main/start-app! port)
    (try
      (.browse (java.awt.Desktop/getDesktop) (java.net.URI. uri))
      (catch java.awt.HeadlessException _))))

(defn stop
  "A convenience function to stop the app from the repl."
  []
  (main/stop-app!)
  (stop-figwheel!))

(defn start-cljs-repl
  []
  (start-figwheel!)
  (cljs-repl))