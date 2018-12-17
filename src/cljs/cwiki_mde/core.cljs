;;;;
;;;; This is the MDE Markdown editor, such as it is, derived from
;;;; Carmen La's Reagent Markdown Editor in Reagent Recipes.
;;;;

(ns cwiki-mde.core
  (:require [clojure.string :refer [blank?]]
            [cljs.pprint :as pprint]
            [cwiki-mde.keyboard-shortcuts :as kbs]
            [cwiki-mde.tag-editor :as te]
            [cwiki-mde.ws :as ws]
            [reagent.core :as r]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

;;;-----------------------------------------------------------------------------
;;; Global variables.
;;;

;; The global page map is set when the server sends it over. It is used when
;; setting up the innner editor container.
(defonce ^{:private true} glbl-page-map (r/atom nil))

;; The delay-handle stores the handle to the autosave countdown timer.
(def ^{:private true} glbl-delay-handle (atom nil))

;; A flag indicating whether or not the textarea has unsaved changes.
(def ^{:private true} glbl-editor-is-dirty (r/atom nil))

;; Flag indicating that it is ok to exit the editor, even if there are
;; unsaved changes. Used by the "Unsaved Changes" modal dialog to indicate
;; whether the use wants to exit and lose the changes.
(def ^{:private true} glbl-ok-to-exit (atom nil))

;; A value set after the first save of a new page. When the page is saved,
;; the server responds with its database id. This value is used in all
;; subsequent saves. Not doing so would allow multiple "duplicates" of the
;; new page to be saved if the user changes page titles between saved.
(def ^{:private true} glbl-id-for-next-save (atom nil))

;;;-----------------------------------------------------------------------------
;;; Utility functions.
;;;

(defn- get-element-by-id [the-id]
  (.getElementById js/document the-id))

(defn toggle-modal
  "Toggle the display state of the modal dialog with the given id."
  [ele-id]
  (let [close-button (get-element-by-id ele-id)
        overlay (get-element-by-id "modal-overlay")]
    (when (and close-button overlay)
      (.toggle (.-classList close-button) "closed")
      (.toggle (.-classList overlay) "closed"))))

(defn toggle-unsaved-changes-modal
  "Toggle the display state of the modal dialog that is shown when there
  are unsaved changes."
  []
  (toggle-modal "unsaved-changes-modal"))

(defn toggle-duplicate-title-modal
  "Toggle the display state of the modal dialog that is shown when the
  user is trying to save a new page with a title that duplicates a page
  already in the wiki."
  []
  (toggle-modal "duplicate-title-modal"))

(defn tell-server-to-quit
  [state]
  (let [fxn (:re-assembler-fn state)
        new-page-map (fxn)
        page_id (:page_id new-page-map)
        page_title (:page_title new-page-map)
        referrer (.-referrer js/document)]
    (ws/send-message! [:hey-server/quit-editing
                       {:page-id    page_id
                        :page-title page_title
                        :referrer   referrer}])))

;;;-----------------------------------------------------------------------------
;;; Websocket message handlers to work with the server.
;;;

(defn doc-save-fn
  "Send a message to the server to save the document."
  [page-map]
  (ws/send-message! [:hey-server/save-doc {:data page-map}])
  (reset! glbl-editor-is-dirty nil))

(defn editor-handshake-handler
  "Handle the handshake event between the server and client. This function
  sends the message to the server to send over the document for editing."
  [{:keys [?data]}]
  (trace "Editor: Connection established!")
  (ws/send-message! [:hey-server/send-document-to-editor {}]))

(defn editor-state-handler
  "Handle changes in the state of the editor."
  [{:keys [?data]}]
  (trace "Editor: Connection state changed!"))

(defn editor-message-handler
  [{:keys [?data]}]
  (when ?data
    (tracef "?data %s" ?data))
  (let [message-id (first ?data)]
    (tracef "editor-message-handler: message-id: %s" message-id)
    (cond
      (= message-id
         :hey-editor/here-is-the-document) (when-let [page-map (second ?data)]
                                             (tracef "editor-message-handler: the-data: %s"
                                                     (with-out-str
                                                       (pprint/pprint page-map)))
                                             ; This is where the global page map is set.
                                             (reset! glbl-page-map page-map))
      (= message-id
         :hey-editor/here-is-the-id) (reset! glbl-id-for-next-save (second ?data))

      (= message-id
         :hey-editor/that-page-already-exists) (do
                                                 (trace "Saw :hey-editor/that-page-already-exists")
                                                 (toggle-duplicate-title-modal)
                                                 (reset! glbl-editor-is-dirty true))
      (= message-id
         :hey-editor/shutdown-and-go-to) (when-let [new-location (str "/" (second ?data))]
                                           (tracef "The new location is: %s" new-location)
                                           (kbs/unbind-shortcut-keys)
                                           (ws/stop-router!)
                                           (.replace js/location new-location)))))

;;;-----------------------------------------------------------------------------
;;; Auto-save-related functions.
;;;

(defn clear-autosave-delay! []
  "Clear the autosave countdown timer."
  (.clearTimeout js/window @glbl-delay-handle))

(defn restart-autosave-delay!
  "Restart the autosave countdown timer."
  [the-save-fn delay-ms]
  (reset! glbl-delay-handle (.setTimeout js/window the-save-fn delay-ms)))

(defn notify-autosave
  "Notify the autosave functionality that a change has occurred. When the
  autosave duration is greater than zero, restarts the countdown until the
  document is saved automatically."
  [state]
  (let [delay (* 1000 (:editor_autosave_interval state))
        the-save-fn (:assemble-and-save-fn state)]
    (when (pos? delay)
      (clear-autosave-delay!)
      (restart-autosave-delay! the-save-fn delay))))

(defn mark-page-dirty
  "Mark the page as dirty and reset the autosave timer."
  [state]
  (notify-autosave state)
  (reset! glbl-editor-is-dirty true))

;;;-----------------------------------------------------------------------------
;;; Dialogs
;;;

(defn layout-unsaved-changes-warning-dialog
  "Brings up a modal dialog that informs the user that there are unsaved
  changes to the page. Asks them what to do before exiting and losing
  the new work."
  [state]
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
                             (tell-server-to-quit state))}]
       [:input {:type     "button"
                :class    "form-button button-bar-item"
                :value    "No. Return to the Editor."
                :title    "Close this dialog and return to the editor"
                :on-click #(do
                             (reset! glbl-ok-to-exit nil)
                             (toggle-unsaved-changes-modal))}]]]]))

