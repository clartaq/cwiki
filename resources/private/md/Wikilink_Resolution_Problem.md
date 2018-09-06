---
author: david
title: Wikilink Resolution Problem
date: 2018-09-01T11:04:58.627-04:00
modified: 2018-09-06T17:20:54.112-04:00
tags:
  - bugs
  - debugging
  - technical note
  - wikilinks
---

Having a problem switching from the existing wikilink resolver in CWiki to one based on an extension to flexmark.

It doesn't seem to be due to a difference in the resolver that I can find -- they both produce results that are identical on a character-by-character basis.

## The Problem ##

After some of the "All ..." pages, clicking on the links produced fails because the URL is malformed. Things work Ok on the [[All Pages]] links, but the links on the [[All Users]] and [All Tags]] are messed up. For example, "All Users"->"CWiki"->"About" links produces an URL:

`localhost:1350/CWiki/About`

As opposed to "All Pages"->"About," which produces:

`localhost:1350/About`

as expected and desired.

Likewise, "All Tags"->"help"->"About Tags" points to:

`localhost:1350/help/About%20Tags`

rather than the desired

`localhost:1350/About%20Tags`

that would ​work.

Not sure what is happening, but the request that loads the page has a URI​ field with the undesired section.