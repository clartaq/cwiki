;;;;
;;;; This namespace contains functions related to handling files on the
;;;; server side. It also contains some string-related functions for
;;;; historical reasons.
;;;;

(ns cwiki.util.files
  (:require [clj-yaml.core :as yaml]
            [clojure.java.io :as io]
            [clojure.set :refer [intersection]]
            [clojure.string :as s]
            [cwiki.util.datetime :as dt]
            [cwiki.util.zip :as zip]
            [toml.core :as toml])
  (:import (java.io BufferedReader InputStreamReader File)))

(def ^:const sep (File/separator))

;;;
;;; Some string-related utilities for playing with directory and file
;;; names and paths.
;;;

(defn in?
  "Return true if coll contains elm. Too, works when searching for a character
  in a string, but not a string within another string."
  [elm coll]
  (some #(= elm %) coll))

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

(defn trim-leading-and-trailing-underscores
  "Return a copy of the string in which any leading and trailing
  underscores have been removed."
  [s]
  (when s
    (s/replace
      (s/replace-first s (re-pattern "^\\_+") "")
      (re-pattern "\\_+$")
      "")))

;;;
;;; These directories should probably be in the preferences rather than
;;; hard-coded.
;;;

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

(defn get-exported-page-directory
  "Return the canonical path to the directory where exported pages are saved."
  []
  (.getCanonicalPath (File. ^String (file-name-from-parts
                                      ["." "exported-pages"]))))

(defn get-backup-directory
  []
  (.getCanonicalPath (File. ^String (file-name-from-parts
                                      ["." "backups"]))))
;;;
;;; Functions for working with files and file names.
;;;

;; Characters that are illegal in file names in some operating systems.
(def ^:const illegal-chars [\newline, \space, \tab, \formfeed, \backspace,
                            \return \/ \\ \u0000 \` \' \" \? \| \* \< \> \:])

;; Device names that are reserved on some operating systems. From
;; org.eclipse.core.resources.IWorkspace.validateName(String, int)
(def ^:const reserved-dev-names ["aux" "clock$ " "com1" "com2" "com3" "com4"
                                 "com5" "com6" "com7" "com8" "com9" "con"
                                 "lpt1" "lpt2" "lpt3" "lpt4" "lpt5" "lpt6"
                                 "lpt7" "lpt8" "lpt9" "nul" "prn"])

(defn- replace-illegal-chars
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
  (str (reduce replace-illegal-chars (StringBuilder.) s)))

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

(defn files-in-directory
  "Return a sequence of all of the files (including other directories) in
  'dir-name'."
  [dir-name]
  (let [directory (io/file dir-name)
        files (file-seq directory)]
    files))

(defn just-files-no-directories
  "Return a sequence of files from the directory where any subdirectories
  have been filtered out."
  [dir-name]
  (let [files-and-directories (files-in-directory dir-name)
        files-only (filter #(.isFile %) files-and-directories)]
    files-only))

(defn files-with-ext
  "Return a filtered sequence of `files` which have the extension `ext'."
  [files ext]
  (let [filtered-files (filter #(s/ends-with? (.getName %) ext) files)]
    filtered-files))

;;;
;;; Functions for working with pages.
;;;

(def yaml-marker "---")
(def toml-marker "+++")

(defn split-front-matter-from-body
  "Given a collection of text lines, split any front matter from the body
  of the text. If present, the front matter must be the first thing appearing
  in the text and marked by leading and trailing lines consisting of one of
  the two known markers, `yaml-marker` or `toml-marker`. A map is returned
  containing the collection of lines constituting the body under the :body
  key. If front matter is present, it is returned in the map under the key
  of :front and the particular marker (both YAML and TOML are recognized)
  under the key :marker."
  [coll]
  (let [lines (drop-lines-while-blank coll)
        first-line (first lines)
        fm-marker (cond
                    (= first-line yaml-marker) yaml-marker
                    (= first-line toml-marker) toml-marker
                    :default nil)]
    (cond (= fm-marker yaml-marker)
          (let [[front sep-and-body] (split-with #(not= yaml-marker %) (next lines))]
            {:marker fm-marker :front (vec front) :body (vec (next sep-and-body))})
          (= fm-marker toml-marker)
          (let [[front sep-and-body] (split-with #(not= toml-marker %) (next lines))]
            {:marker fm-marker :front (vec front) :body (vec (next sep-and-body))})
          :default {:marker nil :body (vec lines)})))

(defn yaml->map
  "Translate the string in YAML format to a map."
  [s]
  (yaml/parse-string s))

(defn toml->map
  "Translate the string in TOML format to a map."
  [s]
  (when (seq s)
    (toml/read s :keywordize)))

(defn front-matter->map
  "Convert the front matter from a string to a map. The marker argument
  determines the type of front matter to create: YAML or TOML."
  [marker s]
  (cond
    (= marker yaml-marker) (yaml->map s)
    (= marker toml-marker) (toml->map s)
    :default nil))

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
              (let [meta (front-matter->map (:marker parts)
                                            (s/join "\n" (:front parts)))]
                (reset! result (assoc @result :meta meta))))
            (reset! result (assoc @result :body (s/trim (s/join "\n" (:body parts)))))))))
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

(defn- build-tag-yaml
  "Return the tags section of the YAML front matter."
  [tag-set]
  (if (seq tag-set)
    (let [sb (StringBuffer. "tags:\n")
          ts (into #{} (mapv yaml/generate-string tag-set))]
      (mapv #(.append sb (str "  - " % "\n")) ts)
      (str sb))
    ""))

(defn- build-yaml-front-matter
  "Return the YAML front matter based on the metadata for the page."
  [page-map author-name tags]
  (let [title (:page_title page-map)
        created (dt/get-formatted-date-time (:page_created page-map))
        modified (dt/get-formatted-date-time (:page_modified page-map))
        yaml (StringBuffer. "---\n")]
    (doto yaml
      (.append (str "author: " (yaml/generate-string author-name) "\n"))
      (.append (str "title: " (yaml/generate-string title) "\n"))
      (.append (str "date: " created "\n"))
      (.append (str "modified: " modified "\n"))
      (.append (build-tag-yaml tags))
      (.append "---\n\n"))
    (.toString yaml)))

(defn- build-tag-toml
  "Return the tags section of the TOML front matter."
  [tag-set]
  (if (seq tag-set)
    (let [sb (StringBuffer. "tags = [")]
      ;; Trailing commas are ok in a TOML array.
      (mapv #(.append sb (str "\"" % "\", ")) tag-set)
      (.append sb "]")
      (.toString sb))
    ""))

(defn- build-toml-front-matter
  "Return the TOML front matter based on the metadata for the page."
  [page-map author-name tags]
  (let [title (:page_title page-map)
        created (:page_created page-map)
        modified (:page_modified page-map)
        toml (StringBuffer. "+++\n")
        m {:author author-name
           :title  title
           :tags   (vec tags)}]
    ;(println "toml/write: " (toml/write m))
    (doto toml
      ;; Use the library to handle quotes correctly.
      (.append (toml/write m))
      ;; Handle dates ourselves. Can't seem to get it formatted correctly
      ;; using library
      (.append (str "date = " (dt/get-formatted-date-time created) "\n"))
      (.append (str "modified = " (dt/get-formatted-date-time modified) "\n"))
      (.append "+++\n\n"))
    (.toString toml)))

(defn build-front-matter
  "Build and return a string containing front matter for the page. The
  front matter is in the TOML configuration language."
  [page-map author-name tags]
  (build-yaml-front-matter page-map author-name tags))
  ;(build-toml-front-matter page-map author-name tags))

(defn- save-page
  "Save the page described in the page-map to a file."
  [page-map author-name tags dir]
  (let [page-name (:page_title page-map)
        sanitized-name (sanitize-page-name page-name)]
    (if (empty? sanitized-name)
      (println "Problem with translating the page name: page-map: " page-map)
      (let [path (str dir sep sanitized-name ".md")
            content (:page_content page-map)]
        ;; Needed when saving seed pages while running from an uberjar.
        (io/make-parents path)
        (spit path (str (build-front-matter page-map author-name tags) content))
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

(defn backup-page
  "Backup the page described in the page-map to a file. Similar to doing
  an export, but goes into the designated backup directory."
  [page-map author-name tags]
  (save-page page-map author-name tags (get-backup-directory)))

(defn zip-directory-of
  "Create a zip of all the files ending with 'of' in the directory 'dir-name'
  and output a file named 'output-file-name' in the same directory."
  [dir-name of output-file-name]
  (let [file-list (just-files-no-directories dir-name)
        of-list (files-with-ext file-list of)
        path-list (map #(.getCanonicalPath %) of-list)]
    (cwiki.util.zip/zip-files output-file-name path-list)))

(defn backup-compressed-database
  "Compress all of the markdown files in the backup directory into a single
  zip file in the backup directory."
  []
  (let [timestamp (dt/get-file-timestamp)
        output-path (str (file-name-from-parts [(get-backup-directory)
                                                "backup"]) "-" timestamp ".zip")]
    (zip-directory-of (get-backup-directory) ".md" output-path)))

(defn restore-compressed-pages
  "Unzip all of the files in a compressed database and put them in the backup
  directory. Will uncompress all files in the zip, not just markdown pages.
  Returns a sequence (via unzip-to-path) of paths to all the pages restored."
  [backup-file-name]
  (let [backedup-file-path (file-name-from-parts
                             [(get-backup-directory) backup-file-name])]
    (zip/unzip-to-path backedup-file-path (get-backup-directory))))

;;;
;;; Misc.
;;;

(defn- line-has-content?
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
        filtered-lines (filterv line-has-content? raw-lines)]
    filtered-lines))

(defn- real-is-seed-page?
  "Return true if the input names a seed page, nil otherwise."
  [page-name]
  (let [seeds (load-initial-page-list)
        sanitized-name (str (sanitize-page-name page-name) ".md")]
    (some #(= sanitized-name %) seeds)))

(def is-seed-page? (memoize real-is-seed-page?))