(defn layout-duplicate-page-warning-dialog
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

;;;-----------------------------------------------------------------------------
;;; The editor components.
;;;

(defn layout-title-editor
  "Lay out the title editing control and return the layout."
  [title-atom state]
  (r/create-class
    {:display-name   "title-editor"

     ; Select the title editor and put the cursor at the beginning.
     ;:component-did-mount (fn [this]
     ;                       (let [elm (get-element-by-id
     ;                                   (:editor-title-input-id state))]
     ;                         ;"mde-form-title-field-id")
     ;
     ;                         (doto elm
     ;                           (.focus)
     ;                           (.setSelectionRange 0 0))))

     :reagent-render (fn [title-atom options]
                       (let [ro (when (= "Front Page" @title-atom)
                                  {:readOnly "readOnly"})
                             inp (merge ro
                                        {:type      "text"
                                         :class     "mde-form-title-field"
                                         :name      "page-title"
                                         :id        (:editor-title-input-id options) ;"mde-form-title-field-id"
                                         :autoFocus "true"
                                         :value     (if-let [title @title-atom]
                                                      (do
                                                        (when (= title "favicon.ico")
                                                          (info "Saw funky title request for favicon.icl"))
                                                        title)
                                                      "Enter a Title for the Page Here")
                                         :on-change (fn [arg]
                                                      (let [new-title (-> arg .-target .-value)]
                                                        (mark-page-dirty options)
                                                        (reset! title-atom new-title)
                                                        (when (:send-every-keystroke options)
                                                          (ws/send-message! [:hey-server/title-updated
                                                                             {:data new-title}]))))})]
                         [:section {:class "mde-title-edit-section"}
                          [:div {:class "form-label-div"}
                           [:label {:class "form-label required"
                                    :for   "page-title"} "Page Title"]]
                          [:input inp]]))}))

(defn layout-editor-header
  "Lay out the header section for the editor. Includes the title, tags, and
  button bar."
  [title-atom tags-atom-vector state]
  [:header {:class "editor-header"}
   [layout-title-editor title-atom state]
   [te/layout-tag-list tags-atom-vector state]])

