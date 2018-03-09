---
title: Choosing an Embeddable Markdown Editor
author: CWiki
date: 02/24/2018 03:08:39 PM
updated: 03/08/2018 12:07:53 PM
tags:
  - technical note
  - Markdown
  - cwiki
---

Since it is so involved with writing and manipulating text with Markdown formating, CWiki should probably have an embedded Markdown editor.

[writing](https://github.com/josephernest/writing) by Josheph Ernest has just about everything I want.

- It's embeddabe as demonstrated by the demo.
- It has very good performance.
- It's open source.
- You can use if offline.
- It's based on good technology ([Pagedown](https://code.google.com/archive/p/pagedown/), [Pagedown Extra](https://github.com/jmcmanus/pagedown-extra), and [MathJax](https://www.mathjax.org).)
- There are essentially no dependencies.

There are some issues too.

- The CSS is incomprehensible to me.
- It's Javascript.

So, what to do?

Why, rewrite it all in ClojureScript of course.

Another option to follow was the [Minimalist Online Markdown Editor](https://github.com/pioul/Minimalist-Online-Markdown-Editor/tree/master) by Philippe Masset.