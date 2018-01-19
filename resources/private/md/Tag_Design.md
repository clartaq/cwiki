---
title: Tag Design
author: CWiki
date: 1/13/2018 10:21:29 AM 
updated: 1/18/2018 5:56:41 PM 
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
#### get-tag-ids-for-page ####

**Arguments**: The page id or the page id and the database to be used. When called without the database, the wiki database will be used.

**Return Value**: A sequence of tags associated with the page.

**Example Usage**: The `get-tag-names-for-page` function (see below) uses this function to obtain the tag ids associated with a page.

```prettyprint lang-clj
    (defn get-tag-names-for-page
      "Returns a case-insensitive sorted-set of tag name associated with the page."
      ([page-id]
       (get-tag-names-for-page page-id h2-db))
      ([page-id db]
       (let [tag-ids (get-tag-ids-for-page page-id)
             tag-ids-as-string (convert-seq-to-comma-separated-string tag-ids)
             sql (str "select tag_name from tags where tag_id in ("
                      tag-ids-as-string ");")
             rs (jdbc/query db [sql])]
         (reduce #(conj %1 (:tag_name %2))
                 (sorted-set-by case-insensitive-comparator) rs))))
```

---
#### get-tag-names-for-page ####

**Arguments**: The page id or the page id and the database to be used. When called without the database, the wiki database will be used.

**Return Value**: A case-insensitive sorted-set of strings containing the the names of the tags associated with the page.

**Example Usage**: At present, this function is not used. As tagging functionality is completed, it will be used to display tags when a wiki page is displayed and when editing a wiki page.

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
#### update-tags-for-page ####

This is the "workhorse" of the tags database functions. All attempts to change the tags from outside of the database namespace should be routed through this function.

**Arguments**:

**Return Value**:

**Example Usage**:

## Tags in the User Interface ##

### Displaying a Wiki Page ###

**Note** This description is the way it is intended to work. It doesn't work like this yet.

Near the top of the display of a wiki page there is a line that starts with "Tags:" followed by the list of tags themselves.

### Editing a Wiki Page ###

**Note** This description is the way it is intended to work. It doesn't work like this yet.

When editing a wiki page, the editor page shows a group of inputs for tags just below the page name. These inputs are initialized with the existing values of the tags for the page. When all edits to the page are accepted, any tags in the input are retrieved and used as the argument(s) to the `update-tags-for-page` function described above.