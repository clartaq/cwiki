+++
author = "CWiki"
title = "About TeX and LaTeX"
tags = ["about" "LaTeX" "MathJax" "TeX"]
date = 2017-12-09T13:06:22.000-05:00
modified = 2020-06-10T14:21:05.516-04:00
+++

TeX (often displayed as $\rm\TeX$) is a typesetting system initially​ developed by the famous computer scientist [Donald Knuth](https://en.wikipedia.org/wiki/Donald_Knuth). One of his main reasons for creating such a system was to be able to produce professionally typeset mathematics. Even though this system was designed and written in the late '70s, it is still popular today.

Writing mathematics in $\rm\TeX$ is a deep subject in and of itself. Most writers really use a package of macros called LaTeX, or $\rm\LaTeX$, that is much more convenient than raw $\rm\TeX$. 

You are encouraged to learn as much as you want about the intricacies of these two systems. A good place to start might be the Wikipedia article [here](https://en.wikipedia.org/wiki/TeX) or the [$\rm\LaTeX$ wikibook](https://en.wikibooks.org/wiki/LaTeX) that covers $\rm\TeX$ as well.

We won't try to teach you $\rm\TeX$ or $\rm\LaTeX$ notation here. But to give you a flavor, here is what the formula for the population standard deviation looks like in  $\rm\LaTeX$ notation.

```latex
$$
\sigma = \sqrt{ \frac{1}{N} \sum_{i=1}^N (x_i -\mu)^2}
$$
```

And here is what it looks like when rendered.

$$
\sigma = \sqrt{ \frac{1}{N} \sum_{i=1}^N (x_i -\mu)^2}
$$

CWiki uses a program called [MathJax](https://www.mathjax.org/) to render mathematics entered in $\rm\LaTeX$ notation. The MathJax website is a good place to learn more about MathJax if you are so inclined. But for our purposes, if you know $\rm\LaTeX$ notation, you are good to go.