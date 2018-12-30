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

(defn save-page-cmd
  [editor-state]
  (let [save-fxn (:assemble-and-save-fn editor-state)]
    (save-fxn editor-state)))

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
      (println "Firefox insert")
      (reset! input-atom (str before txt after)))
    (when txt
      (println "Not Firefox")
      (.execCommand js/document "insertText" false txt)))
  ((:dirty-editor-notifier editor-state) editor-state))

(defn insert-time-stamp
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
  [ele editor-state]
  (let [input-atom ele->input-atom
        start (.-selectionStart ele)
        end (.-selectionEnd ele)
        highlighted (.substr (.-value ele) start (- end start))
        encoded (percent-encode highlighted)]
    (println "encoded: " encoded)
    (insert-text-cmd ele encoded input-atom editor-state)))
