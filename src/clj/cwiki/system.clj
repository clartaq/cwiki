(ns cwiki.system
  (:require [com.stuartsierra.component :as component]
            [cwiki.components.endpoint :as ep]
            [cwiki.components.socket-server :as ws-server]
            [cwiki.models.config :as config]
            [cwiki.models.db :as db]
            [cwiki.models.options :as options]
            [cwiki.server :as server]))

(defn new-cwiki-system
  []
  (component/system-map
    :config-map (config/new-config)
    :db (component/using
          (db/new-database)
          {:config-map :config-map})
    :options (component/using
               (options/new-options)
               {:db :db})
    :server (component/using
              (server/new-web-server)
              {:config :config-map})
    :ws-server (component/using
                 (ws-server/new-channel-socket-server {})
                 {:db :db})
    :ws-routes (component/using
                 (ep/new-endpoint ws-server/sente-routes)
                 {:ws-server :ws-server}))
  )

(defn start-cwiki-system [cwiki-system]
  (component/start-system cwiki-system))

(defn stop-cwiki-system [cwiki-system]
  (component/stop-system cwiki-system))

