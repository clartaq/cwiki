Markdown provides a wealth of ways to easily format your content. For the details, I strongly recommend the [Markdown Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet).

This page provides a quick description of some of the ways you can format text in CWiki. It also shows how to use some of the extensions like syntax highlighting in code listings, mathematics, and tables. Remember, you can always just edit this page to see how something was done.

Here are some examples of the types of formatting and things you can do.

You can do inline formatting like:

* Making **bold** items.
* Marking words and *phrases* as italic.
* You can <u>underline</u> some things.
* You can also mark some things as deleted with ~~strikethrough~~.
* And do various combinations of the above like <u>***underlined bold italics***</u>.

## Headers ##

You can make headers by inserting one or more number signs ("#") (or pound signs or hashes) before and after the heading. The more number signs, the lower level the header. A total of six levels are supported.

Here are the different types supported and how to make them.

```
# Header 1 #
## Header 2 ##
### Header 3 ###
#### Header 4 ####
##### Header 5 #####
###### Header 6 ######
```

The above produces:

# Header 1 #
## Header 2 ##
### Header 3 ###
#### Header 4 ####
##### Header 5 #####
###### Header 6 ######

Note that not all Markdown editors require the heading text to be followed by the same number of number signs. However, others do. When importing or exporting text from CWiki, it's probably a good idea to match the number signs. As far as I know, all Markdown editors will work with that markup.

## Lists ##
An unordered, or "bullet", list can be created by preceding each line in the list with an asterisk or dash.

```
- a list item
- another list item
- list item with formatting like **bold**, *italic*.
```
produces:

- a list item
- another list item
- list item with formatting like **bold**, *italic*.

Note that the words within a list item may themselves have additional formatting applied. Lists can also have multiple levels, achieved by indenting the levels:

```
- top level
	- second level
		- third level
			- and so on.
```
- top level
	- second level
		- third level
			- and so on.

You can produce numbered lists by preceding the list items with a number and period. This:

```
1. The first item.
	1. The first sub-item.
	2. And another.
1. The second item.
1. And the third.
```
Produces:

1. The first item.
	1. The first sub-item.
	2. And another.
1. The second item.
1. And the third.

## Code Listings ##

CWiki handles code listings too. 

You can show code inline by surrounding it with the grave character (backquote) . For example, \`a = b + c*34;\` will render as: `a = b + c*34;`.

For multi-line blocks of code, the simplest method to show the listing is just to indent every line four spaces:

     function sayHello()
      {
         alert("Hello there");
      }

 Also, you can use the same convention as Github -- precede the code with a line consisting of three graves and end the listing with another line of three graves. 

    ```
         function sayHello()
          {
             alert("Hello there");
          }
    ```

produces:

```
     function sayHello()
      {
         alert("Hello there");
      }
```

CWiki uses Google's [code-prettify](https://github.com/google/code-prettify) to do language-specific syntax highlighting by following the initial three graves with the word "prettyprint".

Here is some markup to pretty-print a Clojure function.

	```prettyprint lang-clj
    (defn f [x]
      "A comment at the beginning."
      (let [my-x x]
        (println "my-x: " my-x) ; a line comment.
        (+ 2 my-x)))
	```

The markup above will render as:

```prettyprint lang-clj
(defn f [x]
  "A comment at the beginning."
  (let [my-x x]
    (println "my-x: " my-x) ; a line comment.
    (+ 2 my-x)))
```

Using `code-prettify` in this way requires an active internet connection.

There are a couple of additional ways to show code listings. Since Markdown passes through HTML unchanged, you can use the `<pre></pre>` and `<code></code>` tags directly.

The `<code></code>` can be used for inline code like <code>a37 = 1.5*c2 + 15.346;</code>

The `<pre></pre>`  tags can be used for code blocks. This might be useful if "prettyprint" is not recognizing the language in the block. You can specify a class containing the language like this `<pre class="prettyprint lang-clj">`, which indicates that the listing is in Clojure. Here's a listing of the same function as above, but the `<pre class="prettyprint lang-clj"></pre>` tags surround the code block.

<pre class="prettyprint lang-clj">(defn f [x]
  "A comment at the beginning."
  (let [my-x x]
    (println "my-x: " my-x) ; a line comment.
    (+ 2 my-x)))
</pre>

Whew!

## Tables ##

Tables are not a part of the original Markdown. However, they are very useful and available in many of the dialects of Markdown. CWiki implements the Github Flavored Markdown (GFM) version of tables.

Here's some markup

    | Fruit  |  Color | Description | Price ($/lb) |
    |--------|:------:|:------------|-------------:|
    | Apple  |   Red  |    Crisp    |         0.99 |
    | Pear   |  Green |    Sweet    |         1.49 |
    | Plum   | Purple |     Tart    |         1.99 |
    | Orange | Orange | Tangy       |         1.29 |

that produces this:

| Fruit |  Color | Description | Price ($/lb) |
|-------|:------:|:-----------|-------------:|
| Apple  |   Red  |    Crisp    |         0.99 |
| Pear   |  Green |    Sweet    |         1.49 |
| Plum   | Purple |     Tart    |         1.99 |
| Orange | Orange | Tangy |      1.29

There must be at least 3 dashes separating each header cell. The outer pipes (|) are optional, and you don't need to make the raw Markdown line up prettily. So, if you are using a proportional font to layout the table, you don't have to worry about trying to get everything to line up perfectly from line to line. You can also use inline Markdown to format items within the table cells.

    Less | Pretty | Markdown
    --- | --- | ---
    *Still* | `renders` | **nicely**
    1 | 2 | 3

gives:

Less | Pretty | Markdown
--- | --- | ---
*Still* | `renders` | **nicely**
1 | 2 | 3


## Mathematics ##

You can also enter mathematics using the familiar  [[About Tex|$\rm\TeX$]] notation. The program uses [MathJax](https://www.mathjax.org/) to transform your input into nicely formatted math.

The markup to show an equation on a line by itself uses double dollar signs, `$$`, to start and end a block of $\rm\TeX$ like this.

`$$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$$`

The above will render like this:

$$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$$

For inline math, use single dollar signs, `$` to surround the math. For example, here is the markup for the same equation as above `$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$` which renders as $\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$.

## Miscellaneous ##
### Horizontal Rule ###

Here is a horizontal rule produced by at least three or more hyphens in a row: `---`.

---

### Block Quotes ###
Block quotes are produced by preceding each line with a "greater than" sign, ">". For example, this:


    > This is what a block quote looks like.
    > It can go on and on, just like the
    > person you are quoting. You don't
    > have to use it for quotes though. You
    > can use it just as another way to make
    > a particular piece of text stand out.

produces this output:

> This is what a block quote looks like.
> It can go on and on, just like the
> person you are quoting. You don't
> have to use it for quotes though. You
> can use it just as another way to make
> a particular piece of text stand out.


## Tables of Contents ##

CWiki does not include any extensions to produce Tables of Contents since they can be created easily in almost any Markdown editor. See [[How to Make a Table of Contents]] for an example.
