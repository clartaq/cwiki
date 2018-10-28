(ns cwiki-mde.tag-editor
  (:require [clojure.string :refer [blank?]]
            [reagent.core :as r]
            [cwiki-mde.ws :as ws]))

;-------------------------------------------------------------------------------
; Basic operations on the set of tags.

(defn add-new-tag-to-set
  [set-atom new-tag]
  (swap! set-atom conj new-tag))

(defn remove-tag-from-set
  [set-atom tag]
  (swap! set-atom disj tag))

(defn replace-tag-in-set
  [set-atom old-tag new-tag]
  (when (contains? @set-atom old-tag)
    (let [reduced-set-atom (atom (remove-tag-from-set set-atom old-tag))]
      (add-new-tag-to-set reduced-set-atom new-tag))))

(defn add-new-tag
  [tag]
  (println "Adding tag: " tag))

(defn tag-num->id
  [num]
  (str "tag-" (+ 1 num)))

(defn delete-existing-tag
  [tags-vector-atom n]
  (let [old-tag-vec @tags-vector-atom
        new-vec (vec (concat (subvec old-tag-vec 0 n)
                             (subvec old-tag-vec (inc n))))]
    (reset! tags-vector-atom new-vec)))

(defn layout-delete-tag-button
  "Return a button to delete a tag."
  [tags-vector-atom n]
  [:span [:svg {:class    "tag-editor--delete-button tag-editor--button-image"
                :on-click #(delete-existing-tag tags-vector-atom n)}]])

(defn layout-add-tag-button
  "Return a button to initiate adding a tag."
  [tags-vector-atom]
  [:span [:svg {:class    "tag-editor--add-button tag-editor--button-image"
                :on-click #(swap! tags-vector-atom conj "A New Tag")}]])

(defn resize-tag-input
  [tag-id]
  (let [target (.getElementById js/document tag-id)]
    (when target
      (.setAttribute target "size" (-> target .-value .-length))
      (.setAttribute target "style" "width:auto"))))

(defn on-key-up-listener
  []
  (fn [arg]
    (when (= 13 (.-keyCode arg))
      (resize-tag-input (-> arg .-target .-id)))))

(defn tag-change-listener
  "Return a new change listener for the specified tag."
  [tags-vector-atom n single-tag-atom options]
  (fn [arg]
    (let [new-tag (-> arg .-target .-value)
          autosave-notifier (:autosave-notifier-fn options)]
      (reset! single-tag-atom new-tag)
      (when (and autosave-notifier
                 (pos? (:editor_autosave_interval options)))
        (autosave-notifier options))
      (if (blank? new-tag)
        ; User deleted a tag.
        (delete-existing-tag tags-vector-atom n)
        (swap! tags-vector-atom assoc n new-tag))
      (when (:send-every-keystroke options)
        (ws/send-message! [:hey-server/tags-updated
                           {:data @tags-vector-atom}])))))

(defn layout-tag-name-editor
  [tags-vector-atom n options]
  (fn [tags-vector-atom]
    (let [tag-of-interest (r/atom (nth @tags-vector-atom n ""))
          cnt (count @tag-of-interest)
          tag-id (tag-num->id n)]
      [:input {:type      "text"
               :size      cnt
               :class     "tag-editor--name-input"
               :id        tag-id
               :value     @tag-of-interest
               ;:on-key-up (on-key-up-listener)
               :on-focus  #(resize-tag-input tag-id)
               :on-blur   #(resize-tag-input tag-id)
               :on-change (tag-change-listener tags-vector-atom n
                                               tag-of-interest options)}])))

(defn layout-tag-composite-lozenge
  [tags-vector-atom n options]
  (fn [tags-vector-atom n options]
    [:div.tag-editor--composite-lozenge
     [layout-tag-name-editor tags-vector-atom n options]
     [layout-delete-tag-button tags-vector-atom n]]))

(defn layout-tag-list
  [tags-vector-atom options]
  [:section.tag-editor--container
   [:label.tag-editor--label "Tags"]
   [:div.tag-editor--list
    (for [n (range (count @tags-vector-atom))]
      ^{:key (str "tag-bl-" n)}
      [layout-tag-composite-lozenge tags-vector-atom n options])
    [layout-add-tag-button tags-vector-atom]]])
