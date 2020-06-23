+++
author = "CWiki"
title = "About Front Matter"
tags = ["about" "front matter" "metadata"]
date = 2017-12-24T17:15:16.000-05:00
modified = 2020-06-22T17:58:09.041-04:00
+++

This information is relevant only if you use the [[About Import-Export|Import/Export]] capabilities of CWiki to read and save posts to and from disk.

Front matter is meta-information you can include in your post file in a special section of text at the top of a Markdown file. CWiki supports front matter in the [TOML](https://github.com/toml-lang/toml#user-content-local-date-time) format as supported by the [Hugo](https://gohugo.io) or [Jekyll](https://github.com/jekyll/jekyll) static blog generators. (CWiki does not support templating as both Hugo and Jekyll do.)

CWiki handles a minimal subset of what YAML can do. It just looks for a few different tags to use in wiki page descriptions. For example, the front matter for this page consists of:

```toml
+++
author = "CWiki"
title = "About Front Matter"
tags = ["about" "front matter" "metadata"]
date = 2017-12-24T17:15:16.000-05:00
modified = 2020-05-16T10:11:59.766-04:00
+++
```

Note that the section starts and ends with three "plus" characters. This section is not shown​ in the wiki posts that you view. In fact, after loading a file, the front matter is discarded.

The keys that CWiki understands include:

 | Keys  |  Description |
 |--------|:------------|
 | `author`  | The author of the page.  |
 | `title`  |  The title of the page. | 
 | `date` or `created`   | The date (and possibly time) that the post was created. |
 | `updated` or `changed`  or `modified`| The date (and possibly time) that the post was last updated. |
 | `tags` | One or more tags for the page. Note that if you have only one tag, it can be on the same line as the `tags` tag.|

You can see how these items are used and laid out at the top of this page. These may change in the future, so keep your eyes on this page.

## Important Notes About Importing Files ##

The metadata does not *have* to be present when you import a file. You can even import plain text files if you like. The program will make up some items of the metadata if needed.

### There must be a title ###
If no title is present when you import an existing file, CWiki will attempt to create a title from the file name. If a title cannot be generated for some reason, an ugly, random title will be created and used. The ugliness is to make it easier for you to spot in the [[All Pages]] page.

### The author must already be known to CWiki ###
If the author does not already exist in the database, CWiki will be listed as the author.

### The date-time must be in precisely​ the format shown ###

Any other date time format will cause the program to crash. I realize this is extremely fragile and will likely change in the ​future.

I use a keyboard macro to generate timestamps when creating or modifying pages outside of CWiki. See [A Timestamp Plugin for Sublime Text 3](https://yo-dave.com/2018/02/10/a-timestamp-plugin-for-sublime-text-3/) for details.

If you do all of your editing in CWiki, the details are taken care of for you.

### If there is no creation date, the current date and time will be used ###
The creation date and time will be shown as the date and time that the file was loaded into the database.

### If there is no modification date, the creation date and time will be used ###
The creation and modification date/time will be the same in this case.

**Note**: Historically, CWiki originally read and created front matter in the YAML format. It was changed to TOML after Hugo began supporting that format. YAML is now deprecated and will be removed in a future release.