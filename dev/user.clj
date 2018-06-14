;;;
;;; Just some convenience functions to start and stop the application
;;; from the repl.
;;;

(ns user
  (require [cwiki.main :as main]))

(defn start []
  (main/start-app))

(defn stop []
  (main/stop-app))

(defn -main []
  (start))

