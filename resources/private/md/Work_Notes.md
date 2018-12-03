---
author: david
title: Work Notes
date: 2018-11-18T10:10:30.985-05:00
modified: 2018-12-02T17:39:33.524-05:00
tags:
  - cwiki
  - design
  - keyboard
  - shortcut
  - technical note
---

This page is the on-going saga of working on the CWiki program.

## Some "To Do"s ##

- Keystroke shortcuts. See [[Technical Note on Keyboard Shortcuts]].
- Fix the damn CSS! See [[Technical Note on the Editor CSS]].
- Fix the issue with illegal characters in the title
- Add the Markdown help in the editor
- Need to work on the CSS for lists so that sub-lists don't get formatted so weird, 24 Nov 2018, 04:07:18 pm.
- When inserting text with a keystroke shortcut, the page is not marked as dirty and the preview is not updated. 25 Nov 2018, 03:38:23 pm.
- Seems like it's getting to be time to split out "commands", "keyboard-shortcuts", and "buttons" into their own namespaces in the editor. Getting to be like Java, 25 Nov 2018, 05:37:45 pm.
- When importing a plain Markdown file without frontmatter, the page should be named based on the name of the file imported, not the random number that is generated now, 27 Nov 2018, 05:45:36 pm.

##### Stop Deleting the Following Call from the Production Version of cwiki.middleware, 02 Dec 2018, 05:34:11 pm. #####

```clojure
(ns cwiki.middleware
  (:require 
  ...
            [clojure.java.io :as io]
  ...
  ; Have to have this for the uberjar to start on a virgin system.
  ; Just makes sure the "resources" directory exists so that the
  ; "wrap-file" middleware below doesn't blow up.
  (io/make-parents "resources/random-file.txt")
  ...
```

For some reason, I keep deleting this. Without it, you can't start CWiki on a virgin system since the `resources` directory won't exist.

##### Noticed that leading or trailing spaces around the link text or replacement text in a wikilink can cause the link to fail, 02 Dec 2018, 05:30:16 pm. #####

For example, `[[preferences|Preferences]]` works, but `[[preferences | Preferences]]` does not.

##### Newly created tags should begin with the newly created text highlighted so the user can just start typing, 24 Nov 2018, 01:34:56 pm. #####

Added a click listener to each tag input that will highlight the entire tag when it is clicked, 24 Nov 2018, 04:36:43 pm. Not a complete solution, but it is an improvement.

##### Move the keyboard shortcuts into their own namespace, 24 Nov 2018, 01:35:25 pm. #####

Created the `keyboard-shortcuts` namespace and moved all keyboard shortcut code there, 24 Nov 2018, 03:52:14 pm. Made the core file a little less cluttered in anticipation of addition shortcut code.

##### Update the icon fonts to include "shift left", "shift right" and "timestamp" icons, 24 Nov 2018, 01:37:08 pm. ###

Updated the icon font file with a new version that included "indent-left", "indent-right", and "clock", 24 Nov 2018, 03:27:05 pm. Of course, overwriting the font file messed up the VCS integration in IntelliJ. Had to do the commit of the new font file manually at the command line. Added the new icons to the button bar, but did not hook them up.

##### Preventing Duplicate Page Titles #####

During the extensive revisions to the editor, it became apparent that it has always been possible to create and save pages with the same name. When doing so, only the first is viewable. To get to subsequent versions, the earlier version(s) must be deleted.

Now, when creating a new page, on the first attempt to save it, the title is checked against those already in the database.

##### Random Observation #####

Noted an odd series of events
1. After deleting a page
1. And being returned to the Front Page
  1. Not sure if it related to Front Page
1. And starting a new page
1. And exiting the new page without saving
1. The editor is re-invoked with another new page with the title of the deleted page.