(ns cwiki.models.wiki-db
  (:require [buddy.hashers :as hashers]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.set :as set]
            [clojure.string :as s]
            [cwiki.util.files :as files]
            [cwiki.util.special :as special]
            [cwiki.util.datetime :as dt]
            [environ.core :refer [env]]
            [taoensso.timbre :refer [trace debug info warn error
                                     tracef debugf infof warnf errorf]]
            [clojure.pprint :as pp]
            [clojure.edn :as edn])
  (:import (java.io File)
           (java.util UUID)
           (org.h2.jdbc JdbcClob)))

;; Things that deal with the database file and connection.

; The kerfuffle here is to get the directory from which the program
; is running and create an absolute path as required for the H2 database.

(defn get-db-file-name []
  (str (-> (File. ".")
           .getAbsolutePath
           (files/remove-from-end "."))
       "resources/public/db/database.db"))

; Because H2 seems to append this to the name above.
(defn get-db-file-name-long []
  (str (get-db-file-name) ".mv.db"))

(defn get-h2-db-spec []
  {:classname   "org.h2.Driver"
   :subprotocol "h2:file"
   :subname     (get-db-file-name)
   :make-pool?  true})

(defn create-new-post-map
  "Return a new post map with the information provided."
  ([title]
   (create-new-post-map title ""))
  ([title content]
   (create-new-post-map title content 1))
  ([title content author-id]
   {:page_created  (dt/sql-now)
    :page_modified (dt/sql-now)
    :page_author   author-id
    :page_title    title
    :page_content  content}))

(defn get-valid-roles [] ["cwiki" "admin" "editor" "writer" "reader"])

(defn get-initial-users []
  [{:user_name              "CWiki"
    :user_role              "cwiki"
    :user_password          (hashers/derive (str (UUID/randomUUID)))
    :user_new_password      nil
    :user_new_password_time nil
    :user_email             ""
    :user_email_token       0
    :user_email_expires     nil
    :user_touched           (dt/sql-now)
    :user_registration      (dt/sql-now)}
   {:user_name              "admin"
    :user_role              "admin"
    :user_password          (hashers/derive "admin")
    :user_new_password      nil
    :user_new_password_time nil
    :user_email             ""
    :user_email_token       0
    :user_email_expires     nil
    :user_touched           (dt/sql-now)
    :user_registration      (dt/sql-now)}
   {:user_name              "guest"
    :user_role              "reader"
    :user_password          (hashers/derive "guest")
    :user_new_password      nil
    :user_new_password_time nil
    :user_email             ""
    :user_email_token       0
    :user_email_expires     nil
    :user_touched           (dt/sql-now)
    :user_registration      (dt/sql-now)}])

(defn escape-apostrophes
  [bad-string]
  (when bad-string
    (s/replace bad-string "'" "''")))

;-------------------------------------------------------------------------------
; Handling options.

; All of the options are stored in a single map in a single row of the options
; table. They are read in their entirety every time any value is requested and
; are written every time any value is changed.

(defn get-initial-options
  "Return a map of the default option values used the first time the
  program starts."
  []
  {:editor_autosave_interval    0
   :editor_send_every_keystroke true
   :root_page                   "Front Page"
   :wiki_name                   "CWiki"
   :editor_editing_font         "Calibri"
   :confirm_page_deletions      true
   :editor_use_WYSIWYG_editor   false})

(defn- update-option-map
  "Update the database with the new map of options."
  [m db]
  (let [opt-str (with-out-str (pp/pprint m))]
    (jdbc/update! db :options {:options_edn opt-str} ["options_id = ?" 1])))

(defn get-option-map
  "Return the entire options map from the database."
  ([] (get-option-map (get-h2-db-spec)))
  ([db]
   (-> db
       (jdbc/query ["select options_edn from options where options_id=1"])
       (first)
       (:options_edn)
       (edn/read-string))))

(defn get-option-value
  "Return the value of the option associated with the key or nil if there is
  no such key in the options table."
  ([k] (get-option-value k (get-h2-db-spec)))
  ([k db]
   (k (get-option-map db))))

