;;;
;;; This is the MDE Markdown editor, such as it is, derived from
;;; Carmen La's Reagent Markdown Editor in Reagent Recipes.
;;;

(ns cwiki-mde.core
  (:require [ajax.core :refer [GET POST]]
            [cwiki-mde.ws :as ws]
            [reagent.core :as reagent]))

; MathJax would go here too.
(defn highlight-code
  "Highlights any <pre><code></code></pre> blocks in the html."
  [html-node]
  (let [nodes (.querySelectorAll html-node "pre code")]
    (loop [i (.-length nodes)]
      (when-not (neg? i)
        (when-let [item (.item nodes i)]
          (.highlightBlock js/hljs item))
        (recur (dec i))))))

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
                   (ws/send-message! [:mde/content-updated {:?data new-content}])
                   new-content))}])

(defn preview
  "The preview div."
  [content]
  [:div {:class "mde-preview-class"}
   (when (not-empty @content)
     (markdown-component @content))])

(defn grab-document-to-edit
  [doc]
  (println "Trying to grab the document")
  (GET "/serve-document-to-editor"
       {:headers {"Accept" "application/transit+json"}
        :handler #(reset! doc (str %))}))

(defn response-handler
  [content errors]
  (fn [{[_ doc] :?data}]
    (if-let [response-errors (:errors doc)]
      (reset! errors response-errors)
      (do
        (reset! errors nil)
        (reset! content (:content doc))))))

(defn the-editor-container
  "Puts the editor and preview area side-by-side."
  []
  (let [content (reagent/atom nil)
        errors (reagent/atom nil)]
    (ws/start-router! (response-handler content errors))
    (grab-document-to-edit content)
    (ws/send-message! [:mde/send-document-to-editor {:data "Here's a string of data"}])
    (fn []
      [:div {:class "mde-container"}
       [:div {:class "mde-editor-and-preview-container"}
        [editor content]
        [preview content]]])))

(defn reload []
  (reagent/render [the-editor-container] (.getElementById js/document "editor-container")))

(defn ^:export main []
  (reload))
