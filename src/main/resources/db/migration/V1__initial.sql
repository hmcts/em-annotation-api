--SET statement_timeout = 0;
--SET lock_timeout = 0;
--SET idle_in_transaction_session_timeout = 0;
--SET client_encoding = 'UTF8';
--SET standard_conforming_strings = on;
--SELECT pg_catalog.set_config('search_path', '', false);
--SET check_function_bodies = false;
--SET xmloption = content;
--SET client_min_messages = warning;
--SET row_security = off;
--
--SET default_tablespace = '';
--
--SET default_table_access_method = heap;

--
-- Name: annotation; Type: TABLE; Schema: public; Owner: postgres
--

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


--ALTER TABLE public.annotation OWNER TO postgres;

--
-- Name: annotation_set; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.annotation_set (
    id uuid NOT NULL,
    document_id character varying(255),
    created_by character varying(50) NOT NULL,
    created_date timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by character varying(50),
    last_modified_date timestamp without time zone
);


--ALTER TABLE public.annotation_set OWNER TO postgres;

--
-- Name: annotation_tags; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.annotation_tags (
    annotation_id uuid NOT NULL,
    created_by character varying(255) NOT NULL,
    name character varying(20) NOT NULL
);


--ALTER TABLE public.annotation_tags OWNER TO postgres;

--
-- Name: bookmark; Type: TABLE; Schema: public; Owner: postgres
--

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


--ALTER TABLE public.bookmark OWNER TO postgres;

--
-- Name: comment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.comment (
    id uuid NOT NULL,
    content character varying(5000),
    annotation_id uuid,
    created_by character varying(50) NOT NULL,
    created_date timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by character varying(50),
    last_modified_date timestamp without time zone
);


--ALTER TABLE public.comment OWNER TO postgres;

--
-- Name: databasechangelog; Type: TABLE; Schema: public; Owner: postgres
--

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


--ALTER TABLE public.databasechangelog OWNER TO postgres;

--
-- Name: databasechangeloglock; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


--ALTER TABLE public.databasechangeloglock OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: idam_details; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.idam_details (
    id character varying(255) NOT NULL,
    email character varying(255),
    forename character varying(255),
    surname character varying(255)
);


--ALTER TABLE public.idam_details OWNER TO postgres;

--
-- Name: jhi_authority; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.jhi_authority (
    name character varying(50) NOT NULL
);


--ALTER TABLE public.jhi_authority OWNER TO postgres;

--
-- Name: jhi_entity_audit_event; Type: TABLE; Schema: public; Owner: postgres
--

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


--ALTER TABLE public.jhi_entity_audit_event OWNER TO postgres;

--
-- Name: jhi_persistent_audit_event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.jhi_persistent_audit_event (
    event_id uuid NOT NULL,
    principal character varying(1000) NOT NULL,
    event_date timestamp without time zone,
    event_type character varying(255)
);


--ALTER TABLE public.jhi_persistent_audit_event OWNER TO postgres;

--
-- Name: jhi_persistent_audit_evt_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.jhi_persistent_audit_evt_data (
    event_id uuid NOT NULL,
    name character varying(150) NOT NULL,
    audit_data character varying(255)
);


--ALTER TABLE public.jhi_persistent_audit_evt_data OWNER TO postgres;

--
-- Name: metadata; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.metadata (
    id uuid NOT NULL,
    created_by character varying(50) NOT NULL,
    document_id uuid NOT NULL,
    rotation_angle integer NOT NULL
);


--ALTER TABLE public.metadata OWNER TO postgres;

--
-- Name: metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

--ALTER TABLE public.metadata ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
--    SEQUENCE NAME public.metadata_id_seq
--    START WITH 1
--    INCREMENT BY 1
--    NO MINVALUE
--    NO MAXVALUE
--    CACHE 1
--);


--
-- Name: rectangle; Type: TABLE; Schema: public; Owner: postgres
--

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


--ALTER TABLE public.rectangle OWNER TO postgres;

--
-- Name: tag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tag (
    created_by character varying(255) NOT NULL,
    name character varying(20) NOT NULL,
    label character varying(20) NOT NULL,
    color character varying(200)
);


--ALTER TABLE public.tag OWNER TO postgres;

