(ns cwiki.routes.admin
  (:require [compojure.core :refer :all]
            [cwiki.models.db :as db]
            [cwiki.views.layout :as layout]
            [ring.util.response :refer [redirect]]))

(defn- not-yet
  [name]
  (layout/compose-not-yet-view name))

(defn- get-create-user
  [req]
  (layout/view-create-user-page req))

(defn- post-create-user
  "Create a new user as long as a user with the same name does not already
  exist."
  [{{username  "user-name" password "password"
     user-role "user-role" recovery-email "recovery-email"
     referer   "referer"} :multipart-params
    session               :session :as req}]
  (if (db/find-user-by-name username)
    (layout/compose-user-already-exists-page)
    (do
      (db/add-user username password user-role recovery-email)
      (if referer
        (redirect referer)
        (redirect "/Front Page")))))

(defn- get-delete-user
  [req]
  (layout/view-delete-user-page req))

(defn- post-delete-user
  "Delete the user specified."
  [{{username "user-name" referer "referer"} :multipart-params
    session                                  :session :as req}]
  (if-let [user-id (db/user-name->user-id username)]
    (println "I know this user:" username ", user-id:" user-id)
    (println "I DON'T know this user:" username))
  (if referer
    (redirect referer)
    (redirect "/Front Page")))

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
