(ns cwiki-test.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [cwiki-test.core-test]))

(doo-tests 'cwiki-test.core-test)
