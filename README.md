# CWiki ##

This is a simple personal wiki application. You use your browser to view and edit the content. It runs on your local machine. You can link to external sites on the internet, but the content you create stays on your machine.

## Warning ##

This is pre-alpha software. It does useful things, but many planned features are not present. (The program will usually tell you so if you try to do something that hasn't been implemented yet. Or it might just crash.) Probably full of bugs too.

## Features ##

* Written in Clojure
* You can link between pages, just like any other wiki.
* Uses the [H2](http://h2database.com/html/main.html) database to store your information locally without requiring any administration on your part.
* Since it use H2 as the database, there is no DB administration required.
* Uses [Markdown](https://daringfireball.net/projects/markdown/syntax) to write and markup pages in the wiki.
* Uses [MathJax](https://www.mathjax.org/) to produce nicely formatted mathematics. (Requires an active link to the internet at the moment.)
* Your content can be exported at any time as Markdown with [YAML](http://yaml.org) front-matter. There is no lock-in.
* The program is so simple, even I can understand it -- most of the time.

## Prerequisites ##

Java 8 or later must be installed to do development or run the program.

To build the program, you will need [Leiningen](https://github.com/technomancy/leiningen) 1.7.0 or above installed. Just run `lein run` from the main project directory to start the program. Leiningen will take care of downloading all the dependencies for you.

## Running ##

To create a stand-alone program, execute `lein uberjar`. 

After the build process is finished, you can run the program from the command line with

```
java -jar target\cwiki-0.0.9-SNAPSHOT-standalone.jar
```

Your version number may be different.

 When run for the very first time, there will be a pause of several seconds as the database containing the wiki content is created and initialized. The program will start a local web server and start up your default web browser to display the initial content of the wiki.

## License ##

This software is licensed under the BSD Simplified 2-Clause License. See the `LICENSE.txt` file.

Copyright © 2017-2018 David D. Clark
