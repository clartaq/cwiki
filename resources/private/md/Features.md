---
author: CWiki

title: Features

date: 2017-09-09T09:24:01.000-04:00
modified: 2020-05-31T15:27:02.043-04:00
tags:
  - features

  - cwiki

---

These are some features of CWiki.

* Your data is your own. It is not stored on somebody else's cloud platform.
* Pages are written in [Markdown](https://daringfireball.net/projects/markdown/), a simple set of markup conventions to produce text that can easily be translated into attractive HTML.
* Because the native markup language is Markdown with some extensions for internal [[Wikilinks]], the pages you write can freely link to the external [World Wide Web](https://en.wikipedia.org/wiki/World_Wide_Web) and other pages inside your local wiki.
* CWiki includes a straightforward​ Markdown editor with live preview.
* Each page that you write can have tags associated with it.
* You're data are not locked in. You can export the contents of the wiki to plain text Markdown with YAML front matter. You can export individual pages or all of the pages at once.
* You can use CWiki to create pages that attractively format mathematics. For example, here is an inline equation $\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$, and here is the same equation on it's own line:
$$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$$
Math typesetting​ is made possible by using the [MathJax](https://www.mathjax.org/) engine to display mathematics.
* Code listings will be highlighted based on the syntax of the programming language in the listing. For example, here is a Clojure function:

```clojure
(defn insert-new-page!
  "Insert a new page into the database given a title and content.
  Return the post map for the new page (including id and dates).
  If the arguments do not include an author id, use the CWiki
  author id (same as CWiki user id)."
  ([title content]
   (insert-new-page! title content (get-cwiki-user-id)))
  ([title content author-id]
   (let [post-map (create-new-post-map title content author-id)]
     (jdbc/insert! h2-db :pages post-map)
     (find-post-by-title title))))
```

* Tables can be created with a simple syntax using an extension to the Markdown language.

| Fruit |  Color | Description | Price ($/lb) |
|-------|:------:|:-----------|-------------:|
| Apple  |   Red  |    Crisp    |         0.99 |
| Pear   |  Green |    Sweet    |         1.49 |
| Plum   | Purple |     Tart    |         1.99 |
| Orange | Orange | Tangy |      1.29

* All of the data in the program is stored in a single database file. CWiki uses the [H2](http://h2database.com/html/main.html) database engine to store and maintain your data. It requires no administration on your part.
* The program is open source and available on [GitHub](https://github.com) at this [link]( https://github.com/clartaq/cwiki).
* The program is written in the [Clojure](https://clojure.org/) programming language with a bit of [ClojureScript](https://clojurescript.org) thrown in. Clojure is a modern [Lisp](https://en.wikipedia.org/wiki/Lisp_(programming_language)) dialect that is hosted on the [Java Virtual Machine](https://en.wikipedia.org/wiki/Java_virtual_machine) (JVM). ClojureScript is a version of the same language that is cross-compiled to JavaScript.
* The program achieves cross-platform functionality by the use of Clojure, the JVM and using a web server running on your local machine.