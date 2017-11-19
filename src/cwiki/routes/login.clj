;;;
;;; Routes for login, logout, etc.
;;;

(ns cwiki.routes.login
  (:require [compojure.core :refer :all]
            [cwiki.models.db :as db]
            [cwiki.views.layout :as layout]
            [ring.util.response :refer [redirect]]))

(defn get-login []
  (layout/view-login-page))

(defn post-login [{{username "user-name" password "password"} :multipart-params
                   session                                    :session :as req}]
  (if-let [user (db/get-user-by-username-and-password username password)]

    ; If authenticated
    (do
      (db/set-current-user user)
      (let [new-session (assoc (redirect "/")
                          :session (assoc session :identity (:user_id user)))]
        new-session))

    ; Otherwise
    (redirect "/login")))

(defn get-logout
  [req]
  (layout/view-logout-page req))

(defn post-logout [{session :session}]
  (assoc (redirect "/login")
    :session (dissoc session :identity)))

(defroutes login-routes
           (GET "/login" [] (get-login))
           (POST "/login" [] post-login)
           (GET "/logout" [] get-logout)
           (POST "/logout" [] post-logout))


