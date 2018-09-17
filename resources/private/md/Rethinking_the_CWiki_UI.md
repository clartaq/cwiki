---
author: david
title: Rethinking the CWiki UI
date: 2018-08-31T10:18:51.708-04:00
modified: 2018-09-17T12:23:49.092-04:00
tags:
  - cwiki
  - user interface
---

Having spent some time with CWiki over the past year or so of its development, mainly since adding the ClojureScript-based editor, I've come to understand some of the areas of friction for users. Some of that friction comes from simply wrong-headed design choices.

Here are some things that I want to change.

## Get Rid of the Footer ##

It takes up precious vertical space and conveys very little information. The same information should be available from the [[About]] page. **FIXED**

## The Sidebar Should Not Scroll with the Page View ##

I can't tell you the number of times that I've been reading the bottom of a long page and wanted to access something in the [[About the Sidebar|Sidebar]], but it had scrolled off the top of the page. Instead, it should scroll only if it is too long to fit in the current view and the user initiates scrolling themselves. This is mostly **FIXED** in commit [944be76](https://bitbucket.org/David_Clark/cwiki/commits/944be76cb96417b932e3b9520a070286b37f338c). But it still doesn't work on FireFox.

Both areas should scroll independently, if at all.

## The Title Bar Should Never Scroll ##

Similar to my experience with the Sidebar, I often find myself viewing the end of a long page and want to access one of the menu items. But it has scrolled off the top of the screen, and I have to go scrolling back to the top to use it. This is mostly **FIXED** in commit [944be76](https://bitbucket.org/David_Clark/cwiki/commits/944be76cb96417b932e3b9520a070286b37f338c). But it still doesn't work on FireFox.

## There Should be an Editing Toolbar at the Top of the Window ##

The `Save` and `Cancel` buttons should be at the top of the window in a non-scrolling area, similar to the title bar. It should never be possible to scroll them out of view.

There could be some helpful editing icons in a toolbar at the top of the window as well.

## Creating/Editing Tags Should be Simpler ##

The current method of always displaying a grid of text areas to enter tags is just dumb. A single control that allowed entry of multiple tags with some special character to separate them would be much easier on the user.

The method used should also support the possibility of using nested tags in the future.

## Searching Could be Unified ##

Right now the user can search for pages, tags, or users by constructing specials pages by clicking links in the Sidebar. More general searches can be done from the search bar in the title bar. Special syntax for tags, users, and pages could be added to the search bar so that any ​search could be conducted from one place in the program.

## Make Additional Information Available ##

It would be helpful if the program displayed some statistics on the current page like the ​number of characters, words, paragraphs, and estimated reading time.

## The CSS Still Needs a Bit of Work ##

The fact that I am writing this with level 4 headings indicates a problem. Why am I not using Level 2? Because they are too big. Fix it. **FIXED**

The typeface is a bit of an issue too, but a subtle one. Although I love the ligatures, the letters are a bit too close for reading comfortably on a screen. Increase in​ size? Change the font? **FIXED**

What would it take to let the user change the font, font size, line spacing, paragraph spacing, and even line width?