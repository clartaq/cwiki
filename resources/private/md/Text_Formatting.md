---
author: CWiki

title: Text Formatting

date: 2017-10-24T08:57:33.000-04:00
modified: 2020-05-31T15:21:05.306-04:00
tags:
  - MathJax

  - LaTeX

  - Markdown

  - formatting

---

Markdown provides a wealth of ways to quickly format your content. For the details, I strongly recommend the [Markdown Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet). I won't repeat here what that page tells you. However, there are some CWiki-specific things that you should know about, especially some [GitHub-Flavored Markdown](https://github.github.com/gfm/) (GFM) items and how they are handled.

1. [Strikethrough](#strikethrough)
1. [Code Listings](#code-listings)
2. [Tables](#tables)
3. [Mathematics](#mathematics)
4. [Tables of Contents](#toc)

<a name="strikethrough"></a> 
## Strikethrough ##

Markdown has some ways to emphasize text in a paragraph: bold, italic, underline and combinations. For some reason, it does not have a method for formatting text with a strikethrough, something very useful in collaborative editing. GFM _does_ include an extension to support formatting with strikethrough. Surround the text with two tildes. For example, `~~strikethrough~~` will produce ~~strikethrough~~.

<a name="code-listings"></a> 
## Code Listings ##

CWiki handles code listings too. 

You can show code inline by surrounding it with the grave character (backquote) . For example, \`a = b + c\*34;\` will render as: `a = b + c*34;`.

For multi-line blocks of code, the simplest method to show the listing is to indent every line four spaces:

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

CWiki uses [highlight.js](https://highlightjs.org) to do syntax highlighting. It attempts to auto-recognize the language used, but on short samples, like those above, it can make an error. In the snippet below, it will not recognize that the language is Clojure. You can tell it which language to highlight by putting a hint in like so:

````plaintext
    ```clojure
        (defn f [x]
          "A comment at the beginning."
          (let [my-x x]
            (println "my-x: " my-x) ; a line comment.
            (+ 2 my-x)))
    ```
````

The markup above will render as:

```clojure
(defn f [x]
  "A comment at the beginning."
  (let [my-x x]
    (println "my-x: " my-x) ; a line comment.
    (+ 2 my-x)))
```

There are a couple of additional ways to show code listings too. They aren't described here, but you can look at [[Code Formatting and Highlighting]] for more details

<a name="tables"></a> 
## Tables ##

Tables are not a part of the original Markdown. However, they are handy and available in many of the dialects of Markdown. CWiki implements the Github Flavored Markdown (GFM) version of tables.

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
| Orange | Orange | Tangy |      1.29 |

There must be at least three dashes separating each header cell. The outer pipes ("|") are optional, and you don't need to make the raw Markdown line up prettily. So, if you are using a proportional font to layout the table, you don't have to worry about trying to get everything to line up correctly from line to line. You can also use inline Markdown to format items within the table cells.

    Less | Pretty | Markdown
    --- | --- | ---
    *Still* | `renders` | **nicely**
    1 | 2 | 3

gives:

Less | Pretty | Markdown
--- | --- | ---
*Still* | `renders` | **nicely**
1 | 2 | 3


<a name="mathematics"></a> 
## Mathematics ##

You can also enter mathematics using the familiar  [[About TeX and LaTeX|$\rm\LaTeX$]] notation. The program uses [MathJax](https://www.mathjax.org/) to transform your input into nicely formatted math.

The markup to show an equation on a line by itself uses double dollar signs, `$$,` to start and end a block of $\rm\TeX$ like this.

`$$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$$`

The above will render like this:

$$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$$

For inline math, use single dollar signs, `$`, to surround the math. For example, here is the markup for the same equation as above `$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$` which renders as $\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$.

<a name="toc"></a> 
## Tables of Contents ##

CWiki does not include any extensions to produce Tables of Contents since they can be created quickly in almost any Markdown editor. See [[How to Make a Table of Contents]] for example.