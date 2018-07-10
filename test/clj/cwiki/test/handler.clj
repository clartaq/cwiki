(ns cwiki.test.handler
  (:require [clojure.test :refer [deftest testing is]]
            [cwiki.handler :refer [app]]
            [ring.mock.request :refer [request]]))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 302))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 302)))))
