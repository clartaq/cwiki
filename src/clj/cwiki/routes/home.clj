(ns cwiki.routes.home
  (:require [cemerick.url :as url]
            [clojure.string :as s]
            [compojure.core :refer :all]
            [compojure.response :as response]
            [cwiki.layouts.base :as layout]
            [cwiki.models.wiki-db :as db]
            [cwiki.util.files :as files]
            [cwiki.util.pp :as pp]
            [cwiki.util.req-info :as ri]
            [ring.util.response :refer [redirect status]]
            [taoensso.timbre :refer [tracef debugf infof warnf errorf
                                     trace debug info warn error]])
  (:import (org.httpkit BytesInputStream)))

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
  (let [actual-id (Integer/valueOf ^String page-id)
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

(defn- add-imported-page-to-database
  "Add the imported file to the database and return the title of the page."
  [import-map file-name req]
  (let [file-name-only (first (s/split file-name #"\."))
        enhanced-map (assoc import-map :file-name file-name-only)
        imported-page-title (db/add-page-from-map enhanced-map
                                                  (ri/req->user-name req))]
    imported-page-title))

(defn- get-multi-file-import
  [req]
  (layout/compose-multi-file-import-page req))

(defn- post-multi-file-import
  "Import the selected files, overwriting any existing pages in the
  database with the same name."
  [{{file-info "file-info"} :multipart-params :as req}]
  (let [file-vec (if (map? file-info)
                   (conj [] file-info)
                   file-info)]
    (doseq [fm file-vec]
      (let [fyle (:tempfile fm)
            file-name (:filename fm)
            import-map (files/load-markdown-from-file fyle)
            title (get-in import-map [:meta :title])
            existing-id (db/title->page-id title)]
        (when existing-id
          (db/delete-page-by-id! existing-id))
        (add-imported-page-to-database import-map file-name req)))
    (build-response
      (layout/confirm-multi-file-import-page
        "/")
      "post-multi-file-import" req)))

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
        page-id (Integer/valueOf ^String (re-find #"\d+" page-id-str))
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
  "Convert a string to an integer. Return -1 on exception. Parses the first
  (and only the first) contiguous group of digits."
  [x]
  (try (Integer/parseInt (re-find #"\d+" x))
       (catch Exception _
         -1)))

(defn get-preferences
  "Put up a page asking the user to edit any options/preferences."
  [req]
  (layout/compose-get-options-age req))

(defn post-preferences
  "Save the options/preferences."
  [req]
  ; Assumes that form validation in the page layout has only let
  ; valid settings through.
  (let [params (:multipart-params req)
        referer (get params "referer")
        wiki-name (get params "wiki-name")
        wiki-tagline (get params "wiki-tagline")
        article-width (get params "article-width")
        new-article-width (max 300 (safe-parse-int article-width))
        sidebar-width (get params "sidebar-width")
        new-sidebar-width (max 150 (safe-parse-int sidebar-width))
        interval (get params "autosave-interval")
        new-interval (max 0 (safe-parse-int interval))]
    (db/set-option-value :wiki_name wiki-name)
    (db/set-option-value :wiki_tagline wiki-tagline)
    (db/set-option-value :article_width new-article-width)
    (db/set-option-value :sidebar_width new-sidebar-width)
    (db/set-option-value :editor_autosave_interval new-interval)
    (layout/short-message-return-to-referer
      "Preferences Saved"
      "All changes to the preferences have been saved." referer)))

(defn post-sidebar-basis
  "Persist the sidebar width basis."
  [req]
  (let [body ^BytesInputStream (:body req)
        new-basis (safe-parse-int (slurp (.bytes body)))]
    (db/set-option-value :sidebar_width new-basis)
    {:status 200}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes home-routes
           (GET "/" request (home request))
           (GET "/export" request (get-export-page request))
           (POST "/export" request (post-export-page request))
           (GET "/export-all" request (get-export-all-pages request))
           (POST "/export-all" request (post-export-all-pages request))
           (GET "/import" request (get-multi-file-import request))
           (POST "/import" request (post-multi-file-import request))
           (GET "/preferences" request (get-preferences request))
           (POST "/preferences" request (post-preferences request))
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
               (do-search (get params "search-text") request)))
           (POST "/width-of-sidebar" request (post-sidebar-basis request)))
