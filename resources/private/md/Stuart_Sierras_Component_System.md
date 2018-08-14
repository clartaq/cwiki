---
author: david
title: Stuart Sierras Component System
date: 2018-07-19T16:16:38.676-04:00
modified: 2018-08-13T15:28:51.265-04:00
tags:
  - clojure
  - components
  - reloaded workflow
  - state management
  - technical note
---


Starting the implementation of user options (preferences) in CWiki got me thinking about refactoring the project into a shape that is more compatible with Stuart Sierras [reloaded workflow](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded). Naturally, that led to thinking about his [component](https://www.youtube.com/watch?v=13cmHf_kt-Q) architecture. Here are some more resources related to the component architecture.

* Stuart has a blog post about his reloaded workflow, linked above.
* The component code repository is on Github [here](https://github.com/stuartsierra/component).
* The [system](https://github.com/danielsz/system) repository contains many examples and ease-of-use functions.
* The blog post [Retrofitting the Reloaded pattern into Clojure projects](https://martintrojer.github.io/clojure/2013/09/07/retrofitting-the-reloaded-pattern-into-clojure-projects) talks about how you can adapt an existing project to one that can use the reloaded workflow.
* A video on the use of [component at Walmart](https://www.youtube.com/watch?v=av9Xi6CNqq4)
* A great StackOverflow [answer](https://stackoverflow.com/questions/29070883/how-to-use-stuart-sierras-component-library-in-clojure) about how to use the architecture.

So, starting from these resources, I tried to retrofit the component system into CWiki. After about a week's worth of effort, I just gave up.​ If you have the repository, you can examine my effort in the "experimenting" branch. I got several subsystems working, like the configuration, database, and server, but it just seemed like one complication after another. I wanted to get back to working on features and bugs, so I abandoned the branch.

##### Something I Learned #####

One of the key concepts in working with the component system is the elimination of global state. That's good for functional programming in general too. So it gave me a chance to go through my code and eliminate as much global state as possible.

The most straightforward technique, and an absolutely obvious one that just never occurred to me was to replace `def`ed vars with functions that returned the same data. Duh! Now I'm trying to get in the habit of replacing all of my global state data ​with such functions.

Maybe someday in the future, I'll complete the retrofit. Or, more likely, I'll try it at the beginning of another project.