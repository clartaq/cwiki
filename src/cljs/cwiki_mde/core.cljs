;;;
;;; This is the MDE Markdown editor, such as it is, derived from
;;; Carmen La's Reagent Markdown Editor in Reagent Recipes.
;;;

(ns cwiki-mde.core
  (:require [clojure.string :refer [blank?]]
            [cljs.pprint :as pprint]
            [cwiki-mde.ws :as ws]
            [reagent.core :as reagent]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

;-------------------------------------------------------------------------------
; Global variables.
;

; The global page map is set when the server sends it over. It is used when
; setting up the innner editor container.
(def ^{:private true} glbl-page-map (reagent/atom nil))

; The delay-handle stores the handle to the autosave countdown timer.
(def ^{:private true} glbl-delay-handle (atom nil))

;;
;; Websocket messages handlers to work with the server.
;;

;(defn doc-save-function
;  "Send a message to the server to save the document."
;  [page-map-atom]
;  (ws/send-message! [:hey-server/save-doc {:data @page-map-atom}]))

(defn doc-save-fn
  "Send a message to the server to save the document."
  [page-map]
  (println "doc-save-fn: page-map: " page-map)
  (ws/send-message! [:hey-server/save-doc {:data page-map}]))

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
      (= message-id :hey-editor/here-is-the-document) (when-let [page-map (second ?data)]
                                                        (tracef "editor-message-handler: the-data: %s"
                                                                (with-out-str
                                                                  (pprint/pprint page-map)))
                                                        ; This is where the global page map is set.
                                                        (reset! glbl-page-map page-map))
      (= message-id :hey-editor/shutdown-after-save) (when-let [new-location (str "/" (second ?data))]
                                                       (tracef "The new location is: %s" new-location)
                                                       (ws/stop-router!)
                                                       (.replace js/location new-location))
      (= message-id :hey-editor/shutdown-after-cancel) (do (ws/stop-router!)
                                                           (.replace js/location (.-referrer js/document))))))

;-------------------------------------------------------------------------------
; Auto-save-related functions.

(defn clear-autosave-delay! []
  "Clear the autosave countdown timer."
  (.clearTimeout js/window @glbl-delay-handle))

;(defn start-autosave-delay!
;  [doc-save-fn delay-ms page-map-atom]
;  (reset! delay-handle (.setTimeout js/window doc-save-fn delay-ms page-map-atom)))

(defn restart-autosave-delay!
  "Restart the autosave countdown timer."
  [the-save-fn delay-ms]
  (reset! glbl-delay-handle (.setTimeout js/window the-save-fn delay-ms)))

;(defn change-watcher!
;  [doc-save-fn page-map-atom]
;  (let [delay (* 1000 (get-in @page-map-atom [:options :editor_autosave_interval]))]
;    (when (pos? delay)
;      (clear-autosave-delay!)
;      (start-autosave-delay! doc-save-fn delay page-map-atom))))

(defn notify-autosave
  "Notify the autosave functionality that a change has occurred. When the
  autosave duration is greater than zero, restarts the countdown until the
  document is saved automatically."
  [options]
  (println "autosave notified")
  (let [delay (* 1000 (:editor_autosave_interval options))
        the-save-fn (:assemble-and-save-fn options)]
    (println "the-save-fn: " the-save-fn)
    (when (pos? delay)
      (clear-autosave-delay!)
      (restart-autosave-delay! the-save-fn delay))))

;-------------------------------------------------------------------------------
; The editor components.

;;
;; Layout and change handlers for the page.
;;

;(defn tag-change-listener
;  "Return a change listener function for the tag indicated."
;  [page-map-atom n]
;  (fn [arg]
;    (let [new-tag (-> arg .-target .-value)]
;      (change-watcher! doc-save-function page-map-atom)
;      (if (blank? new-tag)
;        (let [old-tag-vec (:tags @page-map-atom)
;              new-vec (vec (concat (subvec old-tag-vec 0 n)
;                                   (subvec old-tag-vec (inc n))))]
;          (swap! page-map-atom assoc :tags new-vec))
;        (swap! page-map-atom assoc-in [:tags n] new-tag))
;      (when (get-in @page-map-atom [:options :send-every-keystroke])
;        (ws/send-message! [:hey-server/content-updated
;                           {:data @page-map-atom}])))))

