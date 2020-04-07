---
author: CWki
title: Markdown Help
date: 2018-11-18T15:39:33.103-05:00
modified: 2020-04-07T14:21:47.868-04:00
tags:
  - help
  - Markdown
---

<a name="top"></a>
This list is largely copied and adapted from the
[Markdown Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet). (**Note:** If you are viewing this help in the editor, wikilinks will not be displayed correctly.)

## Table of Contents ##
[Headers](#headers)  
[Emphasis](#emphasis)  
[Lists](#lists)  
[Links](#links)  
[Images](#images)  
[Code and Syntax Highlighting](#code)  
[Mathematics](#mathematics)  
[Tables of Contents](#toc)  
[Tables](#tables)  
[Blockquotes](#blockquotes)  
[Inline HTML](#html)  
[Horizontal Rule](#hr)  
[Line Breaks](#lines)  

<a name="headers"></a>
## Headers ##

To create a header, precede it with a number of
octothorpes (pound signs) indicating its level.

```plaintext
# H1 #
## H2 ##
### H3 ###
#### H4 ####
##### H5 #####
###### H6 #####
```

# H1 #
## H2 ##
### H3 ###
#### H4 ####
##### H5 #####
###### H6 ######

The trailing markers are totally optional. I tend to use them so I don't have to keep track of which markers require closure and which don't.

[Back to Top](#top)

<a name="emphasis"></a>
## Emphasis ##

Here are some examples of adding emphasis.

```plaintext
An emphasis, aka italics, is created with *asterisks* or 
_underscores_.

Strong emphasis, aka bold, with **asterisks** or 
__underscores__.

Emphasis can be combined with **asterisks and _underscores_**.

Markdown does not include syntax for underlining text. At the time the HTML tags for underlining were deprecated. They are not deprecated in HTML 5. But to still have to insert the tags for underline (`<u>...</u>`) manually to <u>underline</u> where you want it.

Strikethrough uses two tildes. ~~Scratch this.~~
```

An emphasis, aka italics, is created with *asterisks* 
or _underscores_.

Strong emphasis, aka bold, with **asterisks** or 
__underscores__.

Emphasis can be combined with **asterisks and _underscores_**.

Markdown does not include syntax for underlining text. At the time the HTML tags for underlining were deprecated. They are not deprecated in HTML 5. But to still have to insert the tags for underline (`<u>...</u>`) manually to <u>underline</u> where you want it.

Strikethrough uses two tildes. ~~Scratch this.~~

[Back to Top](#top)

<a name="lists"></a> 
## Lists ##

(In this example, leading and trailing spaces are shown with dots: ⋅)

```plaintext
1. First ordered list item
2. Another item
⋅⋅⋅* Unordered sub-list. 
1. Actual numbers don't matter, just that it's a number
⋅⋅⋅1. Ordered sub-list
4. And another item.

⋅⋅⋅You can have properly indented paragraphs within list items. 
Notice the blank line above, and the leading spaces (at least one, but we'll use three here to also align the raw Markdown).

⋅⋅⋅To have a line break without a paragraph, you will need to use two​ trailing spaces.⋅⋅
⋅⋅⋅Note that this line is separate but within the same paragraph.⋅⋅
⋅⋅⋅(This is contrary to the typical GFM line break behavior, where 
trailing spaces are not required.)


* Unordered list can use asterisks
- Or minuses
+ Or pluses
```

1. First ordered list item
2. Another item
   * Unordered sub-list. 
1. Actual numbers don't matter, just that it's a number
   1. Ordered sub-list
4. And another item.

   You can have properly indented paragraphs within list items. Notice the blank line above, and the leading spaces (at least one, but we'll use three here to also align the raw Markdown).

   To have a line break without a paragraph, you will need to use two trailing spaces.  
   Note that this line is separate but within the same paragraph.  
   (This is contrary to the typical GFM line break behavior, where 
trailing spaces are not required.)


* Unordered list can use asterisks
- Or minuses
+ Or pluses

[Back to Top](#top)

<a name="links"></a> 
## Links ##

There are two ways to create links.

```plaintext
[I'm an inline-style link](https://www.google.com)

[I'm an inline-style link with a title](https://www.google.com 
"Google's Homepage")

[I'm a reference-style link][Arbitrary case-insensitive 
reference text]

[I'm a relative reference to a repository file](../blob/master/LICENSE)

[You can use numbers for reference-style link definitions][1]

Or leave it empty and use the [link text itself].

URLs and URLs in angle brackets will automatically get turned into links. 
http://www.example.com or <http://www.example.com> and sometimes 
example.com (but not on Github, for example).

Some text to show that the reference links can follow later.

[arbitrary case-insensitive reference text]: https://www.mozilla.org
[1]: http://slashdot.org
[link text itself]: http://www.reddit.com
```

[I'm an inline-style link](https://www.google.com)

[I'm an inline-style link with a title](https://www.google.com "Google's Homepage")

[I'm a reference-style link][Arbitrary case-insensitive reference text]

[I'm a relative reference to a repository file](../blob/master/LICENSE)

[You can use numbers for reference-style link definitions][1]

Or leave it empty and use the [link text itself].

URLs and URLs in angle brackets will automatically get turned into links. 
http://www.example.com or <http://www.example.com> and sometimes 
example.com (but not on Github, for example).

Some text to show that the reference links can follow later.

[arbitrary case-insensitive reference text]: https://www.mozilla.org
[1]: http://slashdot.org
[link text itself]: http://www.reddit.com

[Back to Top](#top)

<a name="images"></a> 
## Images ##

```
Here's a logo from the [Markdown Here](https://github.com/adam-p/markdown-here) site, where I cribbed most of this help text from (hover to see the title text):

Inline-style: 
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

Reference-style: 
![alt text][logo]

[logo]: https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 2"

Here's a logo from CWiki demonstrating how a local image can be loaded too. This is the inline-style too.

![A 150x150 pixel version of the CWiki icon](/img/favicon/mstile-150x150.png "One of the CWiki icon files used as a favicon.")
```

Here's a logo from the [Markdown Here](https://github.com/adam-p/markdown-here) site, where I cribbed most of this help text from (hover to see the title text):

Inline-style: 
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

Reference-style: 
![alt text][logo]

[logo]: https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 2"

Here's a logo from CWiki demonstrating how a local image can be loaded too. This is the inline-style too.

![A 150x150 pixel version of the CWiki icon](/img/favicon/mstile-150x150.png "One of the CWiki icon files used as a favicon.")

[Back to Top](#top)

<a name="code"></a> 
## Code and Syntax Highlighting ##

Code blocks are part of the Markdown spec, but syntax highlighting isn't. However, many renderers -- like Github's and CWiki's -- support syntax highlighting. Which languages are supported and how those language names should be written will vary from renderer to renderer. CWiki supports highlighting for dozens of languages (and not-really-languages, like diffs and HTTP headers); to see the complete list, and how to write the language names, see the [highlight.js demo page](http://softwaremaniacs.org/media/soft/highlight/test.html).

```plaintext
Inline `code` has `back-ticks around` it.
```

Inline `code` has `back-ticks around` it.

Blocks of code are either fenced by lines with three back-ticks 
<code>```</code>, or are indented with four spaces. I recommend
only using the fenced code blocks -- they're easier and only they
support syntax highlighting.

<pre lang="no-highlight"><code>```javascript
var s = "JavaScript syntax highlighting";
alert(s);
```
 
```python
s = "Python syntax highlighting"
print s
```
 
```
No language indicated, so no syntax highlighting. 
But let's throw in a &lt;b&gt;tag&lt;/b&gt;.
```
</code></pre>



```javascript
var s = "JavaScript syntax highlighting";
alert(s);
```

```python
s = "Python syntax highlighting"
print s
```

```
No language indicated, so no syntax highlighting in CWiki
(varies on Github). 
But let's throw in a <b>tag</b>.
```

[Back to Top](#top)

<a name="mathematics"></a> 
## Mathematics ##

You can also enter mathematics using the familiar  [[About TeX|$\rm\TeX$]] notation. The program uses [MathJax](https://www.mathjax.org/) to transform your input into nicely formatted math.

The markup to show an equation on a line by itself uses double dollar signs, `$$,` to start and end a block of $\rm\TeX$ like this.

`$$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$$`

The above will render like this:

$$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$$

For inline math, use single dollar signs, `$`, to surround the math. For example, here is the markup for the same equation as above `$\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$` which renders as $\sum_{i=0}^n i^2 = \frac{(n^2+n)(2n+1)}{6}$.

[Back to Top](#top)

<a name="toc"></a> 
## Tables of Contents ##

CWiki does not include any extensions to produce Tables of Contents since they can be created quickly in almost any Markdown editor. See [[How to Make a Table of Contents]] for example.

[Back to Top](#top)

<a name="tables"></a> 
## Tables ##

Tables aren't part of the core Markdown spec, but they are part
of GFM and CWiki supports them. They are an easy way of
adding tables to your text -- a task that would otherwise require copy-pasting from another application.

Colons can be used to align columns.

```plaintext
| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |

There must be at least 3 dashes separating each header cell.
The outer pipes (|) are optional, and you don't need to make the​ raw Markdown line up prettily. You can also use inline 
Markdown.

Markdown | Less | Pretty
--- | --- | ---
*Still* | `renders` | **nicely**
1 | 2 | 3
```

| Tables        | Are           | Cool |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |

There must be at least 3 dashes separating each header cell. 
The outer pipes (|) are optional, and you don't need to make the
raw Markdown line up prettily. You can also use inline Markdown.

Markdown | Less | Pretty
--- | --- | ---
*Still* | `renders` | **nicely**
1 | 2 | 3

[Back to Top](#top)

<a name="blockquotes"></a> 
## Blockquotes ##

```plaintext
> Blockquotes are very handy to call out text from another source or authority.
> This line is part of the same quote.

Here is a break in the quoted text.

> This is a very long line that will still be quoted properly when it wraps. Oh boy, let's keep writing to make sure this is long enough to actually wrap for everyone. Oh, you can *put* **Markdown** into a blockquote. 
```

> Blockquotes are very handy to call out text from another source or authority.
> This line is part of the same quote.

Here is a break in the quoted text.

> This is a very long line that will still be quoted properly when it wraps. Oh boy, let's keep writing to make sure this is long enough to actually wrap for everyone. Oh, you can *put* **Markdown** into a blockquote. 

[Back to Top](#top)

<a name="html"></a> 
## Inline HTML ##

You can also use raw HTML in your Markdown, and it'll mostly work pretty well. 

```
<dl>
  <dt>Definition list</dt>
  <dd>Is something people use sometimes.</dd>

  <dt>Markdown in HTML</dt>
  <dd>Does *not* work **very** well. Use HTML <em>tags</em>.</dd>
</dl>
```

<dl>
  <dt>Definition list</dt>
  <dd>Is something people use sometimes.</dd>

  <dt>Markdown in HTML</dt>
  <dd>Does *not* work **very** well. Use HTML <em>tags</em>.</dd>
</dl>

[Back to Top](#top)

<a name="hr"></a> 
## Horizontal Rule ##

```plaintext
Three or more...

---

Hyphens

***

Asterisks

___

Underscores
```

Three or more...

---

Hyphens

***

Asterisks

___

Underscores

[Back to Top](#top)

<a name="lines"></a> 
## Line Breaks ##

My basic recommendation for learning how line breaks work is to
experiment and discover -- hit &lt;Enter&gt; once (_i.e._, insert one newline), 
then hit it twice (_i.e._, insert two newlines), see what happens. You'll
soon learn to get what you want. Or place two spaces before the newline
to create a line break within the current paragraph.
Here are some things to try out:

```plaintext
Here's a line for us to start with.

This line is separated from the one above by two newlines, so it
will be a *separate paragraph*.

This line is also a separate paragraph, but...⋅⋅
This line is only separated by a single newline, so it's a separate line in the *same paragraph*.
```
Note the two "spaces" (dots in the example above) following the ellipsis. The example above produces:

Here's a line for us to start with.

This line is separated from the one above by two newlines, so it
will be a *separate paragraph*.

This line also begins a separate paragraph, but...  
This line is only separated by a single newline, so it's a separate line in the *same paragraph*.

License: [CC-BY](https://creativecommons.org/licenses/by/3.0/)

[Back to Top](#top)