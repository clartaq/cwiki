The Clojure Wiki Project

This is some information about writing a Wiki in Clojure

Got inspired by reading an article about uiki, a wiki written in Racket.

Since that project would not compile or run on Windows (or a current version of Ubuntu Linux), created the project riki based on it that runs on Windows.

I thought I would look at a *real* wiki, MediaWiki. Since I couldn't really find much about making it run on Windows, I tried it on Ubunutu Linux based on this blog post -- [Ubuntu Server + Caddy + Mediawiki](https://yawnbox.com/2017/05/07/ubuntu-server-caddy-mediawiki/). It didn't work flawlessly, but I got something running.

Since I didn't really want to use MySql and I wanted it to always be available, not just when multi-booting Linux, I tried to install on a Raspberry Pi 3, since I already have that running my River of News server.

- Started out based on the instructions in the above mentioned article.

- This is running on Raspian Jessie.

- Skipped the first step in the article, about firewall settings, since this part didn't work on the earlier attempt to install on Ubuntu. It caused subsequent attempts to update to fail, so left it out for now.

- Also skipped the step on altering the `/etc/apt/sources.list` file since this is not an ubuntu xenial distro.

- Did the usual `sudo apt-get update && sudo apt-get upgrade` commands in order to get everything up to date.

- Skipped the installation of MySql since I want to use Sqlite instead.

- Installed with the command line `sudo apt-get install sqlite`. That installed version

`david@river:~ $ sqlite -version
2.8.17`

which is significantly behind the current version, 3.20.1.

That also installed sqlite3.

```
david@river:~ $ sqlite3 -version
3.8.7.1 2014-10-29 13:59:56 3b7b72c4685aa5cf5e675c2c47ebec10d9704221

```

which is still quite far behind the current version.

- Tried to add the custom repository mentioned, but the add-apt-repository command was not available. Added it with 

```
sudo apt-get install software-properties-common python-software-properties
```

- Then added the repository:

```
sudo add-apt-repository ppa:ondrej/php
```

However, then trying to get the PHP 7.1 libraries failed as there was no template for that version. There also was no template for the earlier 7.0 version.

- Removed the ppa

```
```

- Found [this](https://blog.mythic-beasts.com/2017/03/22/php7-on-a-raspberry-pi-3-in-the-cloud/) post about adding PHP 7 for RPi3.

```bash
sudo apt-get install apt-transport-https lsb-release ca-certificates
sudo wget -O /etc/apt/trusted.gpg.d/php.gpg https://packages.sury.org/php/apt.gpg
sudo sh -c 'echo "deb https://packages.sury.org/php/ $(lsb_release -sc) main" > /etc/apt/sources.list.d/php.list'
sudo apt-get update`
```

- Now grab the php stuff

```
sudo apt-get install php7.1 php7.1-curl php7.1-gd php7.1-json \
    php7.1-mcrypt php7.1-fpm \
   php7.1-cli php7.1-intl php7.1-mbstring php7.1-xml
echo "<?=phpinfo()?>" >/var/www/html/info.php 
```

Installed caddy as directed.

# CSS #

CSS is inspired by that used by Github. See the [primer-css](https://github.com/primer/primer-css) project.

Flexbox question [here](https://stackoverflow.com/questions/23794713/flexbox-two-fixed-width-columns-one-flexible) seems to provide solution for fixed and variable columns in a row.