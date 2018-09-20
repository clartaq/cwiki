(ns cwiki.css-source)

(def debugging-css true)

(defn get-css-path
  "Return the path to the development css file."
  []
  (let [q (if debugging-css
            (str "?" (rand-int 2147483647))
            "")
        cssp (str "/css/styles.css" q)]
    cssp))
