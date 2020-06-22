+++
author = "CWiki"
title = "Pages Primer"
tags = ["help" "pages"]
date = 2017-10-24T08:48:43.000-04:00
modified = 2020-05-31T16:17:20.189-04:00
+++

This "Pages Primer" is a brief introduction to creating, editing, and deleting pages in the wiki.

The syntax used to write pages is a slight variant of [Markdown](https://daringfireball.net/projects/markdown/). The language accepted by this wiki includes an extension to allow  [[Wikilinks|wikilinks]], that is, links to other pages within the wiki, in a Markdown file. (See the [[Links Primer]] for details.)

## Creating Pages ##

You can create pages in CWiki if you have been assigned the role of **writer**, **editor**, or **admin**. (See [[About Roles]] for more information on these roles.)

You can create a page by one of three methods.

### 1. Link to A Non-Existent Page ###

When you are editing a page and want to link to a page that doesn't exist yet, enter the link to the non-existent page. When you are done writing the current page, the link to the page you want to create will appear in red, like this: [[page I want to link to]]. Then you can click on the red link. The editor will open up letting you put whatever content you want on the newly created page.

### 2. Click the "New" Link in the Navigation Bar (Recommended) ###

At the top of the wiki window in your browser, there are a series of navigation links, including one that says "New." When you click that link, a new page will be created. Just change the title and enter the content you want. Finally, click the "Done" button at the left end of the button bar. The new page will be saved.

This method is probably the quickest way to get started writing something, but you will still need to link the page into some other part of the wiki. Otherwise, you will have an "Orphan": a page that is not referred to by any other part of the wiki.

### 3. Edit the Browser Address Bar (Not Recommended) ###

If you look at the address bar in your browser, it should show something like `http://localhost:1350/Pages%20Primer`, that is, the title of the page is at the end of the address. You can change that address to the name of the page you want to create. If there is no other page with the same name in the wiki already, you will be shown an editor page to build it and then save it.

Note also that some characters, there is a list [here](https://en.wikipedia.org/wiki/Percent-encoding), must be "percent-encoded", that is, certain characters cannot be present in a web address (known as a "[URL](https://en.wikipedia.org/wiki/URL)") and must be encoded as a percent sign followed by two hexadecimal digits. If you attempt this and screw it up, you may damage the database irreparably. That would require you to delete the database and start over. (But you have a [[backup]], right?)

This method is becoming less reliable. Many browsers now support search directly in the address bar. If this is the case, changing the address bar initiates a search rather than creating a new page.

## Page Content ##

A "page" in the wiki consists of just a few things.

### The Title ###

Every page must have a title. It should be no more than 255 characters, possibly fewer.

The title must be unique within the wiki. The comparison for uniqueness is case-insensitive, so the titles "Title" and "title" are considered the same. Using titles that differ only by letter case can be confusing and is not allowed.

**Warning**: It is recommended that you not start a title with a question mark ("?"). It will usually work, but some special operations in CWiki use a title that begins with a question mark. Using one may produce unexpected results.

### Tags ###

You can specify tags (also known as "keywords" or "categories") to be associated with the page. See [[About Tags]]. When you open the edit/new page editor, you will see an area showing existing tags and a button to let you add more.

When you view a page that has tags associated with it, you will see them near the top, under the author name.

### The Content ###

In addition to a title, a page must have content. Otherwise, what's the point? You can put pretty much any text or image that you want into the wiki. You can fill the page with plain text if you want. You can also add some nice formatting, images, program listings, and mathematics. See [[Text Formatting]] and [[About Images]] for more information.

## Editing Pages ##

If you have the appropriate role (see [[About Roles]]), you can edit most pages in the wiki. When it is possible for you to edit the page you are viewing, there will be an "Edit" link in the navigation bar at the top of the program window. Click it and revise to your hearts' content.

You can edit something as often and as many times as you like.

Be careful with titles. If you change a page title, that will break any links that refer to that title.

For more details, see [[About the Editor]].

## Deleting Pages ##

If you have the appropriate role (see [[About Roles]]), you can delete pages. Just go to the page you want to remove​. If you have rights to do so, you should see "Delete" up in the navigation bar. Click the link and the page will be erased.

You can't undo this. So be thoughtful about your choices.

Note that some pages can't be deleted regardless of your role. These pages are vital to the operation of the CWiki program itself.

## Special Pages ##

There are some [[Special Pages]] in the wiki too. You can read about them by clicking the link.