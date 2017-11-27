(ns cwiki.routes.admin
  (:require [compojure.core :refer :all]
            [compojure.response :as response]
            [cwiki.layouts.admin :as admin-layout]
            [cwiki.layouts.base :as layout]
            [cwiki.models.db :as db]
            [cwiki.util.req-info :as ri]
            [ring.util.response :refer [redirect status]]))

(defn- not-yet
  [name]
  (layout/compose-not-yet-view name))

(defn- get-create-user
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

(defn- get-delete-user
  [req]
  (admin-layout/delete-user-page req))

(defn- build-response
  ([body req]
   (build-response body req 200))
  ([body req stat]
   (-> (response/render body req)
       (status stat)
       (assoc :body body))))

;;
;; This needs more work. Besides just deleting the user, which is easy,
;; what happens to any pages they have authored? All of a sudden, their
;; author field is invalid. Or you could delete any pages authored by
;; that user, which could break all kinds of links and lose any work
;; someone else has contributed to those pages. Maybe just mark the
;; author as "unknown"?

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
          (println "I know this user:" username ", user-id:" user-id)
          (println "I DON'T know this user:" username))
        (if referer
          (redirect referer)
          (redirect "/Front Page"))))))

(defroutes admin-routes
           (GET "/compress" [] (not-yet "compress"))
           (GET "/backup" [] (not-yet "backup"))
           (GET "/restore" [] (not-yet "restore"))
           (GET "/create-user" request (get-create-user request))
           (POST "/create-user" [] post-create-user)
           (GET "/edit-profile" [] (not-yet "edit-profile"))
           (GET "/delete-user" request (get-delete-user request))
           (POST "/delete-user" [] post-delete-user)
           (GET "/reset-password" [] (not-yet "reset-password")))
