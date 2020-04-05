---
author: CWiki
title: About Roles
date: 2017-11-15T09:10:33.000-05:00
modified: 2020-04-04T17:58:37.763-04:00
tags:
  - about
  - roles
---



CWiki supports different roles for different users. The different roles have different capabilities regarding what the user is allowed to do.

## Reader ##

At the lowest, most restricted level, there are **readers**. As the name implies, readers can read things in the wiki. They can't do much (actually anything) else. There is a built-in account for this -- the guest account. To log in as a guest, use "guest" as the username and "guest" as the password when prompted by a login screen. You can also ask the administrator to create a separate reader account for you if you want. 

## Writer ##
Users with the **writers** role can write stuff in the wiki. They can create new pages, delete pages they have written, and link to their own or existing pages. They can add as much stuff as they want. They just can't change anything that some other user has written.

This makes the role a bit problematic. Even though they can create any content they want, they can't point any other part of the wiki at their content without the assistance of an editor or admin user (see below) since they can't edit the links in pages written by others to point at their new content. If you don't need to restrict someone to the role of a writer, make them an editor instead.

## Editor ##

Users assigned the role of **editors**, in addition to all of the capabilities of writers, can revise the work of others. They can do the same things a writer can do but to anyone's pages.

## Admin ##

An **admin** can do everything that an editor can do and a few particular kinds of tasks. An admin can handle user-related tasks such as​ creating new users and deleting departed users. Also, the admin can run maintenance-type tasks such as making backups of the database.

If you are having problems that you don't understand, consult with an admin.

## CWiki ##

The **CWiki** role is a unique role used by the wiki program itself. This role is active when the application is installed and run for the first time. You may notice that the author of the initial pages in the database is CWiki.