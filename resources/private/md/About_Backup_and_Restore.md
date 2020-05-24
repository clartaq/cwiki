---
author: CWiki

title: About Backup and Restore

date: 2017-12-03T10:12:36.000-05:00
modified: 2020-05-24T12:31:39.879-04:00
tags:
  - backup

  - restore

  - admin

  - special pages

  - about

---

Backing up and restoring the database file is pretty easy and can be accomplished in different ways.

## Manual Method ##

### Backup ###

The database file is called `database.db` and resides in the `resources\public\db` directory relative to the location where the program is running. To manually back up the file, copy it to a safe place.

Backup is only safe if there is no one else running the program when you make the copy. Since CWiki is intended as a personal wiki, that is believed to be the usual case. You can copy it to a remote location like [Dropbox](https://www.dropbox.com/) or anywhere else. It's just a file.

### Restore ###

Just copy the version of the saved database you made earlier back into the `resources\public\db` directory and rename it to `database.db` if you have made any changes.

Of course, you will lose any information that has been saved since the last backup.

Don't do this while CWiki is running.

## Automated Method ##

### NOTE: This is very preliminary. ###

Using the automated method has the advantage that it can be accomplished while CWiki is running.

If you are an administrator, you have access to two tasks for backup and restore. Links to those tasks are shown in the default [[Sidebar]] and on the [[Admin]] page. 

The [[backup|Backup]] command will take all of the pages in the database and compress them in a zip file. It will save the zip file in the `backups` directory. (This location is hard coded at the moment.) The name of the backup file will include a timestamp so you can create multiple versions over time if you wish.

If you would prefer additional safety, you can copy the saved file to a different location, perhaps off-site​.

The [[restore|Restore]] link will let you choose among any saved zip files in the `backups` directory and retrieve any pages stored therein. The saved pages will overwrite any pages in the database with the same name.

**<span style="color:red">WARNING</span>** Images are not included in backups. If they are stored externally and are loaded into a page you should be ok. If they are stored on your system, things will not work if they get deleted or you run the program from a different location.

**Note** that any pages that exist in the current database, but are not present in the backup, will not be overwritten. (This will be an option in the future.

**Note** also that the "Automated" method does not store and retrieve user information or preferences at this time. (This will be an option in the future.)