(defproject cwiki "0.1.8"
  :description "A personal wiki written in Clojure and Clojurescript."
  :url "https://github.com/clartaq/cwiki"
  :license {:name         "Simplfied BSD"
            :url          "https://en.wikipedia.org/wiki/BSD_licenses#2-clause"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.10.1" :scope "provided"]
                 [org.clojure/clojurescript "1.10.773" :scope "provided"]
                 [org.clojure/core.async "1.2.603"]
                 [org.clojure/java.jdbc "0.7.11"]

                 [buddy/buddy-auth "2.2.0" :exclusions [com.fasterxml.jackson.core/jackson-core]]
                 [buddy/buddy-hashers "1.4.0"]
                 [com.cemerick/url "0.1.1"]
                 [com.h2database/h2 "1.4.199"]
                 [clj-commons/clj-yaml "0.7.1"]
                 [clj-time "0.15.2"]
                 [cljs-ajax "0.8.0"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [com.taoensso/sente "1.15.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.vladsch.flexmark/flexmark "0.42.12"]
                 [com.vladsch.flexmark/flexmark-ext-gfm-strikethrough "0.42.12"]
                 [com.vladsch.flexmark/flexmark-ext-footnotes "0.42.12"]
                 [com.vladsch.flexmark/flexmark-ext-tables "0.42.12"]
                 [com.vladsch.flexmark/flexmark-ext-wikilink "0.42.12"]
                 [compojure "1.6.1" :exclusions [clout instaparse]]
                 [environ "1.2.0"]
                 [hiccup "1.0.5"]
                 [http-kit "2.3.0"]
                 [keybind "2.2.0"]
                 ; We keep this old version to remain compatible with
                 ; full text search with the H2 database.
                 ; This question explains which versions of lucene that H2
                 ; 1.4.199 is actually compatible with
                 ;https://groups.google.com/forum/#!topic/h2-database/EjAgBCYpjdk
                 [org.apache.lucene/lucene-core "5.5.5"]
                 [org.apache.lucene/lucene-analyzers-common "5.5.5"]
                 [org.apache.lucene/lucene-queryparser "5.5.5"]
                 [org.flatland/ordered "1.5.9"]
                 [reagent "0.10.0"]
                 [ring/ring-defaults "0.3.2"]
                 [toml "0.1.3"]]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-doo "0.1.11"]
            [lein-environ "1.1.0"]
            [lein-ring "0.12.1"]
            [lein-shell "0.5.0"]
            ;[lein-asset-minifier "0.4.6" :exclusions [org.clojure/clojure]]
            ]

  :main cwiki.main

  :uberjar-name "cwiki.jar"

  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]

  :prep-tasks ["javac"]

  ;; Leave this alone. IntelliJ has issues otherwise.
  ;; Note that test-paths doesn't work for ClojureScript tests.
  :test-paths ["test/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"
                                    "test/js/compiled"]

  ;:minify-assets [[:css {:source "resources/public/css/styles.css"
  ;                       :target "resources/public/css/styles.min.css"}]]

  :aliases {;"test-cljs"  ["do" "clean," "doo" "firefox-headless" "test" "once"]
            "start-prod" ["do" "clean," "cljsbuild" "once" "min," "run"]}

  :ring {:handler cwiki.handler/app}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev     {:repl-options {:init-ns          cwiki.repl
                                      :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}

                       :dependencies [[org.clojure/tools.nrepl "0.2.13"]
                                      [binaryage/devtools "1.0.2"]
                                      [cider/piggieback "0.5.0"]
                                      [figwheel-sidecar "0.5.20" :exclusions [org.clojure/tools.nrepl]]
                                      [prone "2020-01-17"]
                                      [ring/ring-mock "0.4.0"]
                                      [ring/ring-devel "1.8.1"]]

                       :source-paths ["env/dev/clj"]

                       :prep-tasks   ["javac" ["compile" "cwiki.extensions.cwikilink-attributes"]]

                       :plugins      [[lein-doo "0.1.11"]
                                      [lein-figwheel "0.5.16"]]
                       ;; Leave this alone. IntelliJ has issues otherwise.
                       :test-paths   ["test/cljs"]
                       :env          {:profile-type "development"
                                      :debugging-css "true"}}

             ;:test    {:env {:profile-type "test"}}

             :uberjar {:aot          :all
                       :omit-source  true
                       :source-paths ["env/prod/clj"]
                       ;:hooks        [minify-assets.plugin/hooks]
                       :prep-tasks   ["clean"
                                      ["javac"]
                                      ["compile" "cwiki.extensions.cwikilink-attributes"]
                                      "compile"
                                      ["cljsbuild" "once" "min"]]
                       ;; This really shouldn't be required. There is some sort
                       ;; of dependency version incompatibility somewhere that
                       ;; needs to be fixed.
                       :dependencies [[ring/ring-mock "0.4.0"]]
                       :env          {:profile-type "production"}}
             }

  :cljsbuild {:builds
              [
               {:id           "min"
                :source-paths ["src/cljs" "env/prod/cljs" "src/cljc"]
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

               ;{:id           "test"
               ; :source-paths ["src/cljs" "test/cljs"]
               ; :compiler     {:output-to     "resources/public/js/compiled/test.js"
               ;                :output-dir    "resources/public/js/compiled/test"
               ;                :main          cwiki-test.runner
               ;                :optimizations :none
               ;                :externs       ["externs/syntax.js"]}}
               ]}
  )
