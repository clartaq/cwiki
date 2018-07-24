(ns cwiki.components.socket-server
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET POST]]
            [cwiki.layouts.editor :as editor-layout]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

(defprotocol SocketServer-Protocol
  (save-new-doc! [this title content tags author-id])
  (save-doc! [this websocket-data])
  (send-document-to-editor! [this client-id])
  (content-updated! [this data])
  (save-edited-document! [this client-id data])
  (cancel-editing [this cliend-id])
  (handle-messages! [this channel-map]))
;;
;; WebSocket message handler and related functions.
;;

;(defn- save-new-doc!
;  "Save a completely new page to the database and display it."
;  [^Db db title content tags author-id]
;  (.insert-new-page db title content tags author-id))
;  ;(db/insert-new-page! title content tags author-id))
;
;(defn- save-doc!
;  "Save new, edited content."
;  [^Db db websocket-data]
;  (tracef "save-doc!: websocket-data: %s" websocket-data)
;  (let [post-map (:data websocket-data)
;        id (.page-map->id db post-map) ;(db/page-map->id post-map)
;        title (.page-map->title db post-map) ;db/page-map->title post-map)
;        content (.page-map->content db post-map) ;db/page-map->content post-map)
;        tags (:tags post-map)]
;    (if id
;      (.update-page-title-and-content! db id title content) ;(set tags)db/update-page-title-and-content! id title (set tags) content)
;      (save-new-doc! db title content tags (:page_author post-map)))))
;
;(defn send-document-to-editor
;  "Get the post to be edited and send it to the editor."
;  [client-id]
;  (trace "server sending document")
;  (ws-send-func client-id [:hey-editor/here-is-the-document
;                           (editor-layout/get-post-map-for-editing)]))
;
;(defn content-updated
;  "When the content of the post being edited changes, do something with it
;  here if desired."
;  [?data]
;  (trace "Saw 'content updated' notification.")
;  (when ?data
;    (editor-layout/update-content-for-websocket ?data)))
;
;(defn save-edited-document
;  "Save the edited post and ask the client to shut itself down."
;  [^Db db client-id ?data]
;  (trace "Editor asked to save edited document.")
;  (when ?data
;    (save-doc! db ?data))
;  (ws-send-func client-id [:hey-editor/shutdown-after-save]))
;
;(defn cancel-editing
;  "Stop editing the post and ask the client to shut itself down."
;  [client-id]
;  (trace "Editor asked to cancel editing.")
;  (ws-send-func client-id [:hey-editor/shutdown-after-cancel]))
;
;(defn handle-message!
;  "Handle any message that we know about. It is an error to send
;  unrecognized messages."
;  [{:keys [id client-id ?data]}]
;  (tracef "handle-message!: id: %s, client-id: %s" id client-id)
;  (cond
;    (= id :hey-server/send-document-to-editor) (send-document-to-editor client-id)
;    (= id :hey-server/content-updated) (content-updated ?data)
;    (= id :hey-server/save-edited-document) (save-edited-document client-id ?data)
;    (= id :hey-server/cancel-editing) (cancel-editing client-id)
;    (= id :chsk/uidport-open) (trace ":chsk/uidport-open")
;    (= id :chsk/uidport-close) (trace ":chsk/uidport-close")
;    (= id :chsk/ws-ping) (trace ":chsk/ws-ping")
;    :default (errorf "Unknown message id received: %s" id)))
;


;(defroutes websocket-routes
;           (GET "/ws" req (ring-ajax-get-or-ws-handshake req))
;           (POST "/ws" req (ring-ajax-post req)))

(defn sente-routes [{{ring-ajax-post :ring-ajax-post
                      ring-ajax-get-or-ws-handshake :ring-ajax-get-or-ws-handshake}
                     :sente}]
  (routes
    (GET "/ws" req (ring-ajax-get-or-ws-handshake req))
    (POST "/ws" req (ring-ajax-post req))))

