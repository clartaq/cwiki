(ns cwiki-test.runner
    (:require [cwiki-test.font-detection-test]
              [cwiki-test.tag-editor-test]
              [doo.runner :refer-macros [doo-tests]]))

(doo-tests 'cwiki-test.font-detection-test
           'cwiki-test.tag-editor-test)
