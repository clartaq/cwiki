---
title: A Timestamp Plugin for Sublime Text 3
author: david
date: 2018-02-10T10:16:57.00000-05:00 
modified: 2018-03-09T16:12:17.294154-05:00

tags:
  - sublime text
  - plugins
  - timestamp
---

 Since switching from Windows to a Mac in the middle of developing the [[About CWiki|CWiki]] blogging system, I lost access to the [MarkdownPad](http://markdownpad.com) [Markdown](https://daringfireball.net/projects/markdown/) editor -- it's Windows only. One of the nice features of MarkdownPad was the ability to insert a timestamp easily. It's what I used to insert the creation date and the time of the last modification.

 Since the switch, I've been using [Sublime Text 3 as my editor for Markdown](http://plaintext-productivity.net/2-04-how-to-set-up-sublime-text-for-markdown-editing.html) files. I wanted to recreate the ability to insert timestamps with a keystroke.

 I found this [answer](https://stackoverflow.com/questions/11879481/can-i-add-date-time-for-sublime-snippet#13882791) to a similar question on StackOverflow. 

 Getting a timestamp worked, but it was in the wrong format. (Not really wrong, just an old version that I used based on the MarkdownPad editor.) I wanted to make the the output of the `Export` function of CWiki as compatible as possible with the input expected by the [Hugo](https://gohugo.io) static blog generator. That required a different format. For example, as I write this, the ISO 8601 version of the current instant is `2018-03-09T10:26:35.249840-05:00`.

 Based on that, I added the following little snippet as a plugin for Sublime Text (Tools/Developer/New Plugin...).

 ```
import datetime, time, getpass
import sublime, sublime_plugin

from time import gmtime, strftime
from datetime import datetime, timezone

class AddDateCommand(sublime_plugin.TextCommand):
    def run(self, edit):
        self.view.run_command("insert_snippet", { "contents": "%s" %  datetime.now().strftime("%d %B %Y (%A)") } )

class AddTimeCommand(sublime_plugin.TextCommand):
    def run(self, edit):
        self.view.run_command("insert_snippet", { "contents": "%s" %  datetime.now().strftime("%H:%M") } )

class AddOldTimestampCommand(sublime_plugin.TextCommand):
    def run(self, edit):
        self.view.run_command("insert_snippet", { "contents": "%s" %  datetime.now().strftime("%m/%d/%Y %I:%M:%S %p") } )

class AddTimestampCommand(sublime_plugin.TextCommand):
    def run(self, edit):
        self.view.run_command("insert_snippet", { "contents": "%s" %  (datetime.now(timezone.utc).astimezone().isoformat() )} )
 ```

 The `AddOldTimestampCommand` produces timestamp formatting that is almost exactly the same as that used by MarkdownPad.

 The `AddTimestampCommand` produces the ISO format used by the export function and Hugo.

 Then I added the following code to my key preferences (Sublime Text/Preferences/Key Bindings)

 ```
[
   { "keys": ["alt+m"], "command": "markdown_preview", "args": {"target": "browser", "parser":"markdown"} },
   { "keys": ["ctrl+shift+,"], "command": "add_date" },
   { "keys": ["ctrl+shift+."], "command": "add_time" },
   { "keys": ["alt+shift+t"], "command": "add_old_timestamp" },
   { "keys": ["alt+t"], "command": "add_timestamp" }
]
 ```

 _Viola!_ Now I can insert timestamps into my front matter to my heart's content.