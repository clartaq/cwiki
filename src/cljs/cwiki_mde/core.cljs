;;;
;;; This is the MDE Markdown editor, such as it is, derived from
;;; Carmen La's Reagent Markdown Editor in Reagent Recipes.
;;;

(ns cwiki-mde.core
  (:require [cwiki-mde.ws :as ws]
            [reagent.core :as reagent]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

(def options {:send-every-keystroke true})

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
                   (when (:send-every-keystroke options)
                     (ws/send-message! [:hey-server/content-updated
                                        {:?data new-content}]))
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

(def the-doc-content (reagent/atom nil))
(def the-page-map (reagent/atom nil))

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
        (infof "the-data: %s" the-data)
        (reset! the-page-map the-data)
        (reset! the-doc-content (:page_content @the-page-map))))
    (when (= message-id :hey-editor/shutdown-after-save)
      (let [new-location (str "/" (:page_title @the-page-map))]
        (infof "The new location is: %s" new-location)
        (ws/stop-router!)
        (.replace js/location new-location)))
    (when (= message-id :hey-editor/shutdown-after-cancel)
      (ws/stop-router!)
      (.replace js/location (.-referrer js/document)))))

;;
;; Layout and change handlers for the page.
;;

(defn tag-change-listener
  "Return a change listener function for the tag indicated."
  [page-map-atom n]
  (fn [arg]
    (let [new-tag (-> arg .-target .-value)]
      (swap! page-map-atom assoc-in [:tags n] new-tag)
      (when (:send-every-keystroke options)
        (ws/send-message! [:hey-server/content-updated
                           {:?data @page-map-atom}])))))

(defn make-tag-input-element
  "Return an input element for the nth tag from the vector."
  [page-map-atom n]
  ^{:key (str "tag-" n)}
  [:input {:type        "text"
           :class       "mde-tag-text-field"
           :placeholder (str "Tag #" (+ 1 n))
           :value       (nth (:tags @page-map-atom) n "")
           :on-change   (tag-change-listener page-map-atom n)}])

(defn make-tag-list-input-component
  "Build and return the piece of the page allowing tags to be edited."
  [page-map-atom]
  (tracef "tags %s" (:tags @page-map-atom))
  [:div {:class "tag-edit-container tag-edit-section"}
   [:label {:class "tag-edit-label"} "Tags"]
   [:div {:class "mde-tag-edit-list" :id "mde-tag-edit-list"}
    (doall (for [x (range 10)]
             (make-tag-input-element page-map-atom x)))]])

(defn make-title-input-element
  "Build and return an element for displaying/editing the post title"
  [page-map-atom]
  [:div {:class "mde-title-edit-section"}
   [:div {:class "form-label-div"}
    [:label {:class "form-label required"
             :for   "title"} "Page Title"]]
   [:input {:type      "text" :class "mde-form-title-field"
            :name      "page-title"
            :value     (if-let [title (:page_title @page-map-atom)]
                         title
                         "Enter a Title for the Page")
            :on-change (fn [arg]
                         (let [new-title (-> arg .-target .-value)]
                           (swap! page-map-atom assoc :page_title new-title)
                           (when (:send-every-keystroke options)
                             (ws/send-message! [:hey-server/content-updated
                                                {:?data @page-map-atom}]))))}]])

(defn the-editor-container
  "Starts the websocket router and returns a function that lays out
  the editor and preview area side-by-side."
  []
  (ws/start-router! editor-handshake-handler editor-state-handler
                    editor-message-handler)
  (fn []
    [:div {:class "mde-container"}
     (make-title-input-element the-page-map)
     (tracef "the tags: %s" (:tags @the-page-map))
     (make-tag-list-input-component the-page-map)
     [:div {:class "mde-content-label-div"}
      [:label {:class "form-label"
               :for   "content"} "Page Content"]]

     [:div {:class "mde-editor-and-preview-container"}
      [editor the-doc-content]
      [preview the-doc-content]]

     [:div {:class "mde-button-bar-container"}
      [:input {:type    "button"
               :id      "Save Button"
               :name    "save-button"
               :value   "Save Changes"
               :class   "form-button button-bar-item"
               :onClick #(do
                           (trace "Saw Click on Save Button!")
                           (swap! the-page-map assoc :page_content @the-doc-content)
                           (ws/send-message! [:hey-server/save-edited-document
                                              {:data @the-page-map}]))}]
      [:input {:type    "button"
               :id      "Cancel Button"
               :name    "cancel-button"
               :value   "Cancel"
               :class   "form-button button-bar-item"
               :onClick #(do
                           (trace "Saw Click on the Cancel Button!")
                           (ws/send-message! [:hey-server/cancel-editing]))}]]]))

(defn reload []
  (reagent/render [the-editor-container] (.getElementById js/document "editor-container")))

(defn ^:export main []
  (reload))
