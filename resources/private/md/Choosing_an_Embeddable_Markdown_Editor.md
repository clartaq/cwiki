---
title: Choosing an Embeddable Markdown Editor
author: CWiki
date: 02/24/2018 03:08:39 PM
updated: 2018-05-05T10:27:40.191829-04:00
tags:
  - technical note
  - Markdown
  - cwiki
---

Since it is so involved with writing and manipulating text with Markdown
formatting, CWiki should probably have an embedded Markdown editor.

Back in the early days of developing CWiki, I used [MarkdownPad](http://www.markdownpad.com), a Windows-only editor that was quite capable and pleasant to use. It was closed-source and seldom updated, but it did what I needed and was not too expensive.

Then I changed my development system to a nice new Mac. I thought there would be a huge number of choices. And there were. But none of them met my needs. I eventually settled on [Sublime Text](https://www.sublimetext.com) with a few good plugins designed to handle Markdown. Very nice and pleasant to use. But no exemplary live preview. (The [MarkdownLivePreview](https://github.com/math2001/MarkdownLivePreview) sort of works, but is flickery and does not wrap long lines.) Not a deal breaker, but not perfect either.

[AsciidocFX](https://github.com/asciidocfx/AsciidocFX#install-on-mac) is another alternative. Written in JavaFX, it is very flexible. It is open source, has a live preview, _etc_. It's a bit heavyweight though. And not embeddable.

There are some embeddable alternatives too.

[writing](https://github.com/josephernest/writing) by Joseph Ernest has just about everything I want.

- It's embeddable as demonstrated by the demo.
- It has very good performance.
- It's open source.
- You can use it offline.
- It's based on good technology ([Pagedown](https://code.google.com/archive/p/pagedown/), [Pagedown Extra](https://github.com/jmcmanus/pagedown-extra), and [MathJax](https://www.mathjax.org).)
- There are essentially no dependencies.

There are some issues too.

- The CSS is incomprehensible to me.
- It's JavaScript.

Another option to follow was the [Minimalist Online Markdown Editor](https://github.com/pioul/Minimalist-Online-Markdown-Editor/tree/master) by Philippe Masset.

I also looked at Jared Reich's [Pell](https://github.com/jaredreich/pell) editor on GitHub and was blown away by how much could be done with such a small program.

In fact, I took his approach and translated it into ClojureScript, producing [Clich](https://bitbucket.org/David_Clark/clich) is a very simple, small, in-browser [Rich Text](https://en.wikipedia.org/wiki/Rich_Text_Format) Editor written in [ClojureScript](https://clojurescript.org).

But Pell and Clich are Rich Text Editors, not Markdown. For an extremely small Markdown editor with live preview, have a look at Carmen La's [Reagent Markdown Editor](http://carmen.la/blog/2015-06-23-reagent-live-markdown-editor/).

It is 

* written in ClojureScript
* has a nice tutorial article (linked above) that explains it's construction
* embeddable
* using the marked Markdown parser may make using wikilinks possible.
* compatible with [Grammarly](https://www.grammarly.com/).

It also has some problems to overcome for my use.

* It has no facilities for moving text in and out of the editor.
* Changing the CSS to become compatible with the rest of CWiki might be non-trivial.
* Need to synchronize scrollbars in the editor and preview may not be trivial either.
* Moving data across the server/client barrier might be hard or slow.

So these are all things to investigate.
