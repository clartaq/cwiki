(ns cwiki-mde.keyboard-shortcuts
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]
            [cwiki-mde.editor-commands :as cmd]
            [keybind.core :as kbs]))

;;------------------------------------------------------------------------------
;; Utility functions.
;;

;;------------------------------------------------------------------------------
;; Shortcut key binding.
;;

(defn bind-shortcut-keys
  "Bind shortcut keys to actions."
  [editor-state]

  ;; Save the page.
  (letfn [(save-from-keyboard-fxn [evt]
            (cmd/save-page-cmd editor-state)
            (.preventDefault evt)
            (.stopPropagation evt)
            false)]
    (kbs/bind! "defmod-s" ::save-shortcut save-from-keyboard-fxn))

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
               (let [ele (.-target evt)]
                 (cmd/insert-time-stamp ele editor-state)))))

(defn unbind-shortcut-keys
  "Un-bind all of the shortcut keys."
  []
  (kbs/unbind-all!))

