(ns cwiki.components.common)

(defn modal
  [header body footer]
  [:div
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header [:h3 header]]
     [:div.modal-body body]
     [:div.modal-footer
      [:div.bootstrap-dialog-footer
       footer]]]]
   [:div.modal-backdrop.fad.in]])

;(defn error-modal []
;  (when-let [error (session/get :error)]
;    [modal
;     (:message error)
;     [:div.container-fluid
;      [:div.form-group
;       [:div.alert.alert-danger (:cause error)]]]
;     [:div.form-group
;      [:button.btn.btn-danger.btn-lg.btn-block
;       {:type     "submit"
;        :on-click #(session/remove! :error)}
;       "OK"]]]))

(defn input [type id placeholder fields]
  [:input.form-control.input-lg
   {:type        type
    :placeholder placeholder
    :value       (id @fields)
    ;:on-change   #(swap! fields assoc id (-> % .-target .-value))
    }])

(defn form-input [type label id placeholder fields optional?]
  [:div.form-group
   [:label label]
   (if optional?
     [input type id placeholder fields]
     [:div.input-group
      [input type id placeholder fields]
      [:span.input-group-addon
       "✱"]])])

(defn text-input [label id placeholder fields & [optional?]]
  (form-input :text label id placeholder fields optional?))

(defn email-input [label id placeholder fields & [optional?]]
  (form-input :email label id placeholder fields optional?))

(defn password-input [label id placeholder fields & [optional?]]
  (form-input :password label id placeholder fields optional?))
