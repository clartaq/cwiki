---
title: About Compressing the Database
author: CWiki
date: 12/03/2017 1:01:44 PM
updated: 1/8/2018 4:41:54 PM 
tags:
  - about
  - admin
  - special pages
  - database
---

As you work with your wiki, you will change, add, and remove things. For performance reasons, the database program does not recover that space as these actions occur. That means that, over time, the database can accumulate unused space within it causing the database file to be larger than it needs to be.

Normally, this isn't a problem. But, every once in a while, it may be a good idea to reclaim some of that space. If you are an admin user, you can do so by clicking the link to the [[compress|Compress]] page. This will make a copy of your database without the empty space and replace your existing database file with the smaller copy.
