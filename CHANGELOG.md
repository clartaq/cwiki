# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added ###

- The "Backup", "Restore", "Import", and "Export All" pages display a short animation when the user clicks the button to proceed to indicate that _something_ is going on.
- A word count has been added to the information at the top of each page.
- New tags can be created by pressing the "Enter" key in an existing tag.
- For development builds, added a check for Chromium-based browsers before installing devtools.
- The editor preview can be toggled on and off. Typing is substantially faster for long documents when the preview is off. Otherwise, it gets "laggy" at about 30k characters. Toggle the preview with the new icon on the editor button bar.

### Changed ###

- Import now allows importing of multiple files at once. Existing pages with the same name are silently overwritten.
- Changed implementation of header menus. Now smoother with no "gaps".
- The Sente library, used for websocket communications between the server and client, has been updated. The new version includes a fix to a bug that didn't handle CSRF tokens correctly. This resolves issue #1.
- Pages that `POST` to the server, basically any form that sends information back to the server, now use an anti-forgery field in the page. This probably doesn't make any difference if you are running CWiki locally, like I do. Might make a difference if you run CWiki on a server.
- Updated to MathJax 3. In addition to keeping up with the technology, there is no more "MathJax Jitter" when editing documents with math and a live preview.
- Now including the MathJax package in the jar file at the expense of adding about 2MB to its size. Now you don't have to be online to edit pages with math in them.

### Fixed ###

- Fixed Issue #18. Can now import multiple files at once.
- Fixed Issue #10. Styling of the file input components for Import and Restore now match the rest of CWiki.
- Multipl form submission for the "Backup", "Restore", "Import" and "Export All" functions is now prevented. 
- Parts of some long page titles are no longer overwritten by the scroll bars on the "All Pages" page.
- Resolved a couple of development issues that led to error messages in the browser when using development builds.

## [0.1.7] - 2020-04-07 ##

This release is primarily intended to provide a simple backup and restore functionality. As further work is done, some incompatible changes may be made to the database. Using this backup and restore will let you keep any content you have created and restore it in the new version.

### Added ###

- Simple page backup and restore capability. **WARNING**: Images are not included in backup files.

### Changed ###

