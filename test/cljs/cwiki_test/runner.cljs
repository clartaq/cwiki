(ns cwiki-test.runner
    (:require [cwiki-test.font-detection-test]
              [cwiki-test.tag-editor-test]
              [figwheel.main.testing :refer [run-tests-async]]))

(defn -main [& args]
  (run-tests-async 5000))
