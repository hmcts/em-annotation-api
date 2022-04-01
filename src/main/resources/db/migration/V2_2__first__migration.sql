create sequence  IF NOT EXISTS hibernate_sequence START with 1 INCREMENT BY 1;

create TABLE annotation (
  id UUID NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   last_modified_by VARCHAR(50),
   last_modified_date TIMESTAMP WITHOUT TIME ZONE,
   annotation_type VARCHAR(255),
   page INTEGER,
   color VARCHAR(255),
   annotation_set_id UUID,
   CONSTRAINT pk_annotation PRIMARY KEY (id)
);

create TABLE annotation_set (
  id UUID NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   last_modified_by VARCHAR(50),
   last_modified_date TIMESTAMP WITHOUT TIME ZONE,
   document_id VARCHAR(255),
   CONSTRAINT pk_annotation_set PRIMARY KEY (id)
);

create TABLE annotation_tags (
  annotation_id UUID NOT NULL,
   name VARCHAR(20) NOT NULL,
   CONSTRAINT pk_annotation_tags PRIMARY KEY (annotation_id, name)
);

create TABLE bookmark (
  id UUID NOT NULL,
   name VARCHAR(30) NOT NULL,
   document_id UUID NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   num INTEGER NOT NULL,
   x_coordinate DOUBLE PRECISION,
   y_coordinate DOUBLE PRECISION,
   parent UUID,
   previous UUID,
   CONSTRAINT pk_bookmark PRIMARY KEY (id)
);

create TABLE comment (
  id UUID NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   last_modified_by VARCHAR(50),
   last_modified_date TIMESTAMP WITHOUT TIME ZONE,
   content VARCHAR(5000),
   annotation_id UUID,
   CONSTRAINT pk_comment PRIMARY KEY (id)
);

create TABLE idam_details (
  id VARCHAR(255) NOT NULL,
   forename VARCHAR(255),
   surname VARCHAR(255),
   email VARCHAR(255),
   CONSTRAINT pk_idam_details PRIMARY KEY (id)
);

create TABLE jhi_entity_audit_event (
  id UUID NOT NULL,
   entity_id UUID NOT NULL,
   entity_type VARCHAR(255) NOT NULL,
   action VARCHAR(20) NOT NULL,
   entity_value TEXT,
   commit_version INTEGER,
   modified_by VARCHAR(100),
   modified_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   CONSTRAINT pk_jhi_entity_audit_event PRIMARY KEY (id)
);

create TABLE jhi_persistent_audit_event (
  event_id UUID NOT NULL,
   principal VARCHAR(255) NOT NULL,
   event_date TIMESTAMP WITHOUT TIME ZONE,
   event_type VARCHAR(255),
   CONSTRAINT pk_jhi_persistent_audit_event PRIMARY KEY (event_id)
);

create TABLE jhi_persistent_audit_evt_data (
  event_id UUID NOT NULL,
   audit_data VARCHAR(255),
   name VARCHAR(255) NOT NULL,
   CONSTRAINT pk_jhi_persistent_audit_evt_data PRIMARY KEY (event_id, name)
);

create TABLE metadata (
  id BIGINT NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   document_id UUID NOT NULL,
   rotation_angle INTEGER NOT NULL,
   CONSTRAINT pk_metadata PRIMARY KEY (id)
);

create TABLE rectangle (
  id UUID NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   last_modified_by VARCHAR(50),
   last_modified_date TIMESTAMP WITHOUT TIME ZONE,
   x DOUBLE PRECISION,
   y DOUBLE PRECISION,
   width DOUBLE PRECISION,
   height DOUBLE PRECISION,
   annotation_id UUID,
   CONSTRAINT pk_rectangle PRIMARY KEY (id)
);

create TABLE tag (
  name VARCHAR(20) NOT NULL,
   created_by VARCHAR(255) NOT NULL,
   label VARCHAR(20) NOT NULL,
   color VARCHAR(20),
   CONSTRAINT pk_tag PRIMARY KEY (name)
);

alter table annotation_set add CONSTRAINT uc_e2d057d3317fb0a16b10aa5e9 UNIQUE (created_by, document_id);

alter table annotation add CONSTRAINT FK_ANNOTATION_ON_ANNOTATIONSET FOREIGN KEY (annotation_set_id) REFERENCES annotation_set (id);

alter table annotation add CONSTRAINT FK_ANNOTATION_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES idam_details (id);

alter table annotation add CONSTRAINT FK_ANNOTATION_ON_LAST_MODIFIED_BY FOREIGN KEY (last_modified_by) REFERENCES idam_details (id);

alter table annotation_set add CONSTRAINT FK_ANNOTATION_SET_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES idam_details (id);

alter table annotation_set add CONSTRAINT FK_ANNOTATION_SET_ON_LAST_MODIFIED_BY FOREIGN KEY (last_modified_by) REFERENCES idam_details (id);

alter table comment add CONSTRAINT FK_COMMENT_ON_ANNOTATION FOREIGN KEY (annotation_id) REFERENCES annotation (id);

alter table comment add CONSTRAINT FK_COMMENT_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES idam_details (id);

alter table comment add CONSTRAINT FK_COMMENT_ON_LAST_MODIFIED_BY FOREIGN KEY (last_modified_by) REFERENCES idam_details (id);

alter table rectangle add CONSTRAINT FK_RECTANGLE_ON_ANNOTATION FOREIGN KEY (annotation_id) REFERENCES annotation (id);

alter table rectangle add CONSTRAINT FK_RECTANGLE_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES idam_details (id);

alter table rectangle add CONSTRAINT FK_RECTANGLE_ON_LAST_MODIFIED_BY FOREIGN KEY (last_modified_by) REFERENCES idam_details (id);

alter table annotation_tags add CONSTRAINT fk_anntag_on_annotation FOREIGN KEY (annotation_id) REFERENCES annotation (id);

alter table annotation_tags add CONSTRAINT fk_anntag_on_tag FOREIGN KEY (name) REFERENCES tag (name);

alter table jhi_persistent_audit_evt_data add CONSTRAINT fk_jhi_persistent_audit_evt_data_on_persistent_audit_event FOREIGN KEY (event_id) REFERENCES jhi_persistent_audit_event (event_id);