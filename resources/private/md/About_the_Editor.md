---
author: CWiki

title: About the Editor

date: 2018-11-27T17:40:32.822-05:00
modified: 2020-05-16T10:10:02.727-04:00
tags:
  - help

  - editor

  - about

---

CWiki includes a simple Markdown editor with real-time preview. It is not fancy, but in my own work, it is very low friction.

The editor consists of three main areas.

1. The title area lets you edit the page title.
2. The tags area lets you manage the tags associated with a page.
   1. To edit a tag, click the tag and make your changes.
   2. To delete a tag, click the circled "x" next to the tag. You can also backspace until the tag is erased.
   3. To add a new tag, click the circled "+" at the end of the list of tags. You can also press the "Enter" key while editing a tag to create a new one.
    4. You can navigate among the tags using the "Tab" and "Shift-Tab" keys.
3. The main editing section is where you will spend most of your time.

## The Main Editing Section ##

CWiki divides the main editing area into sections too. 

### The Toolbar ###

Along the top, there is a toolbar with a series of buttons. (Most buttons are disabled at the moment.) 

Of particular note is the "floppy" icon near the right end of the toolbar. If there are unsaved changes, the button is enabled. The "floppy" button also reflects the saved state of the title and tags. Clicking it will save those changes and the button will become disabled.

If you have autosave enabled (see [[About Preferences]] and [[preferences|Preferences]].), as you pause during typing you will seed the "floppy" icon switch from enabled to disabled when changes are automatically saved.

Information on the other buttons will be included when they are attached to working functions.

### The Markdown Pane ###

Below the Toolbar, the view is split into two parts. On the left is the Markdown Pane. This is where to will enter text, images, links, math, and so on.

### The Preview Pane ###

The Preview Pane shows a live preview of how the Markdown will be displayed when you exit the editor. Its scroll position does not track the editing position of the Markdown Pane. (That's an enhancement for the future.) You may need to scroll the Preview Pane manually to show the text around the area where you are typing.

### Shortcut Keys ###

Just like most editors, CWiki recognizes keyboard shortcuts. Some are fairly generic and are used by most editors (copy, paste, delete, and so on.) Others are specific to CWiki. See [[About Keyboard Shortcuts]] for more information.

### When You are Done ###

When you are finished with your writing, click the "Done" button at the bottom of the page. If there are unsaved changes, CWiki will ask you to confirm your choice.

## Tips ##

Save your work often. If you navigate away from the editor, by refreshing the page or clicking a link in the Preview pane, you will lose any unsaved work.

The preview pane can be very helpful when entering links to images. When the link is entered correctly, the image will show up in the Preview Pane, even for links to remote images.

If you have autosave enabled, once a save occurs, you cannot revert to an unmodified version of the page.

## One More Thing ##

In case you haven't noticed, I'm not the best writer in the world. To help me, I use [Grammarly](https://www.grammarly.com). Since the editor is based on an HTML `textarea`, it integrates easily. Just click in the Markdown editing area and Grammarly will load and start analyzing your work. Note that Grammarly compatibility seems to be browser dependent. I use it on Safari, where it works well.