;(defn make-tag-input-element
;  "Return an input element for the nth tag from the vector."
;  [page-map-atom n]
;  ^{:key (str "tag-" n)}
;  [:input {:type        "text"
;           :class       "mde-tag-text-field"
;           :placeholder (str "Tag #" (+ 1 n))
;           :value       (nth (:tags @page-map-atom) n "")
;           :on-change   (tag-change-listener page-map-atom n)}])

;(defn make-tag-list-input-component
;  "Build and return the piece of the page allowing tags to be edited."
;  [page-map-atom]
;  (tracef "make-tag-list-input-component: tags %s" (:tags @page-map-atom))
;  [:div {:class "tag-edit-container tag-edit-section"}
;   [:label {:class "tag-edit-label"} "Tags"]
;   [:div {:class "mde-tag-edit-list" :id "mde-tag-edit-list"}
;    (doall (for [x (range 10)]
;             (make-tag-input-element page-map-atom x)))]])

;(defn make-title-input-element
;  "Build and return an element for displaying/editing the post title"
;  ; *** DOES A SPECIAL SEARCH FOR "Front Page" ***
;  [page-map-atom]
;  (let [pm @page-map-atom
;        ro (if (= "Front Page" (:page_title pm))
;             {:readOnly "readOnly"}
;             {})
;        inp (merge ro
;                   {:type      "text"
;                    :class     "mde-form-title-field"
;                    :name      "page-title"
;                    :value     (if-let [title (:page_title pm)]
;                                 (do
;                                   (tracef "make-title-input-element: title: %s" title)
;                                   (when (= title "favicon.ico")
;                                     (infof "Saw funky title: \n%s"
;                                            (with-out-str (pprint/pprint-map pm))))
;                                   title)
;                                 "Enter a Title for the Page")
;                    :on-change (fn [arg]
;                                 (let [new-title (-> arg .-target .-value)]
;                                   (change-watcher! doc-save-function page-map-atom)
;                                   (swap! page-map-atom assoc :page_title new-title)
;                                   (when (get-in pm [:options :send-every-keystroke])
;                                     (ws/send-message! [:hey-server/content-updated
;                                                        {:data pm}]))))})]
;    [:div {:class "mde-title-edit-section"}
;     [:div {:class "form-label-div"}
;      [:label {:class "form-label required"
;               :for   "title"} "Page Title"]]
;     [:input inp]]))

;(defn the-editor-container
;  "Lays out the editor, including the editor heading (title, tags, etc) at the
;  top, and the editor and preview panes side-by-side."
;  [page-map-atom]
;  (tracef "the-editor-container: @the-page-map: %s" @page-map-atom)
;  [:div {:class "inner-editor-container"}
;
;
;   [:header {:class "editor-header"}
;
;    [:div {:class "button-bar-container"}
;     [:input {:type    "button"
;              :id      "Save Button"
;              :name    "save-button"
;              :value   "Save Changes"
;              :class   "form-button button-bar-item"
;              :onClick #(do
;                          (trace "Saw Click on Save Button!")
;                          (ws/send-message! [:hey-server/save-doc-and-quit
;                                             {:data @page-map-atom}]))}]
;     [:input {:type    "button"
;              :id      "Cancel Button"
;              :name    "cancel-button"
;              :value   "Cancel"
;              :class   "form-button button-bar-item"
;              :onClick #(do
;                          (trace "Saw Click on the Cancel Button!")
;                          (ws/send-message! [:hey-server/cancel-editing]))}]]
;
;    [make-title-input-element page-map-atom]
;    [make-tag-list-input-component page-map-atom]
;    [:div {:class "mde-content-label-div"}
;     [:label {:class "form-label"
;              :for   "content"} "Page Content"]]]
;
;   [:section {:class "editor-and-preview-section"}
;    [editor page-map-atom]
;    [preview page-map-atom]]])

(defn layout-button-bar
  "Layout the editor button bar."
  [options]
  (println "Enter layout-button-bar")
  (println "options: " options)
  (println "re-assembler-fn: " (:re-assembler-fn options))
  [:div {:class "button-bar-container"}
   [:input {:type    "button"
            :id      "Save Button"
            :name    "save-button"
            :value   "Save Changes"
            :class   "form-button button-bar-item"
            :onClick #(do
                        (trace "Saw Click on Save Button!")
                        (println "result of calling re-assembler-fn: "
                                 (let [fxn (:re-assembler-fn options)]
                                   (fxn)))

                        (ws/send-message! [:hey-server/save-doc-and-quit
                                           {:data (let [fxn (:re-assembler-fn options)
                                                        new-page-map (fxn)]
                                                    (println "new-page-map: " new-page-map)
                                                    new-page-map)}]))}]
   [:input {:type    "button"
            :id      "Cancel Button"
            :name    "cancel-button"
            :value   "Cancel"
            :class   "form-button button-bar-item"
            :onClick #(do
                        (trace "Saw Click on the Cancel Button!")
                        (ws/send-message! [:hey-server/cancel-editing]))}]])

