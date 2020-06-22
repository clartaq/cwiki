+++
author = "CWiki"
title = "A Tip on Linking within a Page"
tags = ["help" "table of contents" "timestamp" "tip"]
date = 2018-12-31T10:23:22.012-05:00
modified = 2019-05-27T09:59:15.238-04:00
+++

Sometimes you want to link to sections within a page, in a [[How to Make a Table of Contents|Table of Contents]] for example.

An easy way for me to do that is to use an encoded timestamp. After creating the [[How to Insert a Timestamp in the Editor|timestamp]],  [[How to Encode a String in the Editor|encode]] it to make a safe URL, then use the encoded timestamp as the link target.

For example, here is a timestamp:

31 Dec 2018, 03:28:58 pm

and here is the encoded version:

31%20Dec%202018%2C%2003%3A28%3A58%20pm

Not pretty, but (probably) unique and safe for linking.


```
<a name="31%20Dec%202018%2C%2003%3A28%3A58%20pm"></a>
### A Heading ###
```

can be linked to with something like:

`Here is a link to the [heading](#31%20Dec%202018%2C%2003%3A28%3A58%20pm).`

Which looks like this:

<a name="31%20Dec%202018%2C%2003%3A28%3A58%20pm"></a>
### A Heading ###

Here is a link to the [heading](#31%20Dec%202018%2C%2003%3A28%3A58%20pm).