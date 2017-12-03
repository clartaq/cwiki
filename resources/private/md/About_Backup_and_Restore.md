Backing up and restoring the database file is pretty easy and can be accomplished in different ways.

## Manual Method ##

### Backup ###

The database file is called `database.db` and is located in the `resources\public\db` directory relative to the location where the program is running. To manually backup the file, just copy it to a safe location.

This is only safe if there is no one else running the program when you make the copy. Since CWiki is intended as a personal wiki, that is believed to be the usual case. You can copy it to a remote location like [Dropbox](https://www.dropbox.com/) or anywhere else. It's just a file.

### Restore ###

Just copy the version of the saved database you made earlier back into the `resources\public\db` directory and rename it to `database.db` if you have made any changes.

Of course, you will lose any information that has been saved since the last backup.

Don't do this while CWiki is running.

## Automated Method ##

Using the automated method has the advantage that it can be accomplished while CWiki is running.

If you are an administrator, you have access to two tasks for backup and restore. Links to those tasks are shown in the default [[Sidebar]] and on the [[Admin]] page. 

These commands will make copies of the database and store them in the `resources\private\db` directory. If you would prefer additional safety, you can copy the saved file to a different location, perhaps off site.

The [[restore|Restore]] link will retrieve the `database.db` file and move it to the active database directory `resources\public\db`.