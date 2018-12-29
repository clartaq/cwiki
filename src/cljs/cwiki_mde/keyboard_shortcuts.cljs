(ns cwiki-mde.keyboard-shortcuts
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]
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
  (.focus ele)
  (if-not (exists? js/InstallTrigger)
    (let [start (.-selectionStart ele)
          end (.-selectionEnd ele)
          val (.-value ele)
          before (.substring val 0 start)
          after (.substring val end (.-length val))]
      (println "Firefox insert")
      (reset! input-atom (str before txt after)))
    (when txt
      (println "Not Firefox")
      (.execCommand js/document "insertText" false txt)))
  ((:dirty-editor-notifier editor-state) editor-state))

(defn- ele->input-atom
  "Return the input atom embedded in the given html element. Returns
  nil if the element is not the page content atom."
  [ele editor-state]
  (when-let [id (.-id ele)]
    (when (= id (:editor-textarea-id editor-state))
      (:page-content-ratom editor-state))))

;;------------------------------------------------------------------------------
;; Shortcut key binding.
;;

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
                 (when (and ele formatted-now input-atom))
                 (insert-text-into-input ele
                                         formatted-now
                                         input-atom
                                         editor-state)))))

(defn unbind-shortcut-keys
  "Un-bind all of the shortcut keys."
  []
  (kbs/unbind-all!))

