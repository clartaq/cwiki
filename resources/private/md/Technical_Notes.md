These are some technical notes about CWiki. If you are only interested in using the wiki, you can ignore this stuff. If you want to know how CWiki works or why it works the way it does or how to build and modify your own version, the information here might be useful.

## Motivation ##

You might rightly ask "Why does the world need another wiki program?" Well, CWiki probably isn't vital to the survival of civilization as we know it. But I was motivated by a few things.

First, I've always been fascinated by wiki software (and blog software, and editors, and news aggregators). I've been using wikis in one form or another for years, decades actually. I wanted to see what it would be like to write my own just for me. No schedule, nobody else's set of features and requirements, just whatever I wanted.

**Written in Clojure**. I know the implementation language isn't too important, but I like [Clojure](https://clojure.org/). No Javascript. Even though it is very popular right now, it is ugly. I don't like it at all.

**Markdown with Extensions**. I like Markdown too. But this is a wiki. It has to support wiki links as well and HTML links.

**Syntax-Highlighted Code Listings**. I put a lot of code listings in some of the things I write. Has to look nice and be easy to do.

**Mathematics**. I want to be able to write content containing mathematics. Markdown does not support it natively. I wanted to be able to use [$\rm\TeX$](https://en.wikibooks.org/wiki/LaTeX/Mathematics).

**Runs in a Browser**. Since Markdown generates HTML and a lot of the links will point to external web sites, might as well use the browser as the UI for the program too.

CWiki was written as a learning experience and to develop a tool I would want to use.

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
* [url](https://github.com/cemerick/url) is used for manipulating URLs.
* [Compojure](https://github.com/weavejester/compojure) is used for routing.
* [Hiccup](https://github.com/weavejester/hiccup) is used for "lispy" creation of HTML.
* [Ring](https://github.com/ring-clojure/ring) is the web applications library.
* [sqlite-jdbc](https://bitbucket.org/xerial/sqlite-jdbc/overview) is used for "lispy" access to the sqlite (see bellow) database program.

### Everything Else ###

#### Database ####

[SQLite](https://sqlite.org/) is used for the database functions of the wiki. It is perhaps not as "heavy duty" as some other possibilities. However, it has the tremendous benefit that it requires no administration by the user.

SQLite is reputed not to scale well to truly huge databases. I don't know where that crossover occurs for something like this wiki. (I know that [MediaWiki](https://www.mediawiki.org/wiki/MediaWiki) can use SQLite as it's database.) If it looks like it will become an issue for a moderately sized wiki, the database may change in the future.

#### Editor ####

#### CSS ####

At this point, the CSS used is just plain ol' [CSS3](https://www.w3schools.com/css/css3_intro.asp). I strongly considered using [SCSS](http://sass-lang.com/), but did not want the additional dependency on [Ruby](https://www.ruby-lang.org/en/) to build the project. This may change in the future.

You can use the CSS file to re-style CWiki to your liking.

## Tests ##

## Security ##

There really isn't any. 

* Don't put anything in CWiki that you wouldn't want someone else to see. 
* Don't put CWiki on a remote server. It could be hacked easily to steal your information or post someone else's.

Authentication is session based requiring a user name and password.

Authorization is home-grown and based on the roles users have been assigned. See [[About Roles]] for more information.

## How Pages Get Rendered ##

The pages in CWiki are a mashup of [Markdown](https://daringfireball.net/projects/markdown/syntax), [[WikiLinks]], and [[About TeX|$\rm\TeX$]]. No single parser/HTML generator handles all of those pieces. So rendering a page happens in several stages.

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

However, the strategy I have been using has just been to find the wikilink markers, "\[\[" and "\]\]", use the contents between the markers to build a link, and replace the contents with the link.

Doing it that way causes examples of wikilinks embedded in a quoted section to be replaced anyway. Need to handle it differently. Probably need to actually parse the string rather than using a find and replace type approach.