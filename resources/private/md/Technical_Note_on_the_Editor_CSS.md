---
author: david
title: Technical Note on the Editor CSS
date: 2018-11-24T12:27:35.123-05:00
modified: 2018-11-24T12:36:52.324-05:00
tags:
  - CSS
  - cwiki
  - editor
  - technical note
---

Initially, it was easy to get the CSS to do what I wanted. All I had to do was view the sidebar, the main article, a header, and (at that time) a footer. 

After adding the ClojureScript editor, it became more of a hassle. The problems largely consisted of having the various containers fill their space and preventing their contents from overflowing the container. At first the problems with overflowing beyond container boundaries was related to vertical overflow. However, the "pathological" Markdown help page, cribbed from the [Markdown Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet), also caused problems with overflow or expansion along the horizontal axis.

These two things were solved by settings for the vertical height (`height: 100%;`) and overflow (`overflow: hidden;`) and making sure _every ancestor_ of the problematic component had the correct setting.

Although everything seems to be working as this is written (24 Nov 2018, 12:35:54 pm, according to my new timestamp shortcut), there is still a problem with the "Done" button at the bottom of the editor being pushed halfway off the screen by something.