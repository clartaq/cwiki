;;;
;;; Functions in this namespace are used to extract bits of
;;; information from an HTTP request map.
;;;
;;; Note that the functions "know" very little about the contents
;;; of the request or session. They just do simple queries of the
;;; maps to find particular bits of information.
;;;

(ns cwiki.util.req-info)

(defn req->user-id
  "Extract a user id from a request containing a session and
  identity (i.e. an authenticated user.)"
  [req]
  (get-in req [:session :identity :user_id]))

(defn req->user-name
  "Extract the name of the current user from the session."
  [req]
  (get-in req [:session :identity :user_name]))

(defn req->user-role
  "Extract the role of the user."
  [req]
  (get-in req [:session :identity :user_role]))

(defn is-authenticated-user?
  "Return the user identity map if a valid user has
  logged into the session, nil otherwise."
  [req]
  (get-in req [:session :identity]))

(defn is-admin-user?
  "Return true if the user is an admin, false otherwise."
  [req]
  (= ":admin" (req->user-role req)))

(defn is-cwiki-user?
  "Return true if CWiki is the logged in user, false otherwise."
  [req]
  (= ":cwiki" (req->user-role req)))


