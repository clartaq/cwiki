(ns cwiki.routes.admin
  (:require [buddy.hashers :as hashers]
            [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.string :as s]
            [compojure.core :refer :all]
            [compojure.response :as response]
            [cwiki.layouts.admin :as admin-layout]
            [cwiki.layouts.base :as layout]
            [cwiki.models.wiki-db :as db]
            [cwiki.util.files :as files]
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

;;
;; Functions related to saving a seed page.
;;

(defn- get-save-seed-page
  [req]
  (admin-layout/compose-save-seed-page req))

(defn- get-params-for-save
  "Based on the page name, get the page-map and tags required to do the
  export."
  [page-name]
  (let [page-map (db/find-post-by-title page-name)
        author-name (db/page-map->author page-map)
        page-id (db/page-map->id page-map)
        tags (db/get-tag-names-for-page page-id)]
    {:page-map page-map :author-name author-name :tags tags}))

(defn- post-save-seed-page
  "Save a single page from the wiki to a Markdown file."
  [req]
  (let [params (:multipart-params req)
        referer (get params "referer")
        page-id-str (get params "page-id")
        page-id (Integer/valueOf ^String (re-find #"\d+" page-id-str))
        page-name (db/page-id->title page-id)
        param-map (get-params-for-save page-name)]
    (let [res (files/export-seed-page (:page-map param-map) (:author-name param-map)
                                      (:tags param-map))]
      (if res
        (admin-layout/confirm-save-seed-page page-name res referer)
        (layout/short-message-return-to-referer
          "There was a Problem"
          "The page was not saved correctly."
          referer)))))

;;
;; Functions related to creating a new user.
;;

(defn- get-create-user
  "Get a create-user page and return it."
  [req]
  (admin-layout/create-user-page req))

(defn- post-create-user
  "Create a new user as long as a user with the same name does not already
  exist."
  [{{username  "user-name" password "password"
     user-role "user-role" recovery-email "recovery-email"
     referer   "referer"} :multipart-params :as req}]
  (if (db/find-user-by-name username)
    (admin-layout/compose-user-already-exists-page)
    (do
      (db/add-user username password user-role recovery-email)
      (build-response
        (admin-layout/confirm-creation-page username referer)
        req 201))))

;;
;; Functions related to letting an admin edit a user profile.
;;

(defn get-user-to-edit
  "Get an edit-profile page and return it."
  [req]
  (admin-layout/select-user-to-edit-page req))

(defn post-user-to-edit
  [{{username "user-name" password "password"
     referer  "referer"} :multipart-params
    session              :session :as req}]
  (let [session-user (ri/req->user-name req)]
    (if-not (db/get-user-by-username-and-password session-user password)
      (build-response (admin-layout/wrong-password-page req) req 422)
      (let [user-referer {:original-referer referer}]
        (if-let [user-info (merge user-referer (db/find-user-by-name username))]
          (let [new-session (assoc (redirect "/edit-profile")
                              :session (assoc session :edit-user-info user-info))]
            new-session)
          ; should never happen
          (do
            (build-response (admin-layout/cannot-find-user req) req 500)
            (if referer
              (redirect referer)
              (redirect "/Front Page"))))))))

(defn get-edit-profile
  [req]
  (admin-layout/edit-user-page req))

(defn proposed-name-same-as-existing-name?
  "Return true when the proposed name is the same as the
  existing name or only differs in case, otherwise return nil."
  [proposed-name]
  (when (db/find-user-by-case-insensitive-name proposed-name)
    true))

(defn- get-new-password
  "Return a digest for the new password or nil if the proposed change
  violates any rules."
  [old-digest proposed-password]
  ; Don't make any changes if they've left the password field empty.
  ; If they've added something, compare the digest of the old password,
  ; already in 'old-digest', with the digest of the proposed password.
  ; If they differ, return the digest of the new password.
  (when (seq proposed-password)
    (let [new-digest (hashers/derive proposed-password)]
      (when (not= old-digest new-digest)
        new-digest))))

(defn get-new-role
  "Return a new role or nil if the proposed change violates any rules."
  [old-role proposed-role]
  ; The proposed-role will always be a non-empty string selected from a
  ; drop-down of available roles. If the proposed-role is different from
  ; the old-role, return a keyword-ized version of the new role.
  ; Note that even though we return a keyword, it is stored as a
  ; string.
  (when (not= proposed-role old-role)
    (keyword proposed-role)))

(defn- get-new-email
  "Return a new email address or nil if the proposed change violoates
  any rules."
  [old-email proposed-email]
  ; No restrictions. We just pass back the new email if it differs
  ; from the old.
  (when (not= old-email proposed-email)
    proposed-email))

(defn post-edit-profile
  "Change the profile data of a user."
  [{{new-name "new-user-name" new-password "new-password"
     new-role "new-role" new-email "new-email"} :multipart-params
    session                                     :session :as req}]
  ; No matter how we return, we should use a modified request where the
  ; profile information about the user being modified has been removed.
  (let [new-session (atom (dissoc session :edit-user-info))
        new-req (assoc req :session @new-session)]
    (if-not (seq new-name)
      ; Should never happen since this is a required field in the form.
      (build-response (admin-layout/must-not-be-empty-page) new-req 400)
      ; Normal code path.
      (let [changes (atom {})
            existing-user-info (:edit-user-info session)
            existing-user-id (:user_id existing-user-info)
            logged-in-user (get-in session [:identity :user_name])
            old-name (:user_name existing-user-info)
            same-as-logged-in-user (= old-name logged-in-user)
            old-role (s/replace-first
                       (:user_role existing-user-info)
                       ":" "")
            old-email (:user_email existing-user-info)
            old-password (:user_password existing-user-info)
            original-referer (:original-referer existing-user-info)]

        ; Run the functions to check the new settings against the rules.
        (if (and (not= old-name new-name)
                 (proposed-name-same-as-existing-name? new-name))
          (build-response (admin-layout/compose-user-already-exists-page) new-req 409)
          (do
            (when (not= old-name new-name)
              (swap! changes conj {:user_name new-name}))
            (when-let [new-user-role (get-new-role old-role new-role)]
              (swap! changes conj {:user_role (name new-user-role)}))
            (when-let [new-user-email (get-new-email old-email new-email)]
              (swap! changes conj {:user_email new-user-email}))
            (when-let [new-user-password (get-new-password old-password new-password)]
              (swap! changes conj {:user_password new-user-password}))
            (when (seq @changes)
              (swap! changes conj {:user_touched (c/to-sql-time (t/now))})
              (let [cleaned-existing (dissoc existing-user-info :original-referer)
                    merged-changes (merge cleaned-existing @changes)]
                (db/update-user existing-user-id merged-changes)
                ; If the user is updating their own profile, update the session too.
                (when same-as-logged-in-user
                  (swap! new-session
                         assoc :identity (dissoc merged-changes :user_password)))))
            (assoc (build-response
                     (admin-layout/confirm-edits-page
                       new-name
                       old-name
                       original-referer)
                     req 200)
              :session @new-session)))))))

;;
;; Functions relating to deleting a user.
;;

(defn- get-delete-user
  "Get a delete-user page and return it."
  [req]
  (admin-layout/delete-user-page req))

(defn- post-delete-user
  "Delete the user specified."
  [{{username "user-name"
     password "password"
     referer  "referer"} :multipart-params :as req}]
  (let [session-user (ri/req->user-name req)]
    (if-not (db/get-user-by-username-and-password session-user password)
      (build-response (admin-layout/wrong-password-page req) req 422)
      (if-let [user-id (db/user-name->user-id username)]
        (do
          (db/delete-user user-id)
          (build-response
            (admin-layout/confirm-deletion-page username referer)
            req 200))
        ; should never happen
        (build-response (admin-layout/cannot-find-user req) req 500)))))

(defroutes admin-routes
           (GET "/save-seed-page" request (get-save-seed-page request))
           (POST "/save-seed-page" [] post-save-seed-page)
           (GET "/compress" [] (layout/compose-not-yet-view "compress"))
           (GET "/backup" [] (layout/compose-not-yet-view "backup"))
           (GET "/restore" [] (layout/compose-not-yet-view "restore"))
           (GET "/create-user" request (get-create-user request))
           (POST "/create-user" [] post-create-user)
           (GET "/select-profile" request (get-user-to-edit request))
           (POST "/select-profile" [] post-user-to-edit)
           (GET "/edit-profile" request (get-edit-profile request))
           (POST "/edit-profile" [] post-edit-profile)
           (GET "/delete-user" request (get-delete-user request))
           (POST "/delete-user" [] post-delete-user)
           (GET "/reset-password" [] (layout/compose-not-yet-view "reset-password")))
