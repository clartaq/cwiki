---
author: CWiki
title: Links Primer
date: 2017-10-24T08:55:54.000-04:00
modified: 2018-06-18T17:49:18.142-04:00
tags:
  - about
  - help
  - links
---

​
This is a page that details how to create and use links in the wiki.

## HTML Links ##

The use of HTML links in Markdown (like CWiki) has already been well described. See [this](https://daringfireball.net/projects/markdown/syntax#link).

## Wikilinks ##

The wikilinks used in CWiki are modeled after those in [MediaWiki](https://www.mediawiki.org/wiki/MediaWiki). You can surround a page name with double square brackets like this "\[\[" and this "\]\]" and CWiki will create an internal link to the page.

That is, this "\[\[Front Page\]\]" becomes [[Front Page]].

Sometimes you may not want to use the page name in your text. You can show a link that displays something different than the page name. To do that, you start with the same set of double square brackets and page name, but follow the page name with a pipe character ("|") and the text you want to display.

For example, this "\[\[Front Page|this text\]\]" becomes [[Front Page|this text]].

This can also be handy if you want to use the existing page name but not the same capitalization.

This "\[\[Front Page|front page\]\]" becomes a reference to the [[Front Page|front page]].

At some point in the future, CWiki may also support "namespaces" and other things, but that is a ways off.

**Note**: At present, wikilinks cannot be included in code examples. The wikilink resolver in the program does not recognize that wikilinks included between backquotes ("\`"), `<code></code>`, and `<pre></pre>` tags should not be converted. Something for the future.