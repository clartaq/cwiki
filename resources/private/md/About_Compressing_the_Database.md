---
author: CWiki
title: About Compressing the Database
date: 2017-12-03T13:01:44.000-05:00
modified: 2018-06-15T16:44:56.145-04:00
tags:
  - about
  - admin
  - database
  - special pages
---


As you work with your wiki, you will change, add, and remove things. For performance reasons, the database program does not recover that space as these actions occur. That means that, over time, the database can accumulate unused space within it causing the database file to be larger than it needs to be.

Typically, this isn't a problem. But, every once in a​ while, it may be a good idea to reclaim some of that space. If you are an admin user, you can do so by clicking the link to the [[compress|Compress]] page. Compressing the database will make a copy of your database without the empty space and replace your existing database file with the smaller version.