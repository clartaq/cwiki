---
author: CWiki
title: Other Wiki Software
date: 2017-10-10T13:20:24.000-04:00
modified: 2018-12-05T17:44:34.237-05:00
tags:
  - alternatives
  - note-taking software
  - wiki software
---

Here are some other wikis (and note-taking apps and personal knowledge bases) that I find interesting. Some of these are quite mature and very polished. Each has an opinionated approach to information management that works very smoothly when used as intended. Of course, each is missing something that I want. Otherwise, I would not have found it necessary to write CWiki.

These wiki programs are all open source and under active development, as this is written (Autumn 2017). I encourage you to visit the home page of each of these projects and see what you think.

## TiddlyWiki ##

I have used [TiddlyWiki](http://tiddlywiki.com/) for many years, over a decade. It is easy to use and very reliable.

### Features ###

- Written in JavaScript. The "classic" version is a single file.
- Inherently cross-platform.
- [More](http://tiddlywiki.com/#Features)

### Pros ###

- Stores data in a human-readable format.
- Comfortable interface.
- Nice WYSIWYG editor.
- Very flexible and customizable.
- There are tons of excellent documentation.

### Cons ###

- Can get very slow with an extensive collection of data.
- It seems like the user community is shrinking.

## WikidPad ##

[WikidPad](http://wikidpad.sourceforge.net/) is a stand-alone desktop application 
that lets you organize notes and random knowledge in a wiki-like structure.
See the Wikipedia article [here](https://en.wikipedia.org/wiki/WikidPad).

### Features ###

- Written in Python.
- Standalone program. It doesn't need a browser.
- Cross-platform.
- You can organize content in a tree structure.

### Pros ###

- Data is stored in plain text.

### Cons ###

- Frankly, it's ugly.
- The editor is neither plain text or WYSIWYG. It is distracting. It makes things difficult to read. And there is no page format just for reading without the clutter.
- Following links is un-intuitive.

## Trilium Notes ##

[Trilium Notes](https://github.com/zadam/trilium) is a note-taking application intended to help you build a personal knowledge base.

What I find particularly interesting is that it can organize your notes hierarchically with one note appearing at more than one place in the hierarchy.

### Features ###

Lots of interesting features. You can see a list at the [repository site](https://github.com/zadam/trilium).

- Scriptable
- Hierarchical
- Evernote and Markdown import.
- Several different types of notes: text, code, _etc.

### Pros ###

- Hierarchical notes

### Cons ###

- It's an [Electron](https://electronjs.org)-based application written in JavaScript.
- The UI is attractive, but a bit "busy."

## Mindforger ##

[Mindforger](https://github.com/dvorka/mindforger) bills itself as a "Thinking Notebook & Markdown IDE".

### Features ###

- Makes associations as you read and write.

### Pros ###

- Unknown

### Cons ###

- Unknown

## CherryTree ##

[CherryTree](http://www.giuspen.com/cherrytree/) organizes data in a tree. It is somewhat similar to wikidPad, described above. It is much more attractive. It has been under development and being refined since 2009.

### Features ###

- Written in Python.
- It has code execution features.
- [More](http://www.giuspen.com/cherrytree/#features) from the program web page.

### Pros ###

- There is of internationalization.
- Attractive interface.
- Hierarchical note-taking.
- It has a lovely, if somewhat dated, user manual written by a third party.

### Cons ###

- It's open source, but you can only download the source as a `tar.xz` file. It would be nice if it were on a public repository site where you could browse the source.
- It is not clear that vast knowledge stores can be effectively organized in a visual tree.

## TreeSheets ##

[TreeSheets](http://strlen.com/treesheets/) takes a somewhat different approach to organizing your data. It is free form in that it lets you arrange your data freely on a page, moving tables and text blocks wherever you like. Any number of "sheets" can be created and open at one time. Each can be arranged as you want in two dimensions.

### Features ###

- Written in C++.
- Sections of a sheet can be magnified or shrunk, as desired.

### Pros ###

- It combines many of the features of a spreadsheet, mind mapper, outliner, and text editor.
 
### Cons ###

- A zillion more keystroke commands to memorize.
- The build process is not simple.

## MediaWiki ##

[MediaWiki](https://www.mediawiki.org/wiki/MediaWiki) is the 800-pound gorilla of wikis. This is mostly because it is so well-known, being the wiki upon which [Wikipedia](https://www.wikipedia.org/) is based. I like it a lot -- some of the features of CWiki are designed to imitate MediaWiki -- but it is overkill for what I want.

### Features ###

- Written in PHP.
- Very robust.
- It supports version control of pages.

### Pros ###

- There are incredible amounts of documentation.
- Very flexible and extensible.

### Cons ###

- Installation can be troublesome. I never managed to get it installed and working on a local Windows machine. Eventually got it working on a remote server.
- All of the documentation is not necessarily consistent or up to date.
- A local install requires database administration.

## XWiki ##

[XWiki](http://www.xwiki.org/xwiki/bin/view/Main/WebHome) is a wiki designed for enterprise use.

### Features ###

- Written in Java
- It supports version control of pages.
- Includes a wiki, blog, file manager, forums, extensions, ...

### Pros ###

- Lots of features relevant to enterprise users.
- Lots of export formats.

### Cons ###

- Huge installation.
- Administering an installation is complicated in my opinion.

## Boostnote ##

[Boostnote](https://boostnote.io/#community) bills itself as a "...note-taking app for developers..." and is quite nice.

###  Features ###

- Markdown, math, code listings.
- Very active development, large community.

### Pros ###

- Uses the [Katex](https://github.com/Khan/KaTeX) parser/renderer for mathematics, which can be hosted locally easily.
- Supports different UI themes.

### Cons ###

- Written in JavaScript using the huge [Electron](https://electronjs.org/) ecosystem.
- Has some problems with $\rm\TeX$, like not being able to render $\rm\TeX$ correctly. (It uses the Katex parser/renderer.)
- Not really a wiki. It's a note-taking app. There is no support for within-wiki linking that I can find.

## Joplin ##

[Joplin](https://joplin.cozic.net) is another note-taking app, but with very extensive features. In fact, if it only managed to handle wiki links, I probably would not have written CWiki.

### Features ###

* Cross-platform, including mobile and desktop apps.
* Can use several cloud storage platforms for synchronization, like [NextCloud](https://nextcloud.com).
* Plenty of localizations.
* Very good import of Evernote notes.
* Content can be divided among any number of notebooks and sub-notebooks.
* Notes can be encrypted.
* It is possible to create links to other notes, even though it is somewhat clumsy.

### Pros ###

* Markdown, $\rm\TeX$, and To-Do support with checkboxes.
* Very active and responsive development.

### Cons ###

* Another Electron app.
* Not capable of wiki linking within its database of notes.
* Doesn't seem to support the creation of Tables of Contents in Markdown in the usual way.
* It doesn't seem possible to change the layout of the application's main page. When notes have long names, they can be truncated in the column that lists the notes in the open notebook.
* It does not seem possible to change the layout of the list of notebooks. For example, you cannot move one notebook higher on the list.
* It would be nice if the application retained a "history" of notes visited such that you could move backward and forward through the list of notes already viewed​ like you can with a browser.

Each of these alternative applications has pluses and minuses. Obviously, or I wouldn't have written CWiki.