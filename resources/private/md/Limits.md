---
title: Limits
author: CWiki
date: 1/7/2018 10:00:07 AM  
updated: 1/7/2018 10:41:14 AM          
tags:
  - technical note
  - limitations
---

Like all programs, CWiki has some limitations. Here are some of them.

## Field Sizes ##

### Text-Based Fields ###

With some exceptions, noted below, all text based fields are simple Java `String` objects. They are stored in the database as `VARCHAR` objects and converted to and from strings as they are moved in and out of the database.

As a result, the text-based fields have a length of approximately $(2^{31} - 1)/2$ characters or about 1,073,741,824 characters. The "approximately" is due to the fact that characters in Java are encoded in [Unicode UTF-16](https://en.wikipedia.org/wiki/UTF-16). This encoding uses one or two 16-bit code units. So some characters occupy different amounts of space. Bottom line, don't use passwords with more than a billion characters. Hah!

### User Recovery email Address ###

The maximum length for this field is the same as that for any email address, 254 characters.

### Page Contents ###

The content of each wiki page is currently implemented as a Java `String` too. It is not a `VARCHAR` in the database though. It is a `CLOB`, a **C**haracter **L**arge **OB**ject. It can be retrieved as a `stream`, meaning the program could handle page contents too large to fit in memory at once. However, it is still treated just like a `String` at the moment.

## Concurrency ##

There really isn't any. Since CWiki is intended as a personal, private wiki, it was not designed for concurrent use. This is not really a problem if it is used for it's intended purpose. However, if you have multiple users on the same database, or are running the program on a remote server, there might be issues.
