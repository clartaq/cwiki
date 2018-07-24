;;;
;;; This component is responsible for retrieving configuration options from
;;; the environment and making them accessible to other parts of the program.
;;; Right now, it just retrieves some settings from the project file.
;;;

(ns cwiki.models.config
  (:require [clojure.pprint :as pp]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [taoensso.timbre :refer [trace debug info warn error
                                     tracef debugf infof warnf errorf]]))

; We need this since environment variables are going to be
; strings, but we want the server port to be an integer to
; the rest of the program.

(defn- parse-int
  "Convert a string to an integer."
  [^String s]
  (Integer. s))

(defprotocol ConfigProtocol
  "If k is a keyword, return whatever it's associated value. Otherwise,
  return nil"
  (get-config-item [this k]))

(defrecord Config [config-map]
  component/Lifecycle

  (start [this]
    (info "Starting config.")
    (assoc this :config-map {:build-type   (env :build-type)
                             :db-file-path (env :db-file-path)
                             :server-port  (or (parse-int (env :server-port))
                                               ; Use a different number so we can
                                               ; see if something odd occurred.
                                               1356)}))

  (stop [this]
    (info "Stopping config.")
    (assoc this :config-map nil))

  ConfigProtocol

  (get-config-item
    [this k]
    (when (keyword? k)
      (let [retval (k config-map)]
        retval)))
  )

(defn new-config
  "Construct a new Config object and return it."
  []
  (map->Config {}))
