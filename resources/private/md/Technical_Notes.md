These are some technical notes about CWiki. If you are only interested in using the wiki, you can ignore this stuff. If you want to know how CWiki works or how to build and modify your own version, the information here might be useful.

## Motivation ##

## Software Dependencies ##

Almost no software is written without dependencies these days -- programs are just too complicated. CWiki is no different. Some useful functionality has already been written by others. Here's a list.

### Java Stuff ###
### Clojure Stuff ###
### Everything Else ###

## Tests ##

## Security ##

There really isn't any. 

* Don't put anything in CWiki that you wouldn't want someone else to see. 
* Don't put CWiki on a remote server. It could be hacked easily to steal your information or post someone else's.

## The CSS ##

You can use the CSS file to re-style CWiki to your liking.

## How Pages Get Rendered ##

The pages in CWiki are a mashup of [Markdown](https://daringfireball.net/projects/markdown/syntax), [[WikiLinks]], and [[About Latex|$\rm\LaTeX$]]. No single parser/HTML generator handles all of those pieces. So rendering a page happens in several stages.

1. First, all of the WikiLinks are located and translated to HTML-style links. These links point to pages within the CWiki database. If there is no such page, the link is displayed in red.
2. The Markdown content, including the translated WikiLinks, are converted to HTML. Since Markdown parsers pass HTML through unaltered, the translated WikiLinks are left intact.
3. Finally, the HTML is passed to MathJax to translate any $\rm\LaTeX$ into something that can be displayed in a web page. The reprsentation is usually common HTML, but you can change that if needed.
4. That big chunk of HTML is plugged into the `<body>` section of a web page containing the header and footer for the page as well as the `<head>` section required for well-formed HTML5
5. That page is then served by a web-server built into CWiki and rendered by your browser. 
