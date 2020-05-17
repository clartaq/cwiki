(ns ^:figwheel-no-load cwiki.dev
  (:require
    [cwiki-mde.core :as core]
    [devtools.core :as devtools]))

;; Determine if running on a chrome-based browser. Based on
;; Jonathan Marzullo's answer to this answer:
;; https://stackoverflow.com/questions/4565112/javascript-how-to-find-out-if-the-user-browser-is-chrome/13348618#13348618
;; and greatly simplified for my purposes.
(defn chromium-based-browser?
  []
  (let [win-nav (.-navigator js/window)
        vendor-name (.-vendor win-nav)
        res (= vendor-name "Google Inc.")]
    res))

(when (chromium-based-browser?)
  (devtools/install!))

(enable-console-print!)

(core/init!)
