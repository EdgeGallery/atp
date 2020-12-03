DROP TABLE IF EXISTS TASK_TABLE;

CREATE TABLE TASK_TABLE (
    ID                       VARCHAR(200)       NOT NULL,
    APPNAME                  VARCHAR(100)       NULL,
    APPVERSION               VARCHAR(100)       NULL,
    STATUS                   VARCHAR(100)       NULL,
    TESTCASEDETAIL           TEXT               NULL,
    CREATETIME               TIMESTAMP          NULL,
    ENDTIME                  TIMESTAMP          NULL,
    PROVIDERID               VARCHAR(100)       NULL,
    PACKAGEPATH              VARCHAR(100)       NULL,
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
	VALUES ('4d203173-1111-4f62-aabb-8ebcec357f87','MFFilePathValidation','','complianceTest','org.edgegallery.atp.schedule.testcase.compliance.MFContentTestCase','Root path must contain the file which name ends of .mf'),
('4d203173-2222-4f62-aabb-8ebcec357f87','MFFileFieldValidation','','complianceTest','org.edgegallery.atp.schedule.testcase.compliance.SourcePathTestCase','.mf file must contain the following field: app_name, app_provider, app_archive_version, app_release_date_time and app_contact'),
('4d203173-3333-4f62-aabb-8ebcec357f87','MFFileSourcePathValidation','','complianceTest','org.edgegallery.atp.schedule.testcase.compliance.SuffixTestCase','The value of Source filed must be right path, the corresponding file must exist'),
('4d203173-4444-4f62-aabb-8ebcec357f87','ToscaFileValidation','','complianceTest','org.edgegallery.atp.schedule.testcase.compliance.TOSCAFileTestCase','ToscaFileValidation	TOSCA.meta file must exist, and it must contain the following field: Entry-Definitions, ETSI-Entry-Manifest, Entry-Tests, ETSI-Entry-Change-Log and Entry-Helm-Package, and the the value of above filed must be right path, the corresponding file must exist'),
('4d203173-5555-4f62-aabb-8ebcec357f87','InstantiateApp','','sandboxTest','org.edgegallery.atp.schedule.testcase.sandbox.InstantiateAppTestCase','Instantiate application and its dependency application on one edge host'),
('4d203173-6666-4f62-aabb-8ebcec357f87','UnInstantiateApp','','sandboxTest','org.edgegallery.atp.schedule.testcase.sandbox.UninstantiateAppTestCase','Uninstantiate application and its dependency application on one edge host'),
('4d203173-7777-4f62-aabb-8ebcec357f87','VirusScanning','','virusScanningTest','org.edgegallery.atp.schedule.testcase.virusScan.VirusScanTestCase','Virus scan by clamav util');