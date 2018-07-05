---
author: CWiki
title: About Images
date: 2017-12-02T10:50:59.000-05:00
modified: 2018-06-19T10:49:48.207-04:00
tags:
  - about
  - images
---


Just as you can insert links to images in Markdown, you can insert links to images in CWiki. There are a couple of ways to do it.

You can create a _relative_ link to the image in the `/public/img/` directory. This type of link shows the path from where the root of the web page is located in the image in a nearby location.​ For example, `![An example image showing a snip of a screenshot.](/public/img/cwiki_snip.png)
`. Here's how that looks.

![An example image showing a snip of a screenshot.](/public/img/cwiki_snip.png)

Of course, you can also store images remotely and link to them over the internet. The same image shown above is also saved on [imgur](https://imgur.com) and can be accessed like this `![An example image showing a snip of a screenshot.](https://i.imgur.com/Wi21mRL.png)` producing the following.

![An example image showing a snip of a screenshot.](https://i.imgur.com/Wi21mRL.png)

Another option that is not used in CWiki would be to store the images in the same database as all of the other page data, but that may be something for the future.