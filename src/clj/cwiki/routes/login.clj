;;;
;;; Routes for login, logout, etc.
;;;

(ns cwiki.routes.login
  (:require [compojure.core :refer :all]
            [cwiki.models.wiki-db :as db]
            [cwiki.layouts.login :as login-layout]
            [cwiki.util.req-info :as ri]
            [ring.util.response :refer [redirect]]))

(defn get-login
  "Gather user credentials for login."
  [req]
  (login-layout/login-page))

(defn post-login
  "Check that the user name and password match credentials in the database.
  If so, add the identity to the current session, otherwise redirect back
  to the login page."
  [{{username "user-name" password "password"} :multipart-params
    session                                    :session :as req}]
  (if-let [user (db/get-user-by-username-and-password username password)]

    ; If authenticated
    (let [identity (dissoc user :user_password)
          new-session (assoc (redirect "/")
                        :session (assoc session :identity identity))]
      (ri/set-user-role! new-session)
      new-session)

    ; Otherwise
    (redirect "/login")))

(defn get-logout
  "Ask the user to verify that they want to log out."
  [req]
  (login-layout/view-logout-page req))

(defn post-logout
  "Log out the current user."
  [{session :session}]
  (let [new-session (assoc (redirect "/login")
    :session (dissoc session :identity))]
    (ri/set-user-role! new-session)
    new-session))

(defroutes login-routes
           (GET "/login" [] get-login)
           (POST "/login" [] post-login)
           (GET "/logout" [] get-logout)
           (POST "/logout" [] post-logout))