(defn layout-editor-button-bar
  "Layout the editor button bar. Tedious but trivial."
  [state]
  [:section {:class "editor-button-bar"}
   ;Buttons on the left side.
   [:section.editor-button-bar--left

    [:button.editor-button-bar--button
     {:title    "Make selection bold"
      :on-click #(println "Saw click on bold button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.bold-icon]]

    [:button.editor-button-bar--button
     {:title    "Make selection italic"
      :on-click #(println "Saw click on italic button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.italic-icon]]

    [:button.editor-button-bar--button
     {:title    "Underline selection"
      :on-click #(println "Saw click on underline button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.underline-icon]]

    [:button.editor-button-bar--button
     {:title    "Strike through selection"
      :on-click #(println "Saw click on strike-through button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.strike-icon]]

    [:span.editor-button-bar--gap]

    [:button.editor-button-bar--button
     {:title    "Make selection a header"
      :on-click #(println "Saw click on header button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.header-icon]]

    [:button.editor-button-bar--button
     {:title    "Format selection as code"
      :on-click #(println "Saw click on code button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.code-icon]]

    [:button.editor-button-bar--button
     {:title    "Make a bulleted (unordered) list"
      :on-click #(println "Saw click on bullet list button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.list-bullet-icon]]

    [:button.editor-button-bar--button
     {:title    "Make a numbered (ordered) list"
      :on-click #(println "Saw click on numbered list button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.list-numbered-icon]]

    [:button.editor-button-bar--button
     {:title    "Make selection a quotation"
      :on-click #(println "Saw click on quote button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.quote-left-icon]]

    [:button.editor-button-bar--button
     {:title    "Insert a web link"
      :on-click #(println "Saw click on web link button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.link-icon]]

    [:span.editor-button-bar--gap]

    [:button.editor-button-bar--button
     {:title    "Indent"
      :on-click #(println "Saw click on indent button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.indent-right-icon]]

    [:button.editor-button-bar--button
     {:title    "Outdent"
      :on-click #(println "Saw click on outdent button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.indent-left-icon]]

    ; from: K. Kilian Lindbergs answer to this question:
    ; https://stackoverflow.com/questions/596481/is-it-possible-to-simulate-key-press-events-programmatically/19883789#19883789
    ;
    ;var pressthiskey = "q"/* <--- :) !? q for example */;
    ;var e = new Event("keydown");
    ;e.key = pressthiskey;
    ;e.keyCode = e.key.charCodeAt(0);
    ;e.which = e.keyCode;
    ;e.altKey = false;
    ;e.ctrlKey = true;
    ;e.shiftKey = false;
    ;e.metaKey = false;
    ;e.bubbles = true;
    ;document.dispatchEvent(e);

    [:button.editor-button-bar--button
     {:title    "Insert a timestamp"
      :on-click (fn [arg]
                  (let [target (get-element-by-id (:editor-textarea-id state))]
                    (println "Saw click on timestamp button.")
                    (println "target: " (.-id target))
                    (let [evt (js/Event. "keydown")]
                      (set! (.-key evt) "T")
                      (set! (.-keyCode evt) (.charCodeAt (.-key evt) 0))
                      (set! (.-which evt) (.-keyCode evt))
                      (set! (.-altKey evt) "true")
                      (set! (.-ctrlKey evt) nil)
                      (set! (.-shiftKey evt) nil)
                      (set! (.-metaKey evt) "true")
                      (set! (.-bubbles evt) "true")
                      (println "evt: " evt)
                      (let [cancelled (.dispatchEvent target evt)]
                        (println "cancelled: " cancelled)))))
      ;#(println "Saw click on timestamp button.")
      ;:disabled "true"
      }
     [:i.editor-button-bar--icon.clock-icon]]

    [:span.editor-button-bar--gap]

    [:button.editor-button-bar--button
     {:title    "Undo the last action"
      :on-click #(println "Saw click on undo button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.ccw-icon]]

    [:button.editor-button-bar--button
     {:title    "Undo the last undo"
      :on-click #(println "Saw click on redo button.")
      :disabled "true"}
     [:i.editor-button-bar--icon.cw-icon]]]

   ; Buttons on the right side.
   [:section.editor-button-bar--right

    [:button.editor-button-bar--button
     {:title    "Save revised content"
      :id       "save-button-id"
      :on-click #(when @glbl-editor-is-dirty
                   ((:assemble-and-save-fn state)))
      :disabled (not @glbl-editor-is-dirty)}
     [:i.editor-button-bar--icon.floppy-icon {:id "floppy-icon"}]]

    [:button.editor-button-bar--button.popup
     {:title    "Get help with Markdown"
      :on-click #(println "Saw click on help button.")}
     [:i.editor-button-bar--icon.question-circle-o-icon]]]])

(defn layout-editor-pane
  "This is the editing area, just a textarea. It sends an update message
  oover the websocket server when state are so configured."
  [content-atom state]
  (trace "Enter layout-editor-pane.")
  (r/create-class
    {
     :name                "editor-pane"

     ;:component-did-mount #(let [ed-id (get-element-by-id "editor-text-area-id")]
     ;                        (if ed-id
     ;                          (do
     ;                            (let [the-keys (js-keys ed-id)]
     ;                              (for [tk the-keys]
     ;                                (println "tk: " tk)))
     ;                            (println "(js-keys ed-id): " (js-keys ed-id))
     ;                            (println)
     ;                            (println "attribute names: " (.getAttributeNames ed-id)))
     ;                          (println "Didn't get editor text area element.")))

     :reagent-render      (fn [content-atom]
                            [:div {:class "editor-container"}
                             [:div {:class "mde-content-label-div"}
                              [:label {:class "form-label"
                                       :for   "content"} "Markdown"]]
                             [:textarea
                              {:class     "editor-textarea"
                               :id        (:editor-textarea-id state)
                               :value     @content-atom
                               :on-change (fn [arg]
                                            (let [new-content (-> arg .-target .-value)]
                                              (reset! content-atom new-content)
                                              (mark-page-dirty state)
                                              (when (:send-every-keystroke state)
                                                (ws/send-message! [:hey-server/content-updated
                                                                   {:data new-content}]))
                                              new-content))}]])}))

(defn highlight-code
  "Highlights any <pre><code></code></pre> blocks in the html."
  [html-node]
  (trace "highlight-code")
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
        (let [node (r/dom-node this)]
          (typeset-latex node)
          (highlight-code node)))})])

(defn layout-preview-pane
  "The preview div."
  [content-atom]
  (fn [content-atom]
    [:div {:class "editor-container"}
     [:div {:class "mde-content-label-div"}
      [:label {:class "form-label"
               :for   "content"} "Preview"]]
     [:div {:class "mde-preview-class" :id "mde-preview-id"}
      (when @content-atom
        (markdown-component @content-atom))]]))

(defn layout-editor-and-preview-section
  "Lay out the side-by-side editing and preview panes for the editor."
  [content-atom state]
  [:section {:class "editor-and-button-bar"}
   [layout-editor-button-bar state]
   [:section {:class "editor-and-preview-section"}
    [layout-editor-pane content-atom state]
    [layout-preview-pane content-atom]]])

(defn quit-fn
  [state]
  (if @glbl-editor-is-dirty
    (toggle-unsaved-changes-modal)
    (tell-server-to-quit state)))

(defn layout-editor-bottom-button-bar
  "Layout the editor button bar."
  [state]
  [:section {:class "button-bar-container"}
   [:input {:type    "button"
            :id      "done-button"
            :name    "done-button"
            :value   "Done"
            :class   "form-button button-bar-item"
            :onClick #(quit-fn state)}]])

(defn layout-inner-editor-container
  "Lays out the section of the wiki page containing the editor, including the
  heading (title, tags, etc.) at the top, and the editor and preview panes
  side-by-side at the bottom. Returns the layout."
  [page-map-atom]
  (tracef "layout-inner-editor-container: @page-map-atom: " @page-map-atom)
  (fn []
    (when @page-map-atom
      (let [pm @page-map-atom
            title-atom (r/atom (:page_title pm))
            tags-atom-vector (r/atom (:tags pm))
            content-atom (r/atom (:page_content pm))
            options (:options pm)]
        (letfn [(re-assembler-fn []
                  (let [page-map-id (or (:page_id pm) @glbl-id-for-next-save)
                        re-assembled-page-map (merge @page-map-atom
                                                     {:page_id      page-map-id
                                                      :page_title   @title-atom
                                                      :tags         @tags-atom-vector
                                                      :page_content @content-atom})]
                    re-assembled-page-map))
                (assemble-and-save-fn []
                  (let [the-doc (re-assembler-fn)
                        sf doc-save-fn]
                    (sf the-doc)))]
          (let [editor-state (merge {:re-assembler-fn       re-assembler-fn
                                     :doc-save-fn           doc-save-fn
                                     :assemble-and-save-fn  assemble-and-save-fn
                                     :quit-fn               quit-fn
                                     :dirty-editor-notifier mark-page-dirty
                                     :editor-textarea-id    "editor-text-area-id"
                                     :editor-title-input-id "mde-form-title-field-id"
                                     :editor-tag-id-prefix  "tag-bl-"
                                     :page-title-atom       title-atom
                                     :tags-atom-vector      tags-atom-vector
                                     :page-content-ratom    content-atom}

                                    options)]

            (kbs/bind-shortcut-keys editor-state)
            [:div {:class "inner-editor-container"}
             [layout-editor-header title-atom tags-atom-vector editor-state]
             [layout-editor-and-preview-section content-atom editor-state]
             [layout-editor-bottom-button-bar editor-state]
             [layout-unsaved-changes-warning-dialog editor-state]
             [layout-duplicate-page-warning-dialog]
             [:div {:class "modal-overlay closed" :id "modal-overlay"}]]))))))

(defn reload []
  (r/render [layout-inner-editor-container glbl-page-map]
            (get-element-by-id "outer-editor-container")))

(defn ^:export main []
  (reload))

(defn init! []
  (ws/start-router! editor-handshake-handler editor-state-handler
                    editor-message-handler)
  (reload))
