---
author: david
title: Technical Note on the Editor CSS
date: 2018-11-24T12:27:35.123-05:00
modified: 2018-11-29T16:32:17.829-05:00
tags:
  - CSS
  - cwiki
  - editor
  - technical note
---

Initially, it was easy to get the CSS to do what I wanted. All I had to do was view the sidebar, the main article, a header, and (at that time) a footer. 

After adding the ClojureScript editor, it became more of a hassle. The problems mainly consisted of having the various containers fill their space and preventing their contents from overflowing the container. At first, the issues with overflowing beyond container boundaries related to vertical overflow. However, the "pathological" Markdown help page, cribbed from the [Markdown Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet), also caused problems with overflow or expansion along the horizontal axis.

These two things were solved by settings for the vertical height (`height: 100%;`) and overflow (`overflow: hidden;`) and making sure _every ancestor_ of the problematic component had the correct setting.

The changes above also made the pages less "janky,"​ that is, scrolling to the end of a page doesn't pull down the page header like it used to. Also, fewer scrollbars show up when scrolling things.

Although everything seems to be working as this is written (24 Nov 2018, 12:35:54 pm, according to my new timestamp shortcut), there is still a problem with the "Done" button at the bottom of the editor being pushed halfway off the screen by something.

Also, there are two anomalies in the preview pane. First, there is no scrollbar when scrolling the preview. Second, the right padding value does not appear to be respected; things extend all the way to the edge (but not beyond.)

##### Update: 29 Nov 2018, 04:27:38 pm #####

Finally figured this out. The CSS required that `box-sizing: border-box;` be applied to several elements. After doing that, it seems like everything is working as intended now.

Man, I hope this is the final time I have to go through this.