--
-- Data for Name: annotation; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.annotation (id, annotation_type, page, color, annotation_set_id, created_by, created_date, last_modified_by, last_modified_date) FROM stdin;
--\.


--
-- Data for Name: annotation_set; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.annotation_set (id, document_id, created_by, created_date, last_modified_by, last_modified_date) FROM stdin;
--\.


--
-- Data for Name: annotation_tags; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.annotation_tags (annotation_id, created_by, name) FROM stdin;
--\.


--
-- Data for Name: bookmark; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.bookmark (id, created_by, document_id, name, num, x_coordinate, y_coordinate, parent, previous) FROM stdin;
--\.


--
-- Data for Name: comment; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.comment (id, content, annotation_id, created_by, created_date, last_modified_by, last_modified_date) FROM stdin;
--\.


--
-- Data for Name: databasechangelog; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.databasechangelog (id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, comments, tag, liquibase, contexts, labels, deployment_id) FROM stdin;
--00000000000001	pawel	db/changelog/00000000000000_initial_schema.xml	2022-08-02 10:01:06.528513	1	EXECUTED	8:a79eff311dbd410dee47cebe5a744b68	createTable tableName=jhi_persistent_audit_event; createTable tableName=jhi_persistent_audit_evt_data; addPrimaryKey tableName=jhi_persistent_audit_evt_data; createIndex indexName=idx_persistent_audit_event, tableName=jhi_persistent_audit_event; c...		\N	4.14.0	\N	\N	9430865831
--20220310-1	yogesh(manual)	db/changelog/00000000000000_initial_schema.xml	2022-08-02 10:01:06.576312	2	EXECUTED	8:b929114572eb7b0a7313d0289c894d28	renameColumn newColumnName=audit_data, oldColumnName=value, tableName=jhi_persistent_audit_evt_data		\N	4.14.0	\N	\N	9430865831
--20220509-1	yogesh (manual)	db/changelog/00000000000000_initial_schema.xml	2022-08-02 10:01:06.615039	3	EXECUTED	8:38ff08089545c2f3fdabead43c05ce65	modifyDataType columnName=principal, tableName=jhi_persistent_audit_event		\N	4.14.0	\N	\N	9430865831
--20180917100756-1	pawel	db/changelog/20180917100756_added_entity_AnnotationSet.xml	2022-08-02 10:01:06.640617	4	EXECUTED	8:d20f6ba6222f2f889a3b6101d1a287cb	createTable tableName=annotation_set		\N	4.14.0	\N	\N	9430865831
--20180917135654-audit-1	jhipster-entity-audit	db/changelog/20180917100756_added_entity_AnnotationSet.xml	2022-08-02 10:01:06.736128	5	EXECUTE8:ad7748c2f8c32f0da2daec22550d1bc4	addColumn tableName=annotation_set	\N	4.14.0	\N	\N	9430865831
--20180917100757-1	pawel	db/changelog/20180917100757_added_entity_Annotation.xml	2022-08-02 10:01:06.761658	6	EXECUTED	8:56e74039e5a5d8cfe1eb4c53487a32f5	createTable tableName=annotation		\N	4.14.0	\N	\N	9430865831
--20180917135654-audit-1	jhipster-entity-audit	db/changelog/20180917100757_added_entity_Annotation.xml	2022-08-02 10:01:06.792744	7	EXECUTED	8:1c1f0bffddb8ac4f184b5374da8aa225	addColumn tableName=annotation		\N	4.14.0	\N	\N	9430865831
--20180917100758-1	pawel	db/changelog/20180917100758_added_entity_Comment.xml	2022-08-02 10:01:06.826175	8	EXECUTED	8:13c9ee559530d818c0acbdd2a4756c73	createTable tableName=comment		\N	4.14.0	\N	\N	9430865831
--20180917135654-audit-1	jhipster-entity-audit	db/changelog/20180917100758_added_entity_Comment.xml	2022-08-02 10:01:06.847004	9	EXECUTED	8:944d31138baaa6b514d69e40985bbc80	addColumn tableName=comment		\N	4.14.0	\N	\N	9430865831
--20180917101255-1	pawel	db/changelog/20180917101255_added_entity_Rectangle.xml	2022-08-02 10:01:06.86375	10	EXECUTED	8:e67b82359d0aad52a6c26803a71e090c	createTable tableName=rectangle		\N	4.14.0	\N	\N	9430865831
--20180917112342-audit-1	jhipster-entity-audit	db/changelog/20180917101255_added_entity_Rectangle.xml	2022-08-02 10:01:06.880047	11	EXECUTED	8:149adda713a15802ab3250babd3fb95e	addColumn tableName=rectangle		\N	4.14.0	\N	\N	9430865831
--20180917112342	pawel	db/changelog/20180917112342_added_entity_EntityAuditEvent.xml	2022-08-02 10:01:06.915677	12	EXECUTED	8:832e07e33138b7aac2f12145892b10e1	createTable tableName=jhi_entity_audit_event; createIndex indexName=idx_entity_audit_event_entity_id, tableName=jhi_entity_audit_event; createIndex indexName=idx_entity_audit_event_entity_type, tableName=jhi_entity_audit_event; dropDefaultValue co...		\N	4.14.0	\N	\N	9430865831
--20180917100757-999	pawel	db/changelog/20180917100757_added_entity_constraints_AnnotationSet.xml	2022-08-02 10:01:06.944022	13	EXECUTED	8:620bd003f206af44ccf5a1f796e827b6	addUniqueConstraint constraintName=unique_set_per_doc_and_user, tableName=annotation_set		\N	4.14.0	\N	\N	9430865831
--20180917100757-2	pawel	db/changelog/20180917100757_added_entity_constraints_Annotation.xml	2022-08-02 10:01:06.957391	14	EXECUTED	8:2efc864142a6e282ab8031df30aeef6e	addForeignKeyConstraint baseTableName=annotation, constraintName=fk_annotation_annotation_set_id, referencedTableName=annotation_set		\N	4.14.0	\N	\N	9430865831
--20180917100758-2	pawel	db/changelog/20180917100758_added_entity_constraints_Comment.xml	2022-08-02 10:01:06.976347	15	EXECUTED	8:ab74818fd9460646816fe0fa0377114d	addForeignKeyConstraint baseTableName=comment, constraintName=fk_comment_annotation_id, referencedTableName=annotation	\N	4.14.0	\N	\N	9430865831
--20180917101255-2	pawel	db/changelog/20180917101255_added_entity_constraints_Rectangle.xml	2022-08-02 10:01:06.99157	16	EXECUTED	8:db01635d734b0b5da43590a72e54fc1e	addForeignKeyConstraint baseTableName=rectangle, constraintName=fk_rectangle_annotation_id, referencedTableName=annotation		\N	4.14.0	\N	\N	9430865831
--1538756035917-1	pawel (generated)	db/changelog/20181005171348_changelog.xml	2022-08-02 10:01:07.029083	17	EXECUTED	8:95369ebc97fc4088591402c7e5b0cf8d	createTable tableName=idam_details		\N	4.14.0	\N	\N	9430865831
--1538756035917-2	pawel (generated)	db/changelog/20181005171348_changelog.xml	2022-08-02 10:01:07.043318	18	EXECUTED	8:adc84be73e0eb357193e791048988807	createTable tableName=jhi_authority		\N	4.14.0	\N	\N	9430865831
--1538756035917-3	pawel (generated)	db/changelog/20181005171348_changelog.xml	2022-08-02 10:01:07.06242	19	EXECUTED	8:1088e54142ace039757e852ac115694e	addPrimaryKey constraintName=idam_detailsPK, tableName=idam_details		\N	4.14.0	\N	\N	9430865831
--1583250597900-2	tomelliott (generated)	db/changelog/20200303154954_added_entity_Tag.xml	2022-08-02 10:01:07.08014	20	EXECUTED	8:123f5d7ee994bb20086fdbddbcb77a26	createTable tableName=annotation_tags		\N	4.14.0	\N	\N	9430865831
--1583250597901-7	tomelliott (generated)	db/changelog/20200303154954_added_entity_Tag.xml	2022-08-02 10:01:07.089455	21	EXECUTED	8:a0f0d5452d682d2c26d013bbf8c48833	createTable tableName=tag		\N	4.14.0	\N	\N	9430865831
--1583250597900-3	tomelliott (generated)	db/changelog/20200303154954_added_entity_Tag.xml	2022-08-02 10:01:07.101482	22	EXECUTED	8:c9a038712efa64d66dabaf33e91da4f5	addPrimaryKey constraintName=tagPK, tableName=tag		\N	4.14.0	\N	\N	9430865831
--1583250597900-5	tomelliott (generated)	db/changelog/20200303154954_added_entity_Tag.xml	2022-08-02 10:01:07.111263	23	EXECUTED	8:72b90e1c9c6121d10dc74ecdd75df33d	addForeignKeyConstraint baseTableName=annotation_tags, constraintName=FK6pyecmrxhwm9gi9q42lo1o2tv, referencedTableName=annotation		\N	4.14.0	\N	\N	9430865831
--1583250597900-8	tomelliott (generated)	db/changelog/20200303154954_added_entity_Tag.xml	2022-08-02 10:01:07.118886	24	EXECUTED	8:71ae54ea517fa56e5bf6f7fec9a43d0e	addForeignKeyConstraint baseTableName=annotation_tags, constraintName=FK9qipuwcr39qbh75i74xe682kg, referencedTableName=tag	\N	4.14.0	\N	\N	9430865831
--1584707813647-1	tomelliott (generated)	db/changelog/20200320123648_added_entity_Bookmark.xml	2022-08-02 10:01:07.133901	25	EXECUTED	8:4e178baed690f35cd975fe7afbc86f77	createTable tableName=bookmark		\N	4.14.0	\N	\N	9430865831
--1587568441367-1	tomelliott (generated)	db/changelog/20200422103555_added_entity_constraints_Bookmark.xml	2022-08-02 10:01:07.152745	26	EXECUTE8:389520c5391642364b3d872c4888d791	addColumn tableName=bookmark		\N	4.14.0	\N	\N	9430865831
--1593467036860-1	yogeshhullatti (generated)	db/changelog/20200629224352_added_entity_Metadata.xml	2022-08-02 10:01:07.16215	27	EXECUTED	8:9df4ee4464a4a10dfcf2717c73a52620	createSequence sequenceName=hibernate_sequence		\N	4.14.0	\N	\N	9430865831
--1593467036860-2	yogeshhullatti (generated)	db/changelog/20200629224352_added_entity_Metadata.xml	2022-08-02 10:01:07.207003	28	EXECUTED	8:5f21dd3a090250d29589e773e71229dd	createTable tableName=metadata		\N	4.14.0	\N	\N	9430865831
--\.


