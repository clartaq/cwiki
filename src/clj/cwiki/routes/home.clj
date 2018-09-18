(ns cwiki.routes.home
  (:require [cemerick.url :as url]
            [compojure.core :refer :all]
            [compojure.response :as response]
            [cwiki.layouts.base :as layout]
            [cwiki.models.wiki-db :as db]
            [cwiki.util.files :as files]
            [cwiki.util.pp :as pp]
            [cwiki.util.req-info :as ri]
            [ring.util.response :refer [redirect status]]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]]))

(defn- build-response
  "Build a response structure, possibly with a non-200 return code."
  ([body req]
   (build-response body req 200))
  ([body req stat]
   (-> (response/render body req)
       (status stat)
       (assoc :body body))))

(defn- read-front-page
  "Read the complete post for the front page."
  []
  (db/find-post-by-title "Front Page"))

(defn- read-about-page
  "Read the 'About' page from the database."
  []
  (db/find-post-by-title "About"))

(defn- do-search
  "Search the content of the wiki pages for the search text and
  return a new pages with links to the relevant pages."
  [search-text req]
  (if (and search-text (pos? (count search-text)))
    (if (ri/is-authenticated-user? req)
      (let [sr (db/search-content search-text {})]
        (layout/compose-search-results-page sr req))
      (redirect "/login"))
    (redirect (get-in req [:headers "referer"]))))

(defn- get-tag-set-from-req
  "Retrieve a set of tags from the request and return them."
  [req]
  (let [params (:multipart-params req)
        tag-keys (for [n (range 10)] (str "tag" n))
        tags (set (loop [t tag-keys v []]
                    (if (empty? t)
                      v
                      (recur (rest t) (let [tv (get params (first t))]
                                        (if (and (seq tv)
                                                 (pos? (count tv)))
                                          (conj v tv)
                                          v))))))]
    tags))

(defn- save-edits
  "Save any edits to the page back to the database."
  [page-id new-title new-content req]
  (info "save-edits")
  (infof "  page-id: :%s" page-id)
  (infof "  new-title: %s" new-title)
  (infof "  new-content: %s ..." (take 20 new-content))
  (infof "  req: %s" (pp/pp-map req))
  (let [actual-id (Integer. ^String page-id)
        tags (get-tag-set-from-req req)]
    (db/update-page-title-and-content! actual-id new-title tags new-content)
    (let [escaped-title (url/url-encode new-title)]
      ; Important! We redirect here so that functions which use the
      ; referring page get the page itself and not the editing page.
      (redirect (str "/" escaped-title)))))

(defn- save-and-view-page
  "Do the actual saving then retrieve and view the page."
  [title content req]
  (let [tags (get-tag-set-from-req req)]
    (db/insert-new-page! title content tags (ri/req->user-id req))
    (layout/view-wiki-page (db/find-post-by-title title) req)))

(defn- save-new-page
  "Save a new page to the database."
  [title content req]
  (if (db/find-post-by-title title)
    (layout/short-message "Can't Do That"
                          "A post with that title already exists.")
    (save-and-view-page title content req)))

