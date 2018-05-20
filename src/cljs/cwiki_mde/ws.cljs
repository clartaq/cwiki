;;;
;;; This namespace contains functions that handle websocket events for the
;;; ClojureScript editor.
;;;

(ns cwiki-mde.ws
  (:require [taoensso.sente :as sente]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

(let [connection (sente/make-channel-socket! "/ws" {:type :auto})]
  (def ch-chsk (:ch-recv connection))    ; ChannelSocket's receive channel
  (def send-message! (:send-fn connection)))

(defn state-handler
  "Handle changes in state."
  [{:keys [?data]}]
  (infof "state changed: %s" ?data)) ; (str "state changed: " ?data)))

(defn handshake-handler
  "Handle messages that the handshake has been established."
  [{:keys [?data]}]
  (infof "connection established: %s" ?data)) ; (str "connection established: " ?data)))

(defn default-event-handler
  "Unidentified events get sent here."
  [ev-msg]
  (infof "Unhandled event: %s" (:event ev-msg))) ;(str "Unhandled event: " (:event ev-msg))))

(defn event-msg-handler
  "The event message handler. Supplies it's own defaults if not all handlers
  are supplied."
  [& [{:keys [message state handshake]
                             :or {message default-event-handler
                                  state state-handler
                                  handshake handshake-handler}}]]
  (fn [ev-msg]
    (case (:id ev-msg)
      :chsk/handshake (handshake ev-msg)
      :chsk/state (state ev-msg)
      :chsk/recv (message ev-msg)
      (default-event-handler ev-msg))))

(def ^{:private true} router (atom nil))

(defn stop-router!
  "Stop the client websocket router."
  []
  (when-let [stop-f @router] (stop-f)))

(defn start-router!
  "Start the client websocket router."
  [handshake-handler state-handler message-handler]
  (info "Trying to start dave router")
  (stop-router!)
  (reset! router (sente/start-chsk-router!
                   ch-chsk
                   (event-msg-handler
                     {:message message-handler
                      :state state-handler
                      :handshake handshake-handler}))))
