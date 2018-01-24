---
title: Design of Import/Export
author: CWiki
date: 1/23/2018 4:25:00 PM 
updated: 1/23/2018 4:38:37 PM 
tags:
  - design
  - import
  - export
  - tech note
---

CWiki has the ability to export pages from the wiki and import files into the wiki if the user signed in has an appropriate role (reader cannot import.)

Menu items in the "More" drop down in the page header provide access to these functions.

## Import ##

## Export and Export All ##

In the case of the "Export" function, the currently visible page will be exported immediately. Any file with the same file name will be overwritten without warning. Current meta-data about the page will be written into the YAML front matter of the file.

The file will be written to the `import_export` directory. No other choice will be available since I am too stupid/lazy to figure out how to provide a file save dialog without using JavaScript.

The only moderately complicated thing about export is translating the page title into a valid, cross-platform file name. The file name generated must be valid across macOS, Linux, and Windows since I want to be able to move the file across operating systems without worrying about such things. 
