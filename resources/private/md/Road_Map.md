---
author: CWiki
title: Road Map
date: 2018-06-04T15:38:47.538-04:00
modified: 2018-09-18T17:45:50.440-04:00
tags:
  - architecture
  - cwiki
  - enhancements
  - process
  - technical note
---



An ever-evolving list of things to be changed and improved. These fall into the following categories:

* [Enhancements](#enhancements). These are ideas for improving the way things work.
* [Architecture](#architecture). These are problems with the structure of the code and project that should be investigated and remedied.
* [Process](#process). Changes to the way things get done that are more systematic, informative and less error-prone.

**Note**: Bugs are not discussed here. See the [issues](https://bitbucket.org/David_Clark/cwiki/issues?status=new&status=open) section of the project repository.

## Enhancements <a name="enhancements"></a> ##

* Synchronize scrolling between the editing and preview panes of the editor.
    * Issue [#8](https://bitbucket.org/David_Clark/cwiki/issues/8/synchronize-editor-scrollbars) was created.

* Automatic continuation of lists. It sure would be nice just to hit return and have the ​bullet marker show up.
    * Issue [#9](https://bitbucket.org/David_Clark/cwiki/issues/9/automatic-continuation-of-lists-in-the) was created.

* Keyboard shortcuts. Making the editor more similar to existing external editors could reduce the cognitive dissonance of using the internal editor.
    * Issue [#10](https://bitbucket.org/David_Clark/cwiki/issues/10/add-keyboard-shortcuts-to-editor) was created.

* Creation of wikilinks needs to understand in-line code spans, `<code></code>`, and `<pre></pre>` so it doesn't translate examples of wikilinks in those spans. Makes it hard to show examples of wikilinks.
    * This issue ([#14](https://bitbucket.org/David_Clark/cwiki/issues/14/markdown-parsers-need-to-understand-wiki)) was **resolved** in commit [bc4eb80](https://bitbucket.org/David_Clark/cwiki/commits/bc4eb803d50bd3e6d1c8a5b9436f25c464a0fb27).

* Create a function that will update the existing database with new versions of the initial pages. Will not overwrite newer, edited versions of the initial pages, like an edited "About" page.

* Implement within-wiki search.
    * This issue ([#13](https://bitbucket.org/David_Clark/cwiki/issues/13/implement-full-text-wiki-search)) was **resolved** in commit [81cfa20](https://bitbucket.org/David_Clark/cwiki/commits/81cfa20).

* Any maintenance tasks like database compression, backup and restore, for example.
    * Issue [#15](https://bitbucket.org/David_Clark/cwiki/issues/15/implement-maintenance-tasks) was created.

* Beautify and style the edit/create pages.

* At some point, we are going to have to stop initializing the database from a bunch of Markdown files and add a pre-built database to the repository. 

* I prefer the styling of inline​ code that used in the editor. It can make things look like buttons. Harmonize the editor preview with the view in the main wiki page.
    * This issue([#12](https://bitbucket.org/David_Clark/cwiki/issues/12/improve-styling-of-inline-code))  was **resolved** [944be76](https://bitbucket.org/David_Clark/cwiki/commits/944be76cb96417b932e3b9520a070286b37f338c).

* Make the options usable.
    * Issue [#11](https://bitbucket.org/David_Clark/cwiki/issues/11/make-program-options-usable) was created. The initial implementation appeared in commit [6eb603f](https://bitbucket.org/David_Clark/cwiki/commits/6eb603f84c79ff1cbf4c5928059d0830e35df737).

* Just a convenience for me, but since I do a lot of editing of the seed pages, it would be nice to save them to the same location as the seed files used to create a new database rather than my current practice of exporting them, then moving them into place manually.
    * This issue ([#16](https://bitbucket.org/David_Clark/cwiki/issues/16/let-admin-save-seed-pages)) was resolved in commit [72e6644](https://bitbucket.org/David_Clark/cwiki/commits/72e6644b6215ac44713fa56cddd51f497283de6d).

* It sure would be nice if the tags interface during editing provided some ​autocompletion or hints so you could see which tags already exist. (Is it "bug" or "bugs" for this type of post?)

* There should be a better separation between the stuff required for development (dependencies, plugins, middleware) and the stuff required for a production build. Might even cut down on the uberjar size.

## Architecture <a name="architecture"></a> ##

* The CSS needs some serious reorganization. Maybe we should look at SCCS again, especially since we are trying to keep CSS for the editor synchronized​ with the appearance of the server.

## Process <a name="process"></a> ##

* Annotate these notes with the commit number that resolves the issue.

* Develop and use a release checklist.
    * Issue [#4](https://bitbucket.org/David_Clark/cwiki/issues/4/develop-a-release-checklist) created.

* Create and maintain a "Changes" file.
    * This issue ([#3](https://bitbucket.org/David_Clark/cwiki/issues/3/should-maintain-a-changelog)) was resolved by adding the CHANGELOG.md file to the repository in commit [6b7b22a](https://bitbucket.org/David_Clark/cwiki/commits/6b7b22a).