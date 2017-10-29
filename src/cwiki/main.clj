(ns cwiki.main
  (:gen-class)
  (:require [cwiki.repl :as repl]))

(defn -main [& args]
  (repl/start-app))
