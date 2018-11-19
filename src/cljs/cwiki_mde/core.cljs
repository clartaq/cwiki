;;;;
;;;; This is the MDE Markdown editor, such as it is, derived from
;;;; Carmen La's Reagent Markdown Editor in Reagent Recipes.
;;;;

(ns cwiki-mde.core
  (:require [clojure.string :refer [blank?]]
            [cljs.pprint :as pprint]
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
(def ^{:private true} glbl-page-map (r/atom nil))

;; The delay-handle stores the handle to the autosave countdown timer.
(def ^{:private true} glbl-delay-handle (atom nil))

;; The id of the save button in the button bar. Used so variout functions
;; can disable or enable the button based on when the content of the editor
;; is saved or changed.
;(defonce ^{:private true} save-button-id "editor-button-bar--save-button")

;; A flag indicating whether or not the textarea has unsaved changes.
(def ^{:private true} glbl-editor-is-dirty (r/atom nil))

;; Flag indicating that at least one save has occurred. Used to remove the
;; "Cancel" button.
;(def ^{:private true} save-has-occurred (atom nil))

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
  [options]
  (println "tell-server-to-quit")
  (let [fxn (:re-assembler-fn options)
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
  [options]
  (let [delay (* 1000 (:editor_autosave_interval options))
        the-save-fn (:assemble-and-save-fn options)]
    (when (pos? delay)
      (clear-autosave-delay!)
      (restart-autosave-delay! the-save-fn delay))))

(defn mark-page-dirty
  "Mark the page as dirty and reset the autosave timer."
  [options]
  (notify-autosave options)
  (reset! glbl-editor-is-dirty true))

;;;-----------------------------------------------------------------------------
;;; Dialogs
;;;

(defn layout-unsaved-changes-warning-dialog
  "Brings up a modal dialog that informs the user that there are unsaved
  changes to the page. Asks them what to do before exiting and losing
  the new work."
  [options]
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
                             (tell-server-to-quit options))}]
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
  [title-atom options]
  (r/create-class
    {:display-name        "title-editor"

     ; Select the title editor and put the cursor at the beginning.
     :component-did-mount (fn [this]
                            (let [elm (get-element-by-id "mde-form-title-field-id")]
                              (doto elm
                                (.focus)
                                (.setSelectionRange 0 0))))

     :reagent-render      (fn [title-atom options]
                            (let [ro (when (= "Front Page" @title-atom)
                                       {:readOnly "readOnly"})
                                  inp (merge ro
                                             {:type      "text"
                                              :class     "mde-form-title-field"
                                              :name      "page-title"
                                              :id        "mde-form-title-field-id"
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
  [title-atom tags-atom-vector options]
  [:header {:class "editor-header"}
   [layout-title-editor title-atom options]
   [te/layout-tag-list tags-atom-vector options]])

(defn layout-editor-button-bar
  "Layout the editor button bar. Tedious but trivial."
  [options]
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
                   ((:assemble-and-save-fn options)))
      :disabled (not @glbl-editor-is-dirty)}
     [:i.editor-button-bar--icon.floppy-icon {:id "floppy-icon"}]]

    [:button.editor-button-bar--button.popup
     {:title    "Get help with Markdown"
      :on-click #(println "Saw click on help button.")}
     [:i.editor-button-bar--icon.question-circle-o-icon]]]])

(defn layout-editor-pane
  "This is the editing area, just a textarea. It sends an update message
  oover the websocket server when options are so configured."
  [content-atom options]
  (trace "Enter layout-editor-pane.")
  (fn [content-atom]
    [:div {:class "editor-container"}
     [:div {:class "mde-content-label-div"}
      [:label {:class "form-label"
               :for   "content"} "Markdown"]]
     [:textarea
      {:class     "editor-textarea"
       :value     @content-atom
       :on-change (fn [arg]
                    (let [new-content (-> arg .-target .-value)]
                      (reset! content-atom new-content)
                      (reset! glbl-editor-is-dirty true)
                      (mark-page-dirty options)
                      (when (:send-every-keystroke options)
                        (ws/send-message! [:hey-server/content-updated
                                           {:data new-content}]))
                      new-content))}]]))

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
  [content-atom options]
  [:section {:class "editor-and-button-bar"}
   [layout-editor-button-bar options]
   [:section {:class "editor-and-preview-section"}
    [layout-editor-pane content-atom options]
    [layout-preview-pane content-atom]]])

(defn layout-editor-bottom-button-bar
  "Layout the editor button bar."
  [options]
  [:section {:class "button-bar-container"}
   [:input {:type    "button"
            :id      "done-button"
            :name    "done-button"
            :value   "Done"
            :class   "form-button button-bar-item"
            :onClick #(if @glbl-editor-is-dirty
                        (toggle-unsaved-changes-modal)
                        (tell-server-to-quit options))}]])

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
          (let [final-options (merge {:re-assembler-fn      re-assembler-fn
                                      :doc-save-fn          doc-save-fn
                                      :assemble-and-save-fn assemble-and-save-fn
                                      :autosave-notifier-fn mark-page-dirty}
                                     options)]

            [:div {:class "inner-editor-container"}
             [layout-editor-header title-atom tags-atom-vector final-options]
             [layout-editor-and-preview-section content-atom final-options]
             [layout-editor-bottom-button-bar final-options]
             [layout-unsaved-changes-warning-dialog final-options]
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
