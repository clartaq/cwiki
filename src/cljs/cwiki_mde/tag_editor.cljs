;;;;
;;;; This is the tag editor component for the CWiki editor.
;;;;

(ns cwiki-mde.tag-editor
  (:require [clojure.string :refer [blank?]]
            [reagent.core :as r]
            [cwiki-mde.ws :as ws]))

;-------------------------------------------------------------------------------
; Utilities
;

(defn- select-all
  "Select the entire contents of the DOM element."
  [ele]
  (.setSelectionRange ele 0 (.-length (.-value ele))))

(defn- get-element-by-id
  "A convenience function to return the DOM element with the given id."
  [id]
  (.getElementById js/document id))

(defn- tag-index->id
  "Return a unique id for the tag input element based on the index of the tag
  and the default tag id prefix."
  [num options]
  (str (:editor-tag-id-prefix options) (inc num)))

(defn- delete-existing-tag
  "Delete an existing tag."
  [tags-vector-atom n]
  (let [old-tag-vec @tags-vector-atom
        new-vec (vec (concat (subvec old-tag-vec 0 n)
                             (subvec old-tag-vec (inc n))))]
    (reset! tags-vector-atom new-vec)))

;-------------------------------------------------------------------------------
; Layout functions
;

(defn- layout-delete-tag-button
  "Return a button to delete a tag."
  [tags-vector-atom n options]
  [:span {:title "Delete this tag"}
   [:svg {:class    "tag-editor--delete-button tag-editor--button-image"
          :on-click #(do
                       (delete-existing-tag tags-vector-atom n)
                       ((:dirty-editor-notifier options) options))}]])

(defn- layout-add-tag-button
  "Return a button to initiate adding a tag."
  [tags-vector-atom options]
  (fn [tags-vector-atom]
    [:span {:title "Add a new tag"}
     [:svg {:class    "tag-editor--add-button tag-editor--button-image"
            :on-click #(do
                         (swap! tags-vector-atom conj
                                (:default-new-tag-label options))
                         ((:dirty-editor-notifier options) options))}]]))

(defn- resize-tag-input
  "Resize the tag input element based on the size of its content."
  [tag-id]
  (let [target (get-element-by-id tag-id)]
    (when target
      (.setAttribute target "size" (-> target .-value .-length))
      (.setAttribute target "style" "width:auto"))))

(defn- tag-change-listener
  "Return a new change listener for the specified tag."
  [tags-vector-atom n single-tag-atom options tag-id]
  (fn [arg]
    (let [new-tag (-> arg .-target .-value)
          dirty-editor-notifier (:dirty-editor-notifier options)]
      (reset! single-tag-atom new-tag)
      (when dirty-editor-notifier
        (dirty-editor-notifier options))
      (if (blank? new-tag)
        ; User deleted a tag.
        (delete-existing-tag tags-vector-atom n)
        ; Just typing? Reset and resize.
        (do
          (swap! tags-vector-atom assoc n new-tag)
          (resize-tag-input tag-id)))
      (when (:send-every-keystroke options)
        (ws/send-message! [:hey-server/tags-updated
                           {:data @tags-vector-atom}])))))

(defn- layout-tag-name-editor
  "Return a function that will layout a tag input element."
  [tags-vector-atom n options]
  (r/create-class
    {:name                "layout-tag-name-editor"

     :component-did-mount (fn [this]
                            (let [tag-idx (second (r/children this))
                                  tag-id (tag-index->id tag-idx options)
                                  ele (get-element-by-id tag-id)
                                  val (.-value ele)]
                              (when (= val (:default-new-tag-label options))
                                (select-all ele)
                                ; Need to focus for Firefox.
                                (.focus ele))))

     :reagent-render      (fn [tags-vector-atom n options]
                            (let [tag-of-interest (r/atom (nth @tags-vector-atom n ""))
                                  tag-id (tag-index->id n options)
                                  ch-cnt (count @tag-of-interest)]
                              [:input {:type         "text"
                                       :autoComplete "off"
                                       :size         ch-cnt
                                       :class        "tag-editor--name-input"
                                       :id           tag-id
                                       :value        @tag-of-interest
                                       :on-change    (tag-change-listener
                                                       tags-vector-atom n
                                                       tag-of-interest
                                                       options tag-id)}]))}))

(defn- layout-tag-composite-lozenge
  "Return a function that will layout a composite element consisting of a text
  input for editing tags and a button to delete the tag."
  [tags-vector-atom n options]
  (fn [tags-vector-atom n options]
    [:div.tag-editor--composite-lozenge
     [layout-tag-name-editor tags-vector-atom n options]
     [layout-delete-tag-button tags-vector-atom n options]]))

(defn layout-tag-list
  "Return a function that will layout a group of tag inputs."
  [tags-vector-atom options]
  (fn [tags-vector-atom options]
    [:section.tag-editor--container
     [:label.tag-editor--label {:for "tag-list"} "Tags"]
     [:form {:name "tag-list"}
      [:div.tag-editor--list
       (for [n (range (count @tags-vector-atom))]
         ^{:key (tag-index->id n options)}
         [layout-tag-composite-lozenge tags-vector-atom n options])
       [layout-add-tag-button tags-vector-atom options]]]]))