(defn- home
  "Handle a request to view the 'Home' page if there is an
  authenticated user. Otherwise, force them to log in first."
  [req]
  (if (ri/is-authenticated-user? req)
    (layout/view-wiki-page (read-front-page) req)
    (if-not (db/has-admin-logged-in?)
      (do
        (db/set-admin-has-logged-in true)
        (layout/inform-admin-of-first-use
          "Hello Admin!"
          [:div
           [:p "It looks like this is the first time
                anyone has logged onto this wiki."]
           [:p "Since you seem to be the first, you are
                the 'admin' (administrative) user. That means
                you have special privileges in terms of the
                functionality available to you."]
           [:p "On the login page that follows, log in
                with the user name 'admin' and the password
                'admin' (without quotes). Then read up on
                what the admin can do."]
           [:p "For privacy, you may want to at least change the user
                name and password from the default values."]]))
      (redirect "/login"))))

(defn- about
  "Handle a request for the 'About' route if there is
  an authenticated user for the session. Otherwise,
  force them to log in."
  [req]
  (if (ri/is-authenticated-user? req)
    (layout/view-wiki-page (read-about-page) req)
    (redirect "/login")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- get-import-file
  "Show a page asking the user to specify a file to upload."
  [req]
  (layout/compose-import-file-page req))

(defn- do-the-import
  "Do the actual file import. Upon completion, notify the user that the
  import has succeeded. When acknowledged, display the imported page."
  [import-map file-name req]
  (let [imported-page-title (db/add-page-from-map import-map
                                                  (ri/req->user-name req))
        new-referer (str "/" imported-page-title)]
    (build-response (layout/confirm-import-page
                      file-name
                      imported-page-title new-referer) req)))

(defn- post-import-page
  "Import the file specified in the upload dialog. Checks the page title
  against existing pages. If a page of the same name exists, asks for
  confirmation before importing."
  [{{file-info "file-info"
     referer   "referer"} :multipart-params :as req}]
  (let [file-name (:filename file-info)
        fyle (:tempfile file-info)]
    (if (or (nil? file-name)
            (empty? file-name))
      (build-response (layout/no-files-to-import-page referer) req 400)
      (let [import-map (files/load-markdown-from-file fyle)
            new-title (get-in import-map [:meta :title])
            existing-id (db/title->page-id new-title)]
        (if existing-id
          (build-response
            (layout/compose-import-existing-page-warning import-map file-name
                                                         referer)
            req)
          (do-the-import import-map file-name req))))))

(defn- get-map-from-string
  "Utility to convert a map in a string to a real map and return it."
  [s]
  (binding [*read-eval* false]
    (read-string s)))

(defn- post-proceed-with-import
  "Proceed with the import since the user has decided to overwrite an
  existing version of the page. Delete the existing version first, then
  import the new one."
  ; The maps are coming in as strings
  [{{import-map "import-map"
     file-name  "file-name"} :multipart-params :as req}]
  (let [im (get-map-from-string import-map)
        title (get-in im [:meta :title])
        page-id (db/title->page-id title)]
    (db/delete-page-by-id! page-id)
    (do-the-import im file-name req)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- get-export-page
  [req]
  (layout/compose-export-file-page req))

(defn- get-params-for-export
  "Based on the page name, get the page-map and tags required to do the
  export."
  [page-name]
  (let [page-map (db/find-post-by-title page-name)
        author-name (db/page-map->author page-map)
        page-id (db/page-map->id page-map)
        tags (db/get-tag-names-for-page page-id)]
    {:page-map page-map :author-name author-name :tags tags}))

(defn- post-export-page
  "Export a single page from the wiki to a Markdown file."
  [req]
  (let [params (:multipart-params req)
        referer (get params "referer")
        page-id-str (get params "page-id")
        page-id (Integer. ^String (re-find #"\d+" page-id-str))
        page-name (db/page-id->title page-id)
        param-map (get-params-for-export page-name)]
    (let [res (files/export-page (:page-map param-map) (:author-name param-map)
                                 (:tags param-map))]
      (if res
        (layout/confirm-export-page page-name res referer)
        (layout/short-message-return-to-referer
          "There was a Problem"
          "The page was not exported correctly."
          referer)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- get-export-all-pages
  "Put up a page asking the user if they want to export all pages."
  [req]
  (layout/compose-export-all-pages req))

(defn- post-export-all-pages
  "Export all pages (except generated pages) to Markdown files in the
  home directory of the program. NO ERROR CHECKING!"
  [req]
  (let [params (:multipart-params req)
        referer (get params "referer")
        page-names (db/get-all-page-names-in-db)
        d (files/get-exported-page-directory)]
    (mapv (fn [name-map]
            (let [title (:page_title name-map)
                  param-map (get-params-for-export title)]
              (files/export-page (:page-map param-map)
                                 (:author-name param-map)
                                 (:tags param-map)))) page-names)
    (layout/confirm-export-all-pages (str "\"" d "\"") referer)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- safe-parse-int
  "Convert a string to an integer. Return -1 on exception."
  [x]
  (try (Integer/parseInt x)
       (catch Exception _
         -1)))

(defn get-preferences
  "Put up a page asking the user to edit any options/preferences."
  [req]
  (layout/compose-get-options-age req))

(defn post-preferences
  "Validate and save any changes to the options/preferences."
  [req]
  (let [params (:multipart-params req)
        referer (get params "referer")
        interval (get params "autosave-interval")
        result (safe-parse-int interval)]
    (if (>= result 0)
      (do
        (db/set-option-value :editor_autosave_interval result)
        (layout/short-message-return-to-referer
          "Preferences Saved"
          "All changes to the preferences have been saved." referer))
      (layout/short-message-return-to-referer
        "Problem with the Autosave Interval!"
        "The autosave interval must be a positive integer number of seconds.
        No new values have been saved."
        referer))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes home-routes
           (GET "/" request (home request))
           (GET "/export" request (get-export-page request))
           (POST "/export" request (post-export-page request))
           (GET "/export-all" request (get-export-all-pages request))
           (POST "/export-all" request (post-export-all-pages request))
           (GET "/import" request (get-import-file request))
           (POST "/import" request (post-import-page request))
           (GET "/preferences" request (get-preferences request))
           (POST "/preferences" request (post-preferences request))
           (POST "/proceed-with-import" request (post-proceed-with-import request))
           (POST "/save-edits" request
             (let [params (request :multipart-params)]
               (save-edits (get params "page-id")
                           (get params "title")
                           (get params "content") request)))
           (POST "/save-new-page" request
             (let [params (request :multipart-params)]
               (save-new-page (get params "title")
                              (get params "content") request)))
           (POST "/search" request
             (let [params (request :multipart-params)]
               (do-search (get params "search-text") request))))
