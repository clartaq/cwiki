(ns cwiki.util.datetime
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clj-time.format :as f]
            )
  (:import (org.joda.time DateTime)))

;; Things related to time formatting.

(def markdown-pad-format (f/formatter-local "MM/dd/yyy h:mm:ss a"))
(def hugo-format (f/formatter-local "yyy-MM-dd'T'HH:mm:ss.SSSZZ"))

(defn get-formatted-date-time
  "Take a sql timestamp and return a formatted string version."
  [timestamp]
  (let [dt (DateTime. (java.sql.Timestamp/valueOf ^String (str timestamp)))]
    (f/unparse markdown-pad-format dt)))

(defn sql-now
  "Return a sql timestamp of the current instant."
  []
  (c/to-sql-time (t/now)))

(defn meta-datetime-to-sql-datetime
  "Return a sql timestamp based on the one passed as argument."
  [timestamp]
  ;; The argument will be either a String or a java.util.Date.
  ;; Apparently the YAML parsing library that I am using passes
  ;; a verbatim string if it doesn't recognize the format. If it
  ;; does recognize the format, it passes an object of that type
  ;; instead.
  (if (string? timestamp)
    (c/to-sql-time (f/parse markdown-pad-format
                            timestamp))
    (c/to-sql-time timestamp)))
