This page describes the ways you can format text in CWiki.

Here are some examples of the types of formatting and things you can do.

You can make headers. Here are the different types supported

# Header 1 #
## Header 2 ##
### Header 3 ###
#### Header 4 ####
##### Header 5 #####
###### Header 6 ######

Here is a horizontal rule.

----------


Here is an unordered list:

- a list item
- another list item
- list item with formatting like **bold**, *italic*.

Here is a numbered list:

1. The first item.
1. The second item.
1. And the third.

Here is some code.

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

