---
author: david
title: Technical Note on Keyboard Shortcuts
date: 2018-11-22T16:54:22.555-05:00
modified: 2018-11-24T12:11:09.135-05:00
tags:
  - cwiki
  - keyboard
  - shortcut
  - technical note
---

I've wanted to implement some keyboard shortcuts for a couple of reasons. First, it would improve accessibility. Second, I want it for my use -- saving files is much more comfortable with a shortcut than moving the mouse to click the save icon. In fact, for one pathological page that I am working on, the editor pushes the preview pane, and the save icon at the right of the button bar way out of the window on the right. Without a shortcut to save the page, I have to scroll way off to the right, the scroll back left in the editor to continue editing.

There are a couple of ways to do it.

## Manually ##

By "manually" I mean some low-level programming. For example, here is how you can implement a "Save" function that is invoked by pressing `Ctrl/Cmd-s`

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

This snippet retrieves the function to assemble and save the page (from an options map passed as an argument to the function in which this code is embedded). Then an event listener is added to the DOM document that watches for "key down" events. When a "key down" event is detected, the function checks if the combination of keys down represents the shortcut. If so, the "Save" function is invoked. The last three lines of the function are to prevent the key combination from "bubbling up" to the browser since the browser uses the same shortcut to save the web page.

## Using a Library ##

As implied above, this is all pretty low-level. Looking around for a ClojureScript library to handle this more easily, I came across the [keybind](https://github.com/piranha/keybind) library. It handles the tedious specification and parsing of the shortcut as part of its functionality. Oddly, it does not have an option to prevent propagation of the keyboard event; you still have to do it explicitly. Now the shortcut to save the page is implemented like this:

```clojure
  ;; Save the page.
  (let [save-fxn (:assemble-and-save-fn editor-options)]
    (letfn [(save-from-keyboard-fxn [evt]
              (save-fxn editor-options)
              (.preventDefault evt)
              (.stopPropagation evt)
              false)]
      (kbs/bind! "defmod-s" ::save-shortcut save-from-keyboard-fxn)))
```

## Puzzlers ##

### A Shortcut to Exit the Editor ###

I have attempted to define a shortcut that quits the editor with a `Ctrl/Cmd-q` key chord. However, in this case,​ I have not been able to stop propagation of the event. When the key chord is pressed, the browser shuts down. I can't even get any debugging messages. It just shuts down.

### Getting Programmatic Changes to Show Up in the Preview ###

23 Nov 2018, 05:19:36 pm

When using the shortcut to insert a timestamp, like that above, it does not show up in the preview pane until there is some sort of user interaction with the editor.

My attempts to simulate `change` and `input` events have been unsuccessful.