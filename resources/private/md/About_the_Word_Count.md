---
author: david

title: About the Word Count

date: 2020-05-10T15:17:00.182-04:00
modified: 2020-05-31T14:00:37.868-04:00
tags:
  - word count

  - about

---

Up at the top of each page, below the author and creation/modification dates, there is a line showing the number of characters in the page along with an estimate of the number of words in the page.

It is only an estimate because, what is a word, really? Do words in links count? Do explicit HTML tags and comments embedded in the Markdown count? How about mathematics? Which parts of a code listing?

So, here's how it works in [[About CWiki|CWiki]].

1. Strip HTML comments. You don't see them, so they shouldn't count.
2. Tabs are removed and replaced with a single space. This just eases further processing.
3. Images and all included text are removed. This includes "alt" text and any optional heading. It could be argued that they should be included.
4. The words in Markdown headers are included but not the characters used to indicate headings (the "#"s, "="s, or "-"s).
5. For HTML links, only the "display" part of the link is counted, not the URL. For example in the link `[two words](example.com)`, only the "two words" are counted.
6. Similarly, for wikilinks, only the text that is actually displayed is counted.
7. For manually embedded HTML tags, nothing is counted. They don't show up when reading, so they don't count.
8. For MultiMarkdown-style footnotes (which are not part of the original Markdown), the references are ignoreded, but the text of the footnote is included.
9. For mathematics in [[About TeX and LaTeX|$\rm\LaTeX$]], everything is ignored. Unfortunately, this also has the effect that multiple prices listed in the same paragraph and all text between each pair, will not be counted.
10. There is a group of special characters (#, *, `, ~, –, ^, =, <, >, +, |, /, :) that are removed completely wherever they are found.
11. Any "stand-alone" punctuation will not be counted. That is a punctuation mark surrounded by white space will not be counted.
12. Likewise, any stand-alone numbers will not be counted.
13. As a result of the above operations, things like table borders, horizontal rules, numbered list markers, unordered list bullets, and other miscellanea will not be counted.
14. Similarly, only the words in program listings will be counted; none of the special syntax of a programming language is counted.

Some of these decisions could be reasoned oppositely, but that is the way it is now. Others might make different choices.

As word counting methods go, this is a pretty expensive way to do it, but it doesn't seem to affect perceived program performance.