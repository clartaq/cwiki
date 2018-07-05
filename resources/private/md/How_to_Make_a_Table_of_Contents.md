---
author: CWiki
title: How to Make a Table of Contents
date: 2017-12-04T13:14:29.000-05:00
modified: 2018-06-19T16:18:01.885-04:00
tags:
  - how-to
  - table of contents
  - TOC
---

​
Sometimes pages get pretty long. Finding specific parts of the page can be hard. In these cases, it would be nice to have a "Table of Contents" (TOC) for the page. There are a lot of extensions available for various Markdown editors. But if you want a method that is portable across just about any Markdown implementation, try something like the following. (Found this solution suggested in answer to a [question on Stack Overflow.](https://stackoverflow.com/questions/11948245/markdown-to-create-pages-and-table-of-contents)).

Note: Some Markdown editors allow spaces in the links. The parser used by CWiki does not. You must replace, and spaces in the link text (both the link and the destination) with an underline or some other character.

Here is a short example:

```markdown
# Table of contents
1. [Introduction](#introduction)
2. [Some paragraph](#paragraph1)
    1. [Sub paragraph](#subparagraph1)
3. [Another paragraph](#Another_paragraph)

## This is the introduction <a name="introduction"></a>
Some introduction text, formatted in heading 2 style

## Some paragraph <a name="paragraph1"></a>
The first paragraph text

### Sub paragraph <a name="subparagraph1"></a>
This is a sub paragraph, formatted in heading 3 style

## Another paragraph <a name="Another_paragraph"></a>
The second paragraph text
```

The above produces:

# Table of contents
1. [Introduction](#introduction)
2. [Some paragraph](#paragraph1)
    1. [Sub paragraph](#subparagraph1)
3. [Another paragraph](#Another_paragraph)

## This is the introduction <a name="introduction"></a>
Some introduction text, formatted in heading 2 style

## Some paragraph <a name="paragraph1"></a>
The first paragraph text

### Sub paragraph <a name="subparagraph1"></a>
This is a sub paragraph, formatted in heading 3 style

## Another paragraph <a name="Another_paragraph"></a>
The second paragraph text