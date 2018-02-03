---
title: Technical Notes
author: CWiki
date: 10/01/2017 5:45:07 PM 
updated: 1/23/2018 4:37:27 PM 
tags:
  - technical note
  - motivation
  - how it works
---
These are some technical notes about CWiki. If you are only interested in using the wiki, you can ignore this stuff. If you want to know how CWiki works or why it works the way it does or how to build and modify your own version, the information here might be useful.

## Links to Related Pages ##

* [[The Motivation for Writing CWiki]] talks about why the program was written in the first place.
* [[Limits]] discusses some of the limitations of the program.
* [[Tag Design]] reviews some of the thinking going into the design of the system of tags.
* [[Design of Import/Export]] describes the thought process that went into creating the file import and export features.

## Software Dependencies ##

Almost no software is written without dependencies these days -- programs are just too complicated. CWiki is no different. Some useful functionality has already been written by others. You can look at the program project file, `project.clj` to see exactly what is in there.  Here's a list.

### Java Stuff ###

* Developed and tested on late versions of Java 8 and early versions of Java 9.
* [Flexmark](https://github.com/vsch/flexmark-java) is used for the Markdown parser and renderer.
* [Jetty](http://www.eclipse.org/jetty/) is used as the server software.

### Clojure Stuff ###

* Developed and tested with Clojure 1.8. (May move to 1.9 once I understand the "spec" stuff.)
* [Buddy](https://github.com/funcool/buddy) is used for authentication.
* [clj-time](https://github.com/clj-time/clj-time) is used for formatting and handling time-related things.
* [clj-yaml](https://github.com/circleci/clj-yaml) (the maintained fork) is used to parse YAML front matter when pages are imported from files.
* [Compojure](https://github.com/weavejester/compojure) is used for routing.
* [Hiccup](https://github.com/weavejester/hiccup) is used for "lispy" creation of HTML.
* [Ring](https://github.com/ring-clojure/ring) is the web applications library.
* [H2](http://h2database.com/html/main.html) is used for database functions.
* [url](https://github.com/cemerick/url) is used for manipulating URLs.

### Everything Else ###

#### Database ####

CWiki uses the [H2](http://h2database.com/html/main.html) database for the database functions of the wiki. It is perhaps not as "heavy duty" as some other possibilities. However, it has the tremendous benefit that it requires no administration by the user.

Earlier in development, the program used [SQLite](https://sqlite.org/). However, it proved too cumbersome (and slow) for continued use in this project.

#### Editor ####

Right now, the editor is just an HTML text field. I want to change to [Writing](https://github.com/josephernest/Writing/) once I can figure out how to embed it in my pages with my CSS.

#### CSS ####

At this point, the CSS used is just plain ol' [CSS3](https://www.w3schools.com/css/css3_intro.asp). I strongly considered using [SCSS](http://sass-lang.com/), but did not want the additional dependency on [Ruby](https://www.ruby-lang.org/en/) to build the project. This may change in the future.

You can use the CSS file to re-style CWiki to your liking.

## Tests ##

Tests have not been written systematically. I tend to write them when I'm having difficulty with something. There are a few scattered around now. They will grow.

## Security ##

There really isn't any. 

* Don't put anything in CWiki that you wouldn't want someone else to see. 
* Don't put CWiki on a remote server. It could be hacked easily to steal your information or post someone else's.

Authentication is session based requiring a user name and password.

Authorization is home-grown and based on the roles users have been assigned. See [[About Roles]] for more information.

## How Pages Get Rendered ##

The pages in CWiki are a mashup of [Markdown](https://daringfireball.net/projects/markdown/syntax), [[Wikilinks]], and [[About TeX|$\rm\TeX$]]. No single parser/HTML generator handles all of those pieces. So rendering a page happens in several stages.

1. First, all of the WikiLinks are located and translated to HTML-style links. These links point to pages within the CWiki database. If there is no such page, the link is displayed in red.
2. The Markdown content, including the translated WikiLinks, are converted to HTML. Since Markdown parsers pass HTML through unaltered, the translated WikiLinks are left intact.
3. Finally, the HTML is passed to MathJax to translate any $\rm\TeX$ into something that can be displayed in a web page. The reprsentation is usually common HTML, but you can change that if needed.
4. That big chunk of HTML is plugged into the `<body>` section of a web page containing the header and footer for the page as well as the `<head>` section required for well-formed HTML5
5. That page is then served by a web-server built into CWiki (Jetty, mentioned above) and rendered by your browser.

Note that, as this is written, wikilinks cannot be included in code listings since the link resolver is unaware of those boundaries.

## Other Issues ##

### Multiple Users ###

Originally, CWiki was envisioned as a private wiki for use by a single person. However, after showing it around a bit. Other members of my family asked if they could use it too.

Of course, they could have set up their own instances, but they wanted to use the same one I was using. So... the capability for multiple users on the same wiki was added. See the sections on authentication and authorization for details.

But, CWiki is by no means "multi-user" in the sense of multiple users running the program simultaneously. There is really nothing to prevent it, but there are no facilities to guarantee exclusive access to the database or pages in those circumstances.

### Namespaces ###

Whether or not to implement namespaces for pages is an open question at this point.

When I look at the pages created during installation, there are lots of the form "About...". Maybe those should be made part of a "Help" namespace freeing up those titles for other uses in additional namespaces. For example, it might be useful for a user to create project-specific namespaces or namespaces for work-related research _vs_. research done for home-based projects.

### Version Control ###

### Authentication ###

### Authorization ###

### Which Version of Markdown to Use ###

Markdown by itself is a great way to write some things. It is intended to be simple and can do those simple things well. It is intended to be readable above all. And that works most of the time for short, simple projects.

But Markdown cannot handle some use cases that are very common, like producing tables. As a result, lots of "extensions" have developed over the years to help fill some of those needs.

Also, Markdown syntax as originally laid down by [John Gruber](https://en.wikipedia.org/wiki/John_Gruber) is ambiguous so various implementations have diverged causing users to sometimes be surprised when the same document produces different results in different programs.

Developers, including myself, have been forced to improvise. To get what I want most -- simple formatting, multi-line code listings, and math formatting -- I've put together yet another Frankenstein version of Markdown that includes those things. So, starting with basic Markdown, I've added:

* A much simplified version of [MediaWiki](https://www.mediawiki.org/wiki/MediaWiki)-style [wikilinks](https://www.mediawiki.org/wiki/Help:Links#Internal_links).
* [GitHub-flavored](https://github.github.com/gfm/) [fenced code blocks](https://github.github.com/gfm/#fenced-code-blocks).
* GitHub-flavored [strikethrough syntax](https://github.github.com/gfm/#strikethrough-extension-).
* GitHub-flavored [table syntax](https://github.github.com/gfm/#tables-extension-).
* Mathematics formatting using [$\rm\TeX$](https://en.wikibooks.org/wiki/LaTeX/Mathematics) syntax being rendered by [MathJax](https://www.mathjax.org/).

Surprisingly, as described above, a rendering pipeline can be constructed that will handle all of these things.

### Deleting Users ###

Deleting a user from a wiki is a pretty extreme measure. Then there is the question of what to do with anything they have written.

The approach taken by CWiki is to go ahead and delete the account, but make no effort to find and remove anything they have worked on. When pages that the deleted user authored are viewed, the author will be listed as "Unknown". The work of all deleted users will be shown with an "Unknown" author. There is no attempt to differentiate which deleted user may have authored something. It is up to the admin or an editor to revise or delete any material the deleted user may have created.

Deleting a user like this loses attribution for any work they may have done. This is generally not a good thing. Another, gentler approach would be for an admin to simple change the password for the deleted user such that they could no longer access their (former) account. That way the attribution remains, but the deleted user no longer has rights to create new content. (They can always sign in on the "guest" account to view anything in the wiki.)

### Handling Images ###

Essentially, I punted on this one. Since CWiki is at heart a single-user, private wiki, it would make sense to put embedded images in the database. There are safety and reliability benefits from doing this.

At the moment though,  images can be embedded by links to online resources or to the file system.

See [[About Images]].

### Rendering Mathematics ###

This was a no-brainer. MathJax is just terrific. But where to retrieve it from? The most performant option is probably from a CDN. But that's a pretty large initial download. Another option would be to load it directly on the server. But does that mean I have to keep an additional ~15-20MB of files in the repository?

At this point, I'll just use it from the CDN until, and if, it becomes a problem.

### Handling Quoted Wikilinks ###

In order to show examples of wikilinks in documentation, you should quote them. A user should be able to quote them using the backqoute ("\`"), triple backquotes  ("\`\`\`"), `<code></code>` tags or `<pre></pre>` tags.

However, the strategy I have been using has just been using regular expressions to find the wikilink markers, "\[\[" and "\]\]", use the contents between the markers to build a link, and replace the contents with the link.

Doing it that way causes examples of wikilinks embedded in a quoted section to be replaced anyway. Need to handle it differently.

It seems like there are 4 possible approaches.

1. Do nothing and warn users "don't do that". Least desirable.
2. Try to refine the regular expression-based approach I'm using now. Regular expressions are not a good way (even a possible way?) to handle embedded HTML.
3. Write a small parser to handle pages looking for quoted blocks. I think this would require me to re-work the dataflow from several passes over the text to one that does the translation in a single pass.
4. Since I'm using flexmark-java as my Markdown to HTML processor, look into the Wikilinks extension. I'm not sure how to handle special formatting for special cases like graying admin pages and making links to non-existent pages red. I'm not sure how extensible it is if I want to add things like namespaces either.

I filed an issue with the flexmark developers and they pointed me to this example:


### Enforcing Foreign Key Constraints with SQLite and clojure.java.jdbc ###

I'm working on a wiki program and using SQLite as the database. I want to create a many-to-many relationship between wiki pages and tags describing those pages. I'm using  `clojure.java.jdbc` to handle the database operations. I would like to enforce foreign key constraints in the page-to-tags cross-reference table. I looked at the information about foreign keys on the SQLite site (https://www.sqlite.org/foreignkeys.html) and believe something like this is what I want;

<!-- language: clojure -->

    (def the-db-name "the.db")
    (def the-db {:classname   "org.sqlite.JDBC"
                 :subprotocol "sqlite"
                 :subname     the-db-name})
    
    (defn create-some-tables
      "Create some tables and a cross-reference table with foreign key constraints."
      []
      (try (jdbc/db-do-commands
             the-db false
             ["PRAGMA foreign_keys = ON;"
              (jdbc/create-table-ddl :pages
                                     [[:page_id :integer :primary :key]
                                      ;...
                                      [:page_content :text]])
              (jdbc/create-table-ddl :tags
                                     [[:tag_id :integer :primary :key]
                                      [:tag_name :text "NOT NULL"]])
              (jdbc/create-table-ddl :tags_x_pages
                                     [[:x_ref_id :integer :primary :key]
                                      [:tag_id :integer]
                                      [:page_id :integer]
                                      ["FOREIGN KEY(tag_id) REFERENCES tags(tag_id)"]
                                      ["FOREIGN KEY(page_id) REFERENCES pages(page_id)"]])])
    
           (catch Exception e (println e))))

But attempting to turn the pragma on has no effect.

Just trying to turn the pragma on and check for effect:

<!-- language: clojure -->

    (println "Check before:" (jdbc/query the-db ["PRAGMA foreign_keys;"]))
    ; Transactions on or off makes no difference.
    (println "Result of execute!:" (jdbc/execute! the-db
                                                  ["PRAGMA foreign_keys = ON;"]))
    (println "Check after:" (jdbc/query the-db ["PRAGMA foreign_keys;"]))
    
    ;=> Check before: ({:foreign_keys 0})
    ;=> Result of execute!: [0]
    ;=> Check after: ({:foreign_keys 0})

The results indicate that the library (org.xerial/sqlite-jdbc "3.21.0.1") was compiled to support foreign keys since there were no errors, but trying to set the pragma has no effect.

I found [this](https://dev.clojure.org/jira/browse/JDBC-38) in the JIRA for the clojure JDBC back in 2012. The described changes have been implemented since then, but the code still has no effect.

Finally found [this answer](https://stackoverflow.com/questions/13348843/in-clojure-what-happens-when-you-call-sql-with-connection-within-another-sql-wi) to a Stackoverflow question that pointed to [this post](https://code-know-how.blogspot.ru/2011/10/how-to-enable-foreign-keys-in-sqlite3.html) back in 2011. That allowed me to cobble together something that did seem to set the pragma. The code below depends on creating a specially configured `Connection`.


<!-- language: clojure -->

    (ns example
      (:require [clojure.java.jdbc :as jdbc])
      (:import (java.sql Connection DriverManager)
               (org.sqlite SQLiteConfig)))
    
    (def the-db-name "the.db")
    (def the-db {:classname   "org.sqlite.JDBC"
                 :subprotocol "sqlite"
                 :subname     the-db-name})
    
    (defn ^Connection get-connection
      "Return a connection to a SQLite database that
      enforces foreign key constraints."
      [db]
      (Class/forName (:classname db))
      (let [config (SQLiteConfig.)]
        (.enforceForeignKeys config true)
        (let [connection (DriverManager/getConnection
                           (str "jdbc:sqlite:" (:subname db))
                           (.toProperties config))]
          connection)))
    
    (defn exec-foreign-keys-pragma-statement
      [db]
      (let [con ^Connection (get-connection db)
            statement (.createStatement con)]
        (println "exec-foreign-keys-pragma-statement:"
                 (.execute statement "PRAGMA foreign_keys;"))))

Based on the above, I rewrote the table creation code above as:


<!-- language: clojure -->
```clojure
    (defn create-some-tables
      "Create some tables and a cross-reference table with foreign key constraints."
      []
      (when-let [conn (get-connection the-db)]
        (try
          (jdbc/with-db-connection
            [conn the-db]
            ; Creating the tables with the foreign key constraints works.
            (try (jdbc/db-do-commands
                   the-db false
                   [(jdbc/create-table-ddl :pages
                                           [[:page_id :integer :primary :key]
                                            [:page_content :text]])
                    (jdbc/create-table-ddl :tags
                                           [[:tag_id :integer :primary :key]
                                            [:tag_name :text "NOT NULL"]])
                    (jdbc/create-table-ddl :tags_x_pages
                                           [[:x_ref_id :integer :primary :key]
                                            [:tag_id :integer]
                                            [:page_id :integer]
                                            ["FOREIGN KEY(tag_id) REFERENCES tags(tag_id)"]
                                            ["FOREIGN KEY(page_id) REFERENCES pages(page_id)"]])])
    
                 ; This still doesn't work.
                 (println "After table creation:"
                          (jdbc/query the-db "PRAGMA foreign_keys;"))
    
                 (catch Exception e (println e))))
    
          ; This returns the expected results.
          (when-let [statement (.createStatement conn)]
            (try
              (println "After creating some tables: PRAGMA foreign_keys =>"
                       (.execute statement "PRAGMA foreign_keys;"))
              (catch Exception e (println e))
              (finally (when statement
                         (.close statement)))))
          (catch Exception e (println e))
          (finally (when conn
                     (.close conn))))))
```

The tables are created as expected. Some of the `clojure.java.jdbc` functions still don't seem to work as desired though. (See the `jdbc/query` call in the middle of the listing.) Getting things to always work as expected seems very "manual" having to fall back on java interop. And it seems like every interaction with the database requires using the specially configured `Connection` returned by the `get-connection` function.

Is there a better way to enforce foreign key constraints in SQLite in Clojure?
 
