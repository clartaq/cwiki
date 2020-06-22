+++
author = "CWiki"
title = "Speed"
tags = ["performance" "VPS"]
date = 2018-08-21T16:21:18.719-04:00
modified = 2018-08-28T16:05:31.513-04:00
+++

Just like any other application, CWiki should be fast enough that the user does not notice any delays in their work. It should minimize the friction of creating, finding and reading the information contained in the wiki.

Since moving development to an iMac Pro, _everything_ runs fast. But not everyone who might use the program has such an excellent​ platform to run it on. Nor should they need it. When it comes down to it, CWiki does pretty mundane stuff.

So to test how it works on more typical computers, occasionally, I build an uberjar, send it off to a VPS, and run it remotely. For the "low end" test bed, I have a VPS on Linode with:

* 1 CPU core
* 1 GB RAM
* 25 GB SSD storage

I run CWiki behind a Nginx proxy that serves it over HTTPS.

So far, it seems to run pretty well on that machine too.