(ns cwiki.test.handler
  (:use clojure.test
        ring.mock.request
        cwiki.handler))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 302))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 302)))))
