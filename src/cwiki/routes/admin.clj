(ns cwiki.routes.admin
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [compojure.core :refer :all]
            [compojure.response :as response]
            [cwiki.layouts.admin :as admin-layout]
            [cwiki.layouts.base :as layout]
            [cwiki.models.db :as db]
            [cwiki.util.pp :as pp]
            [cwiki.util.req-info :as ri]
            [ring.util.response :refer [redirect status]]
            [clojure.string :as s]
            [buddy.hashers :as hashers]))

(defn- not-yet
  "Return a page saying a feature is not ready yet."
  [name]
  (layout/compose-not-yet-view name))

(defn- build-response
  "Build a response structure, possibly with a non-200 return code."
  ([body req]
   (build-response body req 200))
  ([body req stat]
   (-> (response/render body req)
       (status stat)
       (assoc :body body))))

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
     referer   "referer"} :multipart-params
    session               :session :as req}]
  (if (db/find-user-by-name username)
    (admin-layout/compose-user-already-exists-page)
    (do
      (db/add-user username password user-role recovery-email)
      (if referer
        (redirect referer)
        (redirect "/Front Page")))))

;;
;; Functions related to letting an admin edit a user profile.
;;

(defn get-user-to-edit
  "Get an edit-profile page and return it."
  [req]
  (println "get-user-to-edit")
  (admin-layout/select-user-to-edit-page req))

(defn post-user-to-edit
  [{{username "user-name" password "password"
     referer  "referer"} :multipart-params
    session              :session :as req}]
  (println "post-user-to-edit")
  (let [session-user (ri/req->user-name req)]
    (if (not (db/get-user-by-username-and-password session-user password))
      (build-response (admin-layout/wrong-password-page req) req 422)
      (let [user-referer {:original-referer referer}]
        (if-let [user-info (merge user-referer (db/find-user-by-name username))]
          (do
            (println "user-info:" (pp/pp-map user-info))
            (let [new-session (assoc (redirect "/edit-profile")
                                :session (assoc session :edit-user-info user-info))]
              new-session))
          ; should never happen
          (do
            (build-response (admin-layout/cannot-find-user req) req 500)
            (if referer
              (redirect referer)
              (redirect "/Front Page"))))))))

(defn get-edit-profile
  [req]
  (println "get-edit-profile")
  (admin-layout/edit-user-page req))

(defn proposed-name-same-as-existing-name?
  "Return true when the proposed name is the same as the
  existing name or only differs in case, otherwise return nil."
  [proposed-name]
  (if (db/find-user-by-case-insensitive-name proposed-name)
    true
    nil))

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
  (if (not (seq new-name))
    ; Should never happen since this is a required field in the form.
    (build-response (admin-layout/must-not-be-empty-page) 400)

    (let [changes (atom {})
          existing-user-info (:edit-user-info session)
          existing-user-id (:user_id existing-user-info)
          old-name (:user_name existing-user-info)
          old-role (s/replace-first
                     (:user_role existing-user-info)
                     ":" "")
          old-email (:user_email existing-user-info)
          old-password (:user_password existing-user-info)
          original-referer (:original-referer existing-user-info)]

      ; Run the functions to check the new settings against the rules.
      (if (and (not= old-name new-name)
               (proposed-name-same-as-existing-name? new-name))
        (build-response (admin-layout/compose-user-already-exists-page) 409)
        (do
          (when (not= old-name new-name)
            (swap! changes conj {:user_name new-name}))
          (when-let [new-user-role (get-new-role old-role new-role)]
            (swap! changes conj {:user_role new-user-role}))
          (when-let [new-user-email (get-new-email old-email new-email)]
            (swap! changes conj {:user_email new-user-email}))
          (when-let [new-user-password (get-new-password old-password new-password)]
            (swap! changes conj {:user_password new-user-password}))
          (when (not (empty? @changes))
            (swap! changes conj {:user_touched (c/to-sql-time (t/now))}))
          (when (not (empty? @changes))
            (let [cleaned-existing (dissoc existing-user-info :original-referer)
                  merged-changes (merge cleaned-existing @changes)]
              (db/update-user existing-user-id merged-changes)))
          (let [redir (if original-referer
                        original-referer
                        "/Front Page")
                cleaned-session (dissoc session :edit-user-info)
                new-session (assoc (redirect redir)
                              :session cleaned-session)]
            new-session))))))

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
     referer  "referer"} :multipart-params
    session              :session :as req}]
  (let [session-user (ri/req->user-name req)]
    (if (not (db/get-user-by-username-and-password session-user password))
      (build-response (admin-layout/wrong-password-page req) req 422)
      (do
        (if-let [user-id (db/user-name->user-id username)]
          (db/delete-user user-id)
          ; should never happen
          (build-response (admin-layout/cannot-find-user req) req 500))
        (if referer
          (redirect referer)
          (redirect "/Front Page"))))))

(defroutes admin-routes
           (GET "/compress" [] (not-yet "compress"))
           (GET "/backup" [] (not-yet "backup"))
           (GET "/restore" [] (not-yet "restore"))
           (GET "/create-user" request (get-create-user request))
           (POST "/create-user" [] post-create-user)
           (GET "/select-profile" request (get-user-to-edit request))
           (POST "/select-profile" [] post-user-to-edit)
           (GET "/edit-profile" request (get-edit-profile request))
           (POST "/edit-profile" [] post-edit-profile)
           (GET "/delete-user" request (get-delete-user request))
           (POST "/delete-user" [] post-delete-user)
           (GET "/reset-password" [] (not-yet "reset-password")))
