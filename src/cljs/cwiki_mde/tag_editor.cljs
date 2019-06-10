;;;;
;;;; This is the tag editor component for the CWiki editor.
;;;;

(ns cwiki-mde.tag-editor
  (:require [clojure.string :refer [blank?]]
            [cwiki-mde.ws :as ws]
            [cwiki-mde.font-detection :as fd]
            [reagent.core :as r]))

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
  [num editor-state]
  (str (:editor-tag-id-prefix editor-state) (inc num)))

(defn delete-existing-tag
  "Delete an existing tag."
  [tags-vector-atom n]
  (let [old-tag-vec @tags-vector-atom
        new-vec (into (subvec old-tag-vec 0 n) (subvec old-tag-vec (inc n)))]
    (reset! tags-vector-atom new-vec)))

;-------------------------------------------------------------------------------
; Layout functions
;

(defn- layout-delete-tag-button
  "Return a button to delete a tag."
  [n editor-state]
  (fn [n {:keys [tags-atom-vector dirty-editor-notifier] :as editor-state}]
    [:span {:title "Delete this tag"}
     [:svg {:class    "tag-editor--delete-button tag-editor--button-image"
            :on-click #(do
                         (delete-existing-tag tags-atom-vector n)
                         (dirty-editor-notifier editor-state) editor-state)}]]))

(defn- layout-add-tag-button
  "Return a button to initiate adding a tag."
  [editor-state]
  (fn [{:keys [tags-atom-vector dirty-editor-notifier] :as editor-state}]
    [:span {:title "Add a new tag"}
     [:svg {:class    "tag-editor--add-button tag-editor--button-image"
            :on-click #(do
                         (swap! tags-atom-vector conj
                                (:default-new-tag-label editor-state))
                         (dirty-editor-notifier editor-state) editor-state)}]]))

(defn keydown-handler!
  "Handle a keydown event for a tag editor. Watches if the tag is empty.
  If so, and the user press the backspace or delete key, the editor
  element will be removed from the list of tag editors."
  [evt n {:keys [tags-atom-vector dirty-editor-notifier] :as editor-state}]
  (let [tag-of-interest (nth @tags-atom-vector n)]
    (when (and (or (= "Backspace" (.-key evt))
                   (= "Delete" (.-key evt)))
               (empty? tag-of-interest))
      (delete-existing-tag tags-atom-vector n)
      (dirty-editor-notifier editor-state)
      (.stopPropagation evt))))

(defn- get-style
  [ele rule]
  (-> js/document
      .-defaultView
      (.getComputedStyle ele "")
      (.getPropertyValue rule)))

(defn- tag-change-listener!
  "Return a new change listener for the specified tag."
  [n {:keys [tags-atom-vector dirty-editor-notifier] :as editor-state}]
  (fn [arg]
    (let [new-tag (-> arg .-target .-value)]
      (swap! tags-atom-vector assoc n new-tag)
      (dirty-editor-notifier editor-state)
      (when (:send-every-keystroke editor-state)
        (ws/send-message! [:hey-server/tags-updated
                           {:data @tags-atom-vector}])))))

(defn- layout-tag-name-editor
  "A class that handles layout and behavior of a tag input element."
  [n editor-state]
  (let [canvas (atom nil)
        context (atom nil)
        font-style (atom nil)]

    (r/create-class
      {:name                "layout-tag-name-editor"

       :component-did-mount (fn [this]
                              (let [tag-idx (first (r/children this))
                                    tag-id (tag-index->id tag-idx editor-state)
                                    ele (get-element-by-id tag-id)
                                    _ (set! (.-className ele) "tag-editor--name-input")
                                    val (.-value ele)]
                                (when (nil? @canvas)
                                  (reset! canvas (.createElement js/document "canvas"))
                                  (reset! context (.getContext @canvas "2d"))
                                  (let [ff (get-style ele "font-family")
                                        fs (get-style ele "font-size")
                                        font-to-use (fd/font-family->font-used ff)
                                        ff-str (str fs " " font-to-use)]
                                    (reset! font-style ff-str)
                                    (set! (.-font @context) ff-str))
                                  ;; Force another render now that we can measure
                                  ;; correctly. This has the effect of correctly
                                  ;; displaying even some pathological tags, like
                                  ;; one containing many "W"s or other wide
                                  ;; characters for example.
                                  (r/after-render #(r/force-update this)))
                                (when (= val (:default-new-tag-label editor-state))
                                  (select-all ele)
                                  ; Need to focus for Firefox.
                                  (.focus ele))))

       :reagent-render      (fn [n {:keys [tags-atom-vector] :as editor-state}]
                              (let [tag-of-interest (nth @tags-atom-vector n)
                                    tag-id (tag-index->id n editor-state)
                                    ch-cnt (count tag-of-interest)
                                    str-width (if (zero? ch-cnt)
                                                ; Make sure a cursor is visible.
                                                "5px"
                                                (if (and @canvas @context)
                                                  ; Use real measurement.
                                                  (str (.-width (.measureText @context tag-of-interest)) "px")
                                                  ; Use a "pretty close" measure.
                                                  (str ch-cnt "ch")))]
                                [:input {:type         "text"
                                         :tab-index    -1
                                         :autoComplete "off"
                                         :style        {:width str-width
                                                        ; Firefox requires max-width to work
                                                        :max-width str-width}
                                         :class        "tag-editor--name-input"
                                         :id           tag-id
                                         :value        tag-of-interest
                                         :on-keyDown   (fn [evt]
                                                         (keydown-handler!
                                                           evt n editor-state))
                                         :on-change    (tag-change-listener!
                                                         n editor-state)}]))})))

(defn- layout-tag-composite-lozenge
  "Return a function that will layout a composite element consisting of a text
  input for editing tags and a button to delete the tag."
  [n editor-state]
  (fn [n editor-state]
    [:div.tag-editor--composite-lozenge
     [layout-tag-name-editor n editor-state]
     [layout-delete-tag-button n editor-state]]))

(defn layout-tag-list
  "Return a function that will layout a group of tag inputs."
  [editor-state]
  (fn [{:keys [tags-atom-vector] :as editor-state}]
    [:section.tag-editor--container
     [:label.tag-editor--label {:for "tag-list"} "Tags"]
     [:form {:name "tag-list"}
      [:div.tag-editor--list
       (for [n (range (count @tags-atom-vector))]
         ^{:key (tag-index->id n editor-state)}
         [layout-tag-composite-lozenge n editor-state])
       [layout-add-tag-button editor-state]]]]))
