;;;
;;; This namespace contains functions related to handling files on the
;;; server side. It also contains some string-related function for
;;; historical reasons.
;;;

(ns cwiki.util.files
  (:require [clj-yaml.core :as yaml]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [cwiki.util.datetime :as dt])
  (:import (java.io BufferedReader InputStreamReader File)))

(def ^:const sep (File/separator))

(defn file-name-from-parts
  "Return a file name built from the parts in v using the
  os-specific separator sep."
  [v]
  (s/join sep v))

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
    (s/join (funkshun pred coll))))

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
                   (s/blank? line))) coll))

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
              (let [meta (yaml->map (s/join "\n" (:front parts)))]
                (reset! result (assoc @result :meta meta))))
            (reset! result (assoc @result :body (s/join "\n" (:body parts))))))))
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

; Characters that are illegal in file names in some operating systems.
(def ^:const illegal-chars [\newline, \space, \tab, \formfeed, \backspace,
                            \return \/ \\ \u0000 \` \' \" \? \| \* \< \> \:])

; Device names that are reserved on some operating systems. From
; org.eclipse.core.resources.IWorkspace.validateName(String, int)
(def ^:const reserved-dev-names ["aux" "clock$ " "com1" "com2" "com3" "com4"
                                 "com5" "com6" "com7" "com8" "com9" "con"
                                 "lpt1" "lpt2" "lpt3" "lpt4" "lpt5" "lpt6"
                                 "lpt7" "lpt8" "lpt9" "nul" "prn"])

(defn trim-leading-and-trailing-underscores
  "Return a copy of the string in which any leading and trailing
  underscores have been removed."
  [s]
  (when s
    (s/replace
      (s/replace-first s (re-pattern "^\\_+") "")
      (re-pattern "\\_+$")
      "")))

(defn in?
  "Return true if coll contains elm."
  [elm coll]
  (some #(= elm %) coll))

(defn- red-fun
  "A helper function for the following reduction. Replaces illegal characters
  with an underscore."
  [accum elm]
  (if (in? elm illegal-chars)
    (.append accum \_)
    (.append accum elm)))

(defn remove-illegal-file-name-chars
  "Return a version of the sting where illegal characters have been
  replaces with underscores."
  [s]
  (str (reduce red-fun (StringBuilder.) s)))

(defn remove-reserved-device-names
  "Return an empty string if 's' is equal (case-insensitive) to any of the
  reserved device names. Otherwise return 's'."
  [s]
  (if (some #(= (.toLowerCase s) %) reserved-dev-names)
    ""
    s))

(defn sanitize-page-name
  "If possible, return a version of the page name with problematic characters
  and other issues translated to a version that can be used on any
  operating system."
  [page-name]
  (let [sanitary-name (-> (remove-illegal-file-name-chars page-name)
                          (trim-leading-and-trailing-underscores)
                          (remove-reserved-device-names))]
    sanitary-name))

(defn build-tag-yaml
  "Return the tags section of the YAML front matter."
  [tag-set]
  (if (seq tag-set)
    (let [sb (StringBuffer. "tags:\n")]
      (mapv #(.append sb (str "  - " % "\n")) tag-set)
      (str sb))
    ""))

(defn build-yaml
  "Return the YAML front matter based on the metadata for the page."
  [page-map author-name tags]
  (let [title (:page_title page-map)
        created (dt/get-formatted-date-time (:page_created page-map))
        modified (dt/get-formatted-date-time (:page_modified page-map))
        yaml (StringBuffer. "---\n")]
    (doto yaml
      (.append (str "author: " author-name "\n"))
      (.append (str "title: " title "\n"))
      (.append (str "date: " created "\n"))
      (.append (str "modified: " modified "\n"))
      (.append (build-tag-yaml tags)))
    (str (.append yaml "---\n\n"))))

(defn get-execution-directory
  "Return the canonical path where the program is executing."
  []
  (.getCanonicalPath (File. ".")))

(defn- get-private-resource-directory
  "Return the canonical path to where the private Markdown resources are
  saved."
  []
  (.getCanonicalPath (File. ^String (file-name-from-parts
                                      ["." "resources" "private" "md"]))))

(defn- get-exported-page-directory
  "Return the canonical path to the directory where exported pages are saved."
  []
  (.getCanonicalPath (File. ^String (file-name-from-parts
                                      ["." "exported-pages"]))))

(defn- save-page
  "Save the page described in the page-map to a file."
  [page-map author-name tags dir]
  (let [page-name (:page_title page-map)
        sanitized-name (sanitize-page-name page-name)]
    (if (empty? sanitized-name)
      (println "Problem with translating the page name")
      (let [path (str dir sep sanitized-name ".md")
            content (:page_content page-map)]
        ; Needed when saving seed pages while running from an uberjar.
        (io/make-parents path)
        (spit path (str (build-yaml page-map author-name tags) content))
        path))))

;; The only difference between exporting a "normal" page and a "seed" page
;; is the directory that they end up in.

(defn export-page
  "Export the page described in the page-map to a file."
  [page-map author-name tags]
  (save-page page-map author-name tags (get-exported-page-directory)))

(defn export-seed-page
  "Save the seed page described in the page-map to a file."
  [page-map author-name tags]
  (save-page page-map author-name tags (get-private-resource-directory)))

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

(defn- real-is-seed-page?
  "Return true if the input names a seed page, nil otherwise."
  [name]
  (let [seeds (load-initial-page-list)
        sanitized-name (str (sanitize-page-name name) ".md")]
    (some #(= sanitized-name %) seeds)))

(def is-seed-page? (memoize real-is-seed-page?))
