;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns cwiki-mde.ws
  (:require [taoensso.sente :as sente]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

(let [connection (sente/make-channel-socket! "/ws" {:type :auto})]
  (def ch-chsk (:ch-recv connection))    ; ChannelSocket's receive channel
  (def send-message! (:send-fn connection)))

(defn state-handler [{:keys [?data]}]
  (info (str "state changed: " ?data)))

(defn handshake-handler [{:keys [?data]}]
  (info (str "connection established: " ?data)))

(defn default-event-handler [ev-msg]
  (info (str "Unhandled event: " (:event ev-msg))))

(defn event-msg-handler [& [{:keys [message state handshake]
                             :or {state state-handler
                                  handshake handshake-handler}}]]
  (fn [ev-msg]
    (case (:id ev-msg)
      :chsk/handshake (handshake ev-msg)
      :chsk/state (state ev-msg)
      :chsk/recv (message ev-msg)
      (default-event-handler ev-msg))))

(def router (atom nil))

(defn stop-router! []
  (when-let [stop-f @router] (stop-f)))

(defn start-router! [message-handler]
  (println "Trying to start router")
  (stop-router!)
  (reset! router (sente/start-chsk-router!
                   ch-chsk
                   (event-msg-handler
                     {:message   message-handler
                      :state     state-handler
                      :handshake handshake-handler}))))
