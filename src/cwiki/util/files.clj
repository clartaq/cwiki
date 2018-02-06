(ns cwiki.util.files
  (:require [clj-yaml.core :as yaml]
            [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.string :as s])
  (:import (java.io BufferedReader InputStreamReader File)))

(defn remove-from-end
  "Remove any instance of 'end' from the end of string s
  and return the result."
  [s end]
  (if (.endsWith s end)
    (.substring s 0 (- (count s)
                       (count end)))
    s))

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

(defn load-markdown-from-url
  "Load a Markdown file, possibly with YAML front matter from a url.
  Return a map with two top-level keys and subsidiary maps: :body
  contains the body of the Markdown file. :meta contains the
  meta-information, possibly from the YAML front matter."
  [url]
  (let [result (atom {:meta {}
                      :body nil})]
    (when-not (nil? url)
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

(defn load-markdown-from-resource
  "Load a Markdown file, possibly with YAML front matter from a resource,
  usually one inside the program jar. Return a map with two top-level
  keys and subsidiary maps: :body contains the body of the Markdown file.
  :meta contains the meta-information, possibly from the YAML front matter."
  [filename]
  (let [url (io/resource filename)]
    (load-markdown-from-url url)))

(defn load-markdown-from-file
  "Load a Markdown file, possibly with YAML front matter from a file.
  Return a map with two top-level keys and subsidiary maps: :body
  contains the body of the Markdown file. :meta contains the
  meta-information, possibly from the YAML front matter. Returns nil
  if the file does not exist."
  [^File file]
  (when (.exists file)
    (load-markdown-from-url (io/as-url file))))

(defn- filter-predicate
  "Return true if the line is not empty and does not start with a semi-colon"
  [line]
  (and (seq line)
       (not (s/starts-with? line ";"))))

(defn load-initial-page-list
  "Read the file containing the list of initial pages to load into the database
  and return the names of the files in a seq."
  []
  (let [raw-lines (-> (io/resource "private/md/initial_pages.txt")
                      (io/input-stream)
                      (InputStreamReader.)
                      (BufferedReader.)
                      (line-seq)
                      (vec))
        filtered-lines (filterv filter-predicate raw-lines)]
    filtered-lines))
