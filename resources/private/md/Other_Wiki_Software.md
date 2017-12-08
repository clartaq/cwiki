Here are some other wikis that I find interesting. Some of these are quite mature and very polished. Each has an opinionated approach to information management that works very smoothly when used as intended. Of course, each is missing something that I want. Otherwise, I would not have found it necessary to write CWiki.

These wiki programs are all open source and under active development as this is written (Autumn 2017). I encourage you to visit the home page of each of these projects and see what you think.

## TiddlyWiki ##

I have used [TiddlyWiki](http://tiddlywiki.com/) for many years, over a decade. It is easy to use and very reliable.

### Features ###

- Written in JavaScript. The "classic" version is a single file.
- Inherently cross-platform.
- [More](http://tiddlywiki.com/#Features)

### Pros ###

- Data is stored in a human-readable format.
- Comfortable interface.
- Nice WYSIWIG editor.
- Very flexible and customizable.
- Tons of very good documentation.

### Cons ###

- Can get very slow with a large collection of data.
- Seems like the user community is shrinking.

## WikidPad ##

[WikidPad](http://wikidpad.sourceforge.net/) is a stand-alone desktop application 
that lets you organize notes and random knowledge in a wiki-like structure.
See the Wikipedia article [here](https://en.wikipedia.org/wiki/WikidPad).

### Features ###

- Written in Python.
- Standalone program. Doesn't need a browser.
- Cross-platform.
- Content can be arranged in a tree.

### Pros ###

- Data is stored in plain text.

### Cons ###

- Frankly, it's kind of ugly.
- The editor is neither plain text or wysiwig. It is distracting. It actually makes things difficult to read. And there is no page format just for reading without the clutter.
- Following links is un-intuitive.

## CherryTree ##

[CherryTree](http://www.giuspen.com/cherrytree/) organizes data in a tree. It is somewhat similar to wikidPad, described above. It is much more attractive. It has been under development and being refined since 2009.

### Features ###

- Written in Python.
- Has code execution features.
- [More](http://www.giuspen.com/cherrytree/#features) from the program web page.

### Pros ###

- Lots of internationalization.
- Attractive interface.
- Hierarchical note-taking.
- It has a very nice, if somewhat dated, manual written by a third party.

### Cons ###

- It's open source, but you can only download the source as a `tar.xz` file. It would be nice if it were on a public repository site where you could browse the source.
- It is not clear that truly large knowledge stores can be effectively organized in a visual tree.

## TreeSheets ##

[TreeSheets](http://strlen.com/treesheets/) takes a somewhat different approach to organizing your data. It is free form in that it lets you arrange you data freely on a page, moving tables and text blocks wherever you like. Any number of "sheets" can be created and open at one time. Each can be arranged as you like in two dimensions.

### Features ###

- Written in C++.
- Sections of a sheet can be magnified or shrunk, as desired.

### Pros ###

- Combines many of the features of a spreadsheet, mind mapper, outliner, and text editor.
 
### Cons ###

- A zillion more keystroke commands to memorize.
- The build process is not too easy.

## MediaWiki ##

[MediaWiki](https://www.mediawiki.org/wiki/MediaWiki) is the 800 pound gorilla of wikis. This is largely due to the fact that it is so well-known, being the wiki upon which [Wikipedia](https://www.wikipedia.org/) is based. I like it a lot -- some of the features of CWiki are designed to imitate MediaWiki -- but it is overkill for what I want.

### Features ###

- Written in PHP.
- Very robust.
- Supports version control of pages.

### Pros ###

- Incredible amounts of documentation.
- Very flexible and extensible.

### Cons ###

- Installation can be troublesome. I never managed to get it installed and working on a local Windows machine. Eventually got it working on a remote server.
- All of the documentation is not necessarily consistent or up to date.
- A local install requires database administration.

## XWiki ##

[XWiki](http://www.xwiki.org/xwiki/bin/view/Main/WebHome) is a wiki designed for enterprise use.

### Features ###

- Written in Java
- Supports version control of pages.
- Includes a wiki, blog, file manager, forums, extensions, ...

### Pros ###

- Lots of features relevant to enterprise users.
- Lots of export formats.

### Cons ###

- Huge installation.
- Complicated administration in my opinion.

Each of these has pluses and minuses. Obviously, or I wouldn't have written CWiki.
