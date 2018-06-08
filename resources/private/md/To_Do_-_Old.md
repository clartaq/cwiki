---
author: CWiki
title: To Do - Old
date: 2017-10-24T09:17:04.000-04:00
modified: 2018-06-04T16:25:01.595-04:00
tags:
  - cwiki
  - history
  - releases
  - technical note
---

**This list is no longer maintained.** It is kept here for historical reasons only.

This is an ever-evolving list of things that need doing.

* When editing really long pages, like the [[Technical Notes]] page, the `Save` and `Cancel` buttons sometimes get pushed below the bottom of the view and can't be scrolled into view.
* There is a nasty bug that will crash the program and pollute the database if you try to enter a page title or tag containing an apostrophe. Should also check if other characters cause similar problems.
* Creation of wikilinks needs to understand in-line code spans, `<code></code>`, and `<pre></pre>` so it doesn't translate examples of wikilinks in those spans. Makes it hard to show examples of wikilinks.
* Create a function that will update the existing database new versions of the initial pages. Will not overwrite newer, edited versions of the initial pages, like an edited "About" page.
* Implement within-wiki search.
* Any maintenance tasks like database compression, backup and restore, for example.
* Beautify and style the edit/create pages.
* At some point, we are going to have to stop creating the initial database from a bunch of Markdown files and just add a pre-built database to the repository. Need to get the tools
* Make page lookup from links case-insensitive for the first letter in words.
* Version tracking?
* ~~Be consistent in the use of the name -- either "CWiki" or "cwiki".~~
* ~~Layout and use the Sidebar.~~
* ~~Add extensions and CSS for tables.~~
* ~~The edit and create pages should not have a "Delete" link in the nav bar.~~
* ~~Put role-based users in place.~~
*  ~~The routes to the admin pages are spelled, hyphenated, and capitalized differently in different places in the program.~~
*  ~~For users with the "reader" role, links to non-existent pages should be disabled rather than showing a normal, clickable link that then throws an error message.~~
* ~~Add validation to login page and other forms.~~
* ~~New page creation should check for an existing page of the same name before saving.~~
* ~~Somewhere along the way, I introduced a huge bug in the routing created when you try to create a new page from the "New" item in the nav bar.~~
* ~~Add the ability to generate a Table of Contents (TOC) for long pages.~~
* ~~How to handle images. Should they go in a the database? (Almost certainly yes.)~~
* ~~Show a page to create an administrator account upon first use after installation.~~
* ~~The first line in the second level of a list has some poor formatting -- too close to the line above.~~
* ~~When the initial admin user changes their profile then attempts to sign out, the signout page shows the username​ before modifications, presumably because it is still in the session record.~~
* ~~Need some sort of confirmation after adding a user, editing a user, or deleting a user.~~
* ~~When running from an uberjar, the program only works when run from the development directory. It needs to be able to reference the resources in the jar rather than the file system.~~
* ~~Consolidate some of the nav stuff in the page header into a "More" drop-down menu.~~
* ~~Read initial files based on a text file containing the list of files to load.~~
* ~~Display, adding, deletion and editing tags in the page editing page.~~
* ~~Add a tagging system. Look into hierarchical tagging systems.~~
* ~~Ability to import and export pages, including metadata.~~
* ~~Rationalize the CSS~~