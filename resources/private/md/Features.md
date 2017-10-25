These are some features of CWiki.

* The program is open source and available on [BitBucket](https://bitbucket.org/product) at xxx.
* The program is written in the [Clojure](https://clojure.org/) programming language. Clojure is a modern [Lisp](https://en.wikipedia.org/wiki/Lisp_(programming_language)) dialect that is hosted on the [Java Virtual Machine](https://en.wikipedia.org/wiki/Java_virtual_machine) (JVM).
* The program achieves cross-platform functionality by the use of Clojure, the JVM and using a web server running on your local machine.
* All of the data in the program is stored in a single database file. CWiki uses the [SQLite](https://www.sqlite.org/) database engine to store and maintain your data.
* If you know [CSS](https://www.w3.org/Style/CSS/Overview.en.html), you can change some aspects of the way your wiki appears on screen.
* Because the native markup language is [Markdown](https://daringfireball.net/projects/markdown/) with some extension for internal [[WikiLinks]], the pages you write can freely link to the external [World Wide Web](https://en.wikipedia.org/wiki/World_Wide_Web) and other pages inside you local wiki.
* You can use CWiki to create pages that attractively format mathematics. For example, here is an inline equation $\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$, and here is the same equation on it's own:
$$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$$
This is made possible by using the online  [MathJax](https://www.mathjax.org/) engine to display mathematics. Of course, you must be online for this ability to operate correctly.
* Code listings will be highlighted based on the syntax of the programming language in the listing. For example, here is a Clojure function:

```prettyprint
(defn insert-new-page!
  "Insert a new page into the database given a title and content.
  Return the post map for the new page (including id and dates)."
  [title content]
  (let [post-map (create-new-post-map title content)]
    (jdbc/insert! sqlite-db :posts post-map)
    (find-post-by-title title)))
```