(defn new-tag-change-listener
  "Return a new change listener for the specified tag."
  [tags-vector-atom n single-tag-atom options]
  (println "tag changed: n: " n)
  (fn [arg]
    (let [new-tag (-> arg .-target .-value)]
      (println "new-tag: " new-tag)
      (notify-autosave options)
      (if (blank? new-tag)
        ; User deleted a tag.
        (let [old-tag-vec @tags-vector-atom
              new-vec (vec (concat (subvec old-tag-vec 0 n)
                                   (subvec old-tag-vec (inc n))))]
          (println "new-vec: " new-vec)
          (reset! tags-vector-atom new-vec))
        (do
          (reset! single-tag-atom new-tag)
          (println "@single-tag-atom: " @single-tag-atom)
          (let [new-vec (swap! tags-vector-atom assoc n new-tag)]
            ;(swap! tags-vector-atom assoc n new-tag)
            (println "new-vec: " new-vec)
            new-vec)))
      (when (:send-every-keystroke options)
        (ws/send-message! [:hey-server/tags-updated
                           {:data @tags-vector-atom}])))))

(defn layout-tag-input-element
  "Layo ut a single tag input element."
  [tags-vector-atom n options]
  (println "layout-tag-input-element: @tags-vector-atom: " @tags-vector-atom)
  (fn [tags-vector-atom]
    (let [tag-of-interest (reagent/atom (nth @tags-vector-atom n ""))]
      [:input {:type        "text"
               :class       "mde-tag-text-field"
               :placeholder (str "Tag #" (+ 1 n))
               :value       @tag-of-interest
               :on-change   (new-tag-change-listener tags-vector-atom n
                                                     tag-of-interest options)}])))

(defn layout-tags-editor
  "Lay out the inputs for all of the tags."
  [tags-atom options]
  (println "Enter layout-tags-editor")
  (println "options: " options)
  (println "tags-atom: " tags-atom)
  (println "@tags-atom: " @tags-atom)
  [:section {:class "tag-edit-container tag-edition-section"}
   [:label {:class "tag-edit-label"} "Tags"]
   [:div {:class "mde-tag-edit-list" :id "mde-tag-edit-list"}
    (for [n (range 10)]
      ^{:key (str "tag-" n)} [layout-tag-input-element tags-atom n options])]])

(defn layout-title-editor
  "Lay out the title editing control and return the layout."
  [title-atom options]
  (println "layout-title-editor: title-atom: " title-atom)
  (println "@title-atom: " @title-atom)
  (let [ro (when (= "Front Page" @title-atom)
             {:readOnly "readOnly"})
        inp (merge ro
                   {:type      "text"
                    :class     "mde-form-title-field"
                    :name      "page-title"
                    :value     (if-let [title @title-atom]
                                 (do
                                   (tracef "make-title-input-element: title: %s" title)
                                   (when (= title "favicon.ico")
                                     (info "Saw funky title request for favicon.icl"))
                                   title)
                                 "Enter a Title for the Page")
                    :on-change (fn [arg] (let [new-title (-> arg .-target .-value)]
                                           (println "new-title: " new-title)
                                           (notify-autosave options)
                                           (reset! title-atom new-title)
                                           (println "new @title-atom: " @title-atom)
                                           (when (:send-every-keystroke options)
                                             (ws/send-message! [:hey-server/title-updated
                                                                {:data new-title}]))))})]
    (println "@title-atom in function: " @title-atom)
    [:div {:class "mde-title-edit-section"}
     [:div {:class "form-label-div"}
      [:label {:class "form-label required"
               :for   "page-title"} "Page Title"]]
     [:input inp]]))

(defn layout-editor-header
  "Lay out the header section for the editor. Includes the title, tags, and
  button bar."
  [title-atom tags-atom options]
  (println "layout-editor-header")
  [:header {:class "editor-header"}
   [layout-button-bar options]
   [layout-title-editor title-atom options]
   [layout-tags-editor tags-atom options]
   [:div {:class "mde-content-label-div"}
    [:label {:class "form-label"
             :for   "content"} "Page Content"]]])

;(declare doc-save-function)

