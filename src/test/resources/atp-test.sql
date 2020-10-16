DROP TABLE IF EXISTS TASK_TABLE;

CREATE TABLE TASK_TABLE (
    ID                       VARCHAR(200)       NOT NULL,
    APPNAME                  VARCHAR(100)       NULL,
    APPVERSION               VARCHAR(100)       NULL,
    STATUS                   VARCHAR(100)       NULL,
    TESTCASEDETAIL           TEXT               NULL,
    CREATETIME               TIMESTAMP          NULL,
    ENDTIME                  TIMESTAMP          NULL,
    USERID                   VARCHAR(100)       NULL,
    USERNAME                 VARCHAR(100)       NULL,
    CONSTRAINT task_table_pkey PRIMARY KEY (ID)
);

DROP TABLE IF EXISTS TEST_CASE_TABLE;

CREATE TABLE TEST_CASE_TABLE (
    ID                       VARCHAR(200)       NOT NULL,
    NAME                     VARCHAR(100)       NULL,
    TYPE                     VARCHAR(100)       NULL,
    CLASSNAME                VARCHAR(100)       NULL,
    CONTENT                  TEXT               NULL, 
    DESCRIPTION              TEXT               NULL,
    CONSTRAINT test_case_table_pkey PRIMARY KEY (ID)
);

DROP TABLE IF EXISTS BATCH_TASK_MAPPING_TABLE;

CREATE TABLE BATCH_TASK_MAPPING_TABLE (
    ID                       VARCHAR(200)       NOT NULL,
    SUBTASKID                TEXT               NULL,
    USERID                   VARCHAR(100)       NULL,
    USERNAME                 VARCHAR(100)       NULL,
    CONSTRAINT batch_task_mapping_table_pkey PRIMARY KEY (ID)
);

INSERT INTO public.test_case_table(
	id, name, content,type, classname,  description)
	VALUES ('4d203173-1111-4f62-aabb-8ebcec357f87','MFContent','','compliancesTest','org.edgegallery.atp.schedule.testcase.compliance.MFContentTestCase',''),
('4d203173-2222-4f62-aabb-8ebcec357f87','SourcePath','','compliancesTest','org.edgegallery.atp.schedule.testcase.compliance.SourcePathTestCase',''),
('4d203173-3333-4f62-aabb-8ebcec357f87','Suffix','','compliancesTest','org.edgegallery.atp.schedule.testcase.compliance.SuffixTestCase',''),
('4d203173-4444-4f62-aabb-8ebcec357f87','ToscaFile','','compliancesTest','org.edgegallery.atp.schedule.testcase.compliance.TOSCAFileTestCase',''),
('4d203173-5555-4f62-aabb-8ebcec357f87','Instantiate','','sandboxTest','org.edgegallery.atp.schedule.testcase.sandbox.InstantiateAppTestCase',''),
('4d203173-6666-4f62-aabb-8ebcec357f87','UnInstantiate','','sandboxTest','org.edgegallery.atp.schedule.testcase.sandbox.UninstantiateAppTestCase','');