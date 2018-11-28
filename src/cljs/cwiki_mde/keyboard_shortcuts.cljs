(ns cwiki-mde.keyboard-shortcuts
  (:require [clojure.string :as s]
            [cljs-time.coerce :as c]
            [cljs-time.core :as t]
            [cljs-time.format :as f]
            [cwiki-mde.tag-editor :as te]
            [keybind.core :as kbs]))

;;------------------------------------------------------------------------------
;; Utility functions.
;;

; Format a DateTime object nicely.
(def custom-formatter (f/formatter "dd MMM yyyy, hh:mm:ss a"))

(defn- get-formatted-now
  "Return a nicely formatted string containing the local instant time."
  []
  (f/unparse custom-formatter (t/time-now)))

(defn insert-text-into-input
  "Inserts the text into the element wherever the cursor happens to be."
  [ele txt input-atom editor-state]
  (let [start (.-selectionStart ele)
        end (.-selectionEnd ele)
        val (.-value ele)
        before (.substring val 0 start)
        after (.substring val end (.-length val))]
    (reset! input-atom (str before txt after))
    ((:dirty-editor-notifier editor-state) editor-state)))

;; Example of doing this manually.
;(let [save-fxn (:assemble-and-save-fn options)]
;  (.addEventListener js/document "keydown"
;                     (fn [e]
;                       (let [the-key (.-key e)
;                             lc-key (.toLowerCase the-key)]
;                         (when (and (= "s" lc-key)
;                                    (.-metaKey e))
;                           (save-fxn options)
;                           (.preventDefault e)
;                           (.stopPropagation e)
;                           false))) false))

(defn- tag-input-id->tag-input-atom
  [id editor-state]
  (println "tag id: " id)
  (let [n (dec (js/parseInt (re-find  #"\d+" id )))
        _ (println "n: " n)]
    (println "tag number n: " n)
    (println "(:tags-atom-vector editor-state)" (:tags-atom-vector editor-state))
    (println "(deref (:tags-atom-vector editor-state)): " (deref (:tags-atom-vector editor-state)))
    (nth (deref (:tags-atom-vector editor-state)) n)))

(defn- ele->input-atom
  [ele editor-state]
  (when-let [id (.-id ele)]
    (cond
      (= id (:editor-textarea-id editor-state)) (:page-content-ratom editor-state)
      (= id (:editor-title-input-id editor-state)) (:page-title-atom editor-state)
      (s/starts-with? id (:editor-tag-id-prefix editor-state)) (te/tag-id->input-atom id))))

(defn bind-shortcut-keys
  "Bind shortcut keys to actions."
  [editor-state]

  ;; Save the page.
  (let [save-fxn (:assemble-and-save-fn editor-state)]
    (letfn [(save-from-keyboard-fxn [evt]
              (save-fxn editor-state)
              (.preventDefault evt)
              (.stopPropagation evt)
              false)]
      (kbs/bind! "defmod-s" ::save-shortcut save-from-keyboard-fxn)))

  ;; Quit the editor.
  ;(let [quit-fxn (:quit-fn editor-state)]
  ;  (letfn [(quit-from-keyboard-fxn [e editor-state]
  ;           ; (quit-fxn editor-state)
  ;            (.preventDefault e)
  ;            (.stopPropagation e)
  ;            false)]
  ;    (kbs/bind! "defmod-w" ::quit-shortcut quit-from-keyboard-fxn)))

  ;; Insert a timestamp.
  (kbs/bind! "alt-defmod-t" ::timestamp-shortcut
             (fn [evt]
               (let [ele (.-target evt)
                     formatted-now (get-formatted-now)
                     input-atom (ele->input-atom ele editor-state)]
                 (println "ele: " ele ", formatted-now: " formatted-now ", input-atom: " input-atom)
                 (when (and ele formatted-now input-atom))
                   (insert-text-into-input ele
                                           formatted-now
                                           input-atom
                                           editor-state)))));)

(defn unbind-shortcut-keys
  "Un-bind all of the shortcut keys."
  []
  (kbs/unbind-all!))

