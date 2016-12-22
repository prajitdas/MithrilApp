-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2016-12-22 03:26:25.052

-- tables
-- Table: actionlog
CREATE TABLE actionlog (
    id INTEGER NOT NULL AUTOINCREMENT,
    resources_id INTEGER NOT NULL,
    context_id INTEGER NOT NULL,
    requesters_id INTEGER NOT NULL,
    time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    action INTEGER NOT NULL,
    CONSTRAINT actionlog_pk PRIMARY KEY (id)
) COMMENT 'Table showing actions taken for each context, resource, requester tuple';

-- Table: appperm
CREATE TABLE appperm (
    id INTEGER NOT NULL AUTOINCREMENT,
    apps_id INTEGER NOT NULL,
    permissions_id INTEGER NOT NULL,
    CONSTRAINT appperm_pk PRIMARY KEY (id,apps_id,permissions_id)
) COMMENT 'Table showing apps and permissions';

-- Table: apps
CREATE TABLE apps (
    id INTEGER NOT NULL AUTOINCREMENT,
    uid INTEGER NOT NULL,
    description TEXT NULL,
    assocprocname TEXT NULL,
    targetsdkver INTEGER NOT NULL,
    icon blob NOT NULL,
    label TEXT NOT NULL,
    name TEXT NOT NULL,
    verinfo TEXT NOT NULL,
    installed bool NOT NULL DEFAULT true,
    type TEXT NOT NULL,
    installdate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    requesters_id INTEGER NOT NULL,
    UNIQUE INDEX apps_unique_key (name),
    CONSTRAINT apps_pk PRIMARY KEY (id)
) COMMENT 'Table showing metadata for apps';

-- Table: contextlog
CREATE TABLE contextlog (
    id INTEGER NOT NULL AUTOINCREMENT,
    identity TEXT NOT NULL DEFAULT user,
    location TEXT NULL,
    activity TEXT NULL,
    temporal TEXT NULL,
    presenceinfo TEXT NULL,
    time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT contextlog_pk PRIMARY KEY (id)
) COMMENT 'Table showing log of current user context';

-- Table: permissions
CREATE TABLE permissions (
    id INTEGER NOT NULL AUTOINCREMENT,
    name TEXT NOT NULL,
    label TEXT NOT NULL,
    protectionlvl TEXT NOT NULL,
    permgrp TEXT NULL,
    flag TEXT NULL,
    description TEXT NULL,
    icon blob NOT NULL,
    resources_id INTEGER NOT NULL,
    UNIQUE INDEX permissions_unique_name (name),
    CONSTRAINT permissions_pk PRIMARY KEY (id)
) COMMENT 'Table showing metadata for permissions';

-- Table: policyrules
CREATE TABLE policyrules (
    id INTEGER NOT NULL AUTOINCREMENT,
    name TEXT NOT NULL,
    action INTEGER NOT NULL,
    context_id INTEGER NOT NULL,
    requesters_id INTEGER NOT NULL,
    resources_id INTEGER NOT NULL,
    CONSTRAINT policyrules_pk PRIMARY KEY (id)
) COMMENT 'Table showing policy rules defined for apps and requested resources in given context';

-- Table: requesters
CREATE TABLE requesters (
    id INTEGER NOT NULL AUTOINCREMENT,
    name TEXT NOT NULL DEFAULT *,
    CONSTRAINT requester_id PRIMARY KEY (id)
) COMMENT 'Table showing metadata for requesters of user data';

-- Table: resources
CREATE TABLE resources (
    id INTEGER NOT NULL AUTOINCREMENT,
    name TEXT NOT NULL,
    CONSTRAINT resource_id PRIMARY KEY (id)
) COMMENT 'Table showing metadata for resource being requested';

-- Table: violationlog
CREATE TABLE violationlog (
    id INTEGER NOT NULL AUTOINCREMENT,
    resources_id INTEGER NOT NULL,
    requesters_id INTEGER NOT NULL,
    context_id INTEGER NOT NULL,
    description TEXT NOT NULL,
    marker bool NULL,
    time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
-- Reference: actions_context (table: actionlog)
ALTER TABLE actionlog ADD CONSTRAINT actions_context FOREIGN KEY actions_context (context_id)
    REFERENCES contextlog (id);

-- Reference: actions_requesters (table: actionlog)
ALTER TABLE actionlog ADD CONSTRAINT actions_requesters FOREIGN KEY actions_requesters (requesters_id)
    REFERENCES requesters (id);

-- Reference: actions_resources (table: actionlog)
ALTER TABLE actionlog ADD CONSTRAINT actions_resources FOREIGN KEY actions_resources (resources_id)
    REFERENCES resources (id);

-- Reference: appperm_apps (table: appperm)
ALTER TABLE appperm ADD CONSTRAINT appperm_apps FOREIGN KEY appperm_apps (apps_id)
    REFERENCES apps (id)
    ON DELETE CASCADE;

-- Reference: appperm_permissions (table: appperm)
ALTER TABLE appperm ADD CONSTRAINT appperm_permissions FOREIGN KEY appperm_permissions (permissions_id)
    REFERENCES permissions (id);

-- Reference: apps_requesters (table: apps)
ALTER TABLE apps ADD CONSTRAINT apps_requesters FOREIGN KEY apps_requesters (requesters_id)
    REFERENCES requesters (id);

-- Reference: permissions_resources (table: permissions)
ALTER TABLE permissions ADD CONSTRAINT permissions_resources FOREIGN KEY permissions_resources (resources_id)
    REFERENCES resources (id);

-- Reference: policyrules_context (table: policyrules)
ALTER TABLE policyrules ADD CONSTRAINT policyrules_context FOREIGN KEY policyrules_context (context_id)
    REFERENCES contextlog (id);

-- Reference: policyrules_requesters (table: policyrules)
ALTER TABLE policyrules ADD CONSTRAINT policyrules_requesters FOREIGN KEY policyrules_requesters (requesters_id)
    REFERENCES requesters (id);

-- Reference: policyrules_resources (table: policyrules)
ALTER TABLE policyrules ADD CONSTRAINT policyrules_resources FOREIGN KEY policyrules_resources (resources_id)
    REFERENCES resources (id);

-- Reference: violations_context (table: violationlog)
ALTER TABLE violationlog ADD CONSTRAINT violations_context FOREIGN KEY violations_context (context_id)
    REFERENCES contextlog (id);

-- Reference: violations_requesters (table: violationlog)
ALTER TABLE violationlog ADD CONSTRAINT violations_requesters FOREIGN KEY violations_requesters (requesters_id)
    REFERENCES requesters (id);

-- Reference: violations_resources (table: violationlog)
ALTER TABLE violationlog ADD CONSTRAINT violations_resources FOREIGN KEY violations_resources (resources_id)
    REFERENCES resources (id);

-- End of file.