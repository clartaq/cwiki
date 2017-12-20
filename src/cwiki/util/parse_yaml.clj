;;;
;;; The functions in this file handle parsing the YAML front matter from a
;;; text file that is preceeded by such meta data.
;;;
;;; It is inspired by the answer to this question at StackOverflow:
;;; https://stackoverflow.com/questions/18323800/more-idiomatic-line-by-line-handling-of-a-file-in-clojure
;;;

(ns cwiki.util.parse-yaml
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn process-file-with [f filename]
  (with-open [rdr (io/reader (io/file filename))]
    (f (line-seq rdr))))

(defn process-lines [lines]
  (let [ls (->> lines
                (map string/trim)
                (remove string/blank?))]
    (if (= (first ls) "---")
      (let [[front sep-and-body] (split-with #(not= "---" %) (next ls))]
        {:front (vec front) :body (vec (next sep-and-body))})
      {:body (vec ls)})))

(defn process-file-with-frontmatter
  [filename]
  (process-file-with process-lines filename))