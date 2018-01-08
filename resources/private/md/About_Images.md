---
title: About Images
author: CWiki
date: 12/02/2017 10:50:59 AM
updated: 1/8/2018 4:45:28 PM  
tags:
  - about
  - images
---

Just as you can insert links to images in Markdown, you can insert links to images in CWiki. There are a couple of ways to do it.

Here's an absolute link to a file in the file system on Windows,

`![An example image showing a snip of a screen shot.](file:///C:/projects/cwiki/resources/public/img/cwiki_snip.PNG)`

![An example image showing a snip of a screen shot.](file:///C:/projects/cwiki/resources/public/img/cwiki_snip.PNG)

For some reason it doesn't work.

You can create a _relative_ as well. This type of link shows the path from where the root of the web page is located to the image in a nearby location For example, a relative link to the same image is `![An example image showing a snip of a screen shot.](/public/img/cwiki_snip.png)
`. Here's how that looks.

![An example image showing a snip of a screen shot.](/public/img/cwiki_snip.png)


Of course, you can also store images remotely and link to them over the internet. The same image shown above is also saved on imgur and can be accessed like this `![An example image showing a snip of a screen shot.](https://i.imgur.com/Wi21mRL.png)` producing the following.

![An example image showing a snip of a screen shot.](https://i.imgur.com/Wi21mRL.png)

Another option that is not used in CWiki would be to store the images in the same database as all of the other page data, but that may be something for the future.
