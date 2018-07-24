(ns cwiki.test.models.config
  (:require [clojure.test :refer :all]
            [cwiki.models.config :refer :all]
            [com.stuartsierra.component :as component]))

(deftest get-config-item-test
  (testing "get-config-item"
    (let [conf (component/start (new-config))]
      (is (nil? (.get-config-item conf nil)))
      (is (nil? (.get-config-item conf 32)))
      (is (nil? (.get-config-item conf :fake-key)))
      ; This test will faile when run from the repl, but will
      ; pass when run from `lein test`.
      (is (= "test" (.get-config-item conf :build-type)))
      (component/stop conf))))