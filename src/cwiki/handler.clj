(ns cwiki.handler
  (:require [buddy.auth.accessrules :refer [error success wrap-access-rules]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [cemerick.url :as u]
            [clojure.string :as s]
            [compojure.core :refer [defroutes routes]]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [compojure.route :as route]
            [cwiki.models.db :as db]
            [cwiki.views.layout :as layout]
            [cwiki.routes.admin :refer [admin-routes]]
            [cwiki.routes.home :refer [home-routes]]
            [cwiki.routes.login :refer [login-routes]]
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [status]]))

(def backend (backends/session))

(defn init []
  (println "CWiki is starting"))

(defn destroy []
  (println "CWiki is shutting down"))

(defn- build-response
  [body req]
  (-> (response/render body req)
      (status 200)
      (assoc :body body)))

(defn page-finder-route
  [body]
  (fn [request]
    (let [raw-title (u/url-decode (:uri request))
          title (s/replace raw-title "/" "")
          raw-post (db/find-post-by-title title)]
      (cond
        raw-post (let [new-body (db/page-map->content raw-post)
                       new-page (layout/view-wiki-page raw-post request)]
                   (build-response new-page request))

        (= title "All Pages") (let [new-body (layout/compose-all-pages-page request)]
                                (build-response new-body request))

        (= title "All Users") (let [new-body (layout/compose-all-users-page request)]
                                (build-response new-body request))

        (= title "All Namespaces") (let [new-body (layout/compose-all-namespaces-page request)]
                                     (build-response new-body request))

        (= title "All Tags") (let [new-body (layout/compose-all-tags-page request)]
                               (build-response new-body request))

        (s/ends-with? title "/edit") (let [title-only (s/replace title "/edit" "")
                                           new-body (layout/compose-create-or-edit-page
                                                      (db/find-post-by-title title-only) request)]
                                       (build-response new-body request))
        (s/ends-with? title "/delete") (let [title-only (s/replace title "/delete" "")
                                             new-body (layout/view-wiki-page
                                                        (db/find-post-by-title "Front Page") request)]
                                         (db/delete-page-by-id (db/title->page-id title-only))
                                         (build-response new-body request))
        :else (let [title-only (s/replace title "/create" "")
                    new-body (layout/compose-create-or-edit-page
                               (db/create-new-post-map title-only) request)]
                (build-response new-body request))))))

(defn is-authenticated-user?
  [request]
  (println "is-authenticated-user?")
  (println "request:" request)
  (println "identity:" (get-in request [:session :identity]))
  (if (get-in request [:session :identity])
    true
    (error "Only authenticated users allowed")))

(defn is-admin-user?
  [request]
  (println "is-admin-user?")
  (println "role:" (get-in request [:session :identity :user_role]))
  (= ":admin" (get-in request [:session :identity :user_role])))

(defn is-cwiki-user?
  [request]
  (= ":cwiki" (get-in request [:session :identity :user_role])))

(defn any-access
  [request]
  (println "any-access")
  true)

(defn my-unauthorized-handler
  [request metadata]
  (-> (response/render "Unauthorized request" request)
      (assoc :status 403)))

(def rules [{:pattern #"^/admin/.*"
             :handler {:or [is-admin-user? is-cwiki-user?]} ;admin-access operator-access]}
             :redirect "/notauthorized"}
            {:pattern #"^/login$"
             :handler any-access
             :on-error (fn [req _] (response/render "Problem with any-access" req))}
            {:pattern #"^/.*"
             :handler is-authenticated-user? ;authenticated-access
             :on-error (fn [req _] (response/render "Not authenticated ;)" req ))}])

(defroutes app-routes
           (route/resources "/")
           (page-finder-route (layout/compose-404-page))
           (route/not-found (layout/compose-404-page)))

(defn wrap-auth [handler]
  (-> handler
      (wrap-authentication backend)
      (wrap-authorization backend)))

(def app
  (-> (routes admin-routes home-routes login-routes app-routes)
      (wrap-auth)
      (handler/site)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-base-url)))

;(def options {:rules rules :on-error my-unauthorized-handler}) ;on-error})
;(def app (wrap-access-rules combined-routes options))
;;; Wrap the handler with access rules (and run with jetty as example)
;(defn -main
;  [& args]
;  (let [options {:rules rules :on-error on-error}
;        app     (wrap-access-rules your-app-handler options)]
;    (run-jetty app {:port 3000})))

