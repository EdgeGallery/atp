DROP TABLE IF EXISTS TASK_TABLE;

CREATE TABLE TASK_TABLE (
    ID                       VARCHAR(200)       NOT NULL,
    APPNAME                  VARCHAR(200)       NULL,
    APPVERSION               VARCHAR(200)       NULL,
    STATUS                   VARCHAR(200)       NULL,
    TESTCASEDETAIL           TEXT               NULL,
    CREATETIME               TIMESTAMP          NULL,
    ENDTIME                  TIMESTAMP          NULL,
    PROVIDERID               VARCHAR(200)       NULL,
    PACKAGEPATH              VARCHAR(200)       NULL,
    USERID                   VARCHAR(200)       NULL,
    USERNAME                 VARCHAR(200)       NULL,
    CONSTRAINT task_table_pkey PRIMARY KEY (ID)
);

DROP TABLE IF EXISTS TEST_CASE_TABLE;

CREATE TABLE TEST_CASE_TABLE (
    ID                       VARCHAR(200)       NOT NULL,
    NAME                     VARCHAR(200)       NULL,
    TYPE                     VARCHAR(200)       NULL,
    CLASSNAME                VARCHAR(200)       NULL,
    HASHCODE                 TEXT               NULL, 
    DESCRIPTION              TEXT               NULL,
    FILEPATH                 VARCHAR(200)       NULL,
    CODELANGUAGE             VARCHAR(200)       NULL,
    EXPECTRESULT             VARCHAR(200)       NULL,
    VERIFICATIONMODEL        VARCHAR(200)       NULL,
    CONSTRAINT test_case_table_pkey PRIMARY KEY (ID)
);

DROP TABLE IF EXISTS TEST_SCENARIO_TABLE;

CREATE TABLE TEST_SCENARIO_TABLE (
    ID                       VARCHAR(200)       NOT NULL,
    NAMECH                   VARCHAR(200)       NULL,
    NAMEEN                   VARCHAR(200)       NULL,
    DESCRIPTIONCh            TEXT               NULL,
    DESCRIPTIONEN            TEXT               NULL, 
    CONSTRAINT test_scenario_table_pkey PRIMARY KEY (ID)
);

DROP TABLE IF EXISTS TEST_SUITE_TABLE;

CREATE TABLE TEST_SUITE_TABLE (
    ID                       VARCHAR(200)       NOT NULL,
    NAMECH                   VARCHAR(200)       NULL,
    NAMEEN                   VARCHAR(200)       NULL,
    DESCRIPTIONCh            TEXT               NULL,
    DESCRIPTIONEN            TEXT               NULL, 
    SCENARIOIDLIST           VARCHAR(255)       NULL,
    CONSTRAINT test_suite_table_pkey PRIMARY KEY (ID)
);


INSERT INTO public.test_case_table(
  id, name, hashCode,type, classname,  description,filePath,codeLanguage,expectResult,verificationModel)
  VALUES ('4d203173-1111-4f62-aabb-8ebcec357f87','Manifest File Path Validation','','complianceTest','SuffixTestCaseInner','Root path must contain the file which name ends of .mf','','java','there has .mf file in root path.','EdgeGallery,Mobile'),
  ('4d203173-2222-4f62-aabb-8ebcec357f87','Manifest File Field Validation','','complianceTest','MFContentTestCaseInner','.mf file must contain the following field: app_product_name, app_provider_id, app_package_version, app_release_date_time and app_package_description','','java','the requirement fileds must exist.','EdgeGallery,Unicom'),
  ('4d203173-3333-4f62-aabb-8ebcec357f87','Manifest File Source Path Validation','','complianceTest','SourcePathTestCaseInner','The value of Source filed must be right path, the corresponding file must exist','','java','the value of source must right path.','EdgeGallery,Telecom'),
  ('4d203173-4444-4f62-aabb-8ebcec357f87','Tosca File Validation','','complianceTest','TOSCAFileTestCaseInner','ToscaFileValidation  TOSCA.meta file must exist, and it must contain the field: Entry-Definitions, and the the value of the filed must be right path, the corresponding file must exist','','java','tosca file is right.','EdgeGallery,Definition'),
  ('4d203173-5555-4f62-aabb-8ebcec357f87','Application Instantiation','','sandboxTest','InstantiateAppTestCaseInner','Instantiate application and its dependency application on one edge host','','jar','app can instantiate successfully.','EdgeGallery,Mobile'),
  ('4d203173-6666-4f62-aabb-8ebcec357f87','Application Termination','','sandboxTest','UninstantiateAppTestCaseInner','Uninstantiate application and its dependency application on one edge host','','jar','app can uninstantiate successfully.','EdgeGallery,Mobile'),
  ('4d203173-7777-4f62-aabb-8ebcec357f87','Virus Scanning','','securityTest','VirusScanTestCaseInner','Virus scan by clamav util','','java','app has no virus.','EdgeGallery,Mobile'),
  ('4d203173-8888-4f62-aabb-8ebcec357f87','Bomb Defense','','securityTest','BombDefenseTestCase','bomb defense','','jar','no bomb defense.','EdgeGallery,Mobile');