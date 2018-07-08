(defproject cwiki "0.0.10-SNAPSHOT"
  :description "A personal wiki written in Clojure"
  :url "https://bitbucket.org/David_Clark/cwiki"
  :license {:name "Simplfied BSD"
            :url "https://en.wikipedia.org/wiki/BSD_licenses#2-clause"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/java.jdbc "0.7.7"]

                 [buddy/buddy-auth "2.1.0"]
                 [buddy/buddy-hashers "1.3.0"]
                 [com.cemerick/url "0.1.1"]
                 [com.h2database/h2 "1.4.197"]
                 [circleci/clj-yaml "0.5.6"]
                 [clj-time "0.14.4"]
                 [com.taoensso/sente "1.12.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.vladsch.flexmark/flexmark "0.34.4"]
                 [com.vladsch.flexmark/flexmark-ext-gfm-strikethrough "0.34.4"]
                 [com.vladsch.flexmark/flexmark-ext-footnotes "0.34.4"]
                 [com.vladsch.flexmark/flexmark-ext-tables "0.34.4"]
                 [compojure "1.6.1"]
                 [environ "1.1.0"]
                 [hiccup "1.0.5"]
                 [http-kit "2.3.0"]
                 [javax.xml.bind/jaxb-api "2.3.0"]
                 [reagent "0.8.1"]
                 [ring/ring-devel "1.6.3"]
                 [ring/ring-defaults "0.3.2"]]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-environ "1.1.0"]
            [lein-ring "0.12.1"]
            [lein-asset-minifier "0.2.7" :exclusions [org.clojure/clojure]]]

  :main cwiki.main

  :uberjar-name "cwiki.jar"

  :source-paths ["src/clj"]

  :test-paths ["test/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"
                                    "test/js/compiled"]

  :minify-assets {:assets
                  {"resources/public/css/styles.min.css" "resources/public/css/styles.css"
                   "resources/public/css/mde.min.css" "resources/public/css/mde.css"
                   "resources/public/css/editor-styles.min.css" "resources/public/css/editor-styles.css"}}

  :aliases {"test-cljs"  ["doo" "slimer" "test" "auto"]
            "start-prod" ["do" "clean," "cljsbuild" "once" "min," "run"]}

  :ring {:handler cwiki.handler/app
         :init    cwiki.handler/init
         :destroy cwiki.handler/destroy}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev
             {:main user
              :env {:db "dev-db"
                    :dev true
                    :reloading-middleware true}
              :dependencies [;[binaryage/devtools "0.9.10"] I don't use chrome
                             [cider/piggieback "0.3.6"]
                             [figwheel-sidecar "0.5.16"]
                             [ring/ring-mock "0.3.2"]
                             [ring/ring-devel "1.6.3"]]
              :source-paths ["dev" "src/cljs"]
              :plugins      [[lein-doo "0.1.10"]
                             [lein-figwheel "0.5.16"]]
              :test-paths   ["test/cljs"]}
             :uberjar
             {:aot         :all
              :omit-source true
              :hooks        [minify-assets.plugin/hooks]
              :prep-tasks  ["clean" "compile" ["cljsbuild" "once" "min"]]
              ; This really shouldn't be required. There is some sort of
              ; dependency version incompatibility somewhere that needs
              ; to be fixed..
              :dependencies [[ring/ring-mock "0.3.2"]]
              :env          {:production true}}
             }

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