--
-- Data for Name: databasechangeloglock; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.databasechangeloglock (id, locked, lockgranted, lockedby) FROM stdin;
--1	f	\N	\N
--\.


--
-- Data for Name: idam_details; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.idam_details (id, email, forename, surname) FROM stdin;
--\.


--
-- Data for Name: jhi_authority; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.jhi_authority (name) FROM stdin;
--\.


--
-- Data for Name: jhi_entity_audit_event; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.jhi_entity_audit_event (id, entity_id, entity_type, action, entity_value, commit_version, modified_by, modified_date) FROM stdin;
--\.


--
-- Data for Name: jhi_persistent_audit_event; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.jhi_persistent_audit_event (event_id, principal, event_date, event_type) FROM stdin;
--\.


--
-- Data for Name: jhi_persistent_audit_evt_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.jhi_persistent_audit_evt_data (event_id, name, audit_data) FROM stdin;
--\.


--
-- Data for Name: metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.metadata (id, created_by, document_id, rotation_angle) FROM stdin;
--\.


--
-- Data for Name: rectangle; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.rectangle (id, x, y, width, height, annotation_id, created_by, created_date, last_modified_by, last_modified_date) FROM stdin;
--\.


--
-- Data for Name: tag; Type: TABLE DATA; Schema: public; Owner: postgres
--

