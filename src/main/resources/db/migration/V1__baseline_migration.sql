
CREATE TABLE public.annotation (
    id uuid NOT NULL,
    annotation_type character varying(255),
    page integer,
    color character varying(10),
    annotation_set_id uuid,
    created_by character varying(50) NOT NULL,
    created_date timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by character varying(50),
    last_modified_date timestamp without time zone
);

CREATE TABLE public.annotation_set (
    id uuid NOT NULL,
    document_id character varying(255),
    created_by character varying(50) NOT NULL,
    created_date timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by character varying(50),
    last_modified_date timestamp without time zone
);

CREATE TABLE public.annotation_tags (
    annotation_id uuid NOT NULL,
    created_by character varying(255) NOT NULL,
    name character varying(20) NOT NULL
);

CREATE TABLE public.bookmark (
    id uuid NOT NULL,
    created_by character varying(50) NOT NULL,
    document_id uuid NOT NULL,
    name character varying(30) NOT NULL,
    num integer NOT NULL,
    x_coordinate double precision,
    y_coordinate double precision,
    parent uuid,
    previous uuid
);

CREATE TABLE public.comment (
    id uuid NOT NULL,
    content character varying(5000),
    annotation_id uuid,
    created_by character varying(50) NOT NULL,
    created_date timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by character varying(50),
    last_modified_date timestamp without time zone
);

CREATE TABLE public.databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp without time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20),
    contexts character varying(255),
    labels character varying(255),
    deployment_id character varying(10)
);

CREATE TABLE public.databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.idam_details (
    id character varying(255) NOT NULL,
    email character varying(255),
    forename character varying(255),
    surname character varying(255)
);

CREATE TABLE public.jhi_authority (
    name character varying(50) NOT NULL
);

CREATE TABLE public.jhi_entity_audit_event (
    id uuid NOT NULL,
    entity_id uuid NOT NULL,
    entity_type character varying(255) NOT NULL,
    action character varying(20) NOT NULL,
    entity_value text,
    commit_version integer,
    modified_by character varying(100),
    modified_date timestamp without time zone NOT NULL
);

CREATE TABLE public.jhi_persistent_audit_event (
    event_id uuid NOT NULL,
    principal character varying(1000) NOT NULL,
    event_date timestamp without time zone,
    event_type character varying(255)
);

CREATE TABLE public.jhi_persistent_audit_evt_data (
    event_id uuid NOT NULL,
    name character varying(150) NOT NULL,
    audit_data character varying(255)
);

CREATE TABLE public.metadata (
    id bigint NOT NULL,
    created_by character varying(50) NOT NULL,
    document_id uuid NOT NULL,
    rotation_angle integer NOT NULL
);

CREATE TABLE public.rectangle (
    id uuid NOT NULL,
    x numeric(10,6),
    y numeric(10,6),
    width numeric(10,6),
    height numeric(10,6),
    annotation_id uuid,
    created_by character varying(50) NOT NULL,
    created_date timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by character varying(50),
    last_modified_date timestamp without time zone
);

CREATE TABLE public.tag (
    created_by character varying(255) NOT NULL,
    name character varying(20) NOT NULL,
    label character varying(20) NOT NULL,
    color character varying(200)
);

ALTER TABLE public.annotation
    ADD CONSTRAINT annotation_pkey PRIMARY KEY (id);

ALTER TABLE public.annotation_set
    ADD CONSTRAINT annotation_set_pkey PRIMARY KEY (id);

ALTER TABLE public.annotation_tags
    ADD CONSTRAINT annotation_tags_pkey PRIMARY KEY (annotation_id, created_by, name);

ALTER TABLE public.bookmark
    ADD CONSTRAINT "bookmarkPK" PRIMARY KEY (id);

ALTER TABLE public.comment
    ADD CONSTRAINT comment_pkey PRIMARY KEY (id);

ALTER TABLE public.databasechangeloglock
    ADD CONSTRAINT databasechangeloglock_pkey PRIMARY KEY (id);

ALTER TABLE public.idam_details
    ADD CONSTRAINT "idam_detailsPK" PRIMARY KEY (id);

ALTER TABLE public.jhi_entity_audit_event
    ADD CONSTRAINT jhi_entity_audit_event_pkey PRIMARY KEY (id);

ALTER TABLE public.jhi_persistent_audit_event
    ADD CONSTRAINT jhi_persistent_audit_event_pkey PRIMARY KEY (event_id);

ALTER TABLE public.jhi_persistent_audit_evt_data
    ADD CONSTRAINT jhi_persistent_audit_evt_data_pkey PRIMARY KEY (event_id, name);

ALTER TABLE public.metadata
    ADD CONSTRAINT "metadataPK" PRIMARY KEY (id);

ALTER TABLE public.rectangle
    ADD CONSTRAINT rectangle_pkey PRIMARY KEY (id);

ALTER TABLE public.tag
    ADD CONSTRAINT "tagPK" PRIMARY KEY (created_by, name);

ALTER TABLE public.annotation_set
    ADD CONSTRAINT unique_set_per_doc_and_user UNIQUE (created_by, document_id);

CREATE INDEX idx_entity_audit_event_entity_id ON public.jhi_entity_audit_event USING btree (entity_id);

CREATE INDEX idx_entity_audit_event_entity_type ON public.jhi_entity_audit_event USING btree (entity_type);

CREATE INDEX idx_persistent_audit_event ON public.jhi_persistent_audit_event USING btree (principal, event_date);

CREATE INDEX idx_persistent_audit_evt_data ON public.jhi_persistent_audit_evt_data USING btree (event_id);

ALTER TABLE public.annotation_tags
    ADD CONSTRAINT "FK6pyecmrxhwm9gi9q42lo1o2tv" FOREIGN KEY (annotation_id) REFERENCES public.annotation(id);

ALTER TABLE public.annotation_tags
    ADD CONSTRAINT "FK9qipuwcr39qbh75i74xe682kg" FOREIGN KEY (created_by, name) REFERENCES public.tag(created_by, name);

ALTER TABLE public.annotation
    ADD CONSTRAINT fk_annotation_annotation_set_id FOREIGN KEY (annotation_set_id) REFERENCES public.annotation_set(id);

ALTER TABLE public.comment
    ADD CONSTRAINT fk_comment_annotation_id FOREIGN KEY (annotation_id) REFERENCES public.annotation(id);

ALTER TABLE public.jhi_persistent_audit_evt_data
    ADD CONSTRAINT fk_evt_pers_audit_evt_data FOREIGN KEY (event_id) REFERENCES public.jhi_persistent_audit_event(event_id);

ALTER TABLE public.rectangle
    ADD CONSTRAINT fk_rectangle_annotation_id FOREIGN KEY (annotation_id) REFERENCES public.annotation(id);

--
-- PostgreSQL database dump complete
--
