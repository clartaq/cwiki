---
author: david
title: Markdown Help
date: 2018-11-18T15:39:33.103-05:00
modified: 2018-11-29T17:29:37.579-05:00
tags:
  - help
  - Markdown
---

This list is largely copied and adapted from the
[Markdown Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet).

## Table of Contents ##
[Headers](#headers)  
[Emphasis](#emphasis)  
[Lists](#lists)  
[Links](#links)  
[Images](#images)  
[Code and Syntax Highlighting](#code)  
[Tables](#tables)  
[Blockquotes](#blockquotes)  
[Inline HTML](#html)  
[Horizontal Rule](#hr)  
[Line Breaks](#lines)  

## Headers <a name="headers"></a> ##

To create a header, precede it with a number of
octothorpes (pound signs) indicating its level.

```nohighlight
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

## Emphasis <a name="emphasis"></a> ##

Here are some examples of adding emphasis.

```
An emphasis, aka italics, is created with *asterisks* or 
_underscores_.

Strong emphasis, aka bold, with **asterisks** or 
__underscores__.

Emphasis can be combined with **asterisks and _underscores_**.

Strikethrough uses two tildes. ~~Scratch this.~~
```

An emphasis, aka italics, is created with *asterisks* 
or _underscores_.

Strong emphasis, aka bold, with **asterisks** or 
__underscores__.

Emphasis can be combined with **asterisks and _underscores_**.

Strikethrough uses two tildes. ~~Scratch this.~~

## Lists <a name="lists"></a> ##

(In this example, leading and trailing spaces are shown with dots: ⋅)

```nohighlight
1. First ordered list item
2. Another item
⋅⋅* Unordered sub-list. 
1. Actual numbers don't matter, just that it's a number
⋅⋅1. Ordered sub-list
4. And another item.

⋅⋅⋅You can have properly indented paragraphs within list items. 
Notice the blank line above, and the leading spaces (at least one, but we'll use three here to also align the raw Markdown).

⋅⋅⋅To have a line break without a paragraph, you will need to use 
two trailing spaces.⋅⋅
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

## Links <a name="links"></a> ##

There are two ways to create links.

```nohighlight
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


## Images <a name="images"></a> ##

```
Here's our logo (hover to see the title text):

Inline-style: 
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

Reference-style: 
![alt text][logo]

[logo]: https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 2"
```

Here's our logo (hover to see the title text):

Inline-style: 
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

Reference-style: 
![alt text][logo]

[logo]: https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 2"

## Code and Syntax Highlighting <a name="code"></a> ##

Code blocks are part of the Markdown spec, but syntax highlighting isn't. However, many renderers -- like Github's and CWiki's -- support syntax highlighting. Which languages are supported and how those language names should be written will vary from renderer to renderer. *Markdown Here* supports highlighting for dozens of languages (and not-really-languages, like diffs and HTTP headers); to see the complete list, and how to write the language names, see the [highlight.js demo page](http://softwaremaniacs.org/media/soft/highlight/test.html).

```nohighlight
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

## Tables <a name="tables"></a> ##

Tables aren't part of the core Markdown spec, but they are part
of GFM and CWiki supports them. They are an easy way of
adding tables to your text -- a task that would otherwise require copy-pasting from another application.

```nohighlight
Colons can be used to align columns.

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

Colons can be used to align columns.

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

## Blockquotes <a name="blockquotes"></a> ##

```nohighlight
> Blockquotes are very handy to call out text from another source or authority.
> This line is part of the same quote.

Here is a break in the quoted text.

> This is a very long line that will still be quoted properly when it wraps. Oh boy, let's keep writing to make sure this is long enough to actually wrap for everyone. Oh, you can *put* **Markdown** into a blockquote. 
```

> Blockquotes are very handy to call out text from another source or authority.
> This line is part of the same quote.

Here is a break in the quoted text.

> This is a very long line that will still be quoted properly when it wraps. Oh boy, let's keep writing to make sure this is long enough to actually wrap for everyone. Oh, you can *put* **Markdown** into a blockquote. 

## Inline HTML <a name="html"></a> ##

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


## Horizontal Rule <a name="hr"></a> ##

```nohighlight
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


## Line Breaks <a name="lines"></a> ##

My basic recommendation for learning how line breaks work is to
experiment and discover -- hit &lt;Enter&gt; once (_i.e._, insert one newline), 
then hit it twice (_i.e._, insert two newlines), see what happens. You'll
soon learn to get what you want. Or place two spaces before the newline
to create a line break within the current paragraph.
Here are some things to try out:

```nohighlight
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