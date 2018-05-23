;;;
;;; This is the MDE Markdown editor, such as it is, derived from
;;; Carmen La's Reagent Markdown Editor in Reagent Recipes.
;;;

(ns cwiki-mde.core
  (:require [ajax.core :refer [GET]]
            [cwiki-mde.ws :as ws]
            [reagent.core :as reagent]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]
            [cljs.pprint :as pp]))

(defn highlight-code
  "Highlights any <pre><code></code></pre> blocks in the html."
  [html-node]
  (info "highlight-code")
  (let [nodes (.querySelectorAll html-node "pre code")]
    (loop [i (.-length nodes)]
      (when-not (neg? i)
        (when-let [item (.item nodes i)]
          (.highlightBlock js/hljs item))
        (recur (dec i))))))

(defn typeset-latex
  "Typeset any mathematics in the text."
  [latex-node]
  (js/MathJax.Hub.Queue #js ["Typeset" js/MathJax.Hub latex-node]))

(defn markdown-component
  "Set the content in the preview pane, optionally highlighting it."
  [content]
  [(with-meta
     (fn []
       [:div {:dangerouslySetInnerHTML
              {:__html (-> content str js/marked)}}])
     {:component-did-mount
      (fn [this]
        (let [node (reagent/dom-node this)]
          (typeset-latex node)
          (highlight-code node)))})])

(defn editor
  "This is the editing area, just a textarea. It sends an update
  message over the websocket to the server."
  [content]
  [:textarea
   {:class     "mde-editor-class"
    :value     @content
    :on-change (fn [arg]
                 (let [new-content (-> arg .-target .-value)]
                   (reset! content new-content)
                   (ws/send-message! [:hey-server/content-updated {:?data new-content}])
                   new-content))}])

(defn preview
  "The preview div."
  [content]
  [:div {:class "mde-preview-class" :id "mde-preview-id"}
   (when (not-empty @content)
     (markdown-component @content))])

;;
;; Websocket messages handlers to work with the server.
;;

(defn send-document-to-save
  "Send the message (including content) to the server to save the
  edited document."
  [doc]
  (info "Editor: Telling server to save the document.")
  (ws/send-message! [:hey-server/save-edited-document {:data @doc}]))

(def the-doc-content (reagent/atom nil))

(defn editor-handshake-handler
  "Handle the handshake event between the server and client. This function
  sends the message to the server to send over the document for editing."
  [{:keys [?data]}]
  (info "Editor: Connection established!")
  (ws/send-message! [:hey-server/send-document-to-editor {}]))

(defn editor-state-handler
  "Handle changes in the state of the editor."
  [{:keys [?data]}]
  (info "Editor: State changed!"))

(defn editor-message-handler
  [{:keys [?data]}]
  (infof "Editor: Message received: %s" ?data)
  (let [message-id (first ?data)]
    (infof "message-id: %s" message-id)
    (when (= message-id :hey-editor/here-is-the-document)
      (when-let [the-data (second ?data)]
        (reset! the-doc-content the-data)))
    (when (= message-id :hey-editor/shutdown-now)
      (ws/stop-router!)
      (.replace js/location (.-referrer js/document)))))

(defn the-editor-container
  "Starts the websocket router and returns a function that lays out
  the editor and preview area side-by-side."
  []
  (ws/start-router! editor-handshake-handler editor-state-handler
                    editor-message-handler)
  (fn []
    [:div {:class "mde-container"}
     [:div {:class "mde-editor-and-preview-container"}
      [editor the-doc-content]
      [preview the-doc-content]]

     [:div {:class "button-bar-container"}
      [:input {:type    "button"
               :id      "Save Button"
               :name    "save-button"
               :value   "Save Changes"
               :class   "form-button button-bar-item"
               :onClick #(do
                           (.log js/console "Saw Click on Save Button!")
                           (.log js/console (str "Here's the document: " @the-doc-content))
                           (send-document-to-save the-doc-content))}]
      [:input {:type    "button"
               :id      "Cancel Button"
               :name    "cancel-button"
               :value   "Cancel"
               :class   "form-button button-bar-item"
               :onClick #(do
                           (.log js/console "saw click on CANCEL button")
                           (ws/send-message! [:hey-server/cancel-editing]))}]]]))

(defn reload []
  (reagent/render [the-editor-container] (.getElementById js/document "editor-container")))

(defn ^:export main []
  (reload))
