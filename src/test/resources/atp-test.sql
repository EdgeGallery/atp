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
    DESCRIPTION              TEXT               NULL
    CONSTRAINT test_case_table_pkey PRIMARY KEY (ID)
);