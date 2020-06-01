---
author: CWiki

title: Roadmap

date: 2018-09-20T11:11:38.543-04:00
modified: 2020-05-31T16:49:32.359-04:00
tags:
  - process

  - technical note

  - cwiki

  - editor

  - enhancements

  - architecture

---

This project roadmap is an ever-evolving list of things to be changed and improved. Topic​s will appear and disappear from this page as development continues.

These topics fall into the following categories:

* [Enhancements](#enhancements). These are ideas for improving the way things work.
   - [Editor](#editor). The editor gets its section because there are so many improvements that could be made.
   - [Other](#other). Other miscellaneous improvements.
* [Architecture](#architecture). These are problems with the structure of the code and project that should be investigated and remedied.
* [Process](#process). Changes to the way things get done that are more systematic, informative and less error-prone.

**Note**: Bugs are not discussed here. Bugs will be fixed as a matter of course as they are discovered and time permits. See the [issues](https://bitbucket.org/David_Clark/cwiki/issues?status=new&status=open) section of the project repository.

## Enhancements <a name="enhancements"></a> ##

### Editor <a name="editor"></a> ###

* Add more authoring tools like reading time, reading level, character count, word count, sentence count, and so on.

* Synchronize scrolling between the editing and preview panes of the editor.

* Automatic continuation of lists. It sure would be nice just to hit return and have the ​bullet marker show up.

* Keyboard shortcuts. Making the editor more similar to existing external editors could reduce the cognitive dissonance of using the internal editor.

* It sure would be nice if the tags interface during editing provided some ​autocompletion or hints so you could see which tags already exist. (Is it "bug" or "bugs" for this type of post?)

### Other <a name="other"></a> ###

* Create a function that will update the existing database with new versions of the initial pages. Will not overwrite newer, edited versions of the initial pages, like an edited "About" page.

* At some point, we are going to have to stop initializing the database from a bunch of Markdown files and add a pre-built database to the repository. 

* Expand the user options. So many aspects of the program could be user-configured.

* There should be better separation between the stuff required for development (dependencies, plugins, middleware) and the material needed in a production build. Might even cut down on the uberjar size.

## Architecture <a name="architecture"></a> ##

* The CSS needs some serious reorganization. Maybe we should look at SCCS again, especially since we are trying to keep CSS for the editor synchronized​ with the appearance of the server.

## Process <a name="process"></a> ##

* Develop and use a release checklist.