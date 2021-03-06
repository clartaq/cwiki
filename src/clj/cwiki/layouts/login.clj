;;;
;;; This namespace contains functions to build pages related to user
;;; login and logout.
;;;

(ns cwiki.layouts.login
  (:require [cwiki.layouts.base :as base]
            [hiccup.form :refer [form-to hidden-field password-field
                                 submit-button text-field]]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn login-page
  "Return a login page and gather the user name
  and password to log in."
  []
  (base/short-form-template
    (let [csrf-token (force anti-forgery/*anti-forgery-token*)]
      [:div {:class "cwiki-form"}
       [:div#sente-csrf-token {:data-csrf-token csrf-token}]
       (form-to {:enctype "multipart/form-data"}
                [:post "login"]
                (anti-forgery-field)
                [:p {:class "form-title"} "Sign In"]
                [:div
                 [:p "You must be logged in to use this wiki."]
                 [:p "You can always log in with user name \"guest\" and
               password \"guest\" just to look around."]]
                base/required-field-hint
                [:div {:class "form-group"}
                 [:div {:class "form-label-div"}
                  [:label {:class "form-label required"
                           :for   "user_name"} "User Name"]]
                 (text-field {:class        "form-text-field"
                              :required     "true"
                              :autofocus    "autofocus"
                              :autocomplete "off"
                              :placeholder  "User Name"} "user-name")]
                [:div {:class "form-group"}
                 [:div {:class "form-label-div"}
                  [:label {:class "form-label required"
                           :for   "user-password"} "Password"]]
                 (password-field {:class    "form-password-field"
                                  :required "true"} "password")]
                [:div {:class "button-bar-container"}
                 (submit-button {:id    "login-button"
                                 :class "form-button"} "Sign In")])])))

(defn no-user-to-logout-page
  "Return a page stating that there is no logged in user to sign out.
  Should never happen in production. Only used during development."
  []
  (base/short-message "That's a Problem" "There is no user to sign off."))

(defn post-logout-page
  "Return a page asking the user if they really want to log out."
  [user-name]
  (base/short-form-template
    [:div {:class "cwiki-form"}
     (form-to {:enctype "multipart/form-data"}
              [:post "logout"]
              (anti-forgery-field)
              [:p {:class "form-title"} (str "Really Sign Out \"" user-name "\"")]
              [:p "Are you sure?"]
              [:div {:class "button-bar-container"}
               (submit-button {:id        "sign-out-button"
                               :class     "form-button button-bar-item"
                               :autofocus "autofocus"} "Sign Out")
               [:input {:type    "button" :name "cancel-button"
                        :value   "Cancel"
                        :class   "form-button button-bar-item"
                        :onclick "window.history.back();"}]])]))

(defn view-logout-page
  "Choose to return a page asking the user to confirm logout
  unless there is no current user."
  [{session :session}]
  (if-let [user-name (:user_name (:identity session))]
    (post-logout-page user-name)
    (no-user-to-logout-page)))
