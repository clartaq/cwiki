;;;
;;; Server-side Websocket/AJAX routes are setup here.
;;;

(ns cwiki.routes.ws
  (:require [compojure.core :refer [GET POST defroutes]]
            [cwiki.layouts.editor :as editor-layout]
            [cwiki.models.wiki-db :as db]
            [ring.util.response :refer [redirect status]]
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
  (def connected-uids connected-uids))                      ; Watchable, read-only atom

(defn- save-new-doc!
  "Save a completely new page to the database and display it."
  [title content tags author-id]
  (db/insert-new-page! title content tags author-id))

(defn- save-doc!
  "Save new or edited content."
  [page-map]
  (tracef "save-doc!: page-map: %s" page-map)
  (when-let [post-map page-map]
    (let [id (db/page-map->id post-map)
          title (db/page-map->title post-map)
          content (db/page-map->content post-map)
          tags (:tags post-map)]
      (if id
        (db/update-page-title-and-content! id title (set tags) content)
        (save-new-doc! title content tags (:page_author post-map))))))

(defn- send-document-to-editor
  "Get the post to be edited and send it to the editor."
  [client-id]
  (trace "server sending document")
  (chsk-send! client-id [:hey-editor/here-is-the-document
                         (editor-layout/get-post-map-for-editing)]))

(defn- content-updated
  "When the content of the post being edited changes, do something with it
  here if desired."
  [?data]
  (trace "Saw 'content updated' notification.")
  (when ?data
    (editor-layout/update-content-for-websocket ?data)))

(defn- save-doc-and-quit!
  "Save the edited post and ask the client to shut itself down."
  [client-id page-map]
  (trace "Editor asked to save edited document.")
  (let [page-to-return-to (db/page-map->title page-map)]
    (save-doc! page-map)
    (tracef "Here's the data that got saved: %s" page-map) ;?data)
    (tracef "Here's the page-to-return-to: %s" page-to-return-to)
    (chsk-send! client-id [:hey-editor/shutdown-after-save page-to-return-to])))

(defn- cancel-editing
  "Stop editing the post and ask the client to shut itself down."
  [client-id]
  (trace "Editor asked to cancel editing.")
  (chsk-send! client-id [:hey-editor/shutdown-after-cancel]))

(defn handle-message!
  "Handle any message that we know about. It is an error to send
  unrecognized messages."
  [{:keys [id client-id ?data]}]
  (tracef "handle-message!: id: %s, client-id: %s" id client-id)
  (cond
    (= id :hey-server/send-document-to-editor) (send-document-to-editor client-id)
    (= id :hey-server/content-updated) (content-updated ?data)
    (= id :hey-server/save-doc) (save-doc! (:data ?data))
    (= id :hey-server/save-doc-and-quit) (save-doc-and-quit! client-id (:data ?data))
    (= id :hey-server/cancel-editing) (cancel-editing client-id)
    (= id :chsk/uidport-open) (trace ":chsk/uidport-open")
    (= id :chsk/uidport-close) (trace ":chsk/uidport-close")
    (= id :chsk/ws-ping) (trace ":chsk/ws-ping")
    :default (errorf "Unknown message id received: %s" id)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; Sente event router (our `event-msg-handler` loop)

(defonce router_ (atom nil))

(defn stop-ws-router!
  "Stop the websocket router."
  []
  (info "Stopping websocket router.")
  (when-let [stop-fn @router_]
    (stop-fn)))

(defn start-ws-router!
  "Start the websocket router."
  []
  (stop-ws-router!)
  (info "Starting websocket router.")
  (reset! router_
          (sente/start-server-chsk-router!
            ch-chsk handle-message!)))

(defroutes websocket-routes
           (GET "/ws" req (ring-ajax-get-or-ws-handshake req))
           (POST "/ws" req (ring-ajax-post req)))
