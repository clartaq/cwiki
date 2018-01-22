---
title: Tag Design
author: CWiki
date: 1/13/2018 10:21:29 AM 
updated: 1/22/2018 5:41:03 PM 
tags:
  - technical note
  - tags
  - design
  - how it works
---

Tags are a convenience that lets users classify their information in some (hopefully) systematic way. They are a pain to deal with from a programming perspective, but I wouldn't want to use a system without them.

Some of the characteristics of tags include:

- Tags are always associated with at least one page.
- Tags may be of any length up to limits imposed by the operating system and hardware. Since tags are treated as Java `String`s, they may be billions of characters long.
- There may be any number of tags associated with a page. However, there are some limitations imposed by the page editing facilities.
- The contents of a tag, the characters making it up, are [UTF-16](https://en.wikipedia.org/wiki/UTF-16), same as the characters making up Java strings.

## Tag Usage ##

For me personally, I try to minimize the number of tags I use. Otherwise it seems like you're creating a new tag for every page. I also tend to use lower-case only except when the tag is an acronym, like "FAQ", or a proper name, like "TeX".

Trying to stick to either plural or singular tags is probably a good idea too, but hard to do. For example, there are lots of "special" pages. When I write a description of a special page, do I use the tag "special page" or "special pages". I try to be consistent, but am not always successful. You can check the [[All Tags]] page to see if you are sticking to you plan. If it shows that you have used both versions, you can drill down to see if the majority are one form or the other and make corrections. Or maybe it really does make sense to use the plural and singular in some cases.

## Tags in the Database ##

Tags have a many-to-many relationship with pages -- many pages may use the same tag and there may be many tags associated with a typical page. This type of relationship results in a design with three tables.

- The pages table contains lots of stuff including the page id.
- The tags table includes the tag id and the tag name (a string)
- A cross-reference table with a row id, a tag id (from the tag table) and a page id (from the page table). Every combination of page-id and tag-id used in the wiki is stored in this cross-reference table.

### The API ###

There are only a few functions that provide an interface to tag handling by the database. As with all database functions, they reside in the namespace `cwiki.models.wiki-db`.

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
#### get-tag-names-for-page ####

**Arguments**: The page id or the page id and the database to be used. When called without the database, the wiki database will be used.

**Return Value**: A case-insensitive sorted-set of strings containing the the names of the tags associated with the page.

**Example Usage**: This function is used by the layout functions for viewing a wiki page (`view-wiki-page`) and for editing a wiki page (`compose-create-or-edit-page`).

---
#### get-titles-of-all-pages-with-tag ####

**Arguments**: The tag name.

**Return Value**: A case-insensitive sorted-set of all the pages that use the tag.

**Example Usage**: The `compose-all-pages-with-tag` function displays the list of page names that use a tag. It is reached by clicking any of the links in the [[All Tags]] page. It uses this function to get the list of page names to display.

```prettyprint lang-clj
    (defn compose-all-pages-with-tag
      "Return a page listing all of the pages with the tag."
      [tag req]
      (let [query-results (db/get-titles-of-all-pages-with-tag tag)
            content (process-title-set query-results)
            post-map (db/create-new-post-map
                       (str "All Pages with Tag \"" tag "\"") content)]
        (view-list-page post-map query-results req)))
```

---

## Tags in the User Interface ##

### Displaying a Wiki Page ###

Near the top of the display of a wiki page there is a line that starts with "**Tags**:" followed by the list of tags themselves. If there are no tags associated with the page (e.g. any of the [[Special Pages|special pages]] generated on demand), then "None" is displayed next to "**Tags**:".

### Editing a Wiki Page ###

When editing a wiki page, the editor page shows a group of inputs for tags just below the page name. The number of tags that can be used with a page is limited by the number of text fields available for them. Currently there are 10 such fields.

These inputs are initialized with the existing values of the tags for the page. When all edits to the page are accepted, any tags in the input are retrieved and used as the argument(s) to the `update-tags-for-page` function described above. This single function is responsible for removing any tags no longer used by the page (or possibly anywhere else in the wiki) and adding any new tags. When the user accepts changes to the page, it will be displayed with the new tags too.

### The All Tags Page ###

The [[All Tags]] page is one of the [[Special Pages|special pages]] in CWiki. It is one of the links in the default [[Sidebar|sidebar]].

When selected, a page is constructed listing all of the tags known to CWiki in alphabetical order. Each tag is displayed as a link. When the tag/link is clicked, a new page will be constructed and displayed that shows the titles of all of the pages that use the tag. Those titles are also displayed as a link the user can select to view the page.