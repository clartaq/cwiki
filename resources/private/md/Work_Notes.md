---
author: CWiki
title: Work Notes
date: 2018-11-18T10:10:30.985-05:00
modified: 2018-12-17T16:25:38.540-05:00
tags:
  - cwiki
  - design
  - keyboard
  - shortcut
  - technical note
---



This page is the on-going saga of working on the CWiki program.

## Some "To Do"s ##

- ~~Keystroke shortcuts.~~ See [[Technical Note on Keyboard Shortcuts]].
- ~~Fix the damn CSS!~~ See [[Technical Note on the Editor CSS]].
- ~~Fix the issue with illegal characters in the title.~~ See [[Technical Note on Encoding Page Titles]].
- Add the Markdown help in the editor
- Need to work on the CSS for lists so that sub-lists don't get formatted so weird, 24 Nov 2018, 04:07:18 pm.
- When inserting text with a keystroke shortcut, the page is not marked as dirty and the preview is not updated. 25 Nov 2018, 03:38:23 pm.
- Seems like it's getting to be time to split out "commands", "keyboard-shortcuts", and "buttons" into their own namespaces in the editor. Getting to be like Java, 25 Nov 2018, 05:37:45 pm.
- When exiting the editor, the program should check if the page being edited was a seed page. If so, it should offer to save it to a file (so I don't forget to do it before erasing the page database for testing or release), 17 Dec 2018, 04:25:32 pm.

##### Noticed an Intermittent Error When Loading the Editor.17 Dec 2018, 04:16:22 pm. #####

For some time now, I have sometimes observed the editor fail to load the correct page. I _think_ that when this occurs, it is the first edit attempt after loading the wiki. I _know_ it occurs when running either from the REPL or an Uberjar.

The one time I thought to record it, the editor attempted to load `js/compiled/dev/goog/deps.js`, which, of course,​ is one of the output files produced during compilation of the ClojureScript portion of the program.

##### Attempting to Switch to a Maintained Fork of `com.cemerick/url` Causes Issues, 03 Dec 2018, 04:54:38 pm. #####

After trying out `com.arohner/uri "0.1.2"`, a maintained fork of the library, page import would no longer work.

##### Page Titles with "?" and "/" (and possibly others) seem to mess up the database, 03 Dec 2018, 03:53:24 pm. #####

After editing, you can't hit the backlink to get to the previous page. Sometimes the "All Pages" page won't work after editing.

This is part of issues [#21](https://bitbucket.org/David_Clark/cwiki/issues/21/cant-edit-files-with-slash-character-in) and [#31](https://bitbucket.org/David_Clark/cwiki/issues/31/editor-should-flag-illegal-characters-in). Really need to get this fixed since it can cause data loss. (Not really data loss. Just messes up the database so that you want to re-initialize it -- which it shouldn't make you do.)

Probably should look into more standard ways to form the URIs for viewing pages. However, since I use an extension to [flexmark](https://github.com/vsch/flexmark-java) that handles wikilinks, it may be a bit tough.

**07 Dec 2018, 05:46:08 pm.** If I hand encode the URL in the address bar, things work as intended, even with special characters. Creating, editing, linking, deleting all work. So, we "just" need a way to encode the links formed from the page titles. Probably need a custom `NodeRenderer`.

**17 Dec 2018, 11:08:26 am.** Turns out the `NodeRenderer` approach didn't work out so well. A custom `NodeResolver` did. See [[Technical Note on Encoding Page Titles]].

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