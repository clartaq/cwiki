;;;
;;; This namespace includes layouts normally available only to admin users.
;;;

(ns cwiki.layouts.admin
  (:require [cwiki.layouts.base :as base]
            [cwiki.util.req-info :as ri]
            [cwiki.models.db :as db]
            [hiccup.form :refer [drop-down email-field form-to hidden-field
                                 password-field
                                 submit-button text-field]]))

;;
;; Functions related to creating a new user.
;;

(defn compose-user-already-exists-page
  "Return a page stating that the user already exists."
  []
  (base/short-message-template
    [:div {:class "cwiki-form"}
     [:p {:class "form-title"} "403 - Forbidden"]
     [:p "A user with this name already exists."]
     [:div {:class "button-bar-container"}
      [:input {:type    "button" :name "cancel-button"
               :value   "Ok"
               :class   "form-button"
               :onclick "window.history.back();"}]]]))

(defn create-user-page
  "Return a page with a form to gather information needed
  to create a new user."
  [req]
  (base/short-message-template
    [:div {:class "cwiki-form"}
     (form-to {:enctype "multipart/form-data"}
              [:post "create-user"]
              (hidden-field "referer" (get (:headers req) "referer"))
              [:p {:class "form-title"} "Create A New User"]
              [:p "Enter information describing the new user."]
              [:div {:class "form-group"}
               [:div {:class "form-label-div"}
                [:label {:class "form-label"
                         :for   "user-name"} "User Name"]]
               (text-field {:class       "form-text-field"
                            :autofocus   "autofocus"
                            :required    "true"
                            :placeholder "User Name"} "user-name")]
              [:div {:class "form-group"}
               [:div {:class "form-label-div"}
                [:label {:class "form-label"
                         :for   "password"} "Password"]]
               (password-field {:class    "form-password-field"
                                :required "true"}
                               "password")]
              [:div {:class "form-group"}
               [:div {:class "form-label-div"}
                [:label {:class "form-label"
                         :for   "user-role"} "Role"]]
               (drop-down {:class    "form-dropdown"
                           :required "true"}
                          "user-role"
                          ["reader" "writer" "editor" "admin"] "reader")]
              [:div {:class "form-group"}
               [:div {:class "form-label-div"}
                [:label {:class "form-label"
                         :for   "recovery-email"} "Password Recovery email"]]
               (email-field {:class       "form-email-field"
                             :placeholder "email"} "recovery-email")]
              [:div {:class "button-bar-container"}
               (submit-button {:id    "create-user-button"
                               :class "form-button button-bar-item"} "Create")
               [:input {:type    "button" :name "cancel-button"
                        :value   "Cancel"
                        :class   "form-button button-bar-item"
                        :onclick "window.history.back();"}]])]))

;;
;; Functions related to deleting a user.
;;

(defn wrong-password-page
  "Compose and return a short page stating that the password
  checked does not match that of the current user."
  [req]
  (base/short-message-template
    [:div {:class "cwiki-form"}
     [:p {:class "form-title"} "Wrong Password"]
     [:p "The password given does not match that of the current user."]
     [:div {:class "button-bar-container"}
      [:input {:type    "button" :name "cancel-button"
               :value   "Ok"
               :class   "form-button"
               :onclick "window.history.back();"}]]]))

(defn cannot-find-user
  "Return a page saying that we cannot find the user now."
  [req]
  (base/short-message-template
    [:div {:class "cwiki-form"}
     [:p {:class "form-title"} "Well, That's Weird"]
     [:p "Now that user cannot be found."]
     [:div {:class "button-bar-container"}
      [:input {:type    "button" :name "cancel-button"
               :value   "Ok"
               :class   "form-button"
               :onclick "window.history.back();"}]]]))

(defn- no-users-to-delete-page
  "Return a page that displays a message that there
  are no suitable users to delete."
  [req]
  (base/short-message-template
    [:div {:class "cwiki-form"}
     [:p {:class "form-title"} "Nothing to Do"]
     [:p "There are no suitable users to delete."]
     [:div {:class "button-bar-container"}
      [:input {:type    "button" :name "cancel-button"
               :value   "Ok"
               :class   "form-button"
               :onclick "window.history.back();"}]]]))

(defn delete-user-page
  "Return a form to obtain information about a user to be deleted."
  [req]
  (let [all-users (db/get-all-users)
        current-user (ri/req->user-name req)
        cleaned-users (disj all-users "CWiki" current-user)]
    (if (zero? (count cleaned-users))
      (no-users-to-delete-page req)
      (base/short-message-template
        [:div {:class "cwiki-form"}
         (form-to {:enctype "multipart/form-data"}
                  [:post "delete-user"]
                  (hidden-field "referer" (get (:headers req) "referer"))
                  [:p {:class "form-title"} "Delete A User"]
                  [:p base/warning-span "This action cannot be undone."]
                  [:div {:class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label"
                             :for   "user-name"} "User to Delete"]]
                   (drop-down {:class    "form-dropdown"
                               :required "true"}
                              "user-name" cleaned-users)]
                  [:div {class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label"
                             :for   "password"} "Your Password"]]
                   (password-field {:class    "form-password-field"
                                    :required "true"}
                                   "password")]
                  [:div {:class "button-bar-container"}
                   (submit-button {:id    "login-button"
                                   :class "form-button button-bar-item"}
                                  "Delete")
                   [:input {:type    "button" :name "cancel-button"
                            :value   "Cancel"
                            :class   "form-button button-bar-item"
                            :onclick "window.history.back();"}]])]))))
