-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2016-12-21 00:04:45.624

-- views
DROP VIEW apppermview;

-- foreign keys
ALTER TABLE actionlog
    DROP FOREIGN KEY actions_context;

ALTER TABLE actionlog
    DROP FOREIGN KEY actions_requesters;

ALTER TABLE actionlog
    DROP FOREIGN KEY actions_resources;

ALTER TABLE appperm
    DROP FOREIGN KEY appperm_apps;

ALTER TABLE appperm
    DROP FOREIGN KEY appperm_permissions;

ALTER TABLE apps
    DROP FOREIGN KEY apps_requesters;

ALTER TABLE permissions
    DROP FOREIGN KEY permissions_resources;

ALTER TABLE policyrules
    DROP FOREIGN KEY policyrules_context;

ALTER TABLE policyrules
    DROP FOREIGN KEY policyrules_requesters;

ALTER TABLE policyrules
    DROP FOREIGN KEY policyrules_resources;

ALTER TABLE violationlog
    DROP FOREIGN KEY violations_context;

ALTER TABLE violationlog
    DROP FOREIGN KEY violations_requesters;

ALTER TABLE violationlog
    DROP FOREIGN KEY violations_resources;

-- tables
DROP TABLE actionlog;

DROP TABLE appperm;

DROP TABLE apps;

DROP TABLE contextlog;

DROP TABLE permissions;

DROP TABLE policyrules;

DROP TABLE requesters;

DROP TABLE resources;

DROP TABLE violationlog;

-- End of file.