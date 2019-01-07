---
author: CWiki
title: Work Notes
date: 2018-11-18T10:10:30.985-05:00
modified: 2019-01-07T15:23:24.172-05:00
tags:
  - cwiki
  - design
  - keyboard
  - shortcut
  - technical note
---


This page is the on-going saga of working on the CWiki program.

##### Investigate the status of Katex for Rendering LaTeX, 07 Jan 2019, 03:22:36 pm, #####

Should look at Katex again to see if it offers any advantages over using MathJax from a remote server.

##### Downloading Google Fonts and Serving Them from the Program Rather than Google, 07 Jan 2019, 03:21:07 pm. #####

Investigate whether downloading the Google Fonts used by the program and serving them directly is an improvement in privacy.

##### When Editing, Save the Page when it is Hidden, 07 Jan 2019, 11:16:40 am. #####

As it stands now, any unsaved work in the editor will be lost on any page reload.

Added an event listener in the initialization of the editor page. It monitors visibility changes and saves the editor contents whenever the page is hidden, for example, when the user switches to another browser tab.

##### Removing Some Global State, 03 Jan 2019, 10:13:03 am. #####

Finally got rid of a piece of global state that could be more functional. The `glbl-editor-is-dirty` variable was removed.

##### It Feels Like the "Done" Button in the Editor Sticks Out Like a Sore Thumb, 01 Jan 2019, 03:08:32 pm.

It's down there all by itself taking up vertical space. It's probably the most used button, but it's isolated down at the bottom of the page. It should be up with the other buttons.

If we ever get to the point of being able to toggle parts of the editor in and out of visibility, it should be with the other buttons.

**Update:** Removed the lower "Done" button and put it up in the editor toolbar. Made small adjustments to CSS to get it to "fit in" a little better.

It still looks like the buttons elsewhere in the program, not like the other toolbar buttons, but I think that is good.

##### Get Rid of a Piece of Global State by Putting the Editor "Dirty Flag" in the Editor State Map, 31 Dec 2018, 03:57:58 pm. #####

Doing the marking is easy; put a flag in the editor state and toggle it in `mark-page-dirty`. Resetting it after save is problematic. The `doc-save-function` doesn't have access to the editor state when it is called.

##### Separating Editor Commands into their Own Namespace, 31 Dec 2018, 03:49:02 pm. #####

Back on 25 Nov 2018, 05:37:45 pm I mentioned that it might be time to split editor commands out into their own namespace. That would allow common commands to be used from toolbar buttons and keyboard shortcuts. That has now started happening.

##### Waiting for the WebSocket to Deliver the Document to the Editor, 29 Dec 2018, 04:01:59 pm. #####

Early on in the development of the ClojureScript editor, I was puzzled about how to await the document contents before rendering the layout of the editor page. Usually (always?), the editor would render before any data arrived.

Eventually, I just made the page data a reactive atom. Then the page re-rendered correctly when the WebSocket finally delivered the data.

I finally got around to doing it right (well..., better) and it was trivial.
* Just use `core.async` to create a channel.
* Set up the WebSocket message handler to put the page data on the channel.
* Then have the page `reload` function try to take the page map from the channel and block until it arrives.

This trades one piece of global state (the map of page data) for another (the channel) but eliminates several uncontrolled references to the global page data scattered around the program.

