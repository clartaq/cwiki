---
author: Unknown
title: To Do
date: 2018-06-04T15:38:47.538-04:00
modified: 2018-06-04T16:23:00.439-04:00
tags:
  - architecture
  - bugs
  - cwiki
  - enhancements
  - process
  - technical note
---

​An ever-evolving list of things to be fixed and improved. These fall into the following categories:

* [Bugs](#bugs). These are things that are flat out wrong and must be fixed.
* [Enhancements](#enhancements). These are ideas for improving the way things work.
* [Architecture](#architecture). These are problems with the structure of the code and project that should be investigated and remedied.
* [Process](#process). Changes to the way things get done that are more systematic, informative and less error-prone.

### Bugs <a name="bugs"></a>

* There is a nasty bug that will crash the program and pollute the database if you try to enter a page title or tag containing an apostrophe. Should also check if other characters cause similar problems.
* When editing long pages, like the Technical Notes page, the Save and Cancel buttons sometimes get pushed below the bottom of the view and can't be scrolled into view.
* When in the editor, removing a tag by highlighting it and deleting​ it does not always eliminate it.

### Enhancements <a name="enhancements"></a>

* Synchronize scrolling between the editing and preview panes of the editor.
* Automatic continuation of lists. It sure would be nice just to hit return and have the ​bullet marker show up.
* Keyboard shortcuts. Making the editor more similar to existing external editors could reduce the cognitive dissonance of using the internal editor.
* Creation of wikilinks needs to understand in-line code spans, `<code></code>`, and `<pre></pre>` so it doesn't translate examples of wikilinks in those spans. Makes it hard to show examples of wikilinks.
* Create a function that will update the existing database with new versions of the initial pages. Will not overwrite newer, edited versions of the initial pages, like an edited "About" page.
* Implement within-wiki search.
* Any maintenance tasks like database compression, backup and restore, for example.
* Beautify and style the edit/create pages.
* At some point, we are going to have to stop initializing the database from a bunch of Markdown files and add a pre-built database to the repository. Need to get the tools
* I prefer the styling of inline​ code that used in the editor. It can make things look like buttons. Harmonize the editor preview with the view in the main wiki page.

### Architecture <a name="architecture"></a>

* The CSS needs some serious reorganization. Maybe we should look at SCCS again, especially since we are trying to keep CSS for the editor synchronized​ with the appearance of the server.

### Process <a name="process"></a>

* Annotate these notes with the commit number that fixes or addresses the issue.