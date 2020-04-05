---
author: CWiki
title: About Admin Pages
date: 2017-11-20T13:51:44.000-05:00
modified: 2020-04-04T17:57:24.171-04:00
tags:
  - about
  - admin
  - special pages
---



​​
Only a user in the admin role can accomplish administrative tasks. There should be at least one "admin" user created the first time the wiki is started and viewed. That user can also create other admin users if they so desire. If you are using CWiki strictly as a personal wiki and you are the only user, then you are the admin user. (If you can click any of the following links, you are an admin user. If the links are "grayed out" and not clickable, you are not an admin user.)

* **Preferences**: A page to let you changes some settings controlling how the program operates. See also [[About Preferences]].
* **Database Backup**: The [[backup|Backup]] page will let you make a copy of the database and store it somewhere safe. See the [[About Backup and Restore]] page for more information.
* **Restore A Saved Database**: If the database gets screwed up or erased somehow, you can use the [[restore|Restore]] page to re-create it from a previously saved backup. [[About Backup and Restore]] page for more information
* **Create a New User**: If you decide you want to allow another person(s) to access the wiki, you can click the [[create-user|Create User]] link to do so. You will be able to assign their username​, password, and an email address for password recovery.
* **Edit the Profile of an Existing User**: If someone wants to change something about their account, like their password, you can do that on the [[select-profile|Edit User Profile]] page.
* **Delete a User**: If a user no longer needs access to the wiki, you can delete their account on the [[delete-user|Delete User]] page.

   Deleting a user has some repercussions you need to consider. Any work they have created will no longer be attributed to them, for example. Perhaps a better thing to do would be only to change their password or role such that they can no longer add or modify information, but the stuff they created are still attributed correctly.

* **Reset a User's Password**: Sometimes people forget things -- like passwords. If that happens, you can give them a new, temporary password and send them an email to reset it from the [[reset-password|Reset Password]] page. NOTE: This is nowhere near working.

* **Saving Seed Pages**: The "seed" pages or "initial" pages are the pages used to create the wiki database the first time CWiki runs​. If you want to revise them in some way, you can edit them just like another page and save, which saves them to the database like any other page.

   **If you are doing development**, you may also frequently edit the seed pages and rebuild the database. Saving the seed pages directly in the resource directory where they are stored can be handy in this case. Once you have saved your edits, and assuming you are the admin user, the "More" menu will contain an entry called "Save Seed" that will write the revised page directly into the resource directory ready for the next time you regenerate the database.

   This is only useful if you save the pages while running the program from the base of the project directory. The pages are saved into one of the resource directories for the project. **You cannot save seed pages back into the jar file.**