-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2017-06-08 11:02:44.552

-- tables
-- Table: actionlog
CREATE TABLE actionlog (
    id int NOT NULL AUTO_INCREMENT,
    time timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    action int NOT NULL,
    apps_id int NOT NULL,
    context_id int NOT NULL,
    CONSTRAINT actionlog_pk PRIMARY KEY (id)
) COMMENT 'Table showing actions taken for each context, resource, requester tuple';

-- Table: appperm
CREATE TABLE appperm (
    id int NOT NULL AUTO_INCREMENT,
    apps_id int NOT NULL,
    permissions_id int NOT NULL,
    granted int NOT NULL,
    CONSTRAINT appperm_pk PRIMARY KEY (id,apps_id,permissions_id)
) COMMENT 'Table showing apps and permissions';

-- Table: apps
CREATE TABLE apps (
    id int NOT NULL AUTO_INCREMENT,
    uid int NOT NULL,
    description text NULL,
    assocprocname text NULL,
    targetsdkver int NOT NULL,
    icon blob NOT NULL,
    label text NOT NULL,
    name text NOT NULL,
    verinfo text NOT NULL,
    installed bool NOT NULL DEFAULT true,
    type int NOT NULL,
    installdate timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX apps_unique_key (name),
    CONSTRAINT apps_pk PRIMARY KEY (id)
) COMMENT 'Table showing metadata for apps';

-- Table: context
CREATE TABLE context (
    id int NOT NULL AUTO_INCREMENT,
    type text NOT NULL,
    value text NOT NULL,
    CONSTRAINT context_pk PRIMARY KEY (id)
) COMMENT 'Table showing context instances';

-- Table: contextlog
CREATE TABLE contextlog (
    id int NOT NULL AUTO_INCREMENT,
    time timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    context_id int NOT NULL,
    CONSTRAINT contextlog_pk PRIMARY KEY (id)
) COMMENT 'Table showing log of current user context';

-- Table: permissions
CREATE TABLE permissions (
    id int NOT NULL AUTO_INCREMENT,
    name text NOT NULL,
    label text NOT NULL,
    protectionlvl text NOT NULL,
    permgrp text NULL,
    flag text NULL,
    description text NULL,
    icon blob NOT NULL,
    op int NOT NULL DEFAULT -1,
    UNIQUE INDEX permissions_unique_name (name),
    CONSTRAINT permissions_pk PRIMARY KEY (id)
) COMMENT 'Table showing metadata for permissions';

-- Table: policyrules
CREATE TABLE policyrules (
    id int NOT NULL AUTO_INCREMENT,
    name text NOT NULL,
    action int NOT NULL,
    apps_id int NOT NULL,
    context_id int NOT NULL,
    op int NOT NULL,
    CONSTRAINT policyrules_pk PRIMARY KEY (id)
) COMMENT 'Table showing policy rules defined for apps and requested resources in given context';

-- Table: violationlog
CREATE TABLE violationlog (
    id int NOT NULL AUTO_INCREMENT,
    description text NOT NULL,
    marker bool NULL,
    time timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    apps_id int NOT NULL,
    context_id int NOT NULL,
    CONSTRAINT violationlog_pk PRIMARY KEY (id)
) COMMENT 'Table showing violations recorded by MithrilAC and subsequent user feedback';

-- views
-- View: apppermview
CREATE VIEW `mithril.db`.apppermview AS
SELECT
a.name as apppkgname,
p.name as permname,
p.protectionlvl as protectionlevel,
p.label as permlabel,
p.permgrp as permgroup
FROM
apps a, 
permissions p, 
appperm ap
WHERE
ap.apps_id = a.id
AND
ap.permissions_id = p.id;

-- foreign keys
-- Reference: actionlog_apps (table: actionlog)
ALTER TABLE actionlog ADD CONSTRAINT actionlog_apps FOREIGN KEY actionlog_apps (apps_id)
    REFERENCES apps (id);

-- Reference: actionlog_context (table: actionlog)
ALTER TABLE actionlog ADD CONSTRAINT actionlog_context FOREIGN KEY actionlog_context (context_id)
    REFERENCES context (id);

-- Reference: appperm_apps (table: appperm)
ALTER TABLE appperm ADD CONSTRAINT appperm_apps FOREIGN KEY appperm_apps (apps_id)
    REFERENCES apps (id)
    ON DELETE CASCADE;

-- Reference: appperm_permissions (table: appperm)
ALTER TABLE appperm ADD CONSTRAINT appperm_permissions FOREIGN KEY appperm_permissions (permissions_id)
    REFERENCES permissions (id);

-- Reference: contextlog_context (table: contextlog)
ALTER TABLE contextlog ADD CONSTRAINT contextlog_context FOREIGN KEY contextlog_context (context_id)
    REFERENCES context (id);

-- Reference: policyrules_apps (table: policyrules)
ALTER TABLE policyrules ADD CONSTRAINT policyrules_apps FOREIGN KEY policyrules_apps (apps_id)
    REFERENCES apps (id);

-- Reference: policyrules_context (table: policyrules)
ALTER TABLE policyrules ADD CONSTRAINT policyrules_context FOREIGN KEY policyrules_context (context_id)
    REFERENCES context (id);

-- Reference: violationlog_apps (table: violationlog)
ALTER TABLE violationlog ADD CONSTRAINT violationlog_apps FOREIGN KEY violationlog_apps (apps_id)
    REFERENCES apps (id);

-- Reference: violationlog_context (table: violationlog)
ALTER TABLE violationlog ADD CONSTRAINT violationlog_context FOREIGN KEY violationlog_context (context_id)
    REFERENCES context (id);

-- End of file.

