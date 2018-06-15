---
author: CWiki
title: About Admin Pages
date: 2017-11-20T13:51:44.000-05:00
modified: 2018-06-15T16:01:51.949-04:00
tags:
  - about
  - admin
  - special pages
---

​​
Only a user in the admin role can accomplish administrative tasks. There should be at least one "admin" user created the first time the wiki is started and viewed. That user can also create other admin users if they so desire. If you are using CWiki strictly as a personal wiki and you are the only user, then you are the admin user. (If you can click any of the following links, you are an admin user. If the links are "grayed out" and not clickable, you are not an admin user.)

* **Database Compression**: As the program operates, the database is continuously updated with new material or has existing material removed. In the course of normal operations, the database program may take some "shortcuts" to keep database performance speedy. These shortcuts may allow unused space to accumulate in the database. The [[compress|Compress]] page will let you spend a few moments removing this accrued cruft and restoring performance while reducing database size. See [[About Compressing the Database]] for a little more information.
* **Database Backup**: The [[backup|Backup]] page will let you make a copy of the database and store it somewhere safe. See the [[About Backup and Restore]] page for more information.
* **Restore A Saved Database**: If the database gets screwed up or erased somehow, you can use the [[restore|Restore]] page to re-create it from a previously saved backup. [[About Backup and Restore]] page for more information
* **Create a New User**: If you decide you want to allow another person(s) to access the wiki, you can click the [[create-user|Create User]] link to do so. You will be able to assign their username​, password, and an email address for password recovery.
* **Edit the Profile of an Existing User**: If someone wants to change something about their account, like their password, you can do that on the [[select-profile|Edit User Profile]] page.
* **Delete a User**: If a user no longer needs access to the wiki, you can delete their account on the [[delete-user|Delete User]] page.

   Deleting a user has some repercussions you need to consider. Any work they have created will no longer be attributed to them, for example. Perhaps a better thing to do would be only to change their password or role such that they can no longer add or modify information, but the stuff they created are still attributed correctly.

* **Reset a User's Password**: Sometimes people forget things -- like passwords. If that happens, you can give them a new, temporary password and send them an email to reset it from the [[reset-password|Reset Password]] page. NOTE: This is nowhere near working.

* **Saving Seed Pages**: The "seed" pages or "initial" pages are the pages used to create the wiki database the first time CWiki runs​. If you want to revise them in some way, you can edit them just like another page and save, which saves them to the database like any other page.

   If you are doing development, you may also frequently edit the seed pages and rebuild the database. Saving the seed pages directly in the resource directory where they are stored can be handy in this case. Once you have saved your edits, and assuming you are the admin user, the "More" menu will contain an entry called "Save Seed" that will write the revised page directly into the resource directory ready for the next time you regenerate the database.