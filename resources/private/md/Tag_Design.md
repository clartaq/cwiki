---
title: Tag Design
author: CWiki
date: 1/13/2018 10:21:37 AM 
updated: 1/13/2018 5:42:51 PM 
tags:
  - technical note
  - tags
  - design
  - how it works
---

Tags are a convenience that let users classify their information in some (hopefully) systematic way. They are a pain to deal with from a programming perspective, but I wouldn't want to use a system without them.

Some of the characteristics of tags include:

- Tags are always associated with at least one page.
- Tags may be of any length up to limits imposed by the operating system and hardware. Since tags are treated as Java `String`s, they may be billions of characters long.
- There may be any number of tags associated with a page.
- The contents of a tag, the characters making it up, are UTF-16, same as the characters making up Java strings.

## Tag Usage ##

For me personally, I try to minimize the number of tags I use. Otherwise it seems like you're creating a new tag for every page. I also tend to use lower-case only except when the tag is an acronym, like "FAQ", or a proper name, like "TeX".

Trying to stick to either plurals or singular tags is probably a good idea too, but hard to do. For example, there are lots of "special" pages. When I write a description of a special page, do I use the tag "special page" or "special pages". I try to be consistent, but am not always successful. You can check the [[All Tags]] page to see if you are sticking to you plan. If it shows that you have used both versions, you can drill down to see if the majority are one form or the other and make corrections. Or maybe it really does make sense to use the plural and singular in some cases.

## Tags in the Database ##

Tags have a many-to-many relationship with pages -- many pages may use the same tag and there may be many tags associated with a typical page. This type of relationship results in a design with three tables.

- The pages table contains lots of stuff including the page id.
- The tags table includes the tag id and the tag name (a string)
- A cross-reference table with a row id, a tag id (from the tag table) and a page id (from the page table).

### The API ###

There are only a few functions that provide an interface to tag handling by the database.

---
#### get-all-tags ####

**Arguments**: None or the database to be used. When called without a database, the wiki database will be used.

**Return Value**: A case-insensitive sorted set of all of the tags present in the database.

**Example Usage**: The [[All Tags]] page makes use of this function when building the page.

```prettyprint lang-clj
    (defn compose-all-tags-page
      "Return a page listing all of the tags in the wiki."
      [req]
      (let [query-results (db/get-all-tags)
            content (process-tag-set query-results)
            post-map (db/create-new-post-map "All Tags" content)]
        (view-list-page post-map query-results req)))
```

---
#### update-tags-for-page ####

This is the "workhorse" of the tags database functions.

**Arguments**:

**Return Value**:

**Example Usage**:

---
#### get-tag-ids-for-page ####

**Arguments**:

**Return Value**:

**Example Usage**:

---
#### get-tag-names-for-page ####

**Arguments**:

**Return Value**:

**Example Usage**:

#### get-titles-of-all-pages-with-tag ####

**Arguments**:

**Return Value**:

**Example Usage**:

## Tags in the User Interface ##

