(ns cwiki.util.special
  (:gen-class))

(def special-pages
  [{:name "Front Page" :editable? true :deletable? nil :generated? nil}
   {:name "Special Pages" :editable? nil :deletable? nil :generated? nil}
   {:name "All Pages" :editable? nil :deletable? nil :generated? true}
   {:name "All Users" :editable? nil :deletable? nil :generated? true}
   {:name "All Tags" :editable? nil :deletable? nil :generated? true}
   {:name "Sidebar" :editable? true :deletable? nil :generated? nil}
   {:name "Orphans" :editable? nil :deletable? nil :generated? true}
   {:name "Preferences" :editable? true :deletable? nil :generated? nil}
   {:name "Admin" :editable? true :deletable? true :generated? nil :admin-only? true}
   {:name "compress" :editable? nil :deletable? nil :generated? true :admin-only? true}
   {:name "backup" :editable? nil :deletable? nil :generated? true :admin-only? true}
   {:name "restore" :editable? nil :deletable? nil :generated? true :admin-only? true}
   {:name "create-user" :editable? nil :deletable? nil :generated? true :admin-only? true}
   {:name "select-profile" :editable? nil :deletable? nil :generated? true :admin-only? true}
   {:name "delete-user" :editable? nil :deletable? nil :generated? true :admin-only? true}
   {:name "reset-password" :editable? nil :deletable? nil :generated? true :admin-only? true}])

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

(defn is-generated?
  "Return true if the page-name is the name of a 'special' page
  that is generated on demand by the program, nil otherwise."
  [page-name]
  (when-let [m (find-first-with-name page-name)]
    (:generated? m)))

(defn is-admin-only?
  [page-name]
  (when-let [m (find-first-with-name page-name)]
    (:admin-only? m)))

(defn- case-insensitive-comparator
  "Case-insensitive string comparator."
  [^String s1 ^String s2]
  (.compareToIgnoreCase s1 s2))

(defn get-all-special-page-names
  "Return a sorted set of all of the special page titles in the wiki."
  []
  (into (sorted-set-by case-insensitive-comparator)
        (mapv :name special-pages)))

