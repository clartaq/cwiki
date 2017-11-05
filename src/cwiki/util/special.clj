(ns cwiki.util.special
  (:gen-class))

(def special-pages
  [{:name "Front Page" :editable? true :deletable? nil}
   {:name "Special Pages" :editable? nil :deletable? nil}
   {:name "All Pages" :editable? nil :deletable? nil}
   {:name "Sidebar" :editable? true :deletable? nil}
   {:name "About" :editable? true :deletable? true}
   {:name "Orphans" :editable? nil :deletable? nil}
   {:name "Preferences" :editable? true :deletable? nil}])

(defn find-first-with-name
  "Return the map for the 'special' page with the name
  page-name, nil otherwise."
  [page-name]
  (some #(when (= (:name %) page-name) %) special-pages))

(defn is-special?
  "Return true if the page-name represent a 'special' page
  in the wiki, nil otherwise."
  [page-name]
  (find-first-with-name page-name))

(defn is-deletable?
  "Return nil if the page-name is the name of a 'special' page
  that cannot be deleted, true otherwise."
  [page-name]
  (if-let [m (find-first-with-name page-name)]
    (:deletable? m)
    true))

(defn is-editable?
  "Return nil if the page-name is the name of a 'special' page
  that cannot be edited, true otherwise."
  [page-name]
  (if-let [m (find-first-with-name page-name)]
    (:editable? m)
    true))

(defn get-all-special-page-names
  "Return a sorted set of all of the special page titles in the wiki."
  []
  (into (sorted-set) (mapv #(:name %) special-pages)))

