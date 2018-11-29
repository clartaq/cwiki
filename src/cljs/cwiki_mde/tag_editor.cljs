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

(defn tag-index->id
  "Return a unique id for the tag input element based on the index of the tag
  and the default tag id prefix."
  [num options]
  (str (:editor-tag-id-prefix options) (inc num)))

(defn delete-existing-tag
  "Delete an existing tag."
  [tags-vector-atom n]
  (let [old-tag-vec @tags-vector-atom
        new-vec (vec (concat (subvec old-tag-vec 0 n)
                             (subvec old-tag-vec (inc n))))]
    (reset! tags-vector-atom new-vec)))

;-------------------------------------------------------------------------------
; Layout functions
;

(defn layout-delete-tag-button
  "Return a button to delete a tag."
  [tags-vector-atom n options]
  [:span {:title "Delete this tag"}
   [:svg {:class    "tag-editor--delete-button tag-editor--button-image"
          :on-click #(do
                       (delete-existing-tag tags-vector-atom n)
                       ((:dirty-editor-notifier options) options))}]])

(defn layout-add-tag-button
  "Return a button to initiate adding a tag."
  [tags-vector-atom options]
  (fn [tags-vector-atom]
    [:span {:title "Add a new tag"}
     [:svg {:class    "tag-editor--add-button tag-editor--button-image"
            :on-click #(do
                         (swap! tags-vector-atom conj "A New Tag")
                         ((:dirty-editor-notifier options) options))}]]))

(defn resize-tag-input
  "Resize the tag input element based on its size."
  [tag-id]
  (let [target (.getElementById js/document tag-id)]
    (when target
      (.setAttribute target "size" (-> target .-value .-length))
      (.setAttribute target "style" "width:auto"))))

(defn tag-change-listener
  "Return a new change listener for the specified tag."
  [tags-vector-atom n single-tag-atom options]
  (fn [arg]
    (let [new-tag (-> arg .-target .-value)
          dirty-editor-notifier (:dirty-editor-notifier options)]
      (reset! single-tag-atom new-tag)
      (when dirty-editor-notifier
        (dirty-editor-notifier options))
      (if (blank? new-tag)
        ; User deleted a tag.
        (delete-existing-tag tags-vector-atom n)
        (swap! tags-vector-atom assoc n new-tag))
      (when (:send-every-keystroke options)
        (ws/send-message! [:hey-server/tags-updated
                           {:data @tags-vector-atom}])))))

(defn layout-tag-name-editor
  "Return a function that will layout a tag input element."
  [tags-vector-atom n options]
  (fn [tags-vector-atom]
    (let [tag-of-interest (r/atom (nth @tags-vector-atom n ""))
          cnt (count @tag-of-interest)
          tag-id (tag-index->id n options)]
      [:input {:type      "text"
               :size      cnt
               :class     "tag-editor--name-input"
               :id        tag-id
               :value     @tag-of-interest
               :on-focus  #(resize-tag-input tag-id)
              ; :on-blur   #(resize-tag-input tag-id)
               :on-click (fn [arg]
                           (let [ele (.-target arg)]
                             (.setSelectionRange ele 0 (.-length (.-value ele)))))
               :on-change (tag-change-listener tags-vector-atom n
                                               tag-of-interest options)}])))

(defn layout-tag-composite-lozenge
  "Return a function that will layout a composite element consisting of an input
  for editing tags and a button to delete the tag."
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
