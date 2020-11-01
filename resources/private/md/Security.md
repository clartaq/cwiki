---
author: david

title: Security

date: 2020-10-31T10:22:25.103-04:00
modified: 2020-11-01T09:37:53.981-05:00
tags:
  - security

---

If you put any information that is at all sensitive into this wiki, **you need to understand this!**

Just because [[About CWiki|CWiki]] requires a password to sign in, doesn't mean your data is secure. At all.

## On Your Local System

As mentioned elsewhere, your data is stored as [Markdown](https://daringfireball.net/projects/markdown/). It's human-readable. Anyone who can sign in to your computer account (parent, sibling, child, significant other, friend) has access to your data. It can be found with a little fishing around.

To minimize this risk, you may need to adjust file permissions on the directory where CWiki is installed. File permissions are beyond the scope of this page, but there is a good introduction for Mac and Unix-based systems (like Linux) [here](http://hints.macworld.com/article.php?story=20001231152532966).

## On A Remote Server

If you put CWiki on a remote server, it might run, but again, there is no real security. You need to investigate directory permissions and server permissions to make you data inaccessible from the internet.

Best advice? Don't do it.

## Importing Pages

CWiki only makes very weak attempts to ensure that there is no malicious HTML embedded in the Markdown files that it imports. It is conceivable that importing a malicious page could damage your system somehow.

Likewise, the [[About Front Matter|front matter]] can be [YAML](https://en.wikipedia.org/wiki/YAML#Security). YAML is a markup language, but accepts language-specific tags that allow the creation of arbitrary local objects. It is conceivable that a maliciously constructed page could instantiate and execute damaging piece of code.