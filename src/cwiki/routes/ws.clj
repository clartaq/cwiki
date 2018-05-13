;;;
;;; Websocket/AJAX routes are setup here.
;;;

(ns cwiki.routes.ws
  (:require [compojure.core :refer [GET POST defroutes]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

(let [{:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket!
        (get-sch-adapter)
        {:user-id-fn (fn [ring-req] (get-in ring-req [:params :client-id]))})]

  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)                                     ; ChannelSocket's receive channel
  (def chsk-send! send-fn)                                  ; ChannelSocket's send API fn
  (def connected-uids connected-uids)                       ; Watchable, read-only atom
  )

(defn save-doc! [content]
  (infof "save-doc!: content: %s" content)
  ;(db/save-doc! doc)
  content)

(defn update-content
  "Update the local copy of the editor content."
  [new-content]
  (infof "New version of content: %s" new-content))

(defn handle-message! [{:keys [id client-id ?data]}]
  (infof "handle-message!: id: %s, client-id: %s" id client-id)
  (when (= id :mde/content-updated)
    (infof "Saw 'content updated' notification.")
    (when ?data
      (update-content ?data))))

(defn send-document-to-editor [req]
  (infof "Responding to request to send the document for editing: %s" req)
  "The document.")

;;;; Sente event router (our `event-msg-handler` loop)

(defonce router_ (atom nil))

(defn stop-router!
  []
  (info "Stopping websocket router")
  (when-let [stop-fn @router_]
    (stop-fn)))

(defn start-router! []
  (info "Starting websocket router.")
  (stop-router!)
  (reset! router_
          (sente/start-server-chsk-router!
            ch-chsk handle-message!)))

(defroutes websocket-routes
           (GET "/serve-document-to-editor" req (send-document-to-editor req))
           (GET "/ws" req (ring-ajax-get-or-ws-handshake req))
           (POST "/ws" req (ring-ajax-post req)))
