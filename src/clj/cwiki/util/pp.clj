;;;
;;; A utility with a short name to pretty-print maps for debugging.
;;

(ns cwiki.util.pp
  (:require [clojure.pprint :as pretty]))

(defn pp-map
  [m]
  (with-out-str (pretty/pprint m)))
