---
author: CWiki
title: Technical Note on Encoding Page Titles
date: 2018-12-16T16:48:54.307-05:00
modified: 2018-12-16T17:54:30.162-05:00
tags:
  - links
  - pages
  - technical note
---


There were long-standing issues with using punctuation in page titles. (See issues [#21](https://bitbucket.org/David_Clark/cwiki/issues/21/cant-edit-files-with-slash-character-in) and [#31](https://bitbucket.org/David_Clark/cwiki/issues/31/editor-should-flag-illegal-characters-in) in the repository.)

It's natural to want to use punctuation in page titles. Consider possible titles.

* "What is A/B Testing?"
* "Q: Are We Not Men? - A: We Are Devo!"
* "Algorithms + Data Structures = Programs"

All of these caused problems because CWiki uses page titles as HTML links to the article. I modeled the linking on the way [MediaWiki](https://www.mediawiki.org/wiki/MediaWiki) does it. However, my routing is not as sophisticated as MediaWiki's. My attempts to form such links failed because the punctuation in titles uses characters that are reserved (see section 2.2 of [RFC 3986](https://tools.ietf.org/html/rfc3986#section-2.2), having special meaning in [URL](https://en.wikipedia.org/wiki/URL)s.

Eventually, I came to understand that such characters could be encoded using a technique called "[percent encoding](https://en.wikipedia.org/wiki/Percent-encoding)." I wrote a custom  `LinkRenderer` for [flexmark-java](https://github.com/vsch/flexmark-java/wiki) and that worked fine except for one thing. The custom `LinkRenderer` always executes before the custom `LinkAttributeProvider`. (The attribute provider determines the look of links, either red for a non-existent page, blue for an existing page, or disabled if the user doesn't have rights to view the page.)

Since the attribute provider checks the database to determine if a page exists, it needs an un-encoded version of the page title. So the link renderer would encode the link, then the attribute provider would have to un-encode the link to check the database. That seemed like unnecessary work.

The next approach, the one eventually used, was to write a custom `LinkResolver`. It does the same type of percent-encoding, but it runs _after_ the attribute provider. That is what's working now.

One other tricky thing is the ordering of the extensions when the WikiLink parser is built. The custom `CWikiLinkResolverExtension` must come _beore_ the `WikiLinkExtension` for all of this to work correctly. Here's the way it appears in `cwiki.layouts.base.clj`.

```clojure
(def options (-> (MutableDataSet.)
                 (.set Parser/REFERENCES_KEEP KeepType/LAST)
                 (.set HtmlRenderer/INDENT_SIZE (Integer/valueOf 2))
                 (.set HtmlRenderer/PERCENT_ENCODE_URLS true)
                 (.set TablesExtension/COLUMN_SPANS false)
                 (.set TablesExtension/MIN_HEADER_ROWS (Integer/valueOf 1))
                 (.set TablesExtension/MAX_HEADER_ROWS (Integer/valueOf 1))
                 (.set TablesExtension/APPEND_MISSING_COLUMNS true)
                 (.set TablesExtension/DISCARD_EXTRA_COLUMNS true)
                 (.set TablesExtension/WITH_CAPTION false)
                 (.set TablesExtension/HEADER_SEPARATOR_COLUMN_MATCH true)
                 (.set WikiLinkExtension/LINK_FIRST_SYNTAX true)
                 (.set WikiLinkExtension/LINK_ESCAPE_CHARS "")
                 (.set Parser/EXTENSIONS (ArrayList.
                                           [(FootnoteExtension/create)
                                            (StrikethroughExtension/create)
                                            ; Order is important here.
                                            ; Our custom link resolver must
                                            ; preceed the default resolver.
                                            (CWikiLinkResolverExtension/create)
                                            (WikiLinkExtension/create)
                                            (CWikiLinkAttributeExtension/create)
                                            (TablesExtension/create)]))))
```

One final note. I'm not using the custom `LinkRenderer` or extension classes, but I've put them in a [Gist](https://gist.github.com/clartaq/dc08421bf860668b9ee88884e9bdbd47), along with some test code, for posterity.