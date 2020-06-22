+++
author = "CWiki"
title = "About Quotations"
tags = ["about" "block quote" "CSS" "help" "HTML" "quotations" "Tufte"]
date = 2018-09-13T17:40:01.851-04:00
modified = 2018-09-14T12:50:08.454-04:00
+++

Of course, Markdown supports short quotations through the use of quotation marks, just like any editor. Markdown also can create formatting for more extended quotes, called "blockquotes." They look like this:

<blockquote cite="http://www.edwardtufte.com/bboard/q-and-a-fetch-msg?msg_id=0000hB">
 <p>[It is] notable that the Feynman lectures (3 volumes) write about all of physics in 1800 pages, using only 2 levels of hierarchical headings: chapters and A-level heads in the text. It also uses the methodology of <em>sentences</em> which then cumulate sequentially into <em>paragraphs</em>, rather than the grunts of bullet points. Undergraduate Caltech physics is very complicated material, but it didn’t require an elaborate hierarchy to organize.</p>
          <footer><a href="http://www.edwardtufte.com/bboard/q-and-a-fetch-msg?msg_id=0000hB">Edward Tufte, forum post, ‘Book design: advice and examples’ thread</a></footer>
</blockquote>

The above was stolen _verbatim_ from the demo page for [Tufte CSS](https://edwardtufte.github.io/tufte-css/). As such, it uses raw HTML to format the quote. Notice that the quote is pulled out of the regular stream of text by its indentation and the vertical rule along the left edge.​

There is special CSS that can format a nice looking footer within the blockquote for you to provide attribution if you like. This little bit of code: 

```
> Something quoted using Markdown. <footer>An Attribution</footer>
```

Will produce the following:

> Something quoted using Markdown. <footer>An Attribution</footer>

It's a little more complicated if you want to make the attribution a link since you need to add the raw HTML for the site; the usual wikilink style (`[text](link)`) won't work inside the `<footer>...</footer>` tags. Adding a link to `www.example.com`, for example, would look like this:

```
> Something quoted using Markdown. 
<footer><a href="www.example.com">An Attribution</a></footer>
```

> Something quoted using Markdown. <footer><a href="www.example.com">An Attribution</a></footer>