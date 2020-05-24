;;;
;;; This namespace contains functions that handle websocket events for the
;;; ClojureScript editor.
;;;

(ns cwiki-mde.ws
  (:require [goog.string :as gstr]
            [taoensso.sente :as sente :refer [cb-success?]]
            [taoensso.timbre :as timbre :refer-macros [log  trace  debug  info  warn  error  fatal  report
                                                       logf tracef debugf infof warnf errorf fatalf reportf
                                                       spy get-env]]))

(timbre/set-level! :info)

;;;; Util for logging output to on-screen console

;(def output-el (.getElementById js/document "output"))
;(println "output-el: " output-el)
(defn ->output! [fmt & args]
  (let [msg (apply gstr/format fmt args)]
    (debug msg)))

(->output! "ClojureScript appears to have loaded correctly.")

;;;; Define our Sente channel socket (chsk) client

(def ?csrf-token
  (when-let [el (.getElementById js/document "sente-csrf-token")]
    (debugf "?csrf-token: el: $s" el)
    (let [res (.getAttribute el "data-csrf-token")]
      (->output! "?csrf-token: res: %s" res)
      res)))

(if ?csrf-token
  (->output! "CSRF token detected in HTML, great!")
  (->output! "CSRF token NOT detected in HTML, default Sente config will reject requests"))

(let [{:keys [chsk ch-recv send-fn state]} (sente/make-channel-socket! "/ws" ?csrf-token {:type :auto})]
  (def chsk chsk)
  (def ch-chsk ch-recv) ; ChannelSocket's receive channel.
  (def chsk-send! send-fn) ; ChannelSockets's send API function.
  (def chsk-state state))

;; We can watch this atom for changes if we like.
;; (add-watch chsk-state :connected-uids
;;            (fn [_ _ old new]
;;              (when (not= old new)
;;                (debugf "Connected uids change: %s" new))))

(def ^{:private true} router (atom nil))

(defn stop-router!
  "Stop the client websocket router."
  []
  (when-let [stop-f @router] (stop-f)))

(defn start-router!
  "Start the client websocket router."
  [handler-multi-method] ;handshake-handler state-handler message-handler]
  (debug "start-router! handler-multi-method:\n%s" handler-multi-method)
  (stop-router!)
  (reset! router (sente/start-client-chsk-router!
                   ch-chsk
                   handler-multi-method))
  (debug "start-router!: reset complete"))
