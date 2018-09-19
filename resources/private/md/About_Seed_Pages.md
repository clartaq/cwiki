---
author: CWiki
title: About Seed Pages
date: 2018-06-16T09:45:14.557-04:00
modified: 2018-09-18T18:06:47.669-04:00
tags:
  - about
  - cwiki
  - database
  - pages
---




​
"Seed" pages, also known as "Initial" pages, are the wiki pages used to construct the database the first time the program starts.

If you are the [[About Roles|admin]] for the wiki, you can change them. They are pages just like any other except for their role in building the initial wiki database.

### How They Work ###

In the code repository for the project, there is a "special" directory with all of the seed pages. It's at `resources/private/md` relative to the base of the project directory.

Within that particular directory there a many Markdown files, the text for the seed pages, and one plain old text file called `initial_pages.txt`. The `initial_pages.txt` file contains a list of the files to include when the wiki database is constructed. Usually (but not always) the file name of a seed page is the same as that created by the [[About Import-Export|export]] function since many of the pages were built that way.

### Making Changes to Seed Pages ###

Anyone with the "editor" or "admin" role can change the contents of the seed pages just like any other page in the wiki. As you edit and save pages, the materials are updated in the database.

If you are an "admin" for the wiki, you can also make use of the "Save Seed" item in the "More" drop-down menu at the top of the page. This will write the changes over the existing file in the resource directory. This way, you can delete the database and re-run the program to create an initial database with the content you want in the seed pages. **This only makes sense if you are a developer.**

### Changing the Seed Page List ###

If you want a different list of pages when building the initial database, you can edit the contents of the `initial_pages.txt` file to include or exclude any files you want.

The only restriction is that there must be a page with the title "Front Page." It's the "go to"​ page to display when starting the program or some weird error occurs. This may change in the future. You may eventually be able to specify the home page in the program options. But not yet.

**Of course,​ to change the file list, you have to be a developer who can regenerate the program's jar file. You cannot save revisions to the seed pages directly in the jar file.**

### When Running from the Uberjar ###

If you are running the program _via_ the uberjar, you can still save seed pages, but they might not be used to reconstruct the database when the uberjar is rebuilt. If you are running the uberjar from the project directory, things will work just fine. 

If you are running the uberjar from some other directory, the program will create the same directory structure as used in the project directory and save the seed page where it would be in the project directory. To use the revised seed page, you would have to copy it back to the correct location in the project directory.