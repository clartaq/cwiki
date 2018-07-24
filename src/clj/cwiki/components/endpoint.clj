(ns cwiki.components.endpoint
  (:require [com.stuartsierra.component :as component]))

(defrecord Endpoint [routes-fn]
  component/Lifecycle
  (start [component]
    (assoc component :routes (routes-fn component)))
  (stop [component]
    (dissoc component :routes)))

(defn new-endpoint
  "Creates an endpoint. An endpoint is a closure over Compojure routes
  with component dependencies in scope."
  [routes-fn]
  (->Endpoint routes-fn))
