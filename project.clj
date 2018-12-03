(defproject cwiki "0.1.3-SNAPSHOT"
  :description "A personal wiki written in Clojure and Clojurescript."
  :url "https://bitbucket.org/David_Clark/cwiki"
  :license {:name         "Simplfied BSD"
            :url          "https://en.wikipedia.org/wiki/BSD_licenses#2-clause"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.439" :scope "provided"]
                 [org.clojure/core.async "0.4.490"]
                 [org.clojure/java.jdbc "0.7.8"]

                 [buddy/buddy-auth "2.1.0" :exclusions [com.fasterxml.jackson.core/jackson-core]]
                 [buddy/buddy-hashers "1.3.0"]
                 ;[com.cemerick/url "0.1.1"]
                 [com.arohner/uri "0.1.2"]
                 [com.h2database/h2 "1.4.197"]
                 [circleci/clj-yaml "0.5.6"]
                 [clj-time "0.15.1"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [com.taoensso/sente "1.13.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.vladsch.flexmark/flexmark "0.34.58"]
                 [com.vladsch.flexmark/flexmark-ext-gfm-strikethrough "0.34.58"]
                 [com.vladsch.flexmark/flexmark-ext-footnotes "0.34.58"]
                 [com.vladsch.flexmark/flexmark-ext-tables "0.34.58"]
                 [com.vladsch.flexmark/flexmark-ext-wikilink "0.34.58"]
                 [compojure "1.6.1" :exclusions [clout instaparse]]
                 [environ "1.1.0"]
                 [hiccup "1.0.5"]
                 [http-kit "2.3.0"]
                 [javax.xml.bind/jaxb-api "2.3.0"]
                 [keybind "2.2.0"]
                 ; We keep this old version to remain compatible with
                 ; full text search with the H2 database.
                 [org.apache.lucene/lucene-core "3.6.2"]
                 [reagent "0.8.1"]
                 [ring/ring-defaults "0.3.2"]]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-environ "1.1.0"]
            [lein-ring "0.12.1"]
            [lein-asset-minifier "0.2.7" :exclusions [org.clojure/clojure]]]

  :main cwiki.main

  :uberjar-name "cwiki.jar"

  :source-paths ["src/clj"]

  ; Leave this alone. IntelliJ has issues otherwise.
  :test-paths ["test/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"
                                    "test/js/compiled"]

  :minify-assets {:assets
                  {"resources/public/css/styles.min.css" "resources/public/css/styles.css"}}

  :aliases {"test-cljs"  ["doo" "slimer" "test" "auto"]
            "start-prod" ["do" "clean," "cljsbuild" "once" "min," "run"]}

  :ring {:handler cwiki.handler/app}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev     {:repl-options {:init-ns          cwiki.repl
                                      :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}

                       :dependencies [[binaryage/devtools "0.9.10"]
                                      [cider/piggieback "0.3.10"]
                                      [figwheel-sidecar "0.5.17" :exclusions [org.clojure/tools.nrepl]]
                                      [prone "1.6.1"]
                                      [ring/ring-mock "0.3.2"]
                                      [ring/ring-devel "1.7.1"]]

                       :source-paths ["env/dev/clj"]

                       :prep-tasks   [["compile" "cwiki.util.wikilink-attributes"]]

                       :plugins      [[lein-doo "0.1.10"]
                                      [lein-figwheel "0.5.16"]]
                       ; Leave this alone. IntelliJ has issues otherwise.
                       :test-paths   ["test/cljs"]
                       :env          {:profile-type "development"
                                      :debugging-css "true"}}

             :test    {:env {:profile-type "test"}}

             :uberjar {:aot          :all
                       :omit-source  true
                       :source-paths ["env/prod/clj"]
                       :hooks        [minify-assets.plugin/hooks]
                       :prep-tasks   ["clean"
                                      ["compile" "cwiki.util.wikilink-attributes"]
                                      "compile"
                                      ["cljsbuild" "once" "min"]]
                       ; This really shouldn't be required. There is some sort of
                       ; dependency version incompatibility somewhere that needs
                       ; to be fixed.
                       :dependencies [[ring/ring-mock "0.3.2"]]
                       :env          {:profile-type "production"}}
             }

  :cljsbuild {:builds
              [
               {:id           "min"
                :source-paths ["src/cljs" "env/prod/cljs"]
                :compiler     {:output-to       "resources/public/js/compiled/cwiki-mde.js"
                               :output-dir      "resources/public/js/compiled/min"
                               :elide-asserts   true
                               :closure-defines {goog.DEBUG false}
                               :optimizations   :advanced
                               :pretty-print    false
                               :externs         ["externs/syntax.js"]}}

               {:id           "dev"
                :source-paths ["src/cljs" "env/dev/cljs"]
                :figwheel     {:on-jsload "cwiki-mde.core/reload"}
                :compiler     {:main          "cwiki.dev"
                               :output-to     "resources/public/js/compiled/cwiki-mde.js"
                               :output-dir    "resources/public/js/compiled/dev"
                               :asset-path    "js/compiled/dev"
                               :source-map    true
                               :optimizations :none
                               :pretty-print  true
                               :externs       ["externs/syntax.js"]}}

               {:id           "test"
                :source-paths ["src/cljs" "test/cljs"]
                :compiler     {:output-to     "resources/public/js/compiled/test.js"
                               :output-dir    "resources/public/js/compiled/test"
                               :main          cwiki-test.runner
                               :optimizations :none
                               :externs       ["externs/syntax.js"]}}
               ]}
  )
