(defproject cwiki "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.cemerick/url "0.1.1"]
                 [clj-time "0.14.0"]
                 [com.vladsch.flexmark/flexmark "0.28.0"]
                 [com.vladsch.flexmark/flexmark-ext-gfm-strikethrough "0.28.0"]
                 [compojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [org.xerial/sqlite-jdbc "3.20.1"]
                 [ring-server "0.5.0"]]

  :plugins [[lein-ring "0.12.1"]]
  :ring {:handler cwiki.handler/app
         :init    cwiki.handler/init
         :destroy cwiki.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
            {:ring
             {:open-browser? false
              :stacktraces?  false
              :auto-reload?  false}}
   :dev
            {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.6.3"]]}}

  :main cwiki.main)
