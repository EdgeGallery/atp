
 CREATE TABLE IF NOT EXISTS TASK_TABLE (
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
        REPORTPATH               VARCHAR(200)       NULL,
        CONSTRAINT task_table_pkey PRIMARY KEY (ID)
    );

    CREATE TABLE IF NOT EXISTS TEST_CASE_TABLE (
       ID                       VARCHAR(200)       NOT NULL,
       NAMECH                   VARCHAR(200)       NULL,
       NAMEEN                   VARCHAR(200)       NULL,
       TYPE                     VARCHAR(200)       NULL,
       CLASSNAME                VARCHAR(200)       NULL,
       HASHCODE                 TEXT               NULL,
       DESCRIPTIONCH            TEXT               NULL,
       DESCRIPTIONEN            TEXT               NULL,
       FILEPATH                 VARCHAR(200)       NULL,
       CODELANGUAGE             VARCHAR(200)       NULL,
       EXPECTRESULTCH           VARCHAR(200)       NULL,
       EXPECTRESULTEN           VARCHAR(200)       NULL,
       TESTSUITEIDLIST          TEXT               NULL,
       TESTSTEPCH               TEXT               NULL,
       TESTSTEPEN               TEXT               NULL,
       CREATETIME               TIMESTAMP          NULL,
       CONFIGIDLIST             TEXT               NULL,
       CONSTRAINT test_case_table_pkey PRIMARY KEY (ID)
    );

    CREATE TABLE IF NOT EXISTS TEST_SCENARIO_TABLE (
        ID                       VARCHAR(200)       NOT NULL,
        NAMECH                   VARCHAR(200)       NULL,
        NAMEEN                   VARCHAR(200)       NULL,
        DESCRIPTIONCh            TEXT               NULL,
        DESCRIPTIONEN            TEXT               NULL,
        LABEL                    VARCHAR(200)       NULL,
        CREATETIME               TIMESTAMP          NULL,
        CONSTRAINT test_scenario_table_pkey PRIMARY KEY (ID)
    );

    CREATE TABLE IF NOT EXISTS TEST_SUITE_TABLE (
       ID                       VARCHAR(200)       NOT NULL,
       NAMECH                   VARCHAR(200)       NULL,
       NAMEEN                   VARCHAR(200)       NULL,
       DESCRIPTIONCh            TEXT               NULL,
       DESCRIPTIONEN            TEXT               NULL,
       SCENARIOIDLIST           VARCHAR(255)       NULL,
       CREATETIME               TIMESTAMP          NULL,
       CONSTRAINT test_suite_table_pkey PRIMARY KEY (ID)
    );

    CREATE TABLE IF NOT EXISTS FILE_TABLE (
       FILEID                   VARCHAR(200)       NOT NULL,
       TYPE                     VARCHAR(200)       NOT NULL,
       CREATETIME               TIMESTAMP          NULL,
       FILEPATH                 VARCHAR(200)       NULL,
       CONSTRAINT file_table_pkey unique(FILEID,TYPE)
    );

    CREATE TABLE IF NOT EXISTS CONTRIBUTION_TABLE (
        ID                       VARCHAR(200)       NOT NULL,
        NAME                     VARCHAR(200)       NULL,
        OBJECTIVE                VARCHAR(200)       NULL,
        STEP                     TEXT               NULL,
        EXPECTRESULT             TEXT               NULL,
        TYPE                     VARCHAR(255)       NULL,
        CREATETIME               TIMESTAMP          NULL,
        FILEPATH                 VARCHAR(200)       NULL,
        CONSTRAINT contribution_table_pkey PRIMARY KEY (ID)
    );

    CREATE TABLE IF NOT EXISTS CONFIG_TABLE (
             ID                       VARCHAR(200)       NOT NULL,
             NAMECH                   VARCHAR(200)       NULL,
             NAMEEN                   VARCHAR(200)       NULL,
             DESCRIPTIONCh            TEXT               NULL,
             DESCRIPTIONEN            TEXT               NULL,
             CONFIGURATION            TEXT               NOT NULL,
             CREATETIME               TIMESTAMP          NULL,
             CONSTRAINT config_table_pkey PRIMARY KEY (ID)
     );