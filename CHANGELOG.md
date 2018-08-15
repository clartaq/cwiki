# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added ###
- Additional seed pages.

### Changed ###
- Updates to project dependencies.
- Updates to content of seed pages.

### Fixed ###

## [0.0.11] - 2018-08-14
### Added ###
- Full text search. This resolved issue [#13: Implement Full-Text Wiki Search"](https://bitbucket.org/David_Clark/cwiki/issues/13/implement-full-text-wiki-search) in commit [81cfa20](https://bitbucket.org/David_Clark/cwiki/commits/81cfa2006dcd6eed768d329b70727c3c3f792938)
- Ability to run CWiki as an uberjar located in an arbitrary directory.
- Demonstrated that CWiki can run from a VPS. 

### Changed ###
- Lots of updates to the seed pages.
- Updates to the project dependencies.

### Fixed ###
- Resolved issue [#18: "Need Sensible Tab Behavior"](https://bitbucket.org/David_Clark/cwiki/issues/18/need-sensible-tab-behavior) in commit [12ccd43](https://bitbucket.org/David_Clark/cwiki/commits/12ccd4320dd0).
- Resolved issue [#19: "Search should do nothing when given empty search terms"](https://bitbucket.org/David_Clark/cwiki/issues/19/search-should-do-nothing-when-given-empty) in commit [64ef8b9](https://bitbucket.org/David_Clark/cwiki/commits/64ef8b9d7aa9).

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