;;;;
;;;; This namespace includes functions to create a read compressed
;;;; zip files in Clojure. It is largely a copy functions from
;;;; https://github.com/Raynes/fs/blob/master/src/me/raynes/fs/compression.clj
;;;;

(ns cwiki.util.zip
  (:require [clojure.java.io :as io])
  (:import (java.io ByteArrayOutputStream File)))

;; Once you've started a JVM, that JVM's working directory is set in stone
;; and cannot be changed. This library will provide a way to simulate a
;; working directory change. `cwd` is considered to be the current working
;; directory for functions in this library. Unfortunately, this will only
;; apply to functions inside this library since we can't change the JVM's
;; actual working directory.
(def ^{:doc "Current working directory. This cannot be changed in the JVM.
             Changing this will only change the working directory for functions
             in this library."
       :dynamic true}
  *cwd* (.getCanonicalFile (io/file ".")))

;; Library functions will call this function on paths/files so that
;; we get the cwd effect on them.
(defn ^File cw-file
  "If `path` is a period, replaces it with cwd and creates a new File object
   out of it and `paths`. Or, if the resulting File object does not constitute
   an absolute path, makes it absolutely by creating a new File object out of
   the `paths` and cwd."
  [path & paths]
  (when-let [path (apply
                    io/file (if (= path ".")
                              *cwd*
                              path)
                    paths)]
    (if (.isAbsolute ^File path)
      path
      (io/file *cwd* path))))

(defn base-name
  [path]
  (.getName (cw-file path)))

(defn- add-zip-entry
  "Add a zip entry. Works for strings and byte-arrays."
  [^java.util.zip.ZipOutputStream zip-output-stream [^String name content & remain]]
  (.putNextEntry zip-output-stream (java.util.zip.ZipEntry. name))
  (if (string? content) ;string and byte-array must have different methods
    (doto (java.io.PrintStream. zip-output-stream true)
      (.print content))
    (.write zip-output-stream ^bytes content))
  (.closeEntry zip-output-stream)
  (when (seq (drop 1 remain))
    (recur zip-output-stream remain)))

(defn make-zip-stream
  "Create zip file(s) stream. You must provide a vector of the
  following form:
  ```[[filename1 content1][filename2 content2]...]```.
  You can provide either strings or byte-arrays as content.
  The piped streams are used to create content on the fly, which means
  this can be used to make compressed files without even writing them
  to disk."
  [& filename-content-pairs]
  (let [file
        (let [pipe-in (java.io.PipedInputStream.)
              pipe-out (java.io.PipedOutputStream. pipe-in)]
          (future
            (with-open [zip (java.util.zip.ZipOutputStream. pipe-out)]
              (add-zip-entry zip (flatten filename-content-pairs))))
          pipe-in)]
    (io/input-stream file)))

(defn zip
  "Create zip file(s) on the fly. You must provide a vector of the
  following form:
  ```[[filename1 content1][filename2 content2]...]```.
  You can provide either strings or byte-arrays as content."
  [filename & filename-content-pairs]
  (io/copy (make-zip-stream filename-content-pairs)
           (cw-file filename)))

(defn- slurp-bytes [fpath]
  (with-open [data (io/input-stream (cw-file fpath))]
    (with-open [out (ByteArrayOutputStream.)]
      (io/copy data out)
      (.toByteArray out))))

(defn make-zip-stream-from-files
  "Like make-zip-stream but takes a sequential of file paths and builds filename-content-pairs
   based on those"
  [fpaths]
  (let [filename-content-pairs (map (juxt base-name slurp-bytes) fpaths)]
    (make-zip-stream filename-content-pairs)))

(defn zip-files
  "Zip files provided in argument vector to a single zip. Converts the argument list:
  ```(fpath1 fpath2...)```
  into filename-content -pairs, using the original file's basename as the filename in zip`and slurping the content:
  ```([fpath1-basename fpath1-content] [fpath2-basename fpath2-content]...)``"
  [filename fpaths]
  (io/copy (make-zip-stream-from-files fpaths)
           (cw-file filename)))

