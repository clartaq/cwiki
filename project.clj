(defproject cwiki "0.0.9-SNAPSHOT"
  :description "A personal wiki written in Clojure"
  :url "https://bitbucket.org/David_Clark/cwiki"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/java.jdbc "0.7.6"]

                 [buddy/buddy-auth "2.1.0"]
                 [buddy/buddy-hashers "1.3.0"]
                 [com.cemerick/url "0.1.1"]
                 [com.h2database/h2 "1.4.197"]
                 [circleci/clj-yaml "0.5.6"]
                 [clj-time "0.14.4"]
                 [cljs-ajax "0.7.3"]
                 [com.taoensso/sente "1.12.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.vladsch.flexmark/flexmark "0.32.24"]
                 [com.vladsch.flexmark/flexmark-ext-gfm-strikethrough "0.32.24"]
                 [com.vladsch.flexmark/flexmark-ext-footnotes "0.32.24"]
                 [com.vladsch.flexmark/flexmark-ext-tables "0.32.24"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [http-kit "2.3.0"]
                 [javax.xml.bind/jaxb-api "2.2.12"]
                 [reagent "0.8.1"]
                 [ring/ring-defaults "0.3.1"]]

  :main cwiki.main

  :source-paths ["src/clj"]

  :test-paths ["test/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"
                                    "test/js/compiled"]

  :aliases {"test-cljs"  ["doo" "slimer" "test" "auto"]
            "start-prod" ["do" "clean," "cljsbuild" "once" "min," "run"]}

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-ring "0.12.1"]]

  :ring {:handler cwiki.handler/app
         :init    cwiki.handler/init
         :destroy cwiki.handler/destroy}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:uberjar
              {:aot :all
               :omit-source true
               :hooks []
               :prep-tasks ["clean" "compile" ["cljsbuild" "once" "min"]]}
             :dev
              {:dependencies [[binaryage/devtools "0.9.10"]
                              [cider/piggieback "0.3.5"]
                              [figwheel-sidecar "0.5.16"]
                              [ring/ring-devel "1.6.3"]]
               :source-paths ["src/cljs"]
               :plugins      [[lein-doo "0.1.10"]
                              [lein-figwheel "0.5.16"]]
               :test-paths   ["test/cljs"]}}

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/cljs"]
                :figwheel     {:on-jsload "cwiki-mde.core/reload"}
                :compiler     {:main                 cwiki-mde.core
                               :optimizations        :none
                               :output-to            "resources/public/js/compiled/cwiki-mde.js"
                               :output-dir           "resources/public/js/compiled/dev"
                               :asset-path           "js/compiled/dev"
                               :source-map-timestamp true
                               :externs              ["externs/syntax.js"]}}

               {:id           "min"
                :source-paths ["src/cljs"]
                :compiler     {:main            cwiki-mde.core
                               :optimizations   :advanced
                               :output-to       "resources/public/js/compiled/cwiki-mde.js"
                               :output-dir      "resources/public/js/compiled/min"
                               :elide-asserts   true
                               :closure-defines {goog.DEBUG false}
                               :pretty-print    false
                               :externs         ["externs/syntax.js"]}}

               {:id           "test"
                :source-paths ["src/cljs" "test/cljs"]
                :compiler     {:output-to     "resources/public/js/compiled/test.js"
                               :output-dir    "resources/public/js/compiled/test"
                               :main          cwiki-test.runner
                               :optimizations :none
                               :externs       ["externs/syntax.js"]}}
               ]}

  )
