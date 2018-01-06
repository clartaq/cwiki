;;;
;;; This namespace includes layouts normally available only to admin users.
;;;

(ns cwiki.layouts.admin
  (:require [clojure.string :as s]
            [cwiki.layouts.base :as base]
            [cwiki.util.req-info :as ri]
            [cwiki.models.wiki-db :as db]
            [hiccup.form :refer [drop-down email-field form-to hidden-field
                                 password-field
                                 submit-button text-field]]))

;;
;; Functions related to creating a new user.
;;

(defn compose-user-already-exists-page
  "Return a page stating that the user already exists."
  []
  (base/short-message "Can't Do That!" "A user with this name already exits."))

(defn confirm-creation-page
  [user-name referer]
  (base/short-message-return-to-referer
    "Creation Complete"
    (str "User \"" user-name "\" has been created") referer))

(defn create-user-page
  "Return a page with a form to gather information needed
  to create a new user."
  [req]
  (base/short-form-template
    [:div {:class "cwiki-form"}
     (form-to {:enctype      "multipart/form-data"
               :autocomplete "off"}
              [:post "create-user"]
              (hidden-field "referer" (get (:headers req) "referer"))
              [:p {:class "form-title"} "Create A New User"]
              [:p "Enter information describing the new user."]
              base/required-field-hint
              [:div {:class "form-group"}
               [:div {:class "form-label-div"}
                [:label {:class "form-label required"
                         :for   "user-name"} "User Name"]]
               (text-field {:class       "form-text-field"
                            :autofocus   "autofocus"
                            :required    "true"
                            :placeholder "User Name"} "user-name")]
              [:div {:class "form-group"}
               [:div {:class "form-label-div"}
                [:label {:class "form-label required"
                         :for   "password"} "Password"]]
               (password-field {:class    "form-password-field"
                                :required "true"}
                               "password")]
              [:div {:class "form-group"}
               [:div {:class "form-label-div"}
                [:label {:class "form-label required"
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
;; Functions related to editing the profile of an existing user.
;;

(defn- no-users-to-edit-page
  "Return a page that displays a message that there
  are no suitable users to edit."
  [req]
  (base/short-message "Nothing to Do" "There are no suitable users to edit."))

(defn must-not-be-empty-page
  "Return a page informing the user that the field cannot be empty."
  []
  (base/short-message "Can't Do That!" "The user name cannot be empty."))

(defn confirm-edits-page
  "Return a page that confirms the edits have been completed and then
  go to the page given."
  [new-name old-name referer]
  (base/short-message-return-to-referer
    "Changes Complete"
    (if (not= new-name old-name)
      (str "User \"" old-name "\" has been changed to \"" new-name "\"")
      (str "User \"" new-name "\" has been changed"))
      referer))

(defn select-user-to-edit-page
  "Return a form to obtain the name of the user to be edited."
  [req]
  (let [all-users (db/get-all-users)
        cleaned-users (disj all-users "CWiki")]
    (if (zero? (count cleaned-users))
      (no-users-to-edit-page req)
      (base/short-form-template
        [:div {:class "cwiki-form"}
         (form-to {:enctype      "multipart/form-data"
                   :autocomplete "off"}
                  [:post "select-profile"]
                  (hidden-field "referer" (get (:headers req) "referer"))
                  [:p {:class "form-title"} "Edit Profile"]
                  base/required-field-hint
                  [:div {:class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label required"
                             :for   "user-name"} "User Profile to Edit"]]
                   (drop-down {:class    "form-dropdown"
                               :required "true"}
                              "user-name" cleaned-users)]
                  [:div {class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label required"
                             :for   "password"} "Your Password"]]
                   (password-field {:class    "form-password-field"
                                    :required "true"}
                                   "password")]
                  [:div {:class "button-bar-container"}
                   (submit-button {:id    "select-user-button"
                                   :class "form-button button-bar-item"}
                                  "Select")
                   [:input {:type    "button" :name "cancel-button"
                            :value   "Cancel"
                            :class   "form-button button-bar-item"
                            :onclick "window.history.back();"}]])]))))

(defn edit-user-page
  "Handle editing the profile of an existing user."
  [req]
  (let [user-info (get-in req [:session :edit-user-info])
        user-name (:user_name user-info)
        user-role (s/replace-first (:user_role user-info) ":" "")
        user-email (:user_email user-info)]
    (if (nil? user-info)
      (no-users-to-edit-page req)
      (base/short-form-template
        [:div {:class "cwiki-form"}
         (form-to {:enctype      "multipart/form-data"
                   :autocomplete "off"}
                  [:post "edit-profile"]
                  [:p {:class "form-title"} "Edit the Profile of An Existing User"]
                  [:p (str "Make any modifications needed to "
                           user-name "'s profile.")]
                  base/required-field-hint
                  [:div {:class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label required"
                             :for   "new-user-name"} "User Name"]]
                   (text-field {:class     "form-text-field"
                                :autofocus "autofocus"
                                :required  "true"
                                :value     user-name} "new-user-name")
                   [:div {:class "form-restrictions"}
                    "The user name cannot be empty. Any new user name
                    cannot match an existing user name. The comparison is
                    case-insensitive."]]
                  [:div {:class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label"
                             :for   "password"} "Password"]]
                   (password-field {:class "form-password-field"}
                                   "new-password")
                   [:div {:class "form-restrictions"}
                    "To leave the password as is, leave the field blank."]]
                  [:div {:class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label"
                             :for   "new-role"} "Role"]]
                   (drop-down {:class "form-dropdown"}
                              "new-role"
                              ["reader" "writer" "editor" "admin"] user-role)]
                  [:div {:class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label"
                             :for   "recovery-email"} "Password Recovery email"]]
                   (email-field {:class "form-email-field"
                                 :value user-email} "new-email")
                   [:div {:class "form-restrictions"}
                    "To erase the email field, erase delete all of the text
                     in the field and leave it blank."]]
                  [:div {:class "button-bar-container"}
                   (submit-button {:id    "change-user-button"
                                   :class "form-button button-bar-item"} "Change")
                   [:input {:type    "button" :name "cancel-button"
                            :value   "Cancel"
                            :class   "form-button button-bar-item"
                            :onclick "window.history.back();"}]])]))))

;;
;; Functions related to deleting a user.
;;

(defn wrong-password-page
  "Compose and return a short page stating that the password
  checked does not match that of the current user."
  [req]
  (base/short-message "Wrong Password"
                      "The password given does not match that of the current user."))

(defn cannot-find-user
  "Return a page saying that we cannot find the user now."
  [req]
  (base/short-message "Well, That's Weird!" "Now that user cannot be found."))

(defn- no-users-to-delete-page
  "Return a page that displays a message that there
  are no suitable users to delete."
  [req]
  (base/short-message "Nothing to Do" "There are no suitable users to delete."))

(defn confirm-deletion-page
  [user-name referer]
  (base/short-message-return-to-referer
    "Deletion Complete"
    (str "User \"" user-name "\" has been deleted") referer))

(defn delete-user-page
  "Return a form to obtain information about a user to be deleted."
  [req]
  (let [all-users (db/get-all-users)
        current-user (ri/req->user-name req)
        cleaned-users (disj all-users "CWiki" current-user)]
    (if (zero? (count cleaned-users))
      (no-users-to-delete-page req)
      (base/short-form-template
        [:div {:class "cwiki-form"}
         (form-to {:enctype      "multipart/form-data"
                   :autocomplete "off"}
                  [:post "delete-user"]
                  (hidden-field "referer" (get (:headers req) "referer"))
                  [:p {:class "form-title"} "Delete A User"]
                  [:p base/warning-span "This action cannot be undone."]
                  base/required-field-hint
                  [:div {:class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label required"
                             :for   "user-name"} "User to Delete"]]
                   (drop-down {:class    "form-dropdown"
                               :required "true"}
                              "user-name" cleaned-users)]
                  [:div {class "form-group"}
                   [:div {:class "form-label-div"}
                    [:label {:class "form-label required"
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
