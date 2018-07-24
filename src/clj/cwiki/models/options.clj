(ns cwiki.models.options
  (:require [clojure.java.jdbc :as jdbc]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :refer [trace debug info warn error
                                     tracef debugf infof warnf errorf]]))

(defn- save-option!
  [db-spec k v]
  (let [val (if (string? v)
              v
              (str v))]
    (println "inserting option: key: " k ", val: " val)
    (jdbc/insert! db-spec :options {:option_name k :option_value val})))

(defn- create-and-initialize-options-table!
  [db-spec initial-options]
  (infof "create-and-initialize-options-table!:\n    db-spec: %s\n    initial-options: %s" db-spec initial-options)
  (try (jdbc/db-do-commands
         db-spec false
         [(jdbc/create-table-ddl :options
                                 [[:opion_id :integer :auto_increment :primary :key]
                                  [:option_name :varchar]
                                  [:option_value :varchar]])])
       (info "Done creating options table.")
       (doseq [[k v] (seq initial-options)]
         (save-option! db-spec k v))
       (info "Done inserting data in options table.")
       (catch Exception e (println e))))

(defprotocol Options-Protocol
  (init-options! [this defaults])
  (persist-options [this]))

(defrecord Options [db defaults]
  component/Lifecycle

  (start [this]
    (info "Starting options.")
    (let [retval (if db
                   this
                   (assoc this :db db))]
      (init-options! this defaults)
      retval))

  (stop [this]
    (info "Stopping options.")
    (if db
      (do
        (persist-options this)
        (assoc this :db nil))
      this))

  Options-Protocol

  (init-options! [this defaults]
    (when-let [db-spec (:db-spec db)]
      (try
        (jdbc/query db-spec ["SELECT * FROM options WHERE option_id = 1"])
        (catch Exception e (let [msg (.getMessage e)]
                             (when (.contains msg "Table \"OPTIONS\" not found")
                               (create-and-initialize-options-table! db-spec defaults)))))))

  (persist-options [this]
    ;(info "persist-options: Writing stuff to db.")
    ))

;;
;; Default options are setup here so that we don't have any global state.
;;

(defn new-options
  []
  (map->Options {:defaults {:wiki-name                   "CWiki"
                            :root-page                   "Front Page"
                            :editor-editing-font         "Calibri"
                            :editor-preview-font         "Calibri"
                            :confirm-page-deletions      true
                            :confirm-user-deletions      true
                            :editor-send-every-keystroke true
                            :editor-autosave-interval    30.0}
                 }))