--COPY public.tag (created_by, name, label, color) FROM stdin;
--\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

--SELECT pg_catalog.setval('public.hibernate_sequence', 1, false);


--
-- Name: metadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

--SELECT pg_catalog.setval('public.metadata_id_seq', 1, false);


--
-- Name: annotation annotation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.annotation
    ADD CONSTRAINT annotation_pkey PRIMARY KEY (id);


--
-- Name: annotation_set annotation_set_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.annotation_set
    ADD CONSTRAINT annotation_set_pkey PRIMARY KEY (id);


--
-- Name: annotation_tags annotation_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.annotation_tags
    ADD CONSTRAINT annotation_tags_pkey PRIMARY KEY (annotation_id, created_by, name);


--
-- Name: bookmark bookmarkPK; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.bookmark
    ADD CONSTRAINT "bookmarkPK" PRIMARY KEY (id);


--
-- Name: comment comment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.comment
    ADD CONSTRAINT comment_pkey PRIMARY KEY (id);


--
-- Name: databasechangeloglock databasechangeloglock_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.databasechangeloglock
    ADD CONSTRAINT databasechangeloglock_pkey PRIMARY KEY (id);


--
-- Name: idam_details idam_detailsPK; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.idam_details
    ADD CONSTRAINT "idam_detailsPK" PRIMARY KEY (id);


