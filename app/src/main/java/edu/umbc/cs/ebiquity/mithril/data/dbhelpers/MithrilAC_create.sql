-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2016-12-19 11:34:13.656

-- tables
-- Table: actionlog
CREATE TABLE actionlog (
    id int NOT NULL AUTO_INCREMENT,
    resources_id int NOT NULL,
    context_id int NOT NULL,
    requesters_id int NOT NULL,
    time timestamp NOT NULL,
    action int NOT NULL,
    CONSTRAINT actionlog_pk PRIMARY KEY (id)
);

-- Table: appperm
CREATE TABLE appperm (
    id int NOT NULL AUTO_INCREMENT,
    apps_id int NOT NULL,
    permissions_id int NOT NULL,
    CONSTRAINT appperm_pk PRIMARY KEY (id,apps_id,permissions_id)
);

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
    installdate timestamp NOT NULL,
    requesters_id int NOT NULL,
    UNIQUE INDEX apps_unique_key (name),
    CONSTRAINT apps_pk PRIMARY KEY (id)
) COMMENT 'Table showing information of the app data';

-- Table: contextlog
CREATE TABLE contextlog (
    id int NOT NULL AUTO_INCREMENT,
    identity text NOT NULL DEFAULT user,
    location text NULL,
    activity text NULL,
    temporal text NULL,
    time timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT contextlog_pk PRIMARY KEY (id)
) COMMENT 'Table showing information of the current user context';

-- Table: permissions
CREATE TABLE permissions (
    id int NOT NULL AUTO_INCREMENT,
    name text NOT NULL,
    label text NOT NULL,
    protectionlvl text NOT NULL,
    `group` text NULL,
    flag text NULL,
    description text NULL,
    icon blob NOT NULL,
    resources_id int NOT NULL,
    UNIQUE INDEX permissions_unique_name (name),
    CONSTRAINT permissions_pk PRIMARY KEY (id)
);

-- Table: policyrules
CREATE TABLE policyrules (
    id int NOT NULL AUTO_INCREMENT,
    name text NOT NULL,
    action int NOT NULL,
    context_id int NOT NULL,
    requesters_id int NOT NULL,
    resources_id int NOT NULL,
    CONSTRAINT policyrules_pk PRIMARY KEY (id)
);

-- Table: requesters
CREATE TABLE requesters (
    id int NOT NULL AUTO_INCREMENT,
    name text NOT NULL DEFAULT *,
    CONSTRAINT requester_id PRIMARY KEY (id)
) COMMENT 'Table showing information of the who is requesting the data';

-- Table: resources
CREATE TABLE resources (
    id int NOT NULL AUTO_INCREMENT,
    name text NOT NULL,
    CONSTRAINT resource_id PRIMARY KEY (id)
) COMMENT 'Table showing information of the what data is being requested';

-- Table: violationlog
CREATE TABLE violationlog (
    id int NOT NULL,
    resources_id int NOT NULL,
    requesters_id int NOT NULL,
    context_id int NOT NULL,
    description text NOT NULL,
    marker bool NULL,
    time timestamp NOT NULL,
    CONSTRAINT violationlog_pk PRIMARY KEY (id)
);

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
    REFERENCES apps (id);

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
