(ns ^:figwheel-no-load cwiki.dev
  (:require
    [cwiki-mde.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