;(defn editor
;  "This is the editing area, just a textarea. It sends an update
;  message over the websocket to the server."
;  [page-map-atom]
;  (trace "enter editor")
;  [:div {:class "editor-container"}
;   [:textarea
;    {:class     "editor-textarea"
;     :value     (:page_content @page-map-atom)
;     :on-change (fn [arg]
;                  (let [new-content (-> arg .-target .-value)]
;                    (swap! page-map-atom assoc :page_content new-content)
;                    (change-watcher! doc-save-function page-map-atom)
;                    (when (get-in @page-map-atom [:options :send-every-keystroke])
;                      (ws/send-message! [:hey-server/content-updated
;                                         {:data new-content}]))
;                    new-content))}]])

(defn layout-editor-pane
  "This is the editing area, just a textarea. It sends an update message
  oover the websocket server when options are so configured."
  [content-atom options]
  (trace "Enter layout-editor-pane.")
  (fn [content-atom]
    [:div {:class "editor-container"}
     [:textarea
      {:class     "editor-textarea"
       :value     @content-atom
       :on-change (fn [arg]
                    (let [new-content (-> arg .-target .-value)]
                      (println "new-content: " new-content)
                      (reset! content-atom new-content)
                      (println "updated @content-atom after reset!: " @content-atom)
                      (notify-autosave options)
                      (when (:send-every-keystroke options)
                        (ws/send-message! [:hey-server/saw-a-keystroke-in-content
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
        (let [node (reagent/dom-node this)]
          (typeset-latex node)
          (highlight-code node)))})])

;(defn preview
;  "The preview div."
;  [the-page-map-atom]
;  [:div {:class "mde-preview-class" :id "mde-preview-id"}
;   (when-let [content (:page_content @the-page-map-atom)]
;     (markdown-component content))])

(defn layout-preview-pane
  "The preview div."
  [content-atom]
  (fn [content-atom]
    [:div {:class "mde-preview-class" :id "mde-preview-id"}
     (when @content-atom
       (markdown-component @content-atom))]))

(defn layout-editor-and-preview-section
  "Lay out the side-by-side editing and preview panes for the editor."
  [content-atom options]
  (println "layout-editor-and-preview-section")
  [:section {:class "editor-and-preview-section"}
   [layout-editor-pane content-atom options]
   [layout-preview-pane content-atom]])

(defn layout-inner-editor-container
  "Lays out the section of the wiki page containing the editor, including the
  heading (title, tags, etc.) at the top, and the editor and preview panes
  side-by-side at the bottom. Returns the layout."
  [page-map-atom]
  (tracef "layout-inner-editor-container: @page-map-atom: " @page-map-atom)
  (println "layout-inner-editor-container")
  (when @page-map-atom
    (let [pm @page-map-atom
          title-atom (reagent/atom (:page_title pm))
          ; The tags are Reactive, but NOT the tag vector.
          tags-atom (atom (:tags pm))
          content-atom (reagent/atom (:page_content pm))
          options (:options pm)]
      (println "    after let")
      (letfn [(re-assembler-fn []
                (println "Enter re-assembler-fn")
                (println "@title-atom in re-assembler: " @title-atom)
                (let [re-assembled-page-map (merge @page-map-atom
                                                   {:page_title   @title-atom
                                                    :tags         @tags-atom
                                                    :page_content @content-atom})]
                  (println "re-assembled-page-map: " re-assembled-page-map)
                  re-assembled-page-map))
              (assemble-and-save-fn []
                (let [the-doc (re-assembler-fn)
                      sf doc-save-fn]
                  (println "In assemble-and-save-fn")
                  (println "    the-doc: " the-doc)
                  (println "    sf: " sf)
                  (sf the-doc)))]
        (println "    after letfn")
        (let [final-options (merge {:re-assembler-fn      re-assembler-fn
                                    :doc-save-fn          doc-save-fn
                                    :assemble-and-save-fn assemble-and-save-fn}
                                   options)]

          (println "after final-options")
          [:div {:class "inner-editor-container"}
           [layout-editor-header title-atom tags-atom final-options]
           [layout-editor-and-preview-section content-atom final-options]])))))

(defn reload []
  (reagent/render [layout-inner-editor-container            ; the-editor-container
                   glbl-page-map] (.getElementById js/document "outer-editor-container")))

(defn ^:export main []
  (reload))

(defn init! []
  (ws/start-router! editor-handshake-handler editor-state-handler
                    editor-message-handler)
  (reload))
