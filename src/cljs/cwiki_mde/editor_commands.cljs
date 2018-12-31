;;;;
;;;; This namespace contains function that execute various commands on the
;;;; editor.
;;;;

(ns cwiki-mde.editor-commands
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]
            [clojure.string :refer [escape]]))

;-------------------------------------------------------------------------------
; Utilities
;

;;
;; Mapping of RFC 3986 reserved characters (and space and percent) to percent
;; encoded strings. See also: https://en.wikipedia.org/wiki/Percent-encoding.
;;

(defonce mapping
         {\space "%20"
          \!     "%21"
          \#     "%23"
          \$     "%24"
          \%     "%25"
          \&     "%26"
          \'     "%27"
          \(     "%28"
          \)     "%29"
          \*     "%2A"
          \+     "%2B"
          \,     "%2C"
          \/     "%2F"
          \:     "%3A"
          \;     "%3B"
          \=     "%3D"
          \?     "%3F"
          \@     "%40"
          \[     "%5B"
          \]     "%5D"})

(defn percent-encode
  "Return a new string where the reserved characters in s have been
  percent encoded."
  [s]
  (escape s mapping))

; Format a DateTime object nicely.
(def custom-formatter (f/formatter "dd MMM yyyy, hh:mm:ss a"))

(defn- get-formatted-now
  "Return a nicely formatted string containing the local instant time."
  []
  (f/unparse custom-formatter (t/time-now)))

(defn- ele->input-atom
  "Return the input atom embedded in the given html element. Returns
  nil if the element is not the page content atom."
  [ele editor-state]
  (when-let [id (.-id ele)]
    (when (= id (:editor-textarea-id editor-state))
      (:page-content-ratom editor-state))))

;-------------------------------------------------------------------------------
; Commands
;

(defn save-page-cmd
  "Save the page contents in the editor to the server."
  [editor-state]
  (let [save-fxn (:assemble-and-save-fn editor-state)]
    (save-fxn editor-state)))

;; This function inserts text using a method determined by the browser it is
;; running in. If `execCommand` can be used, it is, thus preserving the undo
;; stack. Since FireFox does not support it correctly at the moment, a
;; different method is used, but it breaks the undo stack.
(defn insert-text-cmd
  "Inserts the text into the element wherever the cursor happens to be."
  [ele txt input-atom editor-state]
  (.focus ele)
  (if (exists? js/InstallTrigger)
    (let [start (.-selectionStart ele)
          end (.-selectionEnd ele)
          val (.-value ele)
          before (.substring val 0 start)
          after (.substring val end (.-length val))]
      (reset! input-atom (str before txt after)))
    (when txt
      (.execCommand js/document "insertText" false txt)))
  ((:dirty-editor-notifier editor-state) editor-state))

(defn insert-time-stamp
  "Insert a timestamp into the input component."
  [ele editor-state]
  (let [
        formatted-now (get-formatted-now)
        input-atom (ele->input-atom ele editor-state)]
    (when (and ele formatted-now input-atom))
    (insert-text-cmd ele
                     formatted-now
                     input-atom
                     editor-state)))

(defn percent-encode-selection
  "Replace the hightlighted text in the input component with a percent
  encoded version."
  [ele editor-state]
  (let [input-atom ele->input-atom
        start (.-selectionStart ele)
        end (.-selectionEnd ele)
        highlighted (.substr (.-value ele) start (- end start))
        encoded (percent-encode highlighted)]
    (insert-text-cmd ele encoded input-atom editor-state)))

(defn quit-editor-cmd
  "Quit the editor."
  [editor-state]
  (let [quit-fn (:quit-fn editor-state)]
    (quit-fn editor-state)))
