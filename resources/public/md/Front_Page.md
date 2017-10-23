This is the [[Front Page]] of your new [wiki](https://en.wikipedia.org/wiki/Wiki).

As the linked article describes, a wiki is a program that allows you to organize (or not) bits of information that you want to record somewhere. A wiki differs from a note taking application in that it allows you to link it's various parts together.

The syntax used is a slight variant of [Markdown](https://daringfireball.net/projects/markdown/). The syntax accepted by this wiki includes an extension to allow [wikilinks](https://en.wikipedia.org/wiki/Help:Link), that is, links to other pages within the wiki, in a Markdown file.

Here is an internal link to another page in this wiki about [[Other Wiki Software]].

an empty link [[wamble boota]] on a line

Here is a link to a [[Non-Existent Page]] so you can see what that looks like. Just like links to existing pages, the links to non-existent pages can have [[Non-Existent Page|display text]] and link text. You can click the link to create and edit the page. That's how you create new pages in CWiki, create a link with the title of the new page, click the new link, and edit it. CWiki knows which pages already exist and which do not.

When you are done editing, use the "Save Changes" button below the edit area to save the new page. The next time you see the link, it will look like all the other links that point to real pages.

See [[Internal Links]] for details on adding internal links to your pages.

## Special Pages ##

This wiki has some "special" pages, described here.

### The Front Page ###

The [[Front Page]] is the page that will be displayed when the program starts. Initially, it is set to a page named [[Front Page]], but you can change it to anything else by editing the setting in the [[Preferences]] page (see below.)

If you set it to the name of a non-existent page, the program will display the [[Front Page]] instead.

### The Preferences Page ###

The [[Preferences]] page, as its' name suggests, is a page where you can set the preferences for the way you want the program to operate.

### The Table of Contents Pane ###

This is a side pane on the left side of the screen. It is multi-purpose, depending on the contents of the main page.

### The "Orphaned Pages" Page ###

Most pages in a wiki link to other pages in the wiki. But they don't have to. If you create a page that is not referred to by any other page, it is said to be "orphaned". If you can't find a link to it and you can't remember the *exact* title, you might have a problem finding it again. You can open the "Orphaned Pages" page to see a list of all such pages.

Here are some examples of the types of formatting and things you can do.

Here is a list:

- a list item
- b list item
- list item with formatting like **bold**, *italic*.

Here is a numbered list:

1. The first item.
1. The second item.
1. And the third.


```prettyprint
(defn f [x]
  "A comment at the beginning."
  (let [my-x x]
    (println "my-x: " my-x) ; a line comment.
    (+ 2 my-x)))
```

Whew!

Some math:

$$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$$

Here is the same equation $\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$
inline.

Some inline code `a = c + 45;`.

<code><pre class="prettyprint">(defn f [x]
  "A comment at the beginning."
  (let [my-x x]
    (println "my-x: " my-x) ; a line comment.
    (+ 2 my-x)))
</pre></code>

Here's a link back to the [[main]] page.

Start editing.

Here's a new wiki-link to [[bufar]].

Here's a link to [[conf-interval]].

Here's a link to [[somethin]].
