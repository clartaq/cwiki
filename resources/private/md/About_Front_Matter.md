---
author: CWiki
title: About Front Matter
date: 2017-12-24T17:15:16.000-05:00
modified: 2018-08-14T13:51:36.795-04:00
tags:
  - about
  - front matter
  - metadata
---

This information is relevant only if you use the [[About Import/Export|Import/Export]] capabilities of CWiki to read and save posts to and from disk.

Front matter is meta-information you can include in your post file in a special section of text at the top of a Markdown file. CWiki supports front matter in the [YAML](http://yaml.org/) format as supported by the [Hugo](https://gohugo.io) or [Jekyll](https://github.com/jekyll/jekyll) static blog generators. (CWiki does not support templating as both Hugo and Jekyll do.)

CWiki handles a minimal subset of what YAML can do. It just looks for a few different tags to use in wiki page descriptions. For example, the front matter for this document consists of:

```yaml
---
title: About Front Matter
author: CWiki
date: 2017-12-24T17:15:16.000000-05:00 
updated: 2018-03-11T10:20:12.235913-04:00
tags:
  - about
  - front matter
  - metadata
---
```

Note that the section starts and ends with three hyphen characters. This section is not included in the wiki posts that you view. In fact, after loading a file, the front matter is discarded.

The tags that CWiki understands include

 | Tags  |  Description |
 |--------|:------------|
 | `author`  | The author of the page.  |
 | `title`  |  The title of the page. | 
 | `date` or `created`   | The date (and possibly time) that the post was created. |
 | `updated` or `changed`  or `modified`| The date (and possibly time) that the post was last updated. |
 | `tags` | One or more tags for the page. Note that if you have only one tag, it can be on the same line as the `tags` tag.|

These may change in the future, so keep your eyes on this page.

## Some Important Notes ##

### There must be a title ###
If no title is present, an ugly, random title will be created and used. The ugliness is to make it easier for you to spot in the [[All Pages]] page.

### The author must already be known to CWiki ###
If the author does not already exist in the database, the contents of the file will not be added to the database.

This is not as difficult a restriction as you might think. You can always use "CWiki" as the author, and the file will be accepted. This is the same mechanism the program uses to populate the initial database right after the database tables are created. (The built-in "guest" account cannot be used since it is not authorized to add new content to the wiki.)

Because of this, **this is in no way a security feature**.

### The date-time must be in precisely​ the format shown ###

Any other date time format will cause the program to crash. I realize this is extremely fragile and will likely change in the ​future.

I use a keyboard macro to generate it when creating a page. See [[A Timestamp Plugin for Sublime Text 3]] for details.

If you do all of your editing in CWiki, the details are taken care of for you.

### If there is no creation date, the current date and time will be used ###
The creation date and time will be shown as the date and time that the file was loaded into the database.

### If there is no modification date, the creation date and time will be used ###
The creation and modification date/time will be the same in this case.