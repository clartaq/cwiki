---
title: About Import/Export
author: CWiki
date: 12/10/2017 9:47:06 AM
updated: 02/10/2018 01:21:24 PM
tags:
  - about
  - import
  - export
  - help
---

CWiki lets you import ([[About Roles|if you are allowed]]) and export content from the wiki to the file system and *vice versa*. There are two ways to do it, explained below.

## Cut and Paste

Since you can open a page to edit most pages ([[About Roles|if you are allowed]]), you can export it by using "Select All" (`Ctrl-A`) and "Copy" (`Ctrl-C`) any existing page. Then you can paste what you have copied into any other program.

Import is just as easy. [[Pages Primer|Create a new page]] (click "New" up in the window header) and paste your content into the edit window.

## Reading and Writing to the File System

This method can be more convenient in some cases. If your role is a "reader", it is your only option since you will not be able to open an editing page. Readers can only export, they are not allowed to import content in to the wiki.

### Things You Should Know

#### Import is Fragile

Really fragile. If the front matter is not properly formatted, it will crash the program. No useful error messages other that a stack trace. Bleah. Just to lazy too bullet-proof it right now.

#### Author

When importing from a file, the author must be known to the system. If the meta data contains an author known to the system, and they have the “writer” role or greater (see [\[[About Roles]], the page will be imported with them as the author.
If the page has no author, or the author is unknown to the system, or the author does not have permission to create files (they have the “reader” role), the file will be imported with the currently logged in user listed as the author.

#### Title

If the metadata does not include a title, a random title will be generated. It will be very ugly, so it should be easy to see on the [[All Pages]] page.

#### Timestamps

If the metadata in the file does not contain a creation date and time, the current date and time will be used.

If the metadata does not include a date and time for the last time it was modified, the current date and time will be used.
