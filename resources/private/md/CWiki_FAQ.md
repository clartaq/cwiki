+++
author = "CWiki"
title = "CWiki FAQ"
tags = ["cwiki" "FAQ"]
date = 2017-10-27T10:21:37.000-04:00
modified = 2018-09-17T16:35:53.246-04:00
+++

This FAQ answers some common questions about CWiki.

### Table of Contents ###

* [What is CWiki good for?](#What_is_CWiki_good_for?)
* [What is CWiki NOT good for?](#What_is_CWiki_NOT_good_for?)
* [Why Users?](#Why_Users?)
* [Why Login/Logout?](#Why_Login/Logout?)
* [Can Multiple People Use CWiki at the Same Time?](#Simultaneous_Users?)
* [Why No Mobile?](#Why_No_Mobile?)
* [Why Doesn't the Content of a Page Use the Entire Width of the Browser Window?](#Why_Doesn't_the_Content_of_a_Page_Use_the_Entire_Width_of_the_Browser_Window?)
* [What Version of Markdown Does CWiki Use?](#What_Version_of_Markdown_Does_CWiki_Use?)
*  [Does CWiki Support Automatic Generation of Tables of Contents?](#Does_CWiki_Support_Automatic_Generation_of_Tables_of_Contents?)

#### What is CWiki good for? <a name="What_is_CWiki_ good_ for?"></a> ####

* Easily creating and editing attractive content for viewing in any browser.
* Storage of unstructured knowledge.
* Documentation.
	* The ability to readily crosslink information within the same document rather than being forced to present information linearly.
	* Syntax-highlighted programming examples.
	* Ability to display mathematical algorithms in mathematical notation.

#### What is CWiki NOT good for? <a name="What_is_CWiki_NOT_good_for?"></a> ###

* Huge knowledge collections. The database will not scale to vast data collections. It might be possible if we switch to a DB designed for more "industrial" applications.
* Collections of images. Not a good fit for sites containing lots and lots of photographs or drawings.
* Hosting a knowledge-base securely on the open Internet. CWiki probably _can_ be hosted on an Internet-facing server, but security characteristics are not well-designed.
* Languages other than English. It is not internationalized at all.
* Writing the next Great American (or British, or Australian, or Canadian) novel. At least at present, the editing facilities are too primitive. Maybe in the future.

#### Why Users? <a name="Why_Users?"></a> ####

You may be asking yourself "If CWiki is a personal wiki, why does it allow multiple users?" Well, frankly, when other people see what you can do with it, they will ask "Ooh! Can I sign in and do that too?" When they do, you can let them create an account of their own. If you don't want anyone else to see or fiddle with what you are writing, you could give them a separate installation of the program.

#### Why Login/Logout? <a name="Why_Login/Logout?"></a> ####

Similar to the "Why Users?" question, it's a matter of keeping things separate when you have more than one user.

It can make initially signing in a bit more of a hassle every time you do it, but you can have your browser remember your login credentials if you want.

#### Can Multiple People Use CWiki at the Same Time? <a name="Simultaneous_Users?"></a> ####

I really don't know, but I doubt it. That was not the intent when writing the program and it has never been thoroughly tested for that use case.

Multiple people can probably sign in simultaneously and _read_ pages. However, multiple people editing the same page at once will likely not produce what you want. And other views will not update when someone makes edits.​

I would be interested in hearing your experience if you try it, but don't risk anything important (a production database) if you do.

#### Why No Mobile? <a name="Why_No_Mobile?"></a> ####

Just a personal preference on my part. I have a hard time seeing text on phones (tablets are OK), and it's even harder to read longer, detailed articles. So, I haven't put any effort into it. I don't want to deal with the additional complications if I'm not going to use the feature.

#### Why Doesn't the Content of a Page Use the Entire Width of the Browser Window? <a name="Why_Doesn't_the_Content_of_a_Page_Use_the_Entire_Width_of_the_Browser_Window?"></a> ####

This is on purpose. Since CWiki is intended to contain primarily *readable*​ content, the content is restricted in width, so it is easier to read. Beyond a certain line length, it becomes more difficult to read the text. Therefore, CWiki restricts lines to about 70 characters.

#### What Version of Markdown Does CWiki Use? <a name="What_Version_of_Markdown_Does_CWiki_Use?"></a> ####

Well, it doesn't use any of the "standard" versions. That was one of the reasons for creating it in the first place. It is closest to "[Github Flavored Markdown](https://github.github.com/gfm/)" or GFM. GFM is itself based on [CommonMark](http://spec.commonmark.org/0.28/), an attempt to standardize the Markdown syntax.

The "authoritative" guide for the syntax used by CWiki is probably the [[Text Formatting]] page.

#### Does CWiki Support Automatic Generation of Tables of Contents? <a name="Does_CWiki_Support_Automatic_Generation_of_Tables_of_Contents?"></a> ####

Not exactly. But there is a method that will work with just about any Markdown editor. See [[How to Make a Table of Contents]] for an example of how to do it. The ToC at the top of this page was created using the technique described there.