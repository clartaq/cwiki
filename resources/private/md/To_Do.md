---
author: CWiki
title: To Do
date: 2018-06-04T15:38:47.538-04:00
modified: 2018-08-14T14:18:44.888-04:00
tags:
  - architecture
  - bugs
  - cwiki
  - enhancements
  - process
  - technical note
---






​
An ever-evolving list of things to be fixed and improved. These fall into the following categories:

* [Bugs](#bugs). These are things that are flat out wrong and must be fixed.
* [Enhancements](#enhancements). These are ideas for improving the way things work.
* [Architecture](#architecture). These are problems with the structure of the code and project that should be investigated and remedied.
* [Process](#process). Changes to the way things get done that are more systematic, informative and less error-prone.

### Bugs <a name="bugs"></a>

* There is some sort of version incompatibility in the project dependencies that requires spurious dependency inclusion to get the uberjar target to compile successfully.

* The editor is largely compatible with the Grammarly tool, but the hints provided usually show up in the wrong place. Finding whatever they refer to can be difficult without starting up the Grammarly editor.

*  There is a nasty bug that will crash the program and pollute the database if you try to enter a page title or tag containing an apostrophe. Should also check if other characters cause similar problems.
    * This issue ([#1](https://bitbucket.org/David_Clark/cwiki/issues/1/characters-that-are-invalid-in-sql)) was **resolved** in commit [1de736b](https://bitbucket.org/David_Clark/cwiki/commits/1de736b).

* When editing long pages, like the Technical Notes page, the Save and Cancel buttons sometimes get pushed below the bottom of the view and can't be scrolled into view.
    * Issue [#5](https://bitbucket.org/David_Clark/cwiki/issues/5/save-and-cancel-buttons-can-disappear) created.

* When in the editor, removing a tag by highlighting it and deleting​ it does not always eliminate it.
    * Issue [#6](https://bitbucket.org/David_Clark/cwiki/issues/6/deleting-a-highlighted-tag-does-not-always) created.

* When in the editor, adding a new tag only works if you revise an existing tag or type into the _first_ empty tag field. Typing into one of the other empty tag fields cause a program crash.
    * Issue [#7](https://bitbucket.org/David_Clark/cwiki/issues/7/adding-new-tags-can-crash-the-program) was created.

* After going to the [[All Tags]] page and selecting a particular tag to search for, the program shows a clickable list of all pages with that tag. However, on this list of pages, the header menu shows an "Edit" and "Delete" selection,​ which is something you cannot do on a program-generated page.
    * This issue ([#2](https://bitbucket.org/David_Clark/cwiki/issues/2/program-generated-pages-should-not-be)) was **resolved** in commit [80e6ab0](https://bitbucket.org/David_Clark/cwiki/commits/80e6ab0).

* When you create a new post, the program no longer picks up the name of the currently logged in user as the author of the post.
   * This issue ([#17](https://bitbucket.org/David_Clark/cwiki/issues/17/page-author-not-correctly-assigned-for-new)) was **resolved** in commit [c14bce0](https://bitbucket.org/David_Clark/cwiki/commits/c14bce00feffac5bdd6793c2ed6c5287b6a7f3a3).

* After entering search terms in the "Search" text entry area at the top of the page, pressing the "Tab" button should move the focus to the "Search" button.
     
   This actually seems to be an error in the design of the search area itself. The user should not have to move to or click a button to initiate a search. They should only need to hit the return key. Since a text area will perform a submit when the enter key is tapped, we should just remove the "Search" button.

   * This issue ([#18](https://bitbucket.org/David_Clark/cwiki/issues/18/need-sensible-tab-behavior)) was **resolved** in commit [12ccd43](https://bitbucket.org/David_Clark/cwiki/commits/12ccd4320dd02434f3fa679fd7a57a92cf4d7a71).

* Search should do nothing when given empty search terms. It should not create a search results page with no results.

   * Issue [#19](https://bitbucket.org/David_Clark/cwiki/issues/19/search-should-do-nothing-when-given-empty) was created.

### Enhancements <a name="enhancements"></a>

* Synchronize scrolling between the editing and preview panes of the editor.
    * Issue [#8](https://bitbucket.org/David_Clark/cwiki/issues/8/synchronize-editor-scrollbars) was created.

* Automatic continuation of lists. It sure would be nice just to hit return and have the ​bullet marker show up.
    * Issue [#9](https://bitbucket.org/David_Clark/cwiki/issues/9/automatic-continuation-of-lists-in-the) was created.

* Keyboard shortcuts. Making the editor more similar to existing external editors could reduce the cognitive dissonance of using the internal editor.
    * Issue [#10](https://bitbucket.org/David_Clark/cwiki/issues/10/add-keyboard-shortcuts-to-editor) was created.

* Creation of wikilinks needs to understand in-line code spans, `<code></code>`, and `<pre></pre>` so it doesn't translate examples of wikilinks in those spans. Makes it hard to show examples of wikilinks.
    * Issue [#14](https://bitbucket.org/David_Clark/cwiki/issues/14/markdown-parsers-need-to-understand-wiki) was created.

* Create a function that will update the existing database with new versions of the initial pages. Will not overwrite newer, edited versions of the initial pages, like an edited "About" page.

* Implement within-wiki search.
    * This iIssue ([#13](https://bitbucket.org/David_Clark/cwiki/issues/13/implement-full-text-wiki-search)) was **resolved** in commit [81cfa20](https://bitbucket.org/David_Clark/cwiki/commits/81cfa20).

* Any maintenance tasks like database compression, backup and restore, for example.
    * Issue [#15](https://bitbucket.org/David_Clark/cwiki/issues/15/implement-maintenance-tasks) was created.

* Beautify and style the edit/create pages.

* At some point, we are going to have to stop initializing the database from a bunch of Markdown files and add a pre-built database to the repository. 

* I prefer the styling of inline​ code that used in the editor. It can make things look like buttons. Harmonize the editor preview with the view in the main wiki page.
    * Issue [#12](https://bitbucket.org/David_Clark/cwiki/issues/12/improve-styling-of-inline-code) was created.

* Make the options usable.
    * Issue [#11](https://bitbucket.org/David_Clark/cwiki/issues/11/make-program-options-usable) was created. The initial implementation appeared in commit [6eb603f](https://bitbucket.org/David_Clark/cwiki/commits/6eb603f84c79ff1cbf4c5928059d0830e35df737).

* Just a convenience for me, but since I do a lot of editing of the seed pages, it would be nice to save them to the same location as the seed files used to create a new database rather than my current practice of exporting them, then moving them into place manually.
    * This issue ([#16](https://bitbucket.org/David_Clark/cwiki/issues/16/let-admin-save-seed-pages)) was resolved in commit [72e6644](https://bitbucket.org/David_Clark/cwiki/commits/72e6644b6215ac44713fa56cddd51f497283de6d).

* It sure would be nice if the tags interface during editing provided some ​autocompletion or hints so you could see which tags already exist. (Is it "bug" or "bugs" for this type of post?)

* There should be a better separation between the stuff required for development (dependencies, plugins, middleware) and the stuff required for a production build. Might even cut down on the uberjar size.

### Architecture <a name="architecture"></a>

* The CSS needs some serious reorganization. Maybe we should look at SCCS again, especially since we are trying to keep CSS for the editor synchronized​ with the appearance of the server.

### Process <a name="process"></a>

* Annotate these notes with the commit number that resolves the issue.

* Develop and use a release checklist.
    * Issue [#4](https://bitbucket.org/David_Clark/cwiki/issues/4/develop-a-release-checklist) created.

* Create and maintain a "Changes" file.
    * This issue ([#3](https://bitbucket.org/David_Clark/cwiki/issues/3/should-maintain-a-changelog)) was resolved by adding the CHANGELOG.md file to the repository in commit [6b7b22a](https://bitbucket.org/David_Clark/cwiki/commits/6b7b22a).