(defrecord SocketServer [ring-ajax-post ring-ajax-get-or-ws-handshake
                         ch-chsk chsk-send! connected-uids router
                         web-server-adapter handler options db]
  component/Lifecycle

  (start [component]
    (info "Starting socket server.")
    (let [handler (get-in component [:sente-handler :handler] handler)
          {:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids]}
          (sente/make-channel-socket-server! web-server-adapter options)
          component (assoc component
                      :ring-ajax-post ajax-post-fn
                      :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
                      :ch-chsk ch-recv
                      :chsk-send! send-fn
                      :connected-uids connected-uids)]
      (infof "    keys: %s" (.keySet component))
      (infof "    values: %s" (.values component))
      (assoc component
        :router (sente/start-chsk-router!
                  ch-recv (if (:wrap-component? options)
                            (handler component)
                            handler)))))
  (stop [component]
    (info "Stopping socket server.")
    (if-let [stop-f router]
      (assoc component :router (stop-f))
      component))

  SocketServer-Protocol

  ;  "Save a completely new page to the database and display it."
  (save-new-doc!
    [this title content tags author-id]
    (.insert-new-page db title content tags author-id))
    ;(db/insert-new-page! title content tags author-id))

  ;  "Save new, edited content."
  (save-doc!
    [_ websocket-data]
    (info "save-doc!: websocket-data: %s" websocket-data)
    (let [post-map (:data websocket-data)
          id (.page-map->id db post-map) ;(db/page-map->id post-map)
          title (.page-map->title db post-map) ;db/page-map->title post-map)
          content (.page-map->content db post-map) ;db/page-map->content post-map)
          tags (:tags post-map)]
      (if id
        (.update-page-title-and-content! db id title content) ;(set tags)db/update-page-title-and-content! id title (set tags) content)
        (save-new-doc! db title content tags (:page_author post-map)))))

  ;  "Get the post to be edited and send it to the editor."
  (send-document-to-editor!
    [_ client-id]
    (info "server sending document")
    (chsk-send! client-id [:hey-editor/here-is-the-document
                           (editor-layout/get-post-map-for-editing)]))

  ;  "When the content of the post being edited changes, do something with it
  ;  here if desired."
  (content-updated!
    [_ ?data]
    (info "Saw 'content updated' notification.")
    (when ?data
      (editor-layout/update-content-for-websocket ?data)))

  ;  "Save the edited post and ask the client to shut itself down."
  (save-edited-document!
    [_ client-id ?data]
    (info "Editor asked to save edited document.")
    (when ?data
      (save-doc! db ?data))
    (chsk-send! client-id [:hey-editor/shutdown-after-save]))

  ;  "Stop editing the post and ask the client to shut itself down."
  (cancel-editing
    [_ client-id]
    (info "Editor asked to cancel editing.")
    (chsk-send! client-id [:hey-editor/shutdown-after-cancel]))

  ;  "Handle any message that we know about. It is an error to send
  ;  unrecognized messages."
  (handle-messages!
    [this {:keys [id client-id ?data]}]
    (infof "handle-message!: id: %s, client-id: %s" id client-id)
    (cond
      (= id :hey-server/send-document-to-editor) (send-document-to-editor! this client-id)
      (= id :hey-server/content-updated) (content-updated! this ?data)
      (= id :hey-server/save-edited-document) (save-edited-document! this client-id ?data)
      (= id :hey-server/cancel-editing) (cancel-editing this client-id)
      (= id :chsk/uidport-open) (trace ":chsk/uidport-open")
      (= id :chsk/uidport-close) (trace ":chsk/uidport-close")
      (= id :chsk/ws-ping) (trace ":chsk/ws-ping")
      :default (errorf "Unknown message id received: %s" id)))

  )

(defn new-channel-socket-server
  ; ([event-msg-handler]
  ;  (new-channel-socket-server event-msg-handler {}))
  ([options]                                                ;event-msg-handler options]
   (let [my-options (merge options
                           {:user-id-fn (fn [ring-req] (get-in ring-req [:params :client-id]))})]
     (map->SocketServer {:web-server-adapter (get-sch-adapter) ;web-server-adapter
                         :handler            handle-messages! ;event-msg-handler
                         :options            my-options}))))

