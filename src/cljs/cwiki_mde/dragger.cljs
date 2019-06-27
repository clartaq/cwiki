;;;;
;;;; A utility function to handle dragging the border between the sidebar
;;;; and article portion of the page. To allow changing the width of the
;;;; sidebar with a mouse drag.

(ns cwiki-mde.dragger
  (:require [ajax.core :refer [ajax-request json-request-format json-response-format
                               POST
                               text-request-format
                               text-response-format]]))

;; The minimum allowable width for the sidebar.
(def ^{:private true :const true} min-sidebar-basis 150)

;; Could calculate this, but this is easier. This value must match that
;; used in the css file for the sidebar.

(def ^{:private true :const true} twice-padding-width 48)

;; The resolved elements, just so we don't have to keep recalculating them.

(def ^{:private true} sidebar-ele (atom nil))

;; Other state.

(def ^{:private true} dragging (atom false))
(def ^{:private true} starting-mouse-x (atom 0))
(def ^{:private true} starting-basis (atom 0))
(def ^{:private true} new-basis (atom "0px"))

(enable-console-print!)

(defn response-handler [[ok response]]
  (when-not ok
    (.error js/console (str "Something bad happened: status: "
                            (:status response)
                            ", status-text: " (:status-text response)))))

(defn persist-new-basis
  [new-basis]
  (ajax-request
    {:uri             "/width-of-sidebar"
     :method          :post
     :body            new-basis
     :handler         response-handler
     :format          (text-request-format)
     :response-format (text-response-format)}))

(defn- move [evt]
  (when @dragging
    (let [movement (- (.-pageX evt) @starting-mouse-x)]
      (reset! new-basis (str (max min-sidebar-basis
                                  (+ @starting-basis movement)) "px"))
      (set! (-> (.-style @sidebar-ele) .-flexBasis) @new-basis))))

(defn- stop-tracking [_]
  (when @dragging
    (reset! dragging false)
    (.removeEventListener js/window "mousemove" move)
    (when (not= @starting-basis @new-basis)
      (persist-new-basis @new-basis))))

(defn- start-tracking [evt]
  (reset! starting-mouse-x (.-pageX evt)))

(defn ^{:export true} onclick_handler []
  (reset! sidebar-ele (.getElementById js/document "left-aside"))
  (reset! starting-basis (- (.-offsetWidth @sidebar-ele) twice-padding-width))
  (reset! dragging true)
  (.addEventListener js/window "mousedown" start-tracking)
  (.addEventListener js/window "mousemove" move)
  (.addEventListener js/window "mouseup" stop-tracking))
