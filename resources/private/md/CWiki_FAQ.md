This FAQ answers some common questions about CWiki.

* What is CWiki good for?
* What is CWiki NOT good for?

#### What is CWiki good for? ####

* Easily creating and editing attractive content for viewing in any browser.
* Storage of unstructured knowledge.
* Documentation.
	* The ability to easily crosslink information within the same document rather than being forced present information in a linear fashion.
	* Syntax-highlighted programming examples.
	* Ability to present mathematical algorithms in mathematical notation.


#### What is CWiki NOT good for? ###

* Huge knowledge collections. The database will not scale to really large data collections. Might be possible if we switch to a more sophisticated DB.
* Collections of images. Not really a good fit for sites containing lots and lots of images.
* Hosting a knowledge base securely on the open Internet.

#### Why Users? ####

You may be asking yourself "If CWiki is a personal wiki, why does it allow multiple users?" Well, frankly, when other people see what you can do with it, the will ask "Ooh! Can I sign in and do that too?" When they do, you have the ability to let them create an account of their own. If you don't want anyone else to see or fiddle with what you are writing, you could give them a separate installation of the program.

#### Why Login/Logout? ####

Similar to the "Why Users?" question, it's a matter of keeping things separate when you have more than one user.

It can make initially signing in a bit more of a hassle every time you do it, but you can have your browser remember you login credentials if you want.

#### Why Doesn't the Content of a Page Use the Entire Width of the Browser Window? ####

This is on purpose. Since CWiki is intended to contain primarily readable content, the content is restricted in width so it is easier to read. Beyond a certain line length, it becomes more difficult to read text. Therefore, CWiki restricts lines to about 70 characters.

#### What Version of Markdown Does CWiki Use? ####

Well, it doesn't use any of the "standard" versions. That was one of the reasons for creating it in the first place. It is closest to "[Github Flavored Markdown](https://github.github.com/gfm/)" or GFM. GFM is itself based on [CommonMark](http://spec.commonmark.org/0.28/), an attempt to standardize the Markdown syntax.

The "authoritative" guide for the syntax used by CWiki is probably the [[Text Formatting]] page.

#### Does CWiki Support Automatic Generation of Tables of Contents? ####

Not exactly. But there is a method that will work with just about any Markdown editor. See [[How to Make a Table of Contents]] for an example.
