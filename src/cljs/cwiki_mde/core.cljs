;;;;
;;;; This is the MDE Markdown editor, such as it is, derived from
;;;; Carmen La's Reagent Markdown Editor in Reagent Recipes.
;;;;

(ns cwiki-mde.core
  (:require [cljs.core.async :as async :refer [chan <! >!]]
            [clojure.string :refer [blank?]]
            [cljs.pprint :as pprint ]
            [cwiki-mde.editor-commands :as cmd]
            [cwiki-mde.keyboard-shortcuts :as kbs]
    ;; Include dragger so it gets bundled in output js file.
            [cwiki-mde.dragger :as dr]
            [cwiki-mde.tag-editor :as te]
            [cwiki-mde.ws :as ws]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [taoensso.timbre :as timbre :refer-macros [log trace debug info warn error fatal report
                                                       logf tracef debugf infof warnf errorf fatalf reportf
                                                       spy get-env]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;;;-----------------------------------------------------------------------------
;;; Global variables.
;;;

;; The delay-handle stores the handle to the autosave countdown timer.
(def ^{:private true} glbl-delay-handle (atom nil))

;; Flag indicating that it is ok to exit the editor, even if there are
;; unsaved changes. Used by the "Unsaved Changes" modal dialog to indicate
;; whether the use wants to exit and lose the changes.
(def ^{:private true} glbl-ok-to-exit (atom nil))

;; A value set after the first save of a new page. When the page is saved,
;; the server responds with its database id. This value is used in all
;; subsequent saves. Not doing so would allow multiple "duplicates" of the
;; new page to be saved if the user changes page titles between saved.
(def ^{:private true} glbl-id-for-next-save (atom nil))

;; A channel used to retrieve the page map asynchronously when it becomes
;; available from the websocket.
(defonce ^{:private true} got-page-channel (chan))

;;;-----------------------------------------------------------------------------
;;; Utility functions.
;;;

(defn- get-element-by-id [the-id]
  (.getElementById js/document the-id))

(defn- select-all
  "Select the entire contents of the DOM element."
  [ele]
  (.setSelectionRange ele 0 (.-length (.-value ele))))

(defn- is-editor-dirty? [editor-state]
  @(:dirty-flag editor-state))

(defn- highlight-code
  "Highlights any <pre><code></code></pre> blocks in the html."
  [html-node]
  (trace "highlight-code")
  (let [nodes (.querySelectorAll html-node "pre code")]
    (loop [i (.-length nodes)]
      (when-not (neg? i)
        (when-let [item (.item nodes i)]
          (.highlightBlock js/hljs item))
        (recur (dec i))))))

(defn- typeset-latex
  "Typeset any mathematics in the text."
  [latex-node]
  (js/MathJax.Hub.Queue #js ["Typeset" js/MathJax.Hub latex-node]))

(defn- markdown-component
  "Set the content in the containing node, optionally highlighting it."
  [content]
  [(with-meta
     (fn []
       [:div {:dangerouslySetInnerHTML
              {:__html (-> content str js/marked)}}])
     {:component-did-mount
      (fn [this]
        (let [node (rdom/dom-node this)]
          (typeset-latex node)
          (highlight-code node)))})])

(defn- toggle-modal
  "Toggle the display state of the modal dialog with the given id."
  [ele-id]
  (let [close-button (get-element-by-id ele-id)
        overlay (get-element-by-id "modal-overlay")]
    (when (and close-button overlay)
      (.toggle (.-classList close-button) "closed")
      (.toggle (.-classList overlay) "closed"))))

(defn- tell-server-to-quit
  [editor-state]
  (let [fxn (:re-assembler-fn editor-state)
        new-page-map (fxn)
        page_id (:page_id new-page-map)
        page_title (:page_title new-page-map)
        referrer (.-referrer js/document)]
    (ws/chsk-send! [:hey-server/quit-editing
                       {:page-id    page_id
                        :page-title page_title
                        :referrer   referrer}])))

;;;-----------------------------------------------------------------------------
;;; Websocket message handlers to work with the server.
;;;

(defn- doc-save-fn
  "Send a message to the server to save the document."
  [page-map]
  (ws/chsk-send! [:hey-server/save-doc {:data page-map}]))

(defn- editor-handshake-handler
  "Handle the handshake event between the server and client. This function
  sends the message to the server to send over the document for editing."
  [{:keys [?data]}]
  (debugf "Enter editor-handshake-handler: ?data: " ?data)
  (ws/chsk-send! [:hey-server/send-document-to-editor {}])
  (debug "Exit editor-handshake-handler"))

(defn- editor-state-handler
  "Handle changes in the state of the editor."
  [{:keys [?data]}]
  (trace "Editor: Connection state changed!"))

(declare toggle-duplicate-title-modal)

(defn- editor-message-handler
  [{:as ev-msg :keys [?data]}]
  (debugf "editor-message-handler: ?data: %s" ?data)
  (when ?data
    (debugf "?data %s" ?data))
  (let [message-id (first ?data)]
    (debugf "editor-message-handler: message-id: %s" message-id)
    (cond
      (= message-id
         :hey-editor/here-is-the-document) (when-let [page-map (second ?data)]
                                             (debugf "editor-message-handler: the-data: %s"
                                                     (with-out-str
                                                       (pprint/pprint page-map)))
                                             ; This is where the page map is
                                             ; put on the channel asynchronously.
                                             (go (>! got-page-channel page-map)))
      (= message-id
         :hey-editor/here-is-the-id) (reset! glbl-id-for-next-save (second ?data))

      (= message-id
         :hey-editor/that-page-already-exists) (do
                                                 (trace "Saw :hey-editor/that-page-already-exists")
                                                 (toggle-duplicate-title-modal))
      (= message-id
         :hey-editor/shutdown-and-go-to) (when-let [new-location (str "/" (second ?data))]
                                           (tracef "The new location is: %s" new-location)
                                           (kbs/unbind-shortcut-keys)
                                           (ws/stop-router!)
                                           (.replace js/location new-location)))))

;;;-----------------------------------------------------------------------------
;;; Auto-save-related functions.
;;;

(defn- clear-autosave-delay! []
  "Clear the autosave countdown timer."
  (.clearTimeout js/window @glbl-delay-handle))

(defn- restart-autosave-delay!
  "Restart the autosave countdown timer."
  [the-save-fn delay-ms]
  (reset! glbl-delay-handle (.setTimeout js/window the-save-fn delay-ms)))

(defn- notify-autosave
  "Notify the autosave functionality that a change has occurred. When the
  autosave duration is greater than zero, restarts the countdown until the
  document is saved automatically. Will NOT perform autosave until the title
  of new pages has been changed from the default for new pages."
  [{:keys [editor_autosave_interval default-new-page-name
           page-title-atom assemble-and-save-fn]}]
  (let [delay (* 1000 editor_autosave_interval)]
    (when (and (pos? delay)
               (not= default-new-page-name @page-title-atom))
      (clear-autosave-delay!)
      (restart-autosave-delay! assemble-and-save-fn delay))))

(defn- mark-page-dirty
  "Mark the page as dirty and reset the autosave timer."
  [editor-state]
  (notify-autosave editor-state)
  (reset! (:dirty-flag editor-state) true))

;;;-----------------------------------------------------------------------------
;;; Dialogs
;;;

(defn- toggle-unsaved-changes-modal
  "Toggle the display state of the modal dialog that is shown when there
  are unsaved changes."
  []
  (toggle-modal "unsaved-changes-modal"))

(defn- layout-unsaved-changes-warning-dialog
  "Brings up a modal dialog that informs the user that there are unsaved
  changes to the page. Asks them what to do before exiting and losing
  the new work."
  [editor-state]
  (let [msg [:p {:class "dialog-message"}
             "There are unsaved changes on this page. Do you really want "
             "to quit and lose those changes?"]]
    [:div {:class "modal closed" :id "unsaved-changes-modal" :role "dialog"}
     [:header {:class "modal-header"}
      [:section {:class "modal-header-left"} "Unsaved Changes"]
      [:section {:class "modal-header-right"}
       [:input {:type     "button"
                :class    "form-button"
                :id       "close-unsaved-dialog-button"
                :value    "Close"
                :title    "Close this dialog and return to the editor."
                :on-click #(do
                             (reset! glbl-ok-to-exit nil)
                             (toggle-unsaved-changes-modal))}]]]
     [:section {:class "modal-guts"} msg]
     [:div {:class "modal-footer"}
      [:section {:class "button-bar-container"}
       [:input {:type     "button"
                :class    "form-button button-bar-item"
                :id       "quit-without-saving-button-id"
                :value    "Yes. Exit without Saving."
                :title    "Exit without saving changes"
                :on-click #(do
                             (reset! glbl-ok-to-exit true)
                             (toggle-unsaved-changes-modal)
                             (tell-server-to-quit editor-state))}]
       [:input {:type     "button"
                :class    "form-button button-bar-item"
                :value    "No. Return to the Editor."
                :title    "Close this dialog and return to the editor"
                :on-click #(do
                             (reset! glbl-ok-to-exit nil)
                             (toggle-unsaved-changes-modal))}]]]]))

(defn- toggle-duplicate-title-modal
  "Toggle the display state of the modal dialog that is shown when the
  user is trying to save a new page with a title that duplicates a page
  already in the wiki."
  []
  (toggle-modal "duplicate-title-modal"))

(defn- layout-duplicate-page-warning-dialog
  "Notify the user that a page with the same title already exists in the
  wiki and allow them to return to the editor."
  []
  (let [msg [:div
             [:p {:class "dialog-message"}
              "There is already a page in the wiki with this title."
              " Duplicate titles are not allowed."]
             [:p {:class "dialog-message"}
              "Return to the editor and change the title before saving."]]]
    [:div {:class "modal closed" :id "duplicate-title-modal" :role "dialog"}
     [:header {:class "modal-header"}
      [:section {:class "modal-header-left"} "Duplicate Title"]
      [:section {:class "modal-header-right"}
       [:input {:type     "button"
                :class    "form-button"
                :id       "close-duplicate-dialog-button"
                :value    "Close"
                :title    "Close this dialog and return to the editor."
                :on-click #(toggle-duplicate-title-modal)}]]]
     [:section {:class "modal-guts"} msg]
     [:div {:class "modal-footer"}
      [:section {:class "button-bar-container"}
       [:input {:type     "button"
                :class    "form-button button-bar-item"
                :value    "Ok. Return to the Editor."
                :title    "Close this dialog and return to the editor"
                :on-click #(toggle-duplicate-title-modal)}]]]]))

(defn- toggle-markdown-help-modal
  "Toggle visibility of the Markdown help dialog."
  []
  (toggle-modal "markdown-help-modal"))

(defn- layout-markdown-help-dialog
  "Layout the dialog to contain the Markdown help and return it."
  [page-map]
  (let [state (r/atom nil)]
    (r/create-class
      {:display-name        "markdown-help-dialog"

       ; The purpose here is to change the default size of the dialog based on
       ; the size of the editor window. Since the Markdown help is so long, we
       ; want to allow it to take up a bigger portion of the window.
       :component-did-mount (fn []
                              (let [icn (get-element-by-id "inner-editor-container-id")
                                    dw (* 4 (int (/ (.-offsetWidth icn) 5)))
                                    dh (* 4 (int (/ (.-offsetHeight icn) 5)))]
                                (reset! state {:width dw :height dh})))

       :reagent-render      (fn []
                              (let [help-html (:markdown-help page-map)
                                    width (:width @state)
                                    height (:height @state)]
                                [:div {:class "modal closed"
                                       :id    "markdown-help-modal"
                                       :role  "dialog"
                                       :style {:width width :height height}}
                                 [:header {:class "modal-header"}
                                  [:section {:class "modal-header-left"} "Markdown Help"]
                                  [:section {:class "modal-header-right"}
                                   [:input {:type     "button"
                                            :class    "form-button"
                                            :id       "close-markdown-help-button"
                                            :value    "Close"
                                            :title    "Close this dialog and return to the editor."
                                            :on-click #(toggle-markdown-help-modal)}]]]
                                 [:section {:class "modal-guts"}
                                  (if help-html
                                    (markdown-component help-html)
                                    [:p "The help text is not available!"])]
                                 [:div {:class "modal-footer"}
                                  [:section {:class "button-bar-container"}
                                   [:input {:type     "button"
                                            :class    "form-button button-bar-item"
                                            :value    "Ok. Return to the Editor."
                                            :title    "Close this dialog and return to the editor"
                                            :on-click #(toggle-markdown-help-modal)}]]]]))})))

;;;-----------------------------------------------------------------------------
;;; The editor components.
;;;

(defn- layout-title-editor
  "Lay out the title editing control and return the layout."
  [editor-state]
  (fn [{:keys [page-title-atom] :as editor-state}]
    (let [ro (when (= "Front Page" @page-title-atom)
               {:readOnly "readOnly"})
          inp (merge ro
                     {:type       "text"
                      :class      "mde-form-title-field"
                      :name       "page-title"
                      :id         (:editor-title-input-id editor-state)
                      :auto-focus true
                      :value      (if-let [title @page-title-atom]
                                    (do
                                      (when (= title "favicon.ico")
                                        (info "Saw funky title request for favicon.icl"))
                                      title)
                                    "Enter a Title for the Page Here")
                      :on-change  (fn [arg]
                                    (let [new-title (-> arg .-target .-value)]
                                      (mark-page-dirty editor-state)
                                      (reset! page-title-atom new-title)
                                      (when (:send-every-keystroke editor-state)
                                        (ws/chsk-send! [:hey-server/title-updated
                                                           {:data new-title}]))))})]
      [:section {:class "mde-title-edit-section"}
       [:div {:class "form-label-div"}
        [:label {:class "form-label required"
                 :for   "page-title"} "Page Title"]]
       [:input inp]])))

(defn- layout-editor-header
  "Lay out the header section for the editor. Includes the title, tags, and
  button bar."
  [editor-state]
  (fn [editor-state]
    [:header {:class "editor-header"}
     [layout-title-editor editor-state]
     [te/layout-tag-list editor-state]]))

(defn- quit-editor-fn
  "Warn the user about any unsaved changes, if any. Otherwise, quit the editor."
  [editor-state]
  (if @(:dirty-flag editor-state)
    (toggle-unsaved-changes-modal)
    (tell-server-to-quit editor-state)))

(defn- layout-editor-button-bar
  "Layout the editor button bar. Tedious but trivial."
  [editor-state]
  (fn [editor-state]
    [:section {:class "editor-button-bar"}
     ;Buttons on the left side.
     [:section.editor-button-bar--left

      [:input {:type     "button"
               :id       "done-button"
               :name     "done-button"
               :tabIndex 0
               :value    "Done"
               :title    "Quit the Editor"
               :class    "form-button button-bar-item editor-button-bar--done-button"
               :onClick  #(cmd/quit-editor-cmd editor-state)}]

      [:span.editor-button-bar--gap]

      [:button.editor-button-bar--button
       {:title    "Make selection bold"
        :tabIndex 0
        :on-click #(println "Saw click on bold button.")
        :disabled true}
       [:i.editor-button-bar--icon.bold-icon]]

      [:button.editor-button-bar--button
       {:title    "Make selection italic"
        :tabIndex 0
        :on-click #(println "Saw click on italic button.")
        :disabled true}
       [:i.editor-button-bar--icon.italic-icon]]

      [:button.editor-button-bar--button
       {:title    "Underline selection"
        :tabIndex 0
        :on-click #(println "Saw click on underline button.")
        :disabled true}
       [:i.editor-button-bar--icon.underline-icon]]

      [:button.editor-button-bar--button
       {:title    "Strike through selection"
        :tabIndex 0
        :on-click #(println "Saw click on strike-through button.")
        :disabled true}
       [:i.editor-button-bar--icon.strike-icon]]

      [:span.editor-button-bar--gap]

      [:button.editor-button-bar--button
       {:title    "Make selection a header"
        :tabIndex 0
        :on-click #(println "Saw click on header button.")
        :disabled true}
       [:i.editor-button-bar--icon.header-icon]]

      [:button.editor-button-bar--button
       {:title    "Format selection as code"
        :tabIndex 0
        :on-click #(println "Saw click on code button.")
        :disabled true}
       [:i.editor-button-bar--icon.code-icon]]

      [:button.editor-button-bar--button
       {:title    "Make a bulleted (unordered) list"
        :tabIndex 0
        :on-click #(println "Saw click on bullet list button.")
        :disabled true}
       [:i.editor-button-bar--icon.list-bullet-icon]]

      [:button.editor-button-bar--button
       {:title    "Make a numbered (ordered) list"
        :tabIndex 0
        :on-click #(println "Saw click on numbered list button.")
        :disabled true}
       [:i.editor-button-bar--icon.list-numbered-icon]]

      [:button.editor-button-bar--button
       {:title    "Make selection a quotation"
        :tabIndex 0
        :on-click #(println "Saw click on quote button.")
        :disabled true}
       [:i.editor-button-bar--icon.quote-left-icon]]

      [:button.editor-button-bar--button
       {:title    "Insert a web link"
        :tabIndex 0
        :on-click #(println "Saw click on web link button.")
        :disabled true}
       [:i.editor-button-bar--icon.link-icon]]

      [:span.editor-button-bar--gap]

      [:button.editor-button-bar--button
       {:title    "Indent"
        :tabIndex 0
        :on-click #(println "Saw click on indent button.")
        :disabled true}
       [:i.editor-button-bar--icon.indent-right-icon]]

      [:button.editor-button-bar--button
       {:title    "Outdent"
        :tabIndex 0
        :on-click #(println "Saw click on outdent button.")
        :disabled true}
       [:i.editor-button-bar--icon.indent-left-icon]]

      [:button.editor-button-bar--button
       {:title    "Insert a timestamp"
        :tabIndex 0
        :on-click (fn [arg]
                    (let [target (get-element-by-id (:editor-textarea-id editor-state))]
                      (println "Saw click on timestamp button.")
                      (println "target: " (.-id target))
                      (cmd/insert-time-stamp target editor-state)))}
       [:i.editor-button-bar--icon.clock-icon]]

      [:span.editor-button-bar--gap]

      [:button.editor-button-bar--button
       {:title    "Undo the last action"
        :tabIndex 0
        :on-click #(println "Saw click on undo button.")
        :disabled true}
       [:i.editor-button-bar--icon.ccw-icon]]

      [:button.editor-button-bar--button
       {:title    "Undo the last undo"
        :tabIndex 0
        :on-click #(println "Saw click on redo button.")
        :disabled true}
       [:i.editor-button-bar--icon.cw-icon]]]

     ; Buttons on the right side.
     [:section.editor-button-bar--right

      [:button.editor-button-bar--button
       {:title    "Save revised content"
        :tabIndex 0
        :id       "save-button-id"
        :on-click #(when (is-editor-dirty? editor-state)
                     (cmd/save-page-cmd editor-state))
        :disabled (not (is-editor-dirty? editor-state))}
       [:i.editor-button-bar--icon.floppy-icon {:id "floppy-icon"}]]

      [:button.editor-button-bar--button.popup
       {:title    "Get help with Markdown"
        :tabIndex 0
        :on-click #(toggle-markdown-help-modal)}
       [:i.editor-button-bar--icon.question-circle-o-icon]]]]))

(defn- layout-editor-pane
  "This is the editing area, just a textarea. It sends an update message
  oover the websocket server when editor-state are so configured."
  [{:keys [page-content-ratom] :as editor-state}]
  (trace "Enter layout-editor-pane.")
  (r/create-class
    {
     :display-name           "editor-pane"

     :reagent-render (fn [_]
                       [:div {:class "editor-container"}
                        [:div {:class "mde-content-label-div"}
                         [:label {:class "form-label"
                                  :for   "content"} "Markdown"]]
                        [:textarea
                         {:class     "editor-textarea"
                          :id        (:editor-textarea-id editor-state)
                          :tabIndex  0
                          :value     @page-content-ratom
                          :on-change (fn [arg]
                                       (let [new-content (-> arg .-target .-value)]
                                         (reset! page-content-ratom new-content)
                                         (mark-page-dirty editor-state)
                                         (when (:send-every-keystroke editor-state)
                                           (ws/chsk-send! [:hey-server/content-updated
                                                              {:data new-content}]))
                                         new-content))}]])}))

(defn- layout-preview-pane
  "The preview div."
  [content-atom]
  (fn [content-atom]
    [:div {:class "editor-container"}
     [:div {:class "mde-content-label-div"}
      [:label {:class "form-label"
               :for   "content"} "Preview"]]
     [:div {:class    "mde-preview-class" :id "mde-preview-id"
            :tabIndex 0}
      (when @content-atom
        (markdown-component @content-atom))]]))

(defn- layout-editor-and-preview-section
  "Lay out the side-by-side editing and preview panes for the editor."
  [editor-state]
  (fn [{:keys [page-content-ratom] :as editor-state}]
    [:section {:class "editor-and-button-bar"}
     [layout-editor-button-bar editor-state]
     [:section {:class "editor-and-preview-section"}
      [layout-editor-pane editor-state]
      [layout-preview-pane page-content-ratom]]]))

(defn- handle-visibility-change
  "Handle visibility change events. When the document becomes hidden,
  save the editor contents."
  [editor-state]
  (when (.-hidden js/document)
    (let [save-fn (:assemble-and-save-fn editor-state)]
      (when (is-editor-dirty? editor-state)
        (save-fn editor-state)))))

(defn- build-editor-state
  "Build and return a map representing the editor state."
  [page-map]
  (let [title-atom (r/atom (:page_title page-map))
        tags-atom-vector (r/atom (:tags page-map))
        content-atom (r/atom (:page_content page-map))
        dirty-editor (r/atom nil)
        options (:options page-map)]
    (letfn [(re-assembler-fn []
              (let [page-map-id (or (:page_id page-map)
                                    @glbl-id-for-next-save)
                    re-assembled-page-map (merge page-map
                                                 {:page_id      page-map-id
                                                  :page_title   @title-atom
                                                  :tags         @tags-atom-vector
                                                  :page_content @content-atom})]
                re-assembled-page-map))
            (assemble-and-save-fn []
              (let [the-doc (re-assembler-fn)
                    sf doc-save-fn]
                (reset! dirty-editor nil)
                (sf the-doc)))]
      (let [editor-state (merge {:re-assembler-fn       re-assembler-fn
                                 :doc-save-fn           doc-save-fn
                                 :assemble-and-save-fn  assemble-and-save-fn
                                 :quit-fn               quit-editor-fn
                                 :dirty-editor-notifier mark-page-dirty
                                 :dirty-flag            dirty-editor
                                 :editor-textarea-id    "editor-text-area-id"
                                 :editor-title-input-id "mde-form-title-field-id"
                                 :editor-tag-id-prefix  "tag-bl-"
                                 :page-title-atom       title-atom
                                 :tags-atom-vector      tags-atom-vector
                                 :page-content-ratom    content-atom}

                                options)]
        editor-state))))

(defn- build-component-did-mount-function
  "Create and return the function to be called during the lifecycle
  'componentDidMount` phase."
  [editor-state]
  (fn [_]
    (let [page-title @(:page-title-atom editor-state)
          def-page-title (:default-new-page-name editor-state)]
      (if (= page-title def-page-title)
        (let [ele-to-focus (get-element-by-id (:editor-title-input-id editor-state))]
          (select-all ele-to-focus)
          (.focus ele-to-focus))
        (.focus (get-element-by-id (:editor-textarea-id editor-state)))))))

(defn layout-inner-editor-container
  "Lays out the section of the wiki page containing the editor, including the
  heading (title, tags, etc.) at the top, and the editor and preview panes
  side-by-side at the bottom. Returns the layout."
  [extended-page-map]
  ; The extended-page-map contains more than just the page map. It also
  ; contains an options map and the Markdown for the help page.
  (tracef "layout-inner-editor-container: extended-page-map: " extended-page-map)
  (let [editor-state (build-editor-state extended-page-map)]
    (r/create-class
      {:display-name                "layout-inner-editor-container"

       :component-did-mount (build-component-did-mount-function editor-state)

       :reagent-render      (fn []
                              (when extended-page-map
                                (kbs/bind-shortcut-keys editor-state)
                                (.addEventListener js/document "visibilitychange"
                                                   #(handle-visibility-change
                                                      editor-state))
                                [:div {:class "inner-editor-container"
                                       :id    "inner-editor-container-id"}
                                 [layout-editor-header editor-state]
                                 [layout-editor-and-preview-section editor-state]
                                 [layout-unsaved-changes-warning-dialog editor-state]
                                 [layout-duplicate-page-warning-dialog]
                                 [layout-markdown-help-dialog extended-page-map]
                                 [:div {:class "modal-overlay closed" :id "modal-overlay"}]]))})))

;;;; Sente event handlers.

(defmulti -event-msg-handler
          "Multimethod to handle Sente `event-msg`s"
          :id ; Dispatch on event-id
          )

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler
  :default ; Default/fallback case (no other matching handler)
  [{:as ev-msg :keys [event]}]
  (debugf "Unhandled event: %s" event))

(defmethod -event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (let [[old-state-map new-state-map] ?data]
    (if (:first-open? new-state-map)
      (debugf "Channel socket successfully established!: %s" new-state-map)
      (debugf "Channel socket state change: %s"              new-state-map))
    (editor-state-handler ev-msg)))

(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (debugf "Push event from server: %s" ?data)
  (editor-message-handler ev-msg))

(defmethod -event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (debugf "Handshake: %s" ?data)
    (debugf "About to call editor-handshake-handler: ev-msg:\n %s" (pprint/pprint ev-msg))
    (editor-handshake-handler ev-msg)))

(defn reload []
  (debug "Enter reload")
  (when-let [ele (get-element-by-id "outer-editor-container")]
    (debugf "    ele: %s" ele)
    (go
      (debug "    enter go block")
      (let [pm (<! got-page-channel)]
        (debug "    got-page-channel has returned a page map")
        (rdom/render [layout-inner-editor-container pm] ele)))))

(defn ^:export main []
  (reload))

(defn init! []
  (debugf "Enter init!: event-msg-handler: %s" event-msg-handler)
  (ws/start-router! event-msg-handler)
  (reload))
