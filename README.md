# CWiki

This is a simple personal wiki application. You use your browser to view and edit the content. It runs on you local machine. You can link to external sites on the internet, but the content you enter stays on your machine.

## Features

* Written in Clojure
* Uses the [H2](http://h2database.com/html/main.html) database to store your information locally without requiring any administration on your part.
* Uses [Markdown](https://daringfireball.net/projects/markdown/syntax) to write and markup pages in the wiki.
* Uses [MathJax](https://www.mathjax.org/) to produce nicely formatted mathematics. (Requires an active link to the internet at the moment.)

## Prerequisites

Java 8 or later must be installed to do development or run the program.

To build the program, you will need [Leiningen](https://github.com/technomancy/leiningen) 1.7.0 or above installed. Just run `lein run` from the main project directory to start the program. Leiningen will take care of downloading all the dependencies for you.

## Running

To create a stand-alone program, execute `lein uberjar`. 

After the build process is finished, you can run the program from the command line with

```
java -jar target\cwiki-0.0.8-SNAPSHOT-standalone.jar
```

Your version number may be different.

 When run for the very first time, there will be a pause of several seconds as the database containing the wiki content is created and initialized. The program will start a local web server and start up your default web browser to display the initial content of the wiki.

## License

This software is licensed under the BSD Simplified 2-Clause License. See the `LICENSE.txt` file.

Copyright © 2017-2018 David D. Clark
