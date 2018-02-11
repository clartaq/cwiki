---
title: A Timestamp Plugin for Sublime Text 3
author: david
date: 2/10/2018 10:16:57 AM
updated: 02/10/2018 01:18:05 PM
tags:
  - sublime text
  - plugins
  - timestamp
---

 Since switching from Windows to a Mac in the middle of developing the [[About CWiki|CWiki]] blogging system, I lost access to the [MarkdownPad](http://markdownpad.com) [Markdown](https://daringfireball.net/projects/markdown/) editor -- it's Windows only. One of the nice features of MarkdownPad was the ability to insert a timestamp easily. It's what I used to insert the creation date and the time of the last modification.

 Since the switch, I've been using [Sublime Text 3 as my editor for Markdown](http://plaintext-productivity.net/2-04-how-to-set-up-sublime-text-for-markdown-editing.html) files. I wanted to recreate the ability to insert timestamps with a keystroke.

 I found this [answer](https://stackoverflow.com/questions/11879481/can-i-add-date-time-for-sublime-snippet#13882791) to a similar question on StackOverflow. Based on that, I added the following little snippet as a plugin on Sublime Text (Tools/Developer/New Plugin...).

 ```
    import datetime, getpass
    import sublime, sublime_plugin
    class AddDateCommand(sublime_plugin.TextCommand):
        def run(self, edit):
            self.view.run_command("insert_snippet", { "contents": "%s" %  datetime.date.today().strftime("%d %B %Y (%A)") } )
    
    class AddTimeCommand(sublime_plugin.TextCommand):
        def run(self, edit):
            self.view.run_command("insert_snippet", { "contents": "%s" %  datetime.datetime.now().strftime("%H:%M") } )
    
    class AddTimestampCommand(sublime_plugin.TextCommand):
        def run(self, edit):
            self.view.run_command("insert_snippet", { "contents": "%s" %  datetime.datetime.now().strftime("%m/%d/%Y %I:%M:%S %p") } )

 ```

 The timestamp formatting is almost exactly the same as that used by MarkdownPad.

 Then I added the following code to my key preferences (Sublime Text/Preferences/Key Bindings)

 ```
     { "keys": ["ctrl+shift+,"], "command": "add_date" },
     { "keys": ["ctrl+shift+."], "command": "add_time" },
     { "keys": ["alt+t"], "command": "add_timestamp" }
 ```

 _Viola!_ Now I can insert timestamps into my front matter to my heart's content.