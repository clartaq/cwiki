(ns cwiki-test.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [cwiki-test.core-test]
              [cwiki-test.tag-editor-test]))

(doo-tests 'cwiki-test.core-test
           'cwiki-test.tag-editor-test)
