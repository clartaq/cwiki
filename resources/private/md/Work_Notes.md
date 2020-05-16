---
author: CWiki

title: Work Notes

date: 2018-11-18T10:10:30.985-05:00
modified: 2020-05-16T10:23:45.594-04:00
tags:
  - technical note

  - design

  - cwiki

  - keyboard

  - shortcut

---

This page is the on-going saga of working on the CWiki program.

#### Adding Tags from the Keyboard, 16 May 2020, 10:22:15 am ####

For the longest time, I've wanted to be able to add keys from the keyboard. Just type the "Enter" key when finished editing a previous tag. I've hesitated because I thought it would be complicated. Turns out it was just about trivial.

#### Counting the Words in a Page, 10 May 2020, 04:06:50 pm ####

A word count has been added to the reading view of each page.

There is little agreement about how to do such a thing for text in a complicated Markdown document.

The way it works in CWiki is explained in a new seed page: [[About the Word Count]].

#### Animated Activity Indicators for Long Processes, 25 Apr 2020, 10:13:22 am ####

It always seemed a little jarring to start a long-running process, like restoring the database, and getting no feedback that anything was actually happening.

There is finally a simple animation that is triggered when running a "Backup", "Restore", "Import" or "Export All". They don't give you any indication of progress, but it's something.

It may be a good idea to replace separate "Export" and "Export All" with a single operation. Maybe not.

#### Multi-File Import, 25 Apr 2020, 10:06:47 am ####

When using the program, I have often found myself wanting to import multiple pages files at once.

That was a bit of a problem. The earlier implementation, supporting only one-file-at-a-time import, checked if the new file would overwrite an existing page and warned the user before doing the import.

I could not find a clever way to do that with multiple files. So the new implementation just overwrites any existing file without warning. That's useful for me, but not optimum.

Some time in the future it will be reasonable to implement the warning page offering options of "Overwrite All", "Overwrite None", or "Overwrite Some" and providing a list of page names with checkboxes indicating that a particular page is ok to overwrite.

#### Removing References to Database Compression, 05 Apr 2020, 09:26:59 am ####

From the beginning, some of the explanatory information in the seed pages has mentioned "compression" of the database as a maintenance task.

Using the H2 database, there is really no such thing. Removed references to this task.

#### Backup and Restore, 30 Mar 2020, 02:28:18 pm ####

Seems like a good time to implement a simple backup and restore functionality before making changes that will require re-writing the database, like adding additional preferences.

To start out, it will be very like exporting all pages for the backup and reading all of those pages back in for the restore.

Later issues to deal with will include compressing the backup, whether to overwrite during restore, _etc_.

**Update:** 05 Apr 2020, 12:37:00 pm. Implemented minimal backup/restore scheme. Needs work on several preferences.

#### Upgrading Version of HighlightJs, 30 Mar 2020, 02:00:53 pm ####

