(ns cwiki.util.datetime
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clj-time.format :as f])
  (:import (org.joda.time DateTime)
           (org.joda.time.format DateTimeFormatter)))

;; Things related to time formatting.

(def markdown-pad-format (f/formatter-local "MM/dd/yyy h:mm:ss a"))
(def hugo-format (f/formatter-local "yyy-MM-dd'T'HH:mm:ss.SSSZZ"))
(def cwiki-formatters (merge f/formatters {:markdownpad-formatter markdown-pad-format
                                           :hugo-formatter hugo-format}))

;; This is an almost direct copy of the same function from clj-time.format.
;; It uses the slightly extended set of formatters created above. It is
;; here in an effort to make recognition of timestamps in the YAML of
;; imported pages a little more robust.

(defn ^org.joda.time.DateTime parse
  "Returns a DateTime instance in the UTC time zone obtained by parsing the
   given string according to the given formatter."
  ([^DateTimeFormatter fmt ^String s]
   (.parseDateTime fmt s))
  ([^String s]
   (first
     (for [f (vals cwiki-formatters)
           :let [d (try (parse f s) (catch Exception _ nil))]
           :when d] d))))

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
    (c/to-sql-time (parse ;markdown-pad-format
                            timestamp))
    (c/to-sql-time timestamp)))
