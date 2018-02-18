---
author: CWiki
title: Design of Import/Export
date: 01/23/2018 4:25:00 PM
updated: 02/18/2018 4:23:45 PM
tags:
  - design
  - export
  - import
  - technical note
---

CWiki has the ability to export pages from the wiki and import files into the wiki if the user signed in has an appropriate role (reader cannot import.)

Menu items in the "More" drop down in the page header provide access to these functions.

## Import ##

When this link is selected in the drop down menu, the user will be shown a dialog asking them to select a file to "Upload". When they click the "Browse" button, an OS-specific file dialog will be displayed asking them to select a file. Once they select a file, they will still be presented with the opportunity to continue ("Import") or "Cancel".

If the user elects to "Import", the file will be imported. Another page will be displayed confirming that the import has indeed occurred.

At this point, the user will be shown the page just imported.

## Export and Export All ##

In the case of the "Export" function, the currently visible page will be exported immediately. Any file with the same file name will be overwritten without warning. Current meta-data about the page will be written into the YAML front matter of the file.

The file will be written to the same directory that the program is running from. No other choice will be available since I am too stupid/lazy to figure out how to provide a file save dialog without using JavaScript.

The only moderately complicated thing about export is translating the page title into a valid, cross-platform file name. The file name generated must be valid across macOS, Linux, and Windows since I want to be able to move the file across operating systems without worrying about such things. 