(defn set-option-value
  "Update the options in the database to include the key/value given,
  whether the key existed in the options map before or not, that is,
  a previous k/v pair that was not already in the options will be added."
  ([k v] (set-option-value k v (get-h2-db-spec)))
  ([k v db]
   (let [m (get-option-map db)
         nm (merge m {k v})]
     (update-option-map nm db))))

;-------------------------------------------------------------------------------
; user-id/user-name related functions.

(defn user-name->user-id
  ([name]
   (user-name->user-id name (get-h2-db-spec)))
  ([name db]
   (:user_id (first (jdbc/query
                      db
                      ["select user_id from users where user_name=?" name])))))

(defn user-name->user-role
  "Return the user role (a keyword) assigned to the named user. If the
  user does not exist, return nil."
  ([name]
   (user-name->user-role name (get-h2-db-spec)))
  ([name db]
   (let [string-role (:user_role
                       (first
                         (jdbc/query
                           db
                           ["select user_role from users where user_name=?" name])))]
     (when string-role
       (let [colon-stripped (s/replace-first string-role ":" "")]
         (keyword colon-stripped))))))

(defn get-cwiki-user-id
  []
  (user-name->user-id "CWiki"))

(defn user-id->user-name
  "Given a user id, return a human-readable user name."
  ([id]
   (user-id->user-name id (get-h2-db-spec)))
  ([id db]
   (:user_name (first (jdbc/query
                        db
                        ["select user_name from users where user_id=?" id])))))

(defn find-user-by-name
  "Look up the user in the database and return the map of user attributes
  for a matching entry. If no match, return nil."
  ([name]
   (find-user-by-name name (get-h2-db-spec)))
  ([name db]
   (let [user-map (first (jdbc/query
                           db
                           ["select * from users where user_name=?" name]))]
     user-map)))

(defn find-user-by-case-insensitive-name
  "Look up the user in the database using a case-insensitive search for the
  username. Return the matching entry, if any. Otherwise, return nil."
  ([name]
   (find-user-by-case-insensitive-name name (get-h2-db-spec)))
  ([name db]
   (let [user-map (first (jdbc/query
                           db
                           [(str "select * from users where user_name like '"
                                 name "'")]))]
     user-map)))

(defn get-user-by-username-and-password
  "Look up a user and verify that the password is a match. If so,
  return the user record, otherwise return nil."
  ([username password] (get-user-by-username-and-password
                         username password (get-h2-db-spec)))
  ([username password db]
   (let [result (find-user-by-name username db)
         pw-hash (:user_password result)]
     (when (and (= (:user-name result))
                (hashers/check password pw-hash))
       result))))

(defn lookup-user
  "Look up a user and verify that the password is a match. If the user
  cannot be found or the password doesn't match, return nil. Otherwise,
  return a copy of the user record with the password digest dissociated
  from it."
  ([username password] (lookup-user username password (get-h2-db-spec)))
  ([username password db]
   (when-let [user (find-user-by-name username db)]
     (let [pw (get user :user_password)]
       (when (hashers/check password pw)
         (dissoc user :user_password))))))

(defn update-user
  "Update the database entry for the user with the given id to the
  information included in the map."
  ([user_id user-map] (update-user user_id user-map (get-h2-db-spec)))
  ([user_id user-map db]
   (jdbc/update! db :users user-map ["user_id=?" user_id])))

;-------------------------------------------------------------------------------

(defn- clob->string
  "Return a string created by translating and H2 Clob."
  [clob]
  (with-open [rdr (.getCharacterStream ^JdbcClob clob)]
    (let [lseq (line-seq rdr)
          butlast-line (butlast lseq)
          butlast-line-mapped (map #(str % "\n") butlast-line)
          last-line (last lseq)
          all-lines-with-newline (concat butlast-line-mapped last-line)]
      (s/join all-lines-with-newline))))

(defn- to-map-helper
  "Return a post map created from the result set."
  [rs]
  (let [frs (first rs)
        raw-content (:page_content frs)]
    (if raw-content
      (assoc frs :page_content (clob->string raw-content))
      frs)))

(defn find-post-by-title
  "Look up a post in the database with the given title. Return the page
  map for the post if found; nil otherwise. The lookup is case-insensitive."
  ([title] (find-post-by-title title (get-h2-db-spec)))
  ([title db]
   (let [search-title (escape-apostrophes title)
         sql-str (str "select * from pages where page_title like '" search-title "'")
         res (jdbc/query db sql-str {:result-set-fn to-map-helper})]
     res)))

(defn title->user-id
  ([title] (title->user-id title (get-h2-db-spec)))
  ([title db]
   (:page_author
     (first (jdbc/query db
                        ["select page_author from pages where page_title=?" title])))))

(defn page-id->title
  ([id] (page-id->title id (get-h2-db-spec)))
  ([id db]
   (let [sql-str (str "select page_title from pages where page_id=" id)]
     (:page_title (first (jdbc/query db sql-str))))))

(defn title->page-id
  ([title] (title->page-id title (get-h2-db-spec)))
  ([title db]
   (:page_id (first (jdbc/query
                      db
                      ["select page_id from pages where page_title=?" title])))))

(defn- to-content-helper
  [rs]
  (let [raw-content (:page_content (first rs))]
    (when raw-content
      (clob->string raw-content))))

(defn page-id->content
  ([id] (page-id->content id (get-h2-db-spec)))
  ([id db]
   (let [sql-str (str "select page_content from pages where page_id=" id)
         res (jdbc/query db sql-str {:result-set-fn to-content-helper})]
     res)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Stuff related to full text search.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn int-at-end
  "Return the (base 10) integer at the end of the string or nil if there isn't one."
  [s]
  (when s
    (let [dig-str (re-find #"-?\d*$" s)]
      (when (and dig-str (seq dig-str))
        (Integer/parseInt dig-str)))))

(defn- rs->result-vector
  "Take a result set list and turn it into a vector."
  [rs]
  (let [result (vec rs)]
    result))

(defn- rm->id-score-map
  "Take a result map {:query string :id string} and turn it into a score map
  where the id has been converted to an integer."
  [result-map]
  (let [query (:query result-map)
        id (int-at-end query)]
    {:id id :score (:score result-map)}))

(defn- rv->id-score-vector
  "Take a vector of result maps and return a vector of id/score maps where
  the id is an integer."
  [rv]
  (mapv rm->id-score-map rv))

(defn- case-insensitive-title-comparator
  "Case-insensitive title comparator."
  [m1 m2]
  (.compareToIgnoreCase (:title m1) (:title m2)))

(defn- reverse-case-insensitive-title-comparator
  "Reverse order case-insensitive title comparator"
  [m1 m2]
  (case-insensitive-title-comparator m2 m1))

(defn- lo-to-hi-score-comparator
  "Numeric comparator for low to high sorting."
  [s1 s2]
  (compare (:score s1) (:score s2)))

(defn- hi-to-lo-score-comparator
  "Numeric comparator for high to low sorting."
  [s1 s2]
  (compare (:score s2) (:score s1)))

(defn sort-search-results
  "Sort the vector of results according to the type of sort requested and
  return a sorted vector."
  [v sort-type]
  (when (and v sort-type)
    (cond
      (= sort-type :none) v
      (= sort-type :alphanum-natural) (sort case-insensitive-title-comparator v)
      (= sort-type :alphanum-reverse) (sort reverse-case-insensitive-title-comparator v)
      (= sort-type :score-hi-to-lo) (sort hi-to-lo-score-comparator v)
      (= sort-type :score-lo-to-hi) (sort lo-to-hi-score-comparator v)
      :else (errorf "ACK! Don't know how to sort by %s " sort-type))))

(defn- id-score-map->title-score-map
  [m db-spec]
  "Take a map containing a page id under the :id key and return a map
  containing the page title under the key :title."
  (let [title (page-id->title (:id m) db-spec)]
    (merge {:title title} m)))

(defn- iv->title-score-vector
  "Take a vector of maps containing the keys :id and :score, and return a
  vector of maps where the page titles have been added under the key :title."
  [id-score-vector db-spec]
  (mapv #(id-score-map->title-score-map % db-spec) id-score-vector))

(defn search-results->title-score-vector
  "Take the result set for a search, along with a map of options, and return
  a vector of maps containing :title :id and :scores, with the vector sorted
  according to the options. Sorting defaults to :score-hi-to-lo so that the
  most relevant results are first."
  [srv options]
  (let [db-spec (or (:db-spec options) (get-h2-db-spec))
        sort-type (or (:sort-type options) :score-hi-to-lo)
        tsv (-> srv
                rs->result-vector
                rv->id-score-vector
                (iv->title-score-vector db-spec)
                (sort-search-results sort-type))]
    tsv))

(defn search-content
  "Search the content of all pages in the database for the given text.
  Return a vector of maps. Each map has a page title, page id, and relevance
  score for a page that matches the search criteria. A map of options can
  be used to specify the maximum number of results to return (:max-results,
  defaults to 1000), the offset to use (:offset, defaults to 0), the
  database to search (:db-spec, defaults to the production database),
  and how the output vector should be sorted (:sort-type, defaults to
  :score-hi-to-low)."
  [txt options]
  (let [max-results (or (:max-results options) 1000)
        offset (or (:offset options) 0)
        db-spec (or (:db-spec options) (get-h2-db-spec))
        search-str (str "SELECT * FROM FTL_SEARCH('" txt "', " max-results ", " offset ")")
        rs (jdbc/query db-spec [search-str])
        tsv (search-results->title-score-vector rs options)]
    tsv))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Get information from page maps.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn page-map->id
  [m]
  (:page_id m))

(defn page-map->title
  [m]
  (:page_title m))

(defn page-map->content
  [m]
  (:page_content m))

(defn page-map->author
  ([m] (page-map->author m (get-h2-db-spec)))
  ([m db]
   (let [author-id (:page_author m)
         result (jdbc/query db ["select user_name from users where user_id=?" author-id])
         name (:user_name (first result))]
     (if (and result name)
       name
       "Unknown"))))

(defn page-map->tags
  [m]
  (:page_tags m))

(defn page-map->created-date
  [m]
  (:page_created m))

(defn page-map->modified-date
  [m]
  (:page_modified m))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-all-page-names-in-db
  "Return a collection of page name maps with all of the page names
  in the db (as opposed to any generated pages.)"
  ([]
   (get-all-page-names-in-db (get-h2-db-spec)))
  ([db]
   (let [title-array (jdbc/query db ["select page_title from pages"])]
     title-array)))

(defn get-all-page-names
  "Return a sorted set of all of the page titles in the wiki,
  including all of the 'special' pages."
  ([]
   (get-all-page-names (get-h2-db-spec)))
  ([db]
   (when-let [title-array (get-all-page-names-in-db db)]
     (reduce #(conj %1 (:page_title %2))
             (special/get-all-special-page-names) title-array))))

(defn case-insensitive-comparator
  "Case-insensitive string comparator."
  [^String s1 ^String s2]
  (.compareToIgnoreCase s1 s2))

(defn get-all-users
  "Return a sorted set of all of the user names known to the wiki."
  ([]
   (get-all-users (get-h2-db-spec)))
  ([db]
   (when-let [user-array (jdbc/query db ["select user_name from users"])]
     (into (sorted-set-by case-insensitive-comparator)
           (mapv :user_name user-array)))))

(defn convert-seq-to-comma-separated-string
  "Return a string containing the members of the sequence separated by commas."
  [the-seq]
  (let [but-last-comma-mapped (map #(str % ", ") (butlast the-seq))]
    (s/join (concat but-last-comma-mapped (str (last the-seq))))))

;;
;; The functions related to tags follow.
;;

(defn get-all-tag-names
  "Return a case-insensitive sorted set of all of the tags in the wiki."
  ([]
   (get-all-tag-names (get-h2-db-spec)))
  ([db]
   (when-let [tag-array (jdbc/query db ["select tag_name from tags"])]
     (reduce
       #(conj %1 (:tag_name %2))
       (sorted-set-by case-insensitive-comparator)
       tag-array))))

(defn- get-tag-id-from-name
  "Given a string containing a tag name, return the id from the tags table
  or nil if a tag with the name has not been recorded."
  [name db]
  (let [escaped-name (escape-apostrophes name)
        sql (str "select tag_id from tags where tag_name='" escaped-name "';")
        rs (jdbc/query db [sql])]
    (when (seq rs)
      (:tag_id (first rs)))))

(defn- get-tag-ids-for-page
  "Return a seq of the tag ids associated with the page-id. Returns
  nil if the page id is nil."
  [page-id db]
  (when page-id
    (let [sql (str "select tag_id from tags_x_pages where page_id=" page-id)
          rs (jdbc/query db [sql])]
      (reduce #(conj %1 (:tag_id %2)) [] rs))))

(defn get-tag-names-for-page
  "Returns a case-insensitive sorted-set of tag names associated with the page.
  If there are no such tags (it's nil or an empty seq), returns an empty set."
  ([page-id]
   (get-tag-names-for-page page-id (get-h2-db-spec)))
  ([page-id db]
   (let [tag-ids (filterv (complement nil?) (get-tag-ids-for-page page-id db))]
     (if (or (nil? tag-ids) (empty? tag-ids))
       (sorted-set-by case-insensitive-comparator)
       (let [tag-ids-as-string (convert-seq-to-comma-separated-string tag-ids)
             sql (str "select tag_name from tags where tag_id in ("
                      tag-ids-as-string ");")
             rs (jdbc/query db [sql])]
         (reduce #(conj %1 (:tag_name %2))
                 (sorted-set-by case-insensitive-comparator) rs))))))

(defn- get-tag-name-set-from-meta
  "Return a case-insensitive sorted-set of tag names contained the meta data."
  [meta]
  (reduce conj (sorted-set-by case-insensitive-comparator) (:tags meta)))

(defn- get-row-id
  "Return the row id returned as the result of a single insert operation.
  It's buried in an odd map, hence this function."
  [result]
  (first (vals (first result))))

(defn- is-tag-id-NOT-in-xref-table?
  "Return true if the tag id is recorded anywhere in the xref table,
  false otherwise."
  [tag-id db]
  (let [sql (str "select * from tags_x_pages where tag_id=" tag-id ";")
        res (jdbc/query db [sql])]
    (empty? res)))

(defn- remove-deleted-tags
  "Remove deleted tags for the page, and if there are no more pages
  with the tag, remove them from the tags tables too."
  [tag-id-vector page-id db]
  ; Remove from the cross-reference table.
  (let [sql (str "page_id=" page-id " and tag_id in ("
                 (convert-seq-to-comma-separated-string tag-id-vector) ");")]
    (jdbc/delete! db :tags_x_pages [sql]))
  ; Check if any need to be removed from the tags table because
  ; they are not referred to by any other page.
  (let [not-used (filterv #(is-tag-id-NOT-in-xref-table? % db)
                          tag-id-vector)]
    (when (seq not-used)
      (let [sql (str "tag_id in ("
                     (convert-seq-to-comma-separated-string not-used) ");")]
        (jdbc/delete! db :tags [sql])))))

(defn- delete-unused-tags
  "Given a set of tag name strings, remove them from the cross-reference
  entries for the page and, possibly, the tag table if they are used
  nowhere else."
  [tag-name-set page-id db]
  (let [tag-ids (reduce #(conj %1 (get-tag-id-from-name %2 db)) [] tag-name-set)]
    (remove-deleted-tags tag-ids page-id db)))

(defn- is-tag-name-NOT-in-tag-table?
  "Return true if the tag name is not already recorded in the tags
  table, nil otherwise."
  [tag-name db]
  (let [escaped-tag (escape-apostrophes tag-name)
        sql (str "select tag_id from tags where tag_name='" escaped-tag "';")
        res (jdbc/query db [sql])]
    (empty? res)))

(defn- add-new-tags-for-page
  "Add new tags for the page to the cross-reference table and, possibly,
  the tag table."
  [new-tag-set page-id db]
  ; Add tags that are new to the database to the tabs table.
  (let [completely-new (filterv #(is-tag-name-NOT-in-tag-table? % db)
                                new-tag-set)]
    (when (seq completely-new)
      (mapv #(jdbc/insert! db :tags {:tag_name %}) completely-new)))
  ; Add tags to the xref table.
  (let [tag-ids (reduce #(conj %1 (get-tag-id-from-name %2 db)) [] new-tag-set)]
    (mapv #(jdbc/insert! db :tags_x_pages {:tag_id % :page_id page-id}) tag-ids)))

(defn update-tags-for-page
  "Update the tag tables with tags and associate them with the
  page-id."
  [desired-tag-set page-id db]
  (let [existing-tags (get-tag-names-for-page page-id db)
        tags-to-remove (set/difference existing-tags desired-tag-set)
        tags-to-add (set/difference desired-tag-set existing-tags)]
    (add-new-tags-for-page tags-to-add page-id db)
    (when (seq tags-to-remove)
      (delete-unused-tags tags-to-remove page-id db))))

(defn- rs->page-ids-as-set
  "Take a result set consisting of a sequence of maps with the key
  :page_id and the page id as an integer. Return a set of page ids.
  If the result set is nil, returns nil."
  [rs]
  (when rs
    (reduce #(conj %1 (:page_id %2)) #{} rs)))

(defn- get-ids-of-all-pages-with-tag
  "Return a set of all the page ids for pages with the given tag name."
  [tag-name db]
  (let [rs (jdbc/query
             db
             ["select tag_id from tags where tag_name=?" tag-name])
        tag-id (:tag_id (first rs))]
    (jdbc/query db
                ["select page_id from tags_x_pages where tag_id=?" tag-id]
                {:result-set-fn rs->page-ids-as-set})))

(defn get-titles-of-all-pages-with-tag
  "Return a case-insensitive sorted-set of all of the titles of all
  of the pages that have this tag."
  ([tag-name]
   (get-titles-of-all-pages-with-tag tag-name (get-h2-db-spec)))
  ([tag-name db]
   (let [page-ids (get-ids-of-all-pages-with-tag tag-name db)]
     (reduce #(conj %1 (page-id->title %2 db))
             (sorted-set-by case-insensitive-comparator) page-ids))))

;;
;; End of tag functions.
;;

(defn- get-ids-of-all-pages-with-user
  "Return a set of all the page ids for pages with the given user name."
  [user-name db]
  (let [user-id (user-name->user-id user-name)]
    (jdbc/query
      db
      ["select page_id from pages where page_author=?" user-id]
      {:result-set-fn rs->page-ids-as-set})))

(defn get-titles-of-all-pages-with-user
  "Return a case-insensitive sorted-set of all of the titles of all
  of the pages that have this user name."
  ([user-name] (get-titles-of-all-pages-with-user user-name (get-h2-db-spec)))
  ([user-name db]
   (let [page-ids (get-ids-of-all-pages-with-user user-name db)]
     (reduce #(conj %1 (page-id->title %2 db))
             (sorted-set-by case-insensitive-comparator) page-ids))))

(defn update-page-title-and-content!
  ([id title tag-set content]
   (update-page-title-and-content! id title tag-set content (get-h2-db-spec)))
  ([id title tag-set content db]
   (jdbc/update! db :pages {:page_title    title
                            :page_content  content
                            :page_modified (dt/sql-now)}
                 ["page_id=?" id])
   (update-tags-for-page tag-set id db)))

(defn insert-new-page!
  "Insert a new page into the database given a title, tags and content.
  Return the post map for the new page (including id and dates).
  If the arguments do not include an author id, use the CWiki
  author id (same as CWiki user id)."
  ([title content tags author-id]
   (insert-new-page! title content tags author-id (get-h2-db-spec)))
  ([title content tags author-id db]
   (tracef "insert-new-page!: title: %s, tags: %s, author-id: %s" title tags author-id)
   (let [post-map (create-new-post-map title content author-id)]
     (jdbc/insert! db :pages post-map)
     (let [pm (find-post-by-title title db)
           id (page-map->id pm)]
       (update-tags-for-page tags id db)
       pm))))

(defn delete-page-by-id!
  "Remove the page and related data from the database."
  ([page-id] (delete-page-by-id! page-id (get-h2-db-spec)))
  ([page-id db]
   (when page-id
     (let [tags (get-tag-ids-for-page page-id db)]
       (remove-deleted-tags tags page-id db)
       (jdbc/delete! db :pages ["page_id=?" page-id])))))

(defn- get-author-from-import-meta-data
  "Return the author id based on the content of the meta-data. If there is no
  author or they do not have the appropriate role, return the default id."
  [meta default-author-id db]
  (let [author-name (:author meta)
        author-id (user-name->user-id author-name db)]
    (if (or (nil? author-id)
            (= "reader" (user-name->user-role author-name db)))
      (user-name->user-id default-author-id db)
      author-id)))

(defn add-page-from-map
  "Add a new page to the wiki using the information in a map. The map must
  have two keys, :meta and :body. Meta contains things like the author name,
  tags, etc. The body contains the Markdown content. If the author cannot be
  determined or is not recognized, the contents of the 'default-author'
  argument is used. If there is no title, a random title is created.
  Return the title of the imported page."
  ([m default-author]
   (add-page-from-map m default-author (get-h2-db-spec)))
  ([m default-author db]
   (let [meta (:meta m)
         author-id (get-author-from-import-meta-data meta default-author db)
         title (or (:title meta) (str "Title - " (UUID/randomUUID)))]
     (when (and author-id title)
       (let [content (:body m)
             creation-date-str (or (:date meta)
                                   (:created meta))
             creation-date (if creation-date-str
                             (dt/meta-datetime-to-sql-datetime creation-date-str)
                             (dt/sql-now)
                             )
             update-date-str (or (:updated meta)
                                 (:changed meta)
                                 (:modified meta))
             update-date (if update-date-str
                           (dt/meta-datetime-to-sql-datetime update-date-str)
                           creation-date)
             pm (merge (create-new-post-map title content author-id)
                       {:page_created creation-date}
                       {:page_modified update-date})
             page-id (get-row-id (jdbc/insert! db :pages pm))
             tv (get-tag-name-set-from-meta meta)]
         (update-tags-for-page tv page-id db)
         title)))))

(defn- add-page-with-meta-from-file!
  "Add a page to the database based on the information in a Markdown file
  containing YAML front matter. The file name is appended to the path for
  the initial pages in the uberjar."
  [file-name db]
  (let [resource-prefix "private/md/"
        m (files/load-markdown-from-resource (str resource-prefix file-name))]
    (add-page-from-map m "CWiki" db)))

(defn- insert-user!
  "Utility function to insert a user (specified in a mp) into the specified
  database. Trivial, but used in two places."
  [user-map db]
  (jdbc/insert! db :users user-map))

(defn add-user
  "Add a user to the system with the given user name and password. Optionally,
  add an email address for password recovery."
  ([user-name user-password user-role email]
   (add-user user-name user-password user-role email (get-h2-db-spec)))
  ([user-name user-password user-role user-email db]
   (let [role user-role
         usr {:user_name              user-name
              :user_role              role
              :user_password          (hashers/derive user-password)
              :user_new_password      nil
              :user_new_password_time nil
              :user_email             user-email
              :user_email_token       0
              :user_email_expires     nil
              :user_touched           (dt/sql-now)
              :user_registration      (dt/sql-now)}]
     (insert-user! usr db))))

(defn delete-user
  "Delete the user with the give id."
  ([user-id]
   (delete-user user-id (get-h2-db-spec)))
  ([user-id db]
   (jdbc/delete! db :users ["user_id=?" user-id])))

;!!! PROBLEM -- SHOULD SPECIFY DATABASE. This function is only called in
; one place. It assumes that it is always working with the default wiki
; database.
(defn has-admin-logged-in?
  "Return true if the admin has ever logged in; nil otherwise."
  []
  (let [result (jdbc/query
                 (get-h2-db-spec)
                 ["select admin_has_logged_in from admin where admin_id=?" 1])]
    (:admin_has_logged_in (first result))))

(defn set-admin-has-logged-in
  "Note that the admin user has logged in at least once and record it
  in the database."
  ([b]
   (set-admin-has-logged-in b (get-h2-db-spec)))
  ([b db]
   (jdbc/update! db :admin {:admin_has_logged_in b}
                 ["admin_id=?" 1])))

(defn init-admin-table!
  "Initialize the admin table."
  [db]
  (jdbc/insert! db :admin {:admin_id            1
                           :admin_has_logged_in nil}))

(defn add-initial-users!
  [db]
  (info "Adding initial users.")
  (mapv #(insert-user! % db) (get-initial-users))
  (info "Done!"))


(defn- add-initial-pages!
  "Add the initial pages to the wiki database."
  [db]
  (info "Adding initial pages.")
  (mapv #(add-page-with-meta-from-file! % db) (files/load-initial-page-list))
  (info "Done!"))

(defn add-initial-roles!
  "Add the available roles to the wiki database."
  [db]
  (info "Adding roles.")
  (mapv #(jdbc/insert! db :roles {:role_name %}) (get-valid-roles))
  (info "Done!"))

(defn add-initial-options!
  [db]
  (info "Adding options.")
  (let [opts (get-initial-options)
        opts-str (with-out-str (pp/pprint opts))]
    (jdbc/insert! db :options {:options_edn opts-str}))
  (info "Done!"))

(defn create-tables
  "Create the database tables for the application."
  [db]
  (info "Creating the tables.")
  (try (jdbc/db-do-commands
         db false
         [(jdbc/create-table-ddl :users
                                 [[:user_id :integer :auto_increment :primary :key]
                                  [:user_name :varchar]
                                  [:user_role :varchar]
                                  [:user_password :varchar]
                                  [:user_new_password :varchar]
                                  [:user_new_password_time :datetime]
                                  [:user_email "VARCHAR(254)"]
                                  [:user_email_token :int]
                                  [:user_email_expires :datetime]
                                  [:user_touched :datetime]
                                  [:user_registration :datetime]])
          (jdbc/create-table-ddl :admin
                                 [[:admin_id :integer :auto_increment :primary :key]
                                  [:admin_has_logged_in :boolean]])
          (jdbc/create-table-ddl :pages
                                 [[:page_id :integer :auto_increment :primary :key]
                                  [:page_created :datetime]
                                  [:page_modified :datetime]
                                  [:page_author :integer]
                                  [:page_title :varchar]
                                  [:page_content :text]])
          (jdbc/create-table-ddl :namespaces
                                 [[:namespace_id :integer :auto_increment :primary :key]
                                  [:namespace_name :varchar]])
          (jdbc/create-table-ddl :roles
                                 [[:role_id :integer :auto_increment :primary :key]
                                  [:role_name :varchar]])
          (jdbc/create-table-ddl :options
                                 [[:options_id :integer :auto_increment :primary :key]
                                  [:options_edn :varchar]])
          (jdbc/create-table-ddl :tags
                                 [[:tag_id :integer :auto_increment :primary :key]
                                  [:tag_name :varchar "NOT NULL"]])
          (jdbc/create-table-ddl :tags_x_pages
                                 [[:x_ref_id :integer :auto_increment :primary :key]
                                  [:tag_id :integer]
                                  [:page_id :integer]])
          (jdbc/create-table-ddl :user_x_pages
                                 [[:x_ref_id :integer :auto_increment :primary :key]
                                  [:user_id :integer]
                                  [:page_id :integer]])])
       (jdbc/execute! db "alter table tags_x_pages add foreign key (tag_id) references public.tags(tag_id);")
       (catch Exception e (println e)))
  (info "Done!"))

(defn- create-db
  "Create the database tables and initialize them with content for
  first-time use."
  [db]
  (create-tables db)
  (init-admin-table! db)
  (add-initial-users! db)
  (add-initial-pages! db)
  (add-initial-roles! db)
  (add-initial-options! db))

(defn- db-exists?
  "Return true if the wiki database already exists."
  [db-name]
  (.exists ^File (clojure.java.io/as-file db-name)))

(defn start-db!
  "Initialize the database. Will create the database and
  tables if needed."
  []
  (info "Starting database.")
  (when-not (db-exists? (get-db-file-name-long))
    (info "Creating initial database.")
    (io/make-parents (get-db-file-name))
    (create-db (get-h2-db-spec))))

(defn stop-db!
  []
  (info "Stopping database."))
