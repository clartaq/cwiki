+++
author = "CWiki"
title = "About Import-Export"
tags = ["about" "export" "help" "import"]
date = 2017-12-10T09:47:06.000-05:00
modified = 2020-06-23T15:05:24.215-04:00
+++

CWiki lets you import ([[About Roles|if you are allowed]]) and export content from the wiki to the file system and *vice versa*. There are two ways to do it, explained below.

## Cut and Paste

Since you can open a page to edit most pages ([[About Roles|if you are allowed]]), you can export it by using "Select All" (`Ctrl-A`) and "Copy" (`Ctrl-C`) in any existing page. Then you can paste what you have copied into any other program.

Import is just as easy. [[Pages Primer|Create a new page]] (click "New" up in the window header) and paste your content into the edit window.

## Reading and Writing to the File System

Importing and exporting files is supported by commands on the drop-down menu up in the title bar. Under the "More" drop-down, there are items for "Import" (if allowed), "Export" to export a single page, and "Export All" to export the entire contents of the wiki. These methods work with Markdown files that contain [[About Front Matter|TOML front matter]].

This method can be more convenient in some cases. If your role is a "reader," it is your only option since you will not be able to open an editing page. Readers can only export, they are not allowed to import content into the wiki.

### Importing

When you have a Markdown file that you want to import into the wiki, hover over the "More" drop-down menu. The "Import" menu item will appear.  Click it. You should see a page asking you to select files to import.

### Things You Should Know About Importing

#### Import is Fragile

Really fragile. If the front matter is not correctly formatted, it will crash the program. No useful error messages are shown other than a stack trace. Bleah. Just too lazy to bullet-proof it right now.

#### Author

When importing from a file, the author must be known to the system. If the metadata contains an author known to the system, and they have the “writer” role or higher (see [[About Roles]]), the page will be imported with them as the author.

If the page has no author or the author is unknown to the system, or the author does not have permission to create files (they have the “reader” role), the data will be imported with CWiki listed as the author.

#### Title

If the metadata does not include a title, CWiki will attempt to create a title from the file name. If a suitable file name cannot be created, a random title will be generated. It will be hideous, so it should be easy to see on the [[All Pages]] page. All such pages have a title starting with "Title - " followed by some random characters.

#### Overwriting Existing Pages

If a page already exists in the database with the same title as a page being imported, the version in the database will be overwritten by the imported version with no warning.

This may change in the future.

#### Timestamps

If the metadata in the file does not contain a creation date and time, the current date and time will be used.

If the metadata does not include a date and time for the last time it was modified, the current date and time will be used.

### Exporting a Single Page

When you are viewing a page which you want to export, select the "Export" item from the drop-down menu. You will be asked if you want to do the export. If you confirm your intention, the page will be exported.

Upon success, you will be informed that the export was completed and where the file was stored.

### Exporting All Pages

You can select the drop-down menu item "Export All" to save a copy of every (non-generated) page in the wiki. It could take awhile. It could take up a lot of space.

It's one way to back up your database although importing each page one page at a time might be lengthy and tedious.

### Things You Should Know About Exporting

#### Exporting Generated Pages

You cannot export pages that are generated upon request, like the "All Tags" page or search results.

#### Exporting While Editing.

You cannot export while editing. The option won't even appear in the drop-down menu.

#### Export May Have to Translate Your Page Name

The export function creates a file with a name based on the page name. It tries to create a file named in such a way that it is acceptable on Linux, Windows, and macOS. Some characters that are allowed in page titles are not allowed in file names. As such, some characters in the page name may be changed to some other character, usually an underscore, "_."
​
When the file has been exported successfully, the program will show you the name that was used.

#### Export will Overwrite Existing Files without Warning

If a file with the same name already exists, it will be overwritten by the export function.

#### The "Export All" Will Attempt to Proceed Even if there are Errors

The function will try to export as many files as it can. If a particular file fails to export correctly, it will be skipped without warning.