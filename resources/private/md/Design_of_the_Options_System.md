---
author: CWiki
title: Design of the Options System
date: 2018-07-27T17:36:16.687-04:00
modified: 2018-07-28T16:52:18.894-04:00
tags:
  - design
  - options
  - preferences
  - technical note
---


The terms "options" and "preferences" are used interchangeably throughout the documentation. Either way, they represent choices the user can make about how the software operates.

When designing the system, the first two interlinked choices to make are "How should the data be represented?" and "Where should the data be stored?"

The data (at least so far) are just simple key/value pairs. The datatypes for the values are heterogeneous though.

One choice would be to use a traditional "ini" file; just a text file of values along with a string representation of each of the values.

But, using EDN would be a graceful way to handle all the different data types. It would be a text representation too. You would only need to use the `edn/read-string` function to read in the string, and all of the conversions would be handled.

But why add another file to the system when I have this nice relational database to take care of program information? But the data isn't relational -- it's just key/value pairs.

So in the end, I keep all of the options data in a map (EDN). That map is read and written to a table in the database where it a single row in its own table.
​
### The API ###

There are only a few functions that provide an interface to option handling. As with all database functions, they reside in the namespace `cwiki.models.wiki-db`.

---
#### get-option-map ####

**Arguments**: The database to be used. 

**Return Value**: A map of all of the key/value pairs for the options the program understands.

**Example Usage**: Something like `(get-option-map db-spec)` will return a map of all option key/value pairs in the database.

---
#### get-option-value ####

**Arguments**: A key for the option and the database to be interrogated.

**Return Value**: The value associated with the key or nil if there is no such key in the database. Note that the return value may be of any datatype, for example, string, number, float _etc_.

**Example Usage**: A call would look like `(get-option-value​ :root_page db-spec)`. A call like this will read the entire map of options from the database and return the value associated with the key.

---
#### set-option-value ####

**Arguments**: The key for the option, the value to be saved, and the database where it should be stored.

**Return Value**: The number of rows written to the database -- one unless an error occurred.

**Example Usage**: A call would look like `(set-option-value :wiki_name "CWiki" db-spec)`. This can be an expensive call since the current implementation reads the entire option map from the database, merges the new key/value pair, and writes the entire map back to the database synchro​nously.