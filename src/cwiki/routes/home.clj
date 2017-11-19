(ns cwiki.routes.home
  (:require [compojure.core :refer :all]
            [cwiki.views.layout :as layout]
            [cwiki.models.db :as db]
            [ring.util.response :refer [response redirect]]))

(defn read-front-page
  "Read the complete post for the front page."
  []
  (db/find-post-by-title "Front Page"))

(defn read-about-page
  []
  (db/find-post-by-title "About"))

(defn save-edits
  [page-id new-title new-content]
  (let [actual-id (Integer. ^String page-id)]
    (db/update-page-title-and-content! actual-id new-title new-content)
    (layout/view-wiki-page (db/find-post-by-title new-title))))

(defn save-new-page
  [title content]
  (db/insert-new-page! title content)
  (layout/view-wiki-page (db/find-post-by-title title)))

(defn home []
  (layout/view-wiki-page (read-front-page)))

(defn login []
  (layout/view-login-page))

(defn post-login [{{username "user-name" password "password"} :multipart-params
                   session :session :as req}]
  (println "post-login: username:" username ", password: " password "\n"
           "session:" session)

  (if-let [user (db/get-user-by-username-and-password username password)]

    ; If authenticated
    (do
      (println "user:" user)
      (db/set-current-user user)
      (let [new-session (assoc (redirect "/")
      :session (assoc session :identity (:user_id user)))]
        (println "session:" new-session)
        new-session))

    ; Otherwise
    (redirect "/login")))

(defn get-logout
  [req]
  (layout/view-logout-page req))

(defn post-logout [{session :session}]
  (assoc (redirect "/login")
    :session (dissoc session :identity)))

(defn about []
  (layout/view-wiki-page (read-about-page)))

(defroutes home-routes
           (GET "/" [] (home))
           (GET "/login" [] (login))
           (POST "/login" [] post-login)
           (GET "/logout" [] get-logout)
           (POST "/logout" [] post-logout)
           (GET "/about" [] (about))
           (POST "/save-edits" request
             (let [params (request :multipart-params)]
               (save-edits (get params "page-id")
                           (get params "title")
                           (get params "content"))))
           (POST "/save-new-page" request
             (let [params (request :multipart-params)]
               (save-new-page (get params "title")
                              (get params "content")))))
