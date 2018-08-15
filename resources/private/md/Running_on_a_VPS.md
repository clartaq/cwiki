---
author: david
title: Running on a VPS
date: 2018-08-14T16:21:19.722-04:00
modified: 2018-08-14T17:36:54.868-04:00
tags:
  - VPS
---

You _can_ run CWiki on a VPS (Virtual Private Server) and connect to it through your browser, almost like running it locally, but I wouldn't recommend it. (See below.)

It was first demonstrated with an uberjar built from [this commit](https://bitbucket.org/David_Clark/cwiki/commits/bc56d131747a106fe6b851c394d474afaad77a9e). It ran on a [Linode](https://www.linode.com) VPS in the minimal configuration.

* 1	GB RAM
* 1	CPU Core
* 25	GB SSD Storage

From the project directory, I built the uberjar with the command:

`lein uberjar`

then copied the file to the remote server with `scp`.

`scp -P 1037 target/cwiki.jar david@example.com:cwiki.jar`

(The `P 1037` points it to the port that I use for `ssh` on that server.)

On the server, I started up the program with:

`java -jar cwiki.jar > cwiki.log &`

That starts the program and has it redirect any output to a log file, then returns control to the command line.

I used a [Caddy](https://caddyserver.com) server as a proxy. The contents of the `Caddyfile` I used was similar to this:

```
www.example.com {
    redir https://example.com{uri}
}

example.com {
    proxy / http://localhost:1350 {
        websocket
        transparent
    }
    log access.log
    errors error.log
    gzip
}
```
I started Caddy with:

`caddy &`

and that was it.

After starting CWiki, it built the database and was ready to log in. After logging in, I created a new admin user and deleted the default. Then I used the wiki as I normally would.

### Why Shouldn't I Do This? ###

I know almost nothing about the web and servers​. I know even less about the security of same. I wouldn't trust the program not to lose or corrupt data or get "pwned" by someone because of my lack of expertise. This was just a demonstration.

### Other Lessons ###

While I had the system up, I looked into its performance using the tools at [pingdom](https://tools.pingdom.com/#!/enU2S0/clajistan.com). According to them, speed was Ok, but it seemed slow to me, ta​king about half a second to load. About half of that was DNS lookup. They also had some other suggestions I may follow up on.