--
-- Name: jhi_entity_audit_event jhi_entity_audit_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.jhi_entity_audit_event
    ADD CONSTRAINT jhi_entity_audit_event_pkey PRIMARY KEY (id);


--
-- Name: jhi_persistent_audit_event jhi_persistent_audit_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.jhi_persistent_audit_event
    ADD CONSTRAINT jhi_persistent_audit_event_pkey PRIMARY KEY (event_id);


--
-- Name: jhi_persistent_audit_evt_data jhi_persistent_audit_evt_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.jhi_persistent_audit_evt_data
    ADD CONSTRAINT jhi_persistent_audit_evt_data_pkey PRIMARY KEY (event_id, name);


--
-- Name: metadata metadataPK; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.metadata
    ADD CONSTRAINT "metadataPK" PRIMARY KEY (id);


--
-- Name: rectangle rectangle_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.rectangle
    ADD CONSTRAINT rectangle_pkey PRIMARY KEY (id);


--
-- Name: tag tagPK; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.tag
    ADD CONSTRAINT "tagPK" PRIMARY KEY (created_by, name);


--
-- Name: annotation_set unique_set_per_doc_and_user; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.annotation_set
    ADD CONSTRAINT unique_set_per_doc_and_user UNIQUE (created_by, document_id);


--
-- Name: idx_entity_audit_event_entity_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_entity_audit_event_entity_id ON public.jhi_entity_audit_event USING btree (entity_id);


--
-- Name: idx_entity_audit_event_entity_type; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_entity_audit_event_entity_type ON public.jhi_entity_audit_event USING btree (entity_type);


--
-- Name: idx_persistent_audit_event; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_persistent_audit_event ON public.jhi_persistent_audit_event USING btree (principal, event_date);


--
-- Name: idx_persistent_audit_evt_data; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_persistent_audit_evt_data ON public.jhi_persistent_audit_evt_data USING btree (event_id);


--
-- Name: annotation_tags FK6pyecmrxhwm9gi9q42lo1o2tv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.annotation_tags
    ADD CONSTRAINT "FK6pyecmrxhwm9gi9q42lo1o2tv" FOREIGN KEY (annotation_id) REFERENCES public.annotation(id);


--
-- Name: annotation_tags FK9qipuwcr39qbh75i74xe682kg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.annotation_tags
    ADD CONSTRAINT "FK9qipuwcr39qbh75i74xe682kg" FOREIGN KEY (created_by, name) REFERENCES public.tag(created_by, name);


--
-- Name: annotation fk_annotation_annotation_set_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.annotation
    ADD CONSTRAINT fk_annotation_annotation_set_id FOREIGN KEY (annotation_set_id) REFERENCES public.annotation_set(id);


--
-- Name: comment fk_comment_annotation_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.comment
    ADD CONSTRAINT fk_comment_annotation_id FOREIGN KEY (annotation_id) REFERENCES public.annotation(id);


--
-- Name: jhi_persistent_audit_evt_data fk_evt_pers_audit_evt_data; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.jhi_persistent_audit_evt_data
    ADD CONSTRAINT fk_evt_pers_audit_evt_data FOREIGN KEY (event_id) REFERENCES public.jhi_persistent_audit_event(event_id);


--
-- Name: rectangle fk_rectangle_annotation_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.rectangle
    ADD CONSTRAINT fk_rectangle_annotation_id FOREIGN KEY (annotation_id) REFERENCES public.annotation(id);


--
-- PostgreSQL database dump complete
--
