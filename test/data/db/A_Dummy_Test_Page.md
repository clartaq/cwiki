---
author: david
title: A Dummy Test Page
date: 2018-05-13T17:30:17.395-04:00
modified: 2018-09-08T16:15:41.014-04:00
tags:
  - A Tag for Testing
  - Second Tag
  - Third Tag
  - Wamsutta
---



### For the MDE Editor ###

What is happening with this editor now? This is where silly
test text can be written.
Here is a list of​ some of the things it can do.

* It can do a live preview of Markdown, like **bold** and _italic_.
* It can preview syntax highlighting.
* It can preview math typesetting.

Some inline code: `(+ 1 2 4 9)`.
And a code block:

```clojure
(defn plus-one
  [x]
  (+ 1 x))
```

Here's a [link](https://google.com) to an external page out on the Internet.

Here's a [[wikilink]] to a page that doesn't exist.

Here's a [[Wikilinks|wikilink]] to a page that _does_ exist.

The next couple of paragraphs show examples of what happens when wikilinks are embedded in code sections. The links get expanded when they should not.

Here's `a [[wikilink]] embedded` in an in-line code section.

    Here's a code block
    with [[a wikilink embedded]] in 
    it.

There are some ~~other~~ formatting commands too. You should check out some sites that have tests for Markdown compliance.

Some math inline $\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$ and some
on it's own line:

$$
\sigma = \sqrt{ \frac{1}{N} \sum_{i=1}^N (x_i -\mu)^2}
$$

Looks good, huh?