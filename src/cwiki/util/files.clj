(ns cwiki.util.files
  (:require [clj-yaml.core :as yaml]
            [clojure.java.io :as io]
            [clojure.string :as st])
  (:import (java.io BufferedReader InputStreamReader)))

;(defn drop-chars-while
;  "Remove characters from the beginning of the input string
;  that satisfy the predicate and return them as a string."
;  [pred coll]
;  (let [funkshun (fn [pred coll]
;                   (let [s (seq coll)]
;                     (if (and s (pred (first s)))
;                       (recur pred (rest s))
;                       s)))]
;    (st/join (funkshun pred coll))))

(defn take-chars-while
  "Take characters from the beginning of the input string that
  satisfy the predicate and return them as a string."
  [pred coll]
  (let [funkshun (fn [pred coll]
                   (when-let [s (seq coll)]
                     (when (pred (first s))
                       (cons (first s) (take-chars-while pred (rest s))))))]
    (st/join (funkshun pred coll))))

(defn drop-lines-while
  "While the lines at the beginning of the collection satisfy the
  predicate, drop them, returning the remainder of the collection."
  [pred coll]
  (let [s (seq coll)]
    (if (and s (pred (first s)))
      (recur pred (rest s))
      s)))

(defn drop-lines-while-blank
  "Remove blank lines from the beginning of a sequence of text lines
  and return the remainder."
  [coll]
  (drop-lines-while
    (fn [line] (or (empty? line)
                   (st/blank? line))) coll))

(defn split-front-matter-from-body
  "Given a collection of text lines, split any front matter from the body
  of the text. If present, the front matter must be the first thing appearing
  in the text and marked by leading and trailing lines consisting of three
  hypens ('---'). A map is returned containing the collection of lines
  consituting the body under the :body key. If YAML front matter is present,
  it is returned in the map under the key of :front."
  [coll]
  (let [lines (drop-lines-while-blank coll)]
    (if (= (first lines) "---")
      (let [[front sep-and-body] (split-with #(not= "---" %) (next lines))]
        {:front (vec front) :body (vec (next sep-and-body))})
      {:body (vec lines)})))

(defn yaml->map
  [s]
  (yaml/parse-string s))

(defn load-markdown-resource
  "Load a Markdown file, possibly with YAML front matter. Return a map with
  two top-level keys and subsidiary maps: :body contains the body of the
  Markdown file. :meta contains the meta-information, possibly from the
  YAML front matter."
  [filename]
  (let [result (atom {:meta {}
                      :body nil})
        url (io/resource filename)]
    (when (not (nil? url))
      (let [contents (-> url
                         (io/input-stream)
                         (InputStreamReader.)
                         (BufferedReader.)
                         (line-seq)
                         (vec))]
        (when contents
          (let [parts (split-front-matter-from-body contents)]
            (when (not-empty (:front parts))
              (let [meta (yaml->map (st/join "\n" (:front parts)))]
                (reset! result (assoc @result :meta meta))))
            (reset! result (assoc @result :body (st/join "\n" (:body parts))))))))
    @result))
