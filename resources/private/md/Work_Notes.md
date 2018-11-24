---
author: david
title: Work Notes
date: 2018-11-18T10:10:30.985-05:00
modified: 2018-11-24T12:12:28.367-05:00
tags:
  - cwiki
  - design
  - keyboard
  - shortcut
  - technical note
---

This page is the on-going saga of working on the CWiki program.

Preventing Duplicate Page Titles

During the extensive revisions to the editor, it became apparent that it has always been possible to create and save pages with the same name. When doing so, only the first is viewable. To get to subsequent versions, the earlier version(s) must be deleted.

Now, when creating a new page, on the first attempt to save it, the title is checked against those already in the database.

Noted an odd series of events
1. After deleting a page
1. And being returned to the Front Page
  1. Not sure if it related to Front Page
1. And starting a new page
1. And exiting the new page without saving
1. The editor is re-invoked with another new page with the title of the deleted page.

- Keystroke shortcuts. See [[Technical Note on Keyboard Shortcuts]].
- Fix the damn CSS!
- Fix the issue with illegal characters in the title
- Add the Markdown help in the editor

## Keyboard Shortcuts ##
**22 Nov 2018, 04:29:04 pm**

I want to implement some keyboard shortcuts for a couple of reasons. First, it would improve accessibility. Second, I want it for my use -- saving files is much more comfortable with a shortcut than moving the mouse to click the save icon. In fact, for one pathological page that I am working on, the editor pushes the preview pane, and the save icon at the right of the button bar way out of the window on the right. Without a shortcut to save the page, I have to scroll way off to the right, the scroll back left in the editor to continue editing.

There are a couple of ways to do it.

### "Manually" ###

By "manually" I mean some low-level programming. For example, here is how you can implement a "Save" function that is invoked by pressing `Cmd-s`

```clojure
(let [save-fxn (:assemble-and-save-fn options)]
  (.addEventListener js/document "keydown"
                     (fn [e]
                       (let [the-key (.-key e)
                             lc-key (.toLowerCase the-key)]
                         (when (and (= "s" lc-key)
                                    (.-metaKey e))
                           (save-fxn options)
                           (.preventDefault e)
                           (.stopPropagation e)
                           false))) false))
```

This snippet retrieves the function to assemble and save the page (from an `options` map passed as an argument to the function in which this code is embedded). Then an event listener is added to the DOM document that watches for "key down" events. When a "key down" event is detected, the function checks if the combination of keys down represents the shortcut. If so, the "Save" function is invoked. The last three lines of the function are to prevent the key combination from "bubbling up" to the browser since the browser uses the same shortcut to save the web page.

### Using a Library ###

As implied above, this is all pretty low-level. Looking around for a ClojureScript library to handle this more easily​, I came across.