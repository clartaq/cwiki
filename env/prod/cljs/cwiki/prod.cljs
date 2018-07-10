(ns cwiki.prod
  (:require [cwiki-mde.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