- Updated [Marked](https://github.com/markedjs/marked) to v0.8.2. (Did not record the version used earlier. It appeats to be 3.2.)
- Updated [highlight.js](https://highlightjs.org) from v9.12.0 to 9.18.1. Did not update the styles.
- Shortcut key to insert timestamp in editor is now ctrl/cmd-k.

### Fixed ###

- Removed deprecated Ring middleware.

## [0.1.6]  - 2020-03-27 ##

This is just a "quick and dirty" release to mark the change to Java 11.

### Added ###

- The width of the sidebar can now be set from the "Preferences" page or by dragging the border between the sidebar and the main article with the mouse.

### Changed ###

- Updated dependencies.
- Development and is now occurring with Java 11 (AdoptOpenJDK).
- Updated content.
- Several small tweaks to the CSS.
- Usages of the deprecated Reagent core/render and core/node were replaced by the functions of the same name from the reagent.dom namespace.

### Fixed ###

## [0.1.5] - 2019-06-11 ##

### Added ###

- The autosave functionality will not save changes to a newly created page until the page title has been changed from the default provided during creation.
- Tags listed in the reading page view are now "clickable" and generates to a page showing the titles of all pages using the tag.
- The Author field in the reading page view is now "clickable" and generates a page showing the titles of all pages attributed to that author.

### Changed ###

- Editing tags is smoother now. The cursor is placed correctly throughout the editing process.
- When a new tag is created, it is selected with its text input ready to replace with the new tag name just by starting to type.
- ClojureScript testing has been revamped. More details are in the `README.md` file.
- Updated project dependencies.

### Fixed ###

- Resolved issue [#39: "Tags listed in the page view should be clickable"](https://bitbucket.org/David_Clark/cwiki/issues/39/tags-listed-in-the-page-view-should-be) in commit [e30de89](https://bitbucket.org/David_Clark/cwiki/commits/e30de897606e8d23f562e8699e60f265063d947f).
- Resolved issue [#40: :"The author/user name shown on the page view should be clickable"](https://bitbucket.org/David_Clark/cwiki/issues/40/the-author-user-name-shown-on-the-page) in commit [d613628](https://bitbucket.org/David_Clark/cwiki/commits/d613628bd3d7858b799f9116bda2a4b2ffa7c957).
- Resolved issue [#41: "Deleting a Tag with Repeated Backspace/Delete Key Presses Should Show an Empty Editor Before Removing the Tag Editor
"](https://bitbucket.org/David_Clark/cwiki/issues/41/deleting-a-tag-with-repeated-backspace) in commit [27e5793](https://bitbucket.org/David_Clark/cwiki/commits/27e579364bcb).
- Measurements of tag widths are more accurate -- even those containing especially wide characters like "W"s and "M"s.

## [0.1.4] - 2019-01-07 ##

### Added ###

- Markdown help is now available from a dialog in the editor.
- A couple more keyboard shortcuts in the editor.
- The editor will now save any unsaved work automatically when it is hidden, as when switching to another browser tab.
- Small structural improvements

### Changed ###

- Now built with Clojure 10 and Java 11.
- Multi-level lists look a little better because of changes in line spacing.
- Put the editor "Done" button in the toolbar with all the other buttons.
   - Frees up some vertical space.
   - All the buttons are in the same place.
   - Now has a tooltip message like the other buttons.
- Autosave is now enabled by default. After a period of one second of inactivity in the editor, changes will be saved.

### Fixed ###

- Resolved issue [#24: "Improve CSS Styling of Multi-level Lists"](https://bitbucket.org/David_Clark/cwiki/issues/24/improve-css-styling-of-multi-level-lists) in commit [1c96347](https://bitbucket.org/David_Clark/cwiki/commits/1c96347dbf3c33ddbc9b297ca0cc6c93872769d0).
- Resolved issue [#25: "Modify History After Saving a Seed Page"](https://bitbucket.org/David_Clark/cwiki/issues/25/modify-history-after-saving-a-seed-page) in commit [0af5eb9facb8](https://bitbucket.org/David_Clark/cwiki/commits/0af5eb9facb8).

## [0.1.3] - 2018-11-17 ##

This time, most of the work was devoted to making the editor more usable. In my own use, it has much less friction. The layout helped as did the "Save File" shortcut key.

### Added ###

- Explicitly set the Markdown editor to use a fixed-width font for editing.
- Initial experiments with keyboard shortcuts.

### Changed ###

- Layout of the editor page was improved such that it no longer allows the window header (and associated links) to be scrolled off the top of the page.
- Punctuation characters can now be used in page titles without creating malformed URLs for the wikilinks.
- When you start editing a page, now the title is focused and the cursor is placed at the beginning of any existing title.
- Changed coloring in icon editing buttons for better contrast.
- Expanded and revised page content.
- Updated dependencies.
- Punctuation can now be used in page titles.

### Fixed ###

- Resolved issue [#6: "Deleting a Highlighted Tag does not always Work"](https://bitbucket.org/David_Clark/cwiki/issues/6/deleting-a-highlighted-tag-does-not-always). This was closed because it has not been observed since fundamentally changing tag creation, deletion, and editing in release 0.1.2.
- Resolved issue [#21: "Cant Edit Files with Slash ("/") Character in Page Title"](https://bitbucket.org/David_Clark/cwiki/issues/21/cant-edit-files-with-slash-character-in) in commit [01b62679ccb6]( https://bitbucket.org/David_Clark/cwiki/commits/01b62679ccb6).
- Resolved issue [#31 "Editor Should Flag Illegal Characters in Title"](https://bitbucket.org/David_Clark/cwiki/issues/31/editor-should-flag-illegal-characters-in) in commit [01b62679ccb6]( https://bitbucket.org/David_Clark/cwiki/commits/01b62679ccb6).
- Resolved issue [#30: "Importing a File Should Create the Page Title from the File Name"](https://bitbucket.org/David_Clark/cwiki/issues/30/importing-a-file-should-create-the-page) in commit [6e3d82958720](https://bitbucket.org/David_Clark/cwiki/commits/6e3d82958720).
- Resolved issue [#34: "The Tag Delete and Add Buttons Do Not Trigger an Autosave"](https://bitbucket.org/David_Clark/cwiki/issues/34/the-tag-delete-and-add-buttons-do-not) in commit [84c54811ebb9](https://bitbucket.org/David_Clark/cwiki/commits/84c54811ebb9).
- Resolved issue [#35: "Editor Layout is All Screwed Up Again"](https://bitbucket.org/David_Clark/cwiki/issues/35/editor-layout-is-all-screwed-up-again) in commit [9f393f4492d1](https://bitbucket.org/David_Clark/cwiki/commits/9f393f4492d1). This gets the layout just as I want it.
- Resolved issue [#37: "The \"Cancel\" Button at the Bottom of the Editor Should Not be Available after an Autosave or After Clicking the Save Button"](https://bitbucket.org/David_Clark/cwiki/issues/37/the-cancel-button-at-the-bottom-of-the) in commit [1eb9766b776a](https://bitbucket.org/David_Clark/cwiki/commits/1eb9766b776a). This has since become irrelevant since there is no longer a cancel button in the editor.
- Resolved issue [#38: "If there are no changes to save, the bottom \"Save Changes\" button should just say \"Done\""](https://bitbucket.org/David_Clark/cwiki/issues/38/if-there-are-no-changes-to-save-the-bottom) in commit [19c4cb1529b9](https://bitbucket.org/David_Clark/cwiki/commits/19c4cb1529b9). The editor now uses a single button to exit. It will warn if there are unsaved changes before exiting.

## [0.1.2] - 2018-10-30 ##
### Changed ###

- The marquee item in this release is the new handling of tags in the page editor. Much more flexible and inuitive. It also removes the artificial limit on the number of tags that can be associated with a page.
- Expanded and revised content.
- Updated project dependencies.

## [0.1.1] - 2018-10-16 ##
### Added ###
- CWiki now has a favicon.

### Changed ###
- The "Save" and "Cancel" buttons in the editor are now at the top of the editor window. They do not scroll out of view on long pages.

### Fixed ###
- Resolved issue [#22: "Editing User Profile does not Change Profile"](https://bitbucket.org/David_Clark/cwiki/issues/22/editing-user-profile-does-not-change) in commit [68d222f](https://bitbucket.org/David_Clark/cwiki/commits/68d222fe7f1a5580132bb07befdc8031e33b6ba5). Editing a profile now keeps any changes made.
- Resolved issue [#23: "Different Scrolling Behavior in Firefox"](https://bitbucket.org/David_Clark/cwiki/issues/23/different-scrolling-behavior-in-firefox) in commit [8c10386](https://bitbucket.org/David_Clark/cwiki/commits/8c10386e92f4284fa84fea1c4245fcf2fb330343). Scrolling of the browser window is now consistent on Safari, Firefox, Brave, and Opera.
- Resolved issue [#26: "Editing Area Mis-Sized
Create issue"](https://bitbucket.org/David_Clark/cwiki/issues/26/editing-area-mis-sized) in commit [dd51b0b](https://bitbucket.org/David_Clark/cwiki/commits/dd51b0b3326f4bffcc0bcca8d108445db379f614#chg-resources/public/css/styles.css).
- Resolved issue [#27: "Rule Separating Sidebar and Main Content is Mis-Sized"](https://bitbucket.org/David_Clark/cwiki/issues/27/rule-separating-sidebar-and-main-content) in commit [84d244d](https://bitbucket.org/David_Clark/cwiki/commits/84d244de72d67aaf5d8b500233e3c1d1a19a8d46). The vertical rule between the sidebar and content now extends the full height of the browser window.
- Resolved issue [#29: "Need to Split Parts of Document in Editor
Create issue"](https://bitbucket.org/David_Clark/cwiki/issues/29/need-to-slit-parts-of-document-in-editor) in commit [136621e](https://bitbucket.org/David_Clark/cwiki/commits/136621e55a737ede8b38926069bf5cc133baafe5). You will no longer receive messages from the browser about MathJax formatting while editing the page title or tags.

## [0.1.0] - 2018-09-20
### Added ###
- Scrolling down long pages no longer causes the title bar and menu to scroll out of view at the top of the screen except on FireFox.
- The sidebar no longer scrolls when the main content area scrolls. So scrolling to the end of a long article no longer causes the sidebar content to scroll out of view except on FireFox.
- Numerous additions to improve readability.
    + Font choices are easier to read.
    + Improved contrast meets accessibility standards.
    + Code listings are more consistent and easier to see, especially inline code spans.
- The footer has been removed.
- When editing, the edit pane and preview pane scroll in a semi-coordinated way.
- An additinal "About Quotations" page was added to the seed pages.
- An additional "Tools" section was added to the default sidebar. It has two non-functioning links for "Orphan Pages" and "Dead Pages."

### Changed ###
- Updated `ring/ring-devel` to 1.7.0.
- Updated `cider/piggieback` to 0.3.9
- Updated `com.vladsch.flexmark/flexmark` and extensions to 0.34.30.
- CSS is consolidated into a single file. The appearance of the editor preview pane is more consistent with the normal wiki page view.
- Improvements to blockquote handling.
- CSS is now minified for production builds (uberjars).
- Pages are now exported to the `exported-pages` subdirectory of the program execution directory.
- Removed many of the technical musings from the seed pages for the database.

### Fixed ###
- Resolved issue [#5: "Save and Cancel Buttons can Disappear while Editing"](https://bitbucket.org/David_Clark/cwiki/issues/5/save-and-cancel-buttons-can-disappear) in commit [944be76](https://bitbucket.org/David_Clark/cwiki/commits/944be76cb96417b932e3b9520a070286b37f338c).
- Resolved issue [#12: "Improve Styling of Inline Code
Create issue"](https://bitbucket.org/David_Clark/cwiki/issues/12/improve-styling-of-inline-code) in commit [944be76](https://bitbucket.org/David_Clark/cwiki/commits/944be76cb96417b932e3b9520a070286b37f338c).

## [0.0.12] - 2018-09-10
### Added ###
- Additional seed pages.
- An extension to the Markdown parser that understands and colorizes wikilinks just like the old one did. This means that code spans and blocks can now include wikilinks without having them converted to links. It should make writing examples that contain wikilinks easier.

### Changed ###
- Updates to project dependencies.
- Updates to content of seed pages.
- Mechanism used to build links on "All Users" and "All Tags" pages corrected to be more web-standard compliant.
- Removed namespaces that are no longer used now that the wikilink extension to flexmark is working as desired.

### Fixed ###
- Resolved issue [#19: "Search should do nothing when given empty search terms"](https://bitbucket.org/David_Clark/cwiki/issues/19/search-should-do-nothing-when-given-empty) in commit [1c92442](https://bitbucket.org/David_Clark/cwiki/commits/1c92442096e60162f16da1d37821c0d892c1203c).
- Resolved issue [#20: "Name of Front Page can be Changed"](https://bitbucket.org/David_Clark/cwiki/issues/20/name-of-front-page-can-be-changed) in commit [cc6dbc7](https://bitbucket.org/David_Clark/cwiki/commits/cc6dbc7c9dae93d688487ee2ae02d36edf75da50).
- Finally **really** resolved issue [#20: Markdown Parsers Need to Understand Wiki Links](https://bitbucket.org/David_Clark/cwiki/issues/14/markdown-parsers-need-to-understand-wiki) in commit [bc4eb80](https://bitbucket.org/David_Clark/cwiki/commits/bc4eb803d50bd3e6d1c8a5b9436f25c464a0fb27).

## [0.0.11] - 2018-08-14
### Added ###
- Full text search. This resolved issue [#13: "Implement Full-Text Wiki Search"](https://bitbucket.org/David_Clark/cwiki/issues/13/implement-full-text-wiki-search) in commit [81cfa20](https://bitbucket.org/David_Clark/cwiki/commits/81cfa2006dcd6eed768d329b70727c3c3f792938).
- Ability to run CWiki as an uberjar located in an arbitrary directory.
- Demonstrated that CWiki can run from a VPS. 

### Changed ###
- Lots of updates to the seed pages.
- Updates to the project dependencies.

### Fixed ###
- Resolved issue [#18: "Need Sensible Tab Behavior"](https://bitbucket.org/David_Clark/cwiki/issues/18/need-sensible-tab-behavior) in commit [12ccd43](https://bitbucket.org/David_Clark/cwiki/commits/12ccd4320dd0).
- ~~Resolved issue [#19: "Search should do nothing when given empty search terms"](https://bitbucket.org/David_Clark/cwiki/issues/19/search-should-do-nothing-when-given-empty) in commit [64ef8b9](https://bitbucket.org/David_Clark/cwiki/commits/64ef8b9d7aa9).~~ **UPDATE**: Apparently this issue was not completely fixed by this fix.

## [0.0.10] - 2018-08-01
### Added ###
- Added licensing description to the project file.
- Added this changelog, which resolved issue [#3: "Should Maintain a Changelog"](https://bitbucket.org/David_Clark/cwiki/issues/3/should-maintain-a-changelog) in commit [6b7b22a](https://bitbucket.org/David_Clark/cwiki/commits/6b7b22a).
- Moved some dev-only functions to the "dev" source tree.
- Added the ability for an admin user to save revisions to "seed" pages directly into the directory where seed pages are stored in the repository. This resolved issue [#16: "Let Admin Save Seed Pages"](https://bitbucket.org/David_Clark/cwiki/issues/16/let-admin-save-seed-pages) in commit [72e6644](https://bitbucket.org/David_Clark/cwiki/commits/72e6644b6215ac44713fa56cddd51f497283de6d).
- The exact middleware used during a build is determined by the environment: "dev" or "prod".
- More consistent use of logging. Removing `println` in favor of the [timbre](https://github.com/ptaoussanis/timbre) library.
- Auto-save functionality.

### Changed ###
- Lots of additions and revisions to the pages used to initialize the database on first-time use.
- Closed all remaining (inactive) branches. Just tidying up, so there is now a single repository head.
- Removed contents of the "Research" directory that should never have been in the repository in the first place. Things in this directory included some example CSS files and some examples of other Markdown editors.
- Multiple updates of project dependencies.

### Fixed ###
- Resolved issue [#1: "Characters that are invalid in SQL statements"](https://bitbucket.org/David_Clark/cwiki/issues/1/characters-that-are-invalid-in-sql) in commit [1de736b](https://bitbucket.org/David_Clark/cwiki/commits/1de736b).
- Resolved issue [#2: "Program-Generated Pages Should Not Be Editable"](https://bitbucket.org/David_Clark/cwiki/issues/2/program-generated-pages-should-not-be) in commit [80e6ab0](https://bitbucket.org/David_Clark/cwiki/commits/80e6ab0)
- Resolved issue [#17: "Page Author not Correctly Assigned for New Pages"](https://bitbucket.org/David_Clark/cwiki/issues/17/page-author-not-correctly-assigned-for-new) in commit [c14bce0](https://bitbucket.org/David_Clark/cwiki/commits/c14bce00feffac5bdd6793c2ed6c5287b6a7f3a3).
- Resolved issue [#11: "Make Program Options Usable"](https://bitbucket.org/David_Clark/cwiki/issues/11/make-program-options-usable) in commit [6eb603f](https://bitbucket.org/David_Clark/cwiki/commits/6eb603f84c79ff1cbf4c5928059d0830e35df737).

## [0.0.9] - 2018-06-03
### Added ###
- Made entries in the "All Users" page clickable links pointing to the pages named.

### Changed ###
- Completed and merged the "mde experimentation" branch, which makes a new, ClojureScript-based editor with live preview the default for editing within CWiki.
- Content revisions.
- Syntax highlighting on wiki pages is now done with highlightjs.
- Updated project dependencies.
- YAML frontmatter can now use ISO 8601 format dates.
- Various CSS tweaks.
- Moved to Clojure 1.9 for development.

## [0.0.8] - 2018-02-18 
### Added ###
- Import of files and export of pages with YAML frontmatter.
- Additional new and revised content.

### Changed ###
- Development moved from a Windows 10 machine to an iMac.
- Added focus to some form buttons.

## [0.0.7] - 2018-01-23
### Added ###
- CWiki now supports user-defined tags for each page.
- Added a non-functional search box to the page header in preparation for all text search capability.

### Changed ###
- Switched from using SQLite for the database to H2.
- Changed initial database creation to use a list of files stored in a file instead of hardcoding the list of files to import.
- Added YAML frontmatter to all initial pages.
- The content of initial pages updated.
- Updated copyright years.
- Eliminated infrastructure for wiki namespaces.
- Moved less frequently used commands to a new drop-down menu in the page header.
- Dependencies.

### Fixed ###
- Date format bugs.
- Problems with deleting and restoring the "About" page.
- Fixed to some integration tests.

## [0.0.6] - 2017-12-08
### Added ###
- Detection of initial startup of wiki makes user sign on as the admin user.

### Changed ###
- MathJax configuration.
- Made sorting of page names, users, tags case-insensitive in generated pages.
- More content.

### Fixed ###
- Users with the role of "reader" are no longer allowed to click a link to a non-existent page, thus allowing them to create a new page.

## [0.0.5] - 2017-12-01
### Added ###
- A page of "Admin" links.
- Admin pages to create, edit, and delete users.

### Changed ###
- Refinements to button styling.
- Revisions and updates to the content of seed pages.
- Updated dependencies.

## [0.0.4] - 2017-11-23
### Added ###
- Role-based authorization.
- "Admin-Only" special pages.

### Changed ###
- Revisions, updates and additions to content.

### Fixed ###
- Bug in the ordering of link CSS.

## [0.0.3] - 2017-11-20
### Added ###
- Now enforcing user authentication.
- License file.
- Support for tables.
- More "Special" pages.
- Login capability.

### Changed ###
- The content of README.md file expanded.
- Refactored database structure.
- Dependencies

### Fixed ###
- Many spelling errors.
- Got rid of double slashes in generated links.

## [0.0.2] - 2017-11-06
### Added ###
- More content.
- Capability to create an "Uberjar".
- A "Sidebar" view for most pages.
- Special handling of "Special" pages.

### Changed ###
- Lots of messin' with styles.
- Consistent use of "CWiki" rather than a mixture of "cwiki" and "CWiki."
- Dependencies.

### Fixed ###
- Canceling the editing of a new page now returns the user to the same page where they clicked on the "New" menu item.

## [0.0.1] - 2017-10-24
### Added ###
- Basic creation/editing/deleting of pages working.
- Initial page layout Hiccup and CSS.

### Changed ###
- Information in pages used at initial startup.
- Revisions to routes.
- Refactored creation of the initial database.

## [Initial Commit] - 2017-10-16
### Added ###
- Everything.