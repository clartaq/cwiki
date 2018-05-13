(defproject cwiki "0.0.9-SNAPSHOT"
  :description "A personal wiki written in Clojure"
  :url "https://bitbucket.org/David_Clark/cwiki"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/java.jdbc "0.7.6"]

                 [buddy/buddy-auth "2.1.0"]
                 [buddy/buddy-hashers "1.3.0"]
                 [com.cemerick/url "0.1.1"]
                 [com.h2database/h2 "1.4.197"]
                 [circleci/clj-yaml "0.5.6"]
                 [clj-time "0.14.3"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.vladsch.flexmark/flexmark "0.32.22"]
                 [com.vladsch.flexmark/flexmark-ext-gfm-strikethrough "0.32.22"]
                 [com.vladsch.flexmark/flexmark-ext-footnotes "0.32.22"]
                 [com.vladsch.flexmark/flexmark-ext-tables "0.32.22"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [http-kit "2.3.0"]
                 [ring/ring-defaults "0.3.1"]]

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
            {:dependencies [[ring/ring-mock "0.3.2"]
                            [ring/ring-devel "1.6.3"]]}}

  :main cwiki.main)
