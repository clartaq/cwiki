;;;;
;;;; This namespace contains functions that map keyboard shortcuts to editor
;;;; commands.
;;;;

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
  (kbs/bind! "defmod-s" ::save-shortcut
             (fn [evt]
               (cmd/save-page-cmd editor-state)
               (.preventDefault evt)
               (.stopPropagation evt)
               false))

  ;; Toggle visibility of editor preview pane.
  (kbs/bind! "f9" ::toggle-preview-shortcut
             (fn [evt]
               (cmd/toggle-preview-cmd editor-state)
               (.preventDefault evt)
               (.stopPropagation evt)))

  ;; Insert a timestamp.
  (kbs/bind! "alt-defmod-k" ::timestamp-shortcut
             (fn [evt]
               (let [ele (.-target evt)]
                 (cmd/insert-time-stamp ele editor-state)
                 (.preventDefault evt)
                 (.stopPropagation evt))))

  ;; Percent encode the selection.
  (kbs/bind! "alt-defmod-e" ::percent-encode-shortcut
             (fn [evt]
               (cmd/percent-encode-selection (.-target evt) editor-state)))

  ;; Quit the editor.
  (kbs/bind! "alt-defmod-x" ::quit-editor-shortcut
             (fn [evt]
               (cmd/quit-editor-cmd editor-state)
               (.preventDefault evt)
               (.stopPropagation evt)
               false))

  )

(defn unbind-shortcut-keys
  "Un-bind all of the shortcut keys."
  []
  (kbs/unbind-all!))

