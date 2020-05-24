;;;
;;; Server-side Websocket/AJAX routes are setup here.
;;;

(ns cwiki.routes.ws
  (:require [clojure.string :as s]
            [compojure.core :refer [GET POST defroutes]]
            [cwiki.layouts.editor :as editor-layout]
            [cwiki.models.wiki-db :as db]
            [cwiki.util.percent-encode :as pe]
            [ring.util.response :refer [redirect status]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [taoensso.timbre :as log :refer [log trace debug info warn error fatal report
                                             logf tracef debugf infof warnf errorf fatalf reportf
                                             spy get-env]])
  (:import (java.net URL)))

(log/set-level! :info)

(let [{:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket!
        (get-sch-adapter)
        {:user-id-fn (fn [ring-req] (get-in ring-req [:params :client-id]))})]

  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)                 ;; ChannelSocket's receive channel
  (def chsk-send! send-fn)              ;; ChannelSocket's send API fn
  (def connected-uids connected-uids))  ;; Watchable, read-only atom

;; We can watch this atom for changes if we like
;; (add-watch connected-uids :connected-uids
;;            (fn [_ _ old new]
;;              (when (not= old new)
;;                (debugf "Connected uids changed: new: %s" new))))

(defn- save-new-doc!
  "Save a completely new page to the database and display it. Return the
  page id of the newly saved page. If a page with the same title is
  already present, reply to the editor with a 'Page Alredy Exists' message."
  [client-id title content tags author-id]
  (if (db/find-post-by-title title)
    (chsk-send! client-id [:hey-editor/that-page-already-exists])
    (let [pm (db/insert-new-page! title content tags author-id)
          id (db/page-map->id pm)]
      (chsk-send! client-id [:hey-editor/here-is-the-id id])
      id)))

(defn- save-doc!
  "Save new or edited content. Return the page id of the saved document."
  [client-id page-map]
  (log/debugf "save-doc!: page-map: %s" page-map)
  (when-let [post-map page-map]
    (let [id (db/page-map->id post-map)
          title (db/page-map->title post-map)
          content (db/page-map->content post-map)
          tags (:tags post-map)]
      (if id
        (do
          (db/update-page-title-and-content! id title (set tags) content)
          id)
        (save-new-doc! client-id title content tags (:page_author post-map))))))

(defn- send-document-to-editor
  "Get the post to be edited and send it to the editor."
  [client-id]
  (log/debugf "Enter :hey-server/send-document-to-editor: client-id: %s" client-id)
  (let [info-to-send (editor-layout/get-post-map-for-editing)]
    (log/debugf "    info-to-send: %s" info-to-send)
    (when info-to-send
      (chsk-send! client-id [:hey-editor/here-is-the-document info-to-send])
    (log/debug "Exit :hey-server/send-document-to-editor"))))

(defn- content-updated!
  "When the content of the post being edited changes, do something with it
  here if desired."
  [?data]
  (log/debug "Saw 'content updated' notification.")
  (when ?data
    (editor-layout/update-content-for-websocket ?data)))

(defn- tags-updated!
  [?data]
  (log/debug "Saw 'tags updated' notification.")
  (when ?data
    (editor-layout/update-content-for-websocket ?data)))

(defn- title-updated!
  [?data]
  (log/debug "Saw 'title updated' notification. ?data: " ?data))

(defn- page-from-referrer
  [referrer]
  (let [url (URL. referrer)
        path (.getPath url)
        page-title (s/replace-first path #"/" "")]
    (debugf "page-from-referrer returning: %s" page-title)
    page-title))

(defn- page-to-return-to
  "Returns the title of the page that the editor should return to after it
  shuts down."
  [page-id page-title referrer]
  ;; If the page has been saved, it has a page id. Get the page title for
  ;; that id. If the page has never been saved, there will be no id for it.
  ;; In that case, return to the referring page. If the referring page was
  ;; the home page for the wiki, get the title of that page and return it.
  ;; The special handling for the home page is because the referrer does not
  ;; include the page title, just the slash character
  (let [plain-title (cond
                      (identity page-id) (db/page-id->title page-id)
                      (s/ends-with? referrer "/") (db/get-option-value :root_page)
                      (and page-title (db/title->page-id page-title)) page-title
                      :default (page-from-referrer referrer))]
    (pe/percent-encode plain-title)))

(defn- quit-editing!
  "Handle the message from the editor that it wants to quit editing.
  Return the title of the page the editor should redirect to after it
  finished its shutdown tasks."
  [client-id {:keys [page-id page-title referrer]}]
  (let [page-to-return-to (page-to-return-to page-id page-title referrer)]
    (chsk-send! client-id [:hey-editor/shutdown-and-go-to page-to-return-to])))

(defn handle-message!
  "Handle any message that we know about. It is an error to send
  unrecognized messages."
  [{:keys [id client-id ?data]}]
  (log/debugf "handle-message!: id: %s, client-id: %s" id client-id)
  (cond
    (= id :hey-server/send-document-to-editor) (send-document-to-editor client-id)
    (= id :hey-server/content-updated) (content-updated! ?data)
    (= id :hey-server/tags-updated) (tags-updated! ?data)
    (= id :hey-server/title-updated) (title-updated! ?data)
    (= id :hey-server/save-doc) (save-doc! client-id (:data ?data))
    (= id :hey-server/quit-editing) (quit-editing! client-id ?data)
    (= id :chsk/uidport-open) (log/trace ":chsk/uidport-open")
    (= id :chsk/uidport-close) (log/trace ":chsk/uidport-close")
    (= id :chsk/ws-ping) (log/trace ":chsk/ws-ping")
    :default (log/errorf "Unknown message id received: %s" id)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; Sente event router (our `event-msg-handler` loop)

(defonce router_ (atom nil))

(defn stop-ws-router!
  "Stop the websocket router."
  []
  (log/info "Stopping websocket router.")
  (when-let [stop-fn @router_]
    (stop-fn)))

(defn start-ws-router!
  "Start the websocket router."
  []
  (stop-ws-router!)
  (log/info "Starting websocket router.")
  (reset! router_
          (sente/start-server-chsk-router!
            ch-chsk handle-message!)))

(defroutes websocket-routes
           (GET "/ws" req (ring-ajax-get-or-ws-handshake req))
           (POST "/ws" req (ring-ajax-post req)))