Since I plan to update the version of MarkedJs, I might as well go ahead and update the version of [HighlightJs](https://highlightjs.org) used too.

Looks like this is a "drop-in" replacement too. Upgraded from v9.12.0 to v9.18.1. Did **not** change the styles available.

#### Upgrading Version of MarkedJs, 29 Mar 2020, 09:56:20 am ####

The version of MarkedJs used is over four years old. Should probably update to current version before beginning work on synchronizing scrolling of editor and preview panes.

Looks like this change is a "drop-in" replacement for the older distribution.

#### Upgrading (NOT) H2 and Lucene,28 Mar 2020, 05:51:07 pm ####

Apparently H2 version 1.4.200 is compatible with Lucene versions up to 8. But it is not _binary_ compatible, meaning you would have to re-compile H2 to make it work. H2 is only _binary_ compatible with Lucene 5.5.5.

Also, it seems like H2 1.4.200 is _incredibly slow_.

So, for now we will stick with H2 1.4.199 and Lucene 5.5.5.

#### Clean Up Middleware, 28 Mar 2020, 03:24:29 pm #####

While looking into what is required to upgrade Sente, it became apparent that my middleware stack needs some love. Some elements have been deprecated. Some are redundant.

#### Upgrading the Sente WebSocket Library, 27 Mar 2020, 05:23:21 pm ####

Early versions of the [Sente](https://github.com/ptaoussanis/sente) WebSocket library have a CSRF token leak. That was fixed in version 0.14.0 and later.

CWiki should incorporate this security improvement along with other improvements in newer versions of the library.

#### Release 0.1.6, 27 Mar 2020, 10:12:02 am ####

This is just a quick and dirty release to get a tagged release in the repository after upgrading the Java requirement to 11.

#### Updating Reagent Dependency to "0.10.0", 26 Mar 2020, 04:45:58 pm ####

Reagent made some architectural changes that caused some deprecation warnings. Fixed those easily.

Some other new warnings are present too. Setting the "autoFocus" and "disabled" attributes to `true` instead of "true" fixed most of them.

There is also a `TypeError: 'handleEvent' property of event listener should be callable` but no indication of what is causing it or what it means.

#### Back to Work, 25 Mar 2020, 03:22:05 pm ####

After spending a lot of time getting some of the basics of the [clown](https://github.com/clartaq/clown) outliner/note-taker put together, needed to get back to some maintenance and development on this project.

##### Need to make sure the build still works. #####

- The version of macOS, where I do development has changed. 
- Java 11 LTS has been released. 
- Safari and Firefox have gone through multiple updates.
- Leiningen has been updated.

##### Immediate Changes #####

- Updated default build jdk to AdoptOpenJDK 11.
- Since the default terminal in macOS Catalina is now zsh, added the following line to my `.zshrc` file:

```bash
export FIREFOX_BIN=/Applications/"Firefox Developer Edition.app"/Contents/MacOS/firefox
```

##### Build Results #####

- `lein test` works as expected.
- `lein test-cljs` works as expected, but needs a better report.
- Running `lein repl` and `(start)` from the repl seems to work as expected but React(?) throws some errors and warnings.
- `lein run` will compile and run the Clojure portion of the program, but it does not build the ClojureScript portion if it is not already present Instead, the browser console shows the **spurious error message**: `Refused to execute http://localhost:1350/js/compiled/cwiki-mde.js as script because "X-Content-Type: nosniff" was given and its Content-Type is not a script MIME type.`

    Instead, the sequence `lein clean && lein cljsbuild once min && lein run` runs things correctly.
- My Cursive repl configurations in IntelliJ seem to be missing altogether. Added a new one. Seems to run fine after an uberjar has been built or the `lein start-prod` script has been run. But immediately after a `lein clean` it fails because it cannot build the `cwikilink-attributes` extension and complains about `Error loading complete.core: Could not locate complete/core__init.class, complete/core.clj or complete/core.cljc on classpath.`

    To use the Cursive repl, run something like `lein cljsbuild once min` since the prep for the build creates the extension correctly. Then running the Cursive repl won't have to try and build it.

- `lein start-prod` seems to be working as expected. Same sorts of warnings/error as running from the repl.

- Building an uberjar and running it seems to work as expected.
    - The Safari console shows Sente throwing warnings and errors.
    - There is also a warning `TypeError: 'handleEvent' property of event listener should be callable` but no idea where it happens.

##### What to do? What to do? #####

There are plenty of things that are out of date now.

Updating to the newest version of Reagent in hopes that it (or a newer version of React) will give better error messages pinpointing some of the reported errors.

#### The Scrollbars on Long Articles are Distracting, 16 Jun 2019, 04:44:07 pm, ####

When viewing/reading a long page (like this one), the scroll bars to the right of the text can be distracting. They should fade away unless the mouse is over the text, ready to scroll it.

**Update:** 16 Jun 2019, 04:52:03 pm. After a little web searching, I came across [this link](https://stackoverflow.com/questions/8631799/make-scrollbars-only-visible-when-a-div-is-hovered-over) on StackOverflow. The answer by "kizu" (about half way down the page), though not the "accepted" answer, seems like the right approach.

There was also an interesting answer about fading components in and out [here](https://stackoverflow.com/questions/8631799/make-scrollbars-only-visible-when-a-div-is-hovered-over). But that doesn't seem to apply to scrollbars.

**Update:** 16 Jun 2019, 05:29:34 pm. Adding a `div` of class `scrollbox-content` to the functions `short-form-template`, `view-wiki-page` and `view-list-page` in `cwiki.layouts.base` seems to be all that is required to add the disappearing/appearing scrollbars.

#### Some Change in the Tag Editor Screwed Up the Layout in Firefox, 10 Jun 2019, 08:50:52 am. ####

Somewhere in the changes made to the tag editor, the layout of the tags in Firefox got screwed up. It works fine in Safari, Opera, and Brave, but Firefox gets really messed up.

**Update:** 10 Jun 2019, 05:46:56 pm. It turns out that Firefox requires the `max-width` style attribute to be set as well as `width`.

#### When editing a new page, completing the first tag by pressing the `Enter` key causes an error, 28 May 2019, 05:38:03 pm ####

When editing a new page, after changing the title, creating the first tag and pressing `Enter` or `Return` causes the new page editor to load, losing all entered information.

On an existing page with no tags, editing it, adding a tag, and pressing `Enter` causes the editor to exit, but does not show the new tag, just the default "A New Tag".

Editing an existing page with tags, deleting all the tags, adding a new one and completing it by pressing the `Enter` key, exits the editor, but the new tag shows up correctly.

##### Clicking the Author Name on a Page View Should take the User to a New Page Listing all Pages Written by the Author, 28 May 2019, 09:39:48 am. #####

Similar to "clickable tags", the name of the author/user shown on the page view should be a clickable link that takes the user to a new page showing the titles of all pages written by that author/user.

**Update:** 28 May 2019, 09:41:05 am. Created issue [#40](https://bitbucket.org/David_Clark/cwiki/issues/40/the-author-user-name-shown-on-the-page) to track this enhancement.

**Update:** 28 May 2019, 04:44:28 pm. This enhancment was completed in changeset [d613628bd3d7](https://bitbucket.org/David_Clark/cwiki/commits/d613628bd3d7)

##### Deleting a Tag with Backspaces Should Require Two Backspaces to Delete the Tag, 27 May 2019, 10:07:32 am. #####

The current behavior is that when a tag no longer has any characters, the editing control is removed. This can be extremely disconcerting.

Instead, the UI element should only be removed after the user has tapped backspace on an empty control.

**Update:** 01 Jun 2019, 10:21:43 am. Created issue [#41](https://bitbucket.org/David_Clark/cwiki/issues/41/deleting-a-tag-with-repeated-backspace) to track progress on this enhancement.

**Update:** 11 Jun 2019, 04:19:08 pm. This enhancement was completed in changeset [27e579364bcb](https://bitbucket.org/David_Clark/cwiki/commits/27e579364bcb).

##### Page Information Tags Should be Clickable, 27 May 2019, 10:03:44 am. #####

When a page is displayed with associated tags, the tags should be clickable. Clicking a tag should bring up a list of all pages associated with that tag, just as it does on the [[All Tags]] page.

**Update:** 28 May 2019, 09:37:50 am. Created issue [#39](https://bitbucket.org/David_Clark/cwiki/issues/39/tags-listed-in-the-page-view-should-be) to track this enhancement.

**Update:**28 May 2019, 03:49:48 pm. This enhancement was completed in changeset [e30de897606e](https://bitbucket.org/David_Clark/cwiki/commits/e30de897606e).

##### Code Listing have Weird-Looking Scroll Bars Now, 06 May 2019, 05:32:09 pm. #####

The title says it all. When did that happen?

**Update:** 12 Jun 2019, 04:30:49 pm. Changed some of the CSS for `pre` and `code` tags to improve appearance in changeset [8a6f45c](https://bitbucket.org/David_Clark/cwiki/commits/8a6f45c4da518e3e9f47d77a789f895f87a0876d).

##### Full Text Search is Broken, 06 May 2019, 04:31:44 pm. #####

When did this happen? Just as well since H2 has updated and needs to change the way searching is indexed. (And uses updated versions of Lucene​. Yay!)

**Update:** 06 May 2019, 05:34:11 pm. Deleting the database, rebuilding the program and the database. Now it seems to work again. Might have been something I did with the database construction code, like adding a field for the sidebar width.

##### Image Loading Doesn't Seem to Work Correctly Anymore, 06 May 2019, 04:30:43 pm. #####

Found several places in the wiki where images no longer load correctly. No idea what changed.

**Update:** 06 May 2019, 05:54:01 pm. Seems like rebuilding the program and database fixed this, just like the "broken" full text search.

##### Multicolumn Pages Should put More Space between Items, 05 May 2019, 03:44:22 pm. #####

When there are links or other contents that wrap to occupy multiple lines, as long page titles do, the spacing between lines and paragraphs appears the same. The spacing between items should be distinctive (larger) than the line spacing used for long items (like page titles.)

##### Multicolumn Pages of Links Sometimes have Titles Overwrite the Column Divider, 05 May 2019, 03:42:02 pm. #####

Generated multicolumn pages, such as those showing all of the page titles attributed to a particular user, sometimes show titles that overwrite the column divider.

##### What has Happened to the Build System, 05 May 2019, 03:07:50 pm. #####

Since trying to do some work on this program again, the build system seems to have gone berserk. All kinds of compatibility issues when trying to build for the REPL. Reminds me of DLL hell with Windows.

**Update:** 26 May 2019, 04:47:41 pm. Well it seems to have "fixed itself."

##### Vertical Scroll Bars, 05 May 2019, 10:26:12 am, #####

What happened to scroll bar?! I've been away from this project for awhile working a tree control for use in the sidebar. When I come back, I see these awful scroll bars on long articles. I also see them on code listings. When did that happen? Ick. I hate the look.

I want to make the scroll bars invisible, only appearing when the user attempts to scroll or when hovering the mouse over the area where the scroll bars should appear.

##### Variable Width Sidebar, 05 May 2019, 10:24:18 am. ######

It should be possible to change the width of the sidebar interactively. As I spend time working on an hierarchical tree control for the sidebar, it is obvious that the user may want to change its width depending on the structure of the tree control that they set up.

**Update:** 25 Jun 2019, 04:52:12 pm. Sidebar width can now be set from the "Preferences" page. The sidebar width can also be set visually by dragging the separator between the sidebar and article with the mouse. But the change does not persist yet.

**Update:** 27 Jun 2019, 11:41:57 am. Sidebar width can now be set and persisted by dragging the separator with the mouse. Committed [here](https://bitbucket.org/David_Clark/cwiki/commits/9e582e1a90414db1f977159c34626780faf7f541).

##### Thinking About Organization, 31 Jan 2019, 10:20:31 am. #####

I've spend the past few days looking at how to organize the content in a wiki. To me, it seems like organizing things in a tree would be good. Arbitrary labels in the tree, arbitrary depth, duplication across branches, and so on. It doesn't look easy, so I'll be experimenting with it in another project for awhile.

This came about because I'm now using this wiki as my main note-taking and knowledge management system.

##### Doesn't Provide a Nice Printout from Browser, 30 Jan 2019, 04:18:43 pm. #####

I just tried printing a page from the browser. Not what I expected. Didn't print the complete page either.

##### Could Reduce Line Count and Number of Arguments by Using Destructuring, 13 Jan 2019, 09:58:09 am. #####

I noticed that most of the functions in the tag editor are called with redundant information. Since most of them receive a copy of the editor state map, that map should be used instead of arguments that just contain a copy of stuff in the state map. Should use destructuring on the arguments to the functions.

**Update:** 13 Jan 2019, 05:17:33 pm. As part of the work on deciding where to put the focus when the editor starts, I used destructuring on most of the function arguments in the editor and tag editor. All those changes only saved a couple of lines but did make the argument list shorter for many functions.

##### What to Focus when Opening the Editor, 13 Jan 2019, 09:53:09 am. #####

It is annoying to _always_ have to move the focus from the page title to the content when opening an existing page for editing. But when opening a new page, the title _should be selected and focused to give the user the hint that they should change the title right away.

So, that's what I'll do. Will require converting the page layout function to a "Type 3" Reagent component.

**Update:** 13 Jan 2019, 04:53:13 pm. Done. If it's a new page, the title is selected and focused. When it's an existing page, the focus is put at the beginning of the main content.

##### Reducers Might Improve the Speed of Listing All Pages, 13 Jan 2019, 09:50:48 am, #####

For some reason, listing "All Pages" is slower than listing "All Tags" even though page titles are indexed and there are more tags than pages. I'm missing something. Maybe the function that produces the "All Pages" page should use a reducer rather than just the core `reduce` function.

**Update:** 13 Jan 2019, 05:27:11 pm. Actually, I mis-remembered this. The functions that compose the "All Pages" and "All Tags" pages are nearly identical with a few changed strings. In fact, they both use a set of common functions to create the page. The only difference is in the call to get the titles or tags from the database. That's the next place to look.

**Update:** 14 Jan 2019, 09:01:34 am. Watching the two pages load in the browser, there is about half a second of additional network loading time for the "All Pages" page. The amount of data transferred is about the same, 470KB. All of the other phases of the page load look similar too. This is a real puzzler since both pages are built and displayed using similar or same functions and page templates.

Requires some more thinking.

##### I Keep Deleting Pages Instead of Editing Them, 13 Jan 2019, 09:47:49 am, #####

I keep deleting pages accidentally when I intend to edit them or simply jump to the home page. Clearly the location of the "Delete" item on the menu needs to change or a confirmation dialog is needed. I'm of the opinion that the location should be moved.

##### Highlighting Newly Created Tags: Now Tag Deletions Doesn't Work Correctly, 09 Jan 2019, 05:36:02 pm, #####

The reason for doing the component conversion mentioned [below](#09%20Jan%202019%2C%2005%3A27%3A12%20pm) is to detect and highlight the new tag after creating it. It has a default name when created and it should be changed as soon as possible.

The highlighting works correctly. The new tag editing control is highlighted and focused immediately after the tag is created. However, something wonky is going on with tag deletion.
- Deleting the last tag works as expected.
- Deleting any other tag doesn't appear to work correctly. (It does work. If you save and exit the editor, the correct tags will show up in the page view.) But the appearance is wrong in the editor.

**Update:** 11 Jan 2019, 04:19:14 pm. Turns out there were a few problems. In converting the `layout-tag-name-editor` to a "Form-3" Reagent component, I was returning a _function_ that returned a class, not the class. Also, in the `reagent-render` function of the class, I was using the `tag-of-interest` from the call to `layout-tag-name-editor` rather than re-creating a new `r/atom` during each call to the render function.

<a name="09%20Jan%202019%2C%2005%3A27%3A12%20pm"></a>
##### Converting to a Reagent Type 3 Component, 09 Jan 2019, 05:27:12 pm. #####

Converted the `layout-tag-name-editor` function in `cwiki-mde.tag-editor` to a Reagent Type 3 component, that is, a component that returns a React class with life cycle functions. This is in preparation for doing some special manipulation of the component after it is created. For that, I need access to the `ComponentDidMount` lifecycle function.

In doing so, I corrected some apparent problems in editing and resizing the tags on the fly. Much smoother now. There is no more confusion about the position of the cursor while editing.

##### New Tags Should be Highlighted when Created, 07 Jan 2019, 05:54:27 pm. #####

When a new tag is created, there is always an extra step of having to click on it to highlight it before typing the new tag text. Why not just highlight it and leave the cursor at the start. Typing would erase the existing placeholder and replace it with the newly typed tag name.

I've tried to leave the tag highlighted in the past but it hasn't worked.

##### Inline Tags?, 07 Jan 2019, 05:51:49 pm. #####

Would it make sense to implement inline tags? It would certainly make take entry easier to use something like "@tag-name-here" as a syntax extension. Multi-word tags would be a bit difficult. I don't really want to restrict how tags are written.

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

**Update:** 07 Dec 2018, 05:46:08 pm.If I hand encode the URL in the address bar, things work as intended, even with special characters. Creating, editing, linking, deleting all work. So, we "just" need a way to encode the links formed from the page titles. Probably need a custom `NodeRenderer`.

**Update:** 17 Dec 2018, 11:08:26 am. Turns out the `NodeRenderer` approach didn't work out so well. A custom `NodeResolver` did. See [[Technical Note on Encoding Page Titles]].

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