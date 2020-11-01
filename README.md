# CWiki ##

CWiki is a simple personal wiki application. You use your browser to view and edit the content. It runs on your local machine. You can link to external sites on the internet, but the content you create stays on your computer.

## Note ##

CWiki was moved from BitBucket using Mercurial to Github using git on 5 February, 2020. As a result, all history on tracked issues before that time was lost.

## Warning ##

CWiki is alpha-quality software. It does useful things, but many planned features are not present. (The program will usually tell you so if you try to do something that it cannot accomplish yet. Or it might just crash.) Probably full of bugs too. See the "[Issues](https://github.com/clartaq/cwiki/issues)" page in the project repository.

## Features ##

* The pages you write can **link between pages** within the wiki, just like any other wiki. They can also **link to external websites**.
* You write pages in **[Markdown](https://daringfireball.net/projects/markdown/syntax)** to provide nice formatting with minimal fuss.
* Writing **nicely formatted mathematics** is handled using [MathJax](https://www.mathjax.org/). (Requires an active link to the internet at the moment.)
* You can **search** within your wiki. 
   * Special pages show all page titles, authors, and tags with clickable links.
   * You can also do full-text search on all of the content in the wiki.
* You can associate **tags** with each page.
* Includes a **straightforward editor** with live preview.
* The program **keeps track** of when a page was created, and the last time it was modified.
* The program stores your information in a **local database** that does not require any administration on your part.
* The program contains **numerous "seed" pages** that include help and examples of how the program can be used.
* You can **alter and relink** most of the seed pages to better suit your needs. Or just delete them altogether. (Some "special" pages cannot be changed or deleted.)
* Your content can be **exported** at any time as Markdown with [YAML](http://yaml.org) front-matter. There is **no lock-in**.
* Although intended as a personal wiki, you can add **other users** with varying levels of permission to read, add to, or make changes in the wiki.
* Written in **Clojure and ClojureScript**, a very nice language that is fun to learn and use.
* The program is so **simple**, even I can understand it -- most of the time.

## Running the Program ##

### Prerequisites ###

Java 11 (AdoptOpenJDK 11) for development and testing. It should compile and run under version 8, but this is not tested. 

### Run a Prebuilt Jar ###

**NOTE**: This section is out of date. Since switching to git and GitHub, there are no longer prebuilt jars in the repository. (They were handled separately on BitBucket.) I'm leaving this section here to remind me to fix that soon.

The easiest way to try things out is to download a [prebuilt jar](https://bitbucket.org/David_Clark/cwiki/downloads/) file from the repository. This will only work with CWiki version 0.0.11 or later.

1. Copy the jar to whatever directory you would like. I use `~/cwiki`.
2. In a terminal, change to the directory where the jar is located: `cd ~/cwiki`.
3. Start the program by running the jar: `java -jar cwiki.jar`.

The first time the program starts up in a particular location, it will take a little time to build the initial database of pages and indices. Then it will start your default browser. Log in the first time as "admin" with the password "admin."

The seed database has pages describing how to use CWiki and other stuff. Explore.

### Running in a Development Environment ###

If you have a copy of the repository, you can run from the source code as described next.

#### From the REPL ####

You will need the [Leiningen](https://github.com/technomancy/leiningen) build tool, version 1.7.0 or above, installed. From a terminal in the project directory, start a Leiningen REPL. Leiningen will take care of downloading all the dependencies for you. The REPL will begin in the `cwiki.repl` namespace, which has some functions to start and stop the app quickly.

To start the program, use:

`cwiki.repl=> (start)`

To stop the program, use:

`cwiki.repl=> (stop)`

I ​run this way in IntelliJ using Cursive to start a REPL.

**Note on Using the Cursive REPL**: After a `lein clean` or other operation that deletes the target files, the Cursive REPL will attempt to re-compile the `wiki-attributes.clj` file. You will need to build the extension manually first. Use something like `lein cljsbuild once min` from the command line. Then you should be able to run the Cursive REPL. (The `cljsbuild` task incudes a `prep` task that builds the extension correctly.) Things will proceed as normal.

Attempting to run from the `lein repl` command seems to work just fine.

#### From a Terminal ####

From a terminal in the project directory, type

`lein cljsbuild once min && lein run`

to run, possibly with compilation first.

Use

`lein start-prod`

to clean, recompile, and run the minimized (production) build. 

## Creating and Running a Standalone Program ##

To create a stand-alone program, execute `lein uberjar`. 

After the build process finishes, you can run the program from the command line with

```
java -jar target/cwiki.jar
```

## Running the First Time ##
 When running for the very first time, there will be a pause of several seconds as the database containing the initial wiki content is created. The program will start a local web server and start up your default web browser to display the initial content of the wiki.

 Since the program has never run before, you will be asked to sign in as the "admin" user. Just follow the instructions on the screen.

## Running Tests ##

### Clojure ###

To run tests on the Clojure code, open a terminal in the project directory and enter:

`lein test`

### ClojureScript ###

You can run tests on the ClojureScript code by opening a terminal and typing:

`clj -M:fig:test`

(I switched to using the Clojure command line interface (CLI) and `deps` ([guide](https://clojure.org/guides/deps_and_cli)) along with [Figwheel Main](https://figwheel.org) because of the painful mess that is ClojureScript testing with Leiningen. It is no fault of the Leiningen tool. It's just that the stack of tools is constantly changing and losing support or moving on to next "shiny new thing". There is much less setup this way and it just works.

Yes, it's pretty weird to use both figwheel "classic" and Figwheel Main in the same project.)

## License ##

This software is licensed under the BSD Simplified 2-Clause License. See the [LICENSE.txt](https://bitbucket.org/David_Clark/cwiki/src/default/LICENSE.txt) file.

Copyright © 2017-2020 David D. Clark