---
title: Description of an Import Bug
author: david
date: 02/08/2018 10:16:18 AM
updated: 02/10/2018 12:52:35 PM
tags:
  - bug
  - import
  - technical note
---

During development, a bug was noticed in the import function in that after deleting a page and then re-importing it, it would not show up in the [[All Pages]] page.

It turned out that the "refering page" passed through many of the functions involved pointed back to the page that does the deletion. This referer argument is used by many functions in the program so that they can return to where some action started once that action is complete.

So, although the page appeared to be imported correctly, it was immediately deleted when the final confirmation page was acknowledged and the program attempted to return to the page where the import was initiated.

Tracing the way the referer was passed along from function to function was easy. Modifying things so that the referer pointed somewhere else was complicated and error prone.

Rather than a long complicated mess to modify the referer in special circumstances, something else was tried.

It seems logical that upon completing an import, the user would want to see the page just imported. So, once import is completed, the program now displays the imported page rather than trying to return to where the import process started. Simple.
