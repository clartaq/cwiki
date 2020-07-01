;;;;
;;;; This namespace contains function that execute various commands on the
;;;; editor.
;;;;

(ns cwiki-mde.editor-commands
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]
            [clojure.string :as s :refer [escape]]
            ;[cwiki-mde.chunks :as chunks]
            [cwiki-mde.ws :as ws]
            [reagent.core :as r]))

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

(defn toggle-preview-cmd
  "Toggles whether the editor should show a second column with a preview of
  the page to the right of the editor pane."
  [editor-state]
  (let [vnr (:view-preview-ratom editor-state)
        viewing-now @vnr
        not-now (not viewing-now)]
    (reset! vnr not-now)
    ;;Send result to server to store in options.
    (ws/chsk-send! [:hey-server/save-option {:view_preview not-now}])))

(defn insert-time-stamp
  "Insert a timestamp into the input component."
  [ele editor-state]
  (let [formatted-now (get-formatted-now)
        input-atom (ele->input-atom ele editor-state)]
    (when (and ele formatted-now input-atom)
      (insert-text-cmd ele
                       formatted-now
                       input-atom
                       editor-state))))

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

;; Original comment from pagedown:
;; These two regex are identical except at the beginning and end. Should
;; probably use the regex extension function to make this clearer.
;(def prev-items-regex #"(\n|^)(([ ]{0,3}([*+-]|\d+[.])[ \t]+.*)(\n.+|\n{2,}([*+-].*|\d+[.])[ \t]+.*|\n{2,}[ \t]+\S.*)*)\n*$")
;(def next-items-regex #"^\n*(([ ]{0,3}([*+-]|\d+[.])[ \t]+.*)(\n.+|\n{2,}([*+-].*|\d+[.])[ \t]+.*|\n{2,}[ \t]+\S.*)*)\n*")
;(def bullet "-")
;(def num 1);

;(defn- extend-list
;  [chunks]
;  (println "extend-list")
;  )

  ;// These are identical except at the very beginning and end.
  ;// Should probably use the regex extension function to make this clearer.
  ;var previousItemsRegex = /(\n|^)(([ ]{0,3}([*+-]|\d+[.])[ \t]+.*)(\n.+|\n{2,}([*+-].*|\d+[.])[ \t]+.*|\n{2,}[ \t]+\S.*)*)\n*$/;
  ;var nextItemsRegex = /^\n*(([ ]{0,3}([*+-]|\d+[.])[ \t]+.*)(\n.+|\n{2,}([*+-].*|\d+[.])[ \t]+.*|\n{2,}[ \t]+\S.*)*)\n*/;
  ;
  ;// The default bullet is a dash but others are possible.
  ;// This has nothing to do with the particular HTML bullet,
  ;// it's just a markdown bullet.
  ;var bullet = "-";
  ;
  ;// The number in a numbered list.
  ;var num = 1;


;(defn- extend-blockquote
;  [chunks]
;  (println "extend-blockquote"))
;
;(defn- extend-code-block
;  [chunks]
;  (println "extend-code-block"))
;
;(defn auto-indent
;  [ele editor-state]
;  (println "auto-indent")
;  (let [chunks (chunks/ele->chunks ele)]
;    (println "    @chunks: " @chunks)
;    (let [fake-selection (atom false)
;          content-ratom (:page-content-ratom editor-state)]
;      ;; Remove empty items and block-quote lines.
;      (swap! chunks assoc :before (-> (:before @chunks)
;                                      ;; Match an empty list item alone on a line.
;                                      (s/replace
;                                        #"(\n|^)[ ]{0,3}([*+-]|\d+[.])[ \t]*\n$" "\n\n")
;                                      ;; Match any empty block-quoted lines.
;                                      (s/replace #"(\n|^)[ ]{0,3}>[ \t]*\n$" "\n\n")
;                                      ;; Match any lines containing just whitespace.
;                                      (s/replace #"(\n|^)[ \t]+\n$" "\n\n")))
;
;      ;; There is no selection and the cursor wan't at the end of the line:
;      ;; The user wants to split the current list item /  code line /
;      ;; blockquote line (for the latter, it doesn't really matter) in two.
;      ;; Temporarily select the (rest of the) line to achieve this.
;
;      (when (and (empty? (:selection @chunks))
;                 (nil? (re-find #"^[ \\t]*(?:\\n|$)" (:after @chunks))))
;        (println "WooHoo"))
;
;      ;      // There's no selection, end the cursor wasn't at the end of the line:
;      ;      // The user wants to split the current list item / code line / blockquote line
;      ;      // (for the latter it doesn't really matter) in two. Temporarily select the
;      ;      // (rest of the) line to achieve this.
;      ;      if (!chunk.selection && !/^[ \t]*(?:\n|$)/.test(chunk.after)) {
;      ;          chunk.after = chunk.after.replace(/^[^\n]*/, function (wholeMatch) {
;      ;              chunk.selection = wholeMatch;
;      ;              return "";
;      ;      });
;      ;          fakeSelection = true;
;      ;      }
;
;      (cond
;
;        ;; Handle automatic extension of lists.
;        (re-find #"(\n|^)[ ]{0,3}([*+-]|\d+[.])[ \t]+.*\n$" (:before @chunks))
;        (extend-list chunks)
;
;        ;; Handle extension of blockquote.
;        (re-find #"(\n|^)[ ]{0,3}>[ \t]+.*\n$" (:before @chunks))
;        (extend-blockquote chunks)
;
;        ;; Handle extension of code block.
;        (re-find @"(\\n|^)(\\t|[ ]{4,}).*\\n$" (:before @chunks))
;        (extend-code-block chunks)
;
;        )
;
;      (reset! content-ratom (str (:before @chunks) (:selection @chunks) (:after @chunks)))
;      (println "    (count clean-b4): " (count (:before @chunks))) ;clean-b4))
;      (r/after-render (fn []
;                        (.focus ele)
;                        (let [cnt (count (:before @chunks))] ;clean-b4)]
;                          (println "    (count clean-b4): " (count (:before @chunks))) ;clean-b4))
;                          (.setSelectionRange ele cnt cnt)))))))

(defn quit-editor-cmd
  "Quit the editor."
  [editor-state]
  (let [quit-fn (:quit-fn editor-state)]
    (quit-fn editor-state)))
