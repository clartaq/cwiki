---
author: CWiki
title: About Images
date: 2017-12-02T10:50:59.000-05:00
modified: 2018-09-18T18:04:58.794-04:00
tags:
  - about
  - images
---



Just as you can insert links to images in Markdown, you can insert links to images in CWiki. There are a couple of ways to do it.

You can create a _relative_ link to an image stored with the other "resources" used by the program. By default, the program has automatic access to a directory that branches off from the program directory -- `resources/public`. When CWiki installs itself, it will create a directory below the program directory named `resources/public/img` where it stores a few image files, like the one used for the example on this page. 

To embed an image from this location on​ your page, you can create a link like this, `![An example image showing a snip of a screenshot.](/img/cwiki_snip.PNG "A Snip of a Screenshot")
`. Here's how that looks.

![An example image showing a snip of a screenshot.](/img/cwiki_snip.PNG "A Snip of a Screenshot")

The text in the square brackets,`[ ]`, is what's called "alt text." It is shown when there is a problem accessing the actual image or it can be read by a screen reader for visually impaired readers. The next part of the link, in the parentheses, is a path to the image file. The final part of the link, in the quotation marks, is displayed when you hover the cursor over the image. The last part is optional.

**Note**: that the path to the image is case-sensitive. For example, `/img/cwiki_snip.png` will not work in the link above since the actual file name ends with `PNG` not `png`.

Of course, you can also store images remotely and link to them over the internet. The same image shown above is also saved on [imgur](https://imgur.com) and can be accessed like this `![An example image showing a snip of a screenshot.](https://i.imgur.com/Wi21mRL.png)` producing the following.

![An example image showing a snip of a screenshot.](https://i.imgur.com/Wi21mRL.png "A Snip of a Screenshot")

Another option that is not used in CWiki would be to store the images in the same database as all of the other page data, but that may be something for the future.