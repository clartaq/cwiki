;;;;
;;;; This namespace contains functions to percent encode RFC 3986
;;;; reserved characters in strings and to decode such strings back to
;;;; normal UTF-8.
;;;;


(ns cwiki.util.percent-encode
  (:require [clojure.string :refer [escape]]))

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
