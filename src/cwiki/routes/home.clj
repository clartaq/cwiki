(ns cwiki.routes.home
  (:require [compojure.core :refer :all]
            [compojure.response :as response]
            [cwiki.layouts.base :as layout]
            [cwiki.models.wiki-db :as db]
            [cwiki.util.files :as files]
            [cwiki.util.pp :as pp]
            [cwiki.util.req-info :as ri]
            [ring.util.response :refer [redirect status]]))

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
  (if (ri/is-authenticated-user? req)
    (do
      (println "do-search: saw search-text:" search-text)
      (layout/compose-not-yet-view "search"))
    (redirect "/login")))

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
  (let [actual-id (Integer. ^String page-id)
        tags (get-tag-set-from-req req)]
    (db/update-page-title-and-content! actual-id new-title tags new-content)
    (layout/view-wiki-page (db/find-post-by-title new-title) req)))

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

(defn- post-import-file
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
                                                       referer req)
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

(defn- get-export-file
  [req]
  (layout/compose-export-file-page req))


; <asp:FileUpload ID="FileUpload1" onchange="selectFolder(event)" webkitdirectory mozdirectory msdirectory odirectory directory AllowMultiple="true" runat="server" />
(defn- post-export-file
  [req]
  (let [params (:multipart-params req)]
    (println "params:" (pp/pp-map params))
  (build-response "Want to export file" req)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes home-routes
           (GET "/" request (home request))
           (GET "/about" request (about request))
           (GET "/export" request (get-export-file request)) ;[] (layout/compose-not-yet-view "export"))
           (POST "/export" request (post-export-file request))
           (GET "/export-all" [] (layout/compose-not-yet-view "export-all"))
           (GET "/import" request (get-import-file request))
           (POST "/import" request (post-import-file request))
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
