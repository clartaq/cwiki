;;;
;;; The functions in this namespace are concerned with user
;;; authorization, that is, what a user is allowed to do.
;;;

(ns cwiki.util.authorization
  (:require [cwiki.util.req-info :as ri]
            [cwiki.models.wiki-db :as db]))

(defn can-create?
  "Return true if the user is allowed to create new pages,
  false otherwise."
  [request]
  (let [role (ri/req->user-role request)]
    (or (= role "admin")
        (= role "editor")
        (= role "writer"))))

(defn can-edit-and-delete?
  "Return true if the user in the request is allowed to
  edit and delete pages, false otherwise."
  [request title]
  (let [role (ri/req->user-role request)
        session-user-id (ri/req->user-id request)
        author-id (db/title->user-id title)]
    (cond
      (or (= "admin" role)
          (= "editor" role)) true
      (and (= "writer" role)
           (= session-user-id author-id)) true
      :else nil)))

