-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2017-06-06 04:44:15.361

-- views
DROP VIEW apppermview;

-- foreign keys
ALTER TABLE actionlog
    DROP FOREIGN KEY actionlog_apps;

ALTER TABLE actionlog
    DROP FOREIGN KEY actionlog_context;

ALTER TABLE appperm
    DROP FOREIGN KEY appperm_apps;

ALTER TABLE appperm
    DROP FOREIGN KEY appperm_permissions;

ALTER TABLE contextlog
    DROP FOREIGN KEY contextlog_context;

ALTER TABLE policyrules
    DROP FOREIGN KEY policyrules_apps;

ALTER TABLE policyrules
    DROP FOREIGN KEY policyrules_context;

ALTER TABLE violationlog
    DROP FOREIGN KEY violationlog_apps;

ALTER TABLE violationlog
    DROP FOREIGN KEY violationlog_context;

-- tables
DROP TABLE actionlog;

DROP TABLE appperm;

DROP TABLE apps;

DROP TABLE context;

DROP TABLE contextlog;

DROP TABLE permissions;

DROP TABLE policyrules;

DROP TABLE violationlog;

-- End of file.

