(ns cwiki.components.registration
  (require [cwiki.components.common :as c]))

(defn registration-form
  []
  (let [fields (atom {})]
    [c/modal
     [:div "cwiki Registration"]
     [:div
      [:div.well.well-sm
       [:strong "* required field"]]
      [c/text-input "name" :id "Enter a user name." fields]
      [c/password-input "password" :pass "Enter a password." fields]
      [c/password-input "password" :pass-confirm "Re-enter password." fields]]
     [:div
      [:button.btn.btn-primary "Register"]
      [:button.btn.btn-danger "Cancel"]]]))