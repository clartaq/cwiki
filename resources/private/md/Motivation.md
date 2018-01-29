---
title: The Motivation for Writing CWiki
author: CWiki
date: 1/7/2018 10:24:01 AM 
updated: 1/15/2018 3:27:13 PM         
tags:
  - technical note
  - motivation
---

You might rightly ask "Why does the world need another wiki program?" Well, CWiki probably isn't vital to the survival of civilization as we know it. But I was motivated by a few things.

First, I've always been fascinated by wiki software (and blog software, and editors, and news aggregators). I've been using wikis in one form or another for years, decades actually. I wanted to see what it would be like to write my own just for me. No schedule, nobody else's set of features and requirements, just whatever I wanted.

**Written in Clojure**. I know the implementation language isn't too important, but I like [Clojure](https://clojure.org/). No Javascript. Even though it is very popular right now, Javascript is just ugly. I don't like it at all.

**Markdown with Extensions**. I like Markdown too. But this is a wiki. It has to support wiki links as well as HTML links.

**Syntax-Highlighted Code Listings**. I put a lot of code listings in some of the things I write. Has to look nice and be easy to do.

**Mathematics**. I want to be able to write content containing mathematics. Markdown does not support it natively. I wanted to be able to use [$\rm\TeX$](https://en.wikibooks.org/wiki/LaTeX/Mathematics).

**Runs in a Browser**. Since Markdown generates HTML and a lot of the links will point to external web sites, might as well use the browser as the UI for the program too.

CWiki was written as a learning experience and to develop a tool I would want to use.

