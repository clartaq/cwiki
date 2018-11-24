(ns cwiki-mde.keyboard-shortcuts
  (:require [cljs-time.coerce :as c]
            [cljs-time.core :as t]
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

(defn- get-element-by-id [the-id]
  (.getElementById js/document the-id))

(defn insert-text-into-input
  "Inserts the text into the element wherever the cursor happens to be."
  [ele txt]
  (let [start (.-selectionStart ele)
        end (.-selectionEnd ele)
        se (+ start (.-length txt))
        val (.-value ele)
        before (.substring val 0 start)
        after (.substring val end (.-length val))]
    (set! (.-value ele) (str before txt after))
    (set! (.-selectionStart ele) se)
    (set! (.-selectionEnd ele) se)
    (.focus ele)))

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
  [editor-options]

  ;; Save the page.
  (let [save-fxn (:assemble-and-save-fn editor-options)]
    (letfn [(save-from-keyboard-fxn [evt]
              (save-fxn editor-options)
              (.preventDefault evt)
              (.stopPropagation evt)
              false)]
      (kbs/bind! "defmod-s" ::save-shortcut save-from-keyboard-fxn)))

  ;; Quit the editor.
  ;(let [quit-fxn (:quit-fn editor-options)]
  ;  (letfn [(quit-from-keyboard-fxn [e editor-options]
  ;           ; (quit-fxn editor-options)
  ;            (.preventDefault e)
  ;            (.stopPropagation e)
  ;            false)]
  ;    (kbs/bind! "defmod-w" ::quit-shortcut quit-from-keyboard-fxn)))

  ;; Timestamp.
  (kbs/bind! "alt-defmod-t" ::timestamp-shortcut
             (fn [evt]
               (let [ele (.-target evt)
                     chg-evt (js/Event. "change" #js{:bubbles "true"})]
                 (println "chg-evt: " chg-evt)
                 (println "   (.-bubbles chg-evt): " (.-bubbles chg-evt))
                 (when (= (.-id ele) (:editor-textarea-id editor-options))
                   (insert-text-into-input ele (get-formatted-now))
                   (println "About to dispatch event")
                   (let [cancelled (.dispatchEvent ele chg-evt)]
                     (println "cancelled: " cancelled)))))))

(defn unbind-shortcut-keys
  []
  (kbs/unbind-all!))

