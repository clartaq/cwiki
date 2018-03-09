(ns cwiki.util.datetime
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clj-time.format :as f]
            )
  (:import (org.joda.time DateTime)))

;; Things related to time formatting.

(def markdown-pad-format (f/formatter-local "MM/dd/yyy h:mm:ss a"))

;; Things related to time formatting.

(def markdown-pad-format (f/formatter-local "MM/dd/yyy h:mm:ss a"))
(def hugo-format (f/formatter-local "yyy-MM-dd'T'HH:mm:ss.SSSZZ"))

(defn get-formatted-date-time
  "Take a sql timestamp and return a formatted string version."
  [timestamp]
  (let [dt (DateTime. (java.sql.Timestamp/valueOf ^String (str timestamp)))]
    (f/unparse markdown-pad-format dt)))

(defn sql-now
  []
  (c/to-sql-time (t/now)))

(defn meta-datetime-to-sql-datetime
  [timestamp]
  (println "meta-datetime-to-sql-datetime: timestamp:" timestamp)
  (println "(type timestamp):" (type timestamp))
  (if (string? timestamp)
    (c/to-sql-time (f/parse markdown-pad-format
                            timestamp))
    (let [r (c/to-sql-time timestamp)]
      (println "r:" r)
      r)
    )
  )