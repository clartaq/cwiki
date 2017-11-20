(ns cwiki.routes.admin
  (:require [buddy.auth.accessrules :refer [restrict]]
            [compojure.core :refer :all]
            [cwiki.models.db :as db]
            [cwiki.views.layout :as layout]
            [ring.util.response :refer [redirect]]))

(defn- not-yet
  [name]
  (layout/compose-not-yet-view name))

(defroutes admin-routes
           (GET "/compact" [] (not-yet "compact"))
           (GET "/backup" [] (not-yet "backup"))
           (GET "/restore" [] (not-yet "restore"))
           (GET "/create-user" [] (not-yet "create-user"))
           (GET "/edit-user" [] (not-yet "edit-user"))
           (GET "/delete-user" [] (not-yet "delete-user"))
           (GET "/reset-password" [] (not-yet "reset-password")))