Fixing this came about as part of jumping down the "Rabbit Hole" of implementing a generic undo/redo. See [this](#24-Dec-2018-11:54:23-am).

I also think the way I was loading the data before might have been related to a race condition. See [this](#17-Dec-2018-04:16:22-pm).

<a name="24-Dec-2018-11:54:23-am"></a>
##### Inserting Text in the Editor Programmatically can Break the Undo Chain, 24 Dec 2018, 11:54:23 am. #####

Experimenting with keyboard shortcuts, I want to make sure that it is still possible to "undo" (ctrl/cmd-z) after inserting text programmatically, such as with the "insert timestamp" shortcut.

However, doing so seems to break the "undo" chain; that is, undo is no longer available after doing the insert. A little research on [DuckDuckGo](https://duckduckgo.com) indicates that this should not be a problem if you use the `execCommand` function to do the insertion. And that seems to be the case for [Safari](https://www.apple.com/safari/).

With the other browsers I regularly test with, it is more problematic.

* With [Firefox (Developer Edition)](https://developer.mozilla.org/en-US/docs/Mozilla/Firefox/Developer_Edition), nothing is inserted when using `execCommand`. I have to use a different method of inserting text which still breaks the "undo" stack. This is a [bug](https://bugzilla.mozilla.org/show_bug.cgi?id=1220696) for `textarea` elements.

* When using [Brave](https://brave.com), the text is inserted but the "undo" stack is broken.

* With [Opera](https://www.opera.com), the command inserts text as expected but the "undo" stack still breaks. Also, the shortcut key I have chosen for now (option-ctrl/cmd-t) seems to be used to switch browser tabs.

**Roll Your Own Undo/Redo** 

There is a blog discussing the topic [here](https://macwright.org/2016/02/07/undo-redo-clojurescript.html) along with example code. Unfortunately, it uses [Domina](https://github.com/levand/domina) to handle the DOM. (Domina doesn't appear to be under development or supported anymore.)

There is also a simple [Reagent](https://reagent-project.github.io) example [here](https://reagent-project.github.io/news/cloact-reagent-undo-demo.html).

And there is a Clojure/ClojureScript library [here](https://github.com/oakes/mistakes-were-made).

A plethora of examples to examine, but none really seem applicable to a `textarea` under control of Reagent.

##### Noticed that I've been Advocating Building TOCs in a Style I Don't Like, 23 Dec 2018, 04:29:24 pm. #####

I like to jump to a location where the section heading is visible. Now I'm changing the pages that have TOCs to that style. Before, clicking on a TOC item took you to the correct place, but it showed things just below the heading.

##### Resizing the Markdown Help Dialog, 23 Dec 2018, 03:52:45 pm. #####

Because the Markdown Help is so large, I wanted it to use a larger proportion of the screen than the other dialogs. It uses the reagent `create-class` method so that it can get the size of the editor pane after the component is mounted. Within the `:component-did-mount` function, a stateful `ratom` external to the class is set to contain the desired width and height. The `:reagent-render` function then uses that state to change its style, specifically, the size.

Another interesting part of setting up the Markdown Help dialog was that I could reuse the functions that create the HTML for the preview pane. That way, calling the editor only needs to send the Markdown for the help page; the translation to HTML, code highlighting, and math formatting are all done when the editor is initialized.

##### Maybe Auto-Save Should be the Default, 22 Dec 2018, 05:56:15 pm. #####

After looking at a number of other Markdown editors, closed-source or open, for purchase or for free, I have yet to see one that doesn't just automatically save the user's work.

##### Hiding Regions of the Editor #####

There's no doubt that the editor can get quite busy. Maybe to remove distractions parts of it could be hidden with a user selection. Good candidates are the Title and Tags at the top and the Preview. Doing so would give users the option of just pounding out the words in a bigger area if that is what they are interested in.
##### Text Inserted with a Keystroke Shortcut is not "Un-Do-able", 22 Dec 2018, 12:32:32 pm. #####

##### Making the Markdown Help Available in the Editor, 22 Dec 2018, 12:29:27 pm. #####

I can send the Markdown Help page HTML to the editor when the page is loaded. I put it in a dialog that works fine. But it shows the HTML as text, does not render the HTML.

##### CSS to Make Multi-Level Lists Look Better,19 Dec 2018, 05:37:14 pm. #####

Made the changes, which, for the lists, involved changing paragraph spacing and removing some existing styling from the `li` element.

This messed up the drop-down menu. Had to make some additional "fiddly" padding and margin settings for the drop-down stuff. It very nearly overlaps the search box. Seems a bit fragile. May require additional work if it needs to be more robust.

##### Clojure 10 and Java 11, 18 Dec 2018, 05:53:58 pm. #####

Upgraded development environment to use Clojure 10 and Java 11. Had to add an explicit dependency for `[org.flatland/ordered "1.5.7"]` in the `project.clj` file to get things to compile. After that everything seems to be running normally.

The `uberjar` got a tiny bit bigger too.

It also let me get rid of the dependency on `javax.xml.bind`, 19 Dec 2018, 11:01:22 am.

##### Program Should Offer to Save Seed Pages when Exiting the Editor, 17 Dec 2018, 04:25:32 pm. #####

When exiting the editor, the program should check if the page being edited was a seed page. If so, it should offer to save it to a file (so I don't forget to do it before erasing the page database for testing or release).

##### Have Seen on Occaision the HTTP Requests Contain Two Copies of Authentication and Authorization data, 18 Dec 2018, 04:18:40 pm. #####

Ought to find out how and where this occurs.

<a name="17-Dec-2018-04:16:22-pm"></a>
##### Noticed an Intermittent Error When Loading the Editor, 17 Dec 2018, 04:16:22 pm. #####

For some time now, I have sometimes observed the editor fail to load the correct page. I _think_ that when this occurs, it is the first edit attempt after loading the wiki. I _know_ it occurs when running either from the REPL or an Uberjar.

The one time I thought to record it, the editor attempted to load `js/compiled/dev/goog/deps.js`, which, of course, is one of the output files produced during compilation of the ClojureScript portion of the program.

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

##### Fix the Damn CSS!, 24 Nov 2018, 12:27:35 PM. #####

See [[Technical Note on the Editor CSS]].

##### Newly created tags should begin with the newly created text highlighted so the user can just start typing, 24 Nov 2018, 01:34:56 pm. #####

Added a click listener to each tag input that will highlight the entire tag when it is clicked, 24 Nov 2018, 04:36:43 pm. Not a complete solution, but it is an improvement.

##### Move the keyboard shortcuts into their own namespace, 24 Nov 2018, 01:35:25 pm. #####

Created the `keyboard-shortcuts` namespace and moved all keyboard shortcut code there, 24 Nov 2018, 03:52:14 pm. Made the core file a little less cluttered in anticipation of addition shortcut code.

##### Start Adding some Keyboard Shortcuts, 22 Nov 2018, 04:54:22 PM. #####

There has been a long-standing​ issue in the repository about adding some keyboard shortcuts in the editor. Finally started implementing​ them. See [[Technical Note on Keyboard Shortcuts]].

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