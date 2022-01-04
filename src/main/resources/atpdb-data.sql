  INSERT INTO public.config_table(id,nameCh,nameEn,descriptionCh,descriptionEn,configuration,createTime)
       VALUES('4353982a-abb0-4cf6-9cae-a468a5318c67','虚机应用实例化','vm app instantiation','用于虚机应用实例化的参数','params used for vm app instantiation','DC_ID=FS_M:Manger_VPC;az_dc=nova;mep_ip=119.8.47.5;mep_port=8443;ue_ip_segment=0.0.0.0/0;VDU1_APP_Plane01_IP=;VDU1_APP_Plane01_MASK=255.255.255.0;VDU1_APP_Plane01_GW=;VDU1_APP_Plane02_IP=;VDU1_APP_Plane02_MASK=255.255.255.0;VDU1_APP_Plane02_GW=;VDU1_APP_Plane03_IP=;VDU1_APP_Plane03_MASK=255.255.255.0;VDU1_APP_Plane03_GW=;APP_Plane01_Network=MEC_APP_MP1;APP_Plane01_Physnet=physnet2;APP_Plane01_VlanId=2001;APP_Plane02_Network=MEC_APP_Public;APP_Plane02_Physnet=physnet2;APP_Plane02_VlanId=2002;APP_Plane03_Network=MEC_APP_Private;APP_Plane03_Physnet=physnet2;APP_Plane03_VlanId=2003', now()::timestamp without time zone),
             ('4353982a-0002-4cf6-9cae-a468a5318c67','连通性测试配置','accessible test config','用于连通性测试的配置参数','params used for accessible test','can_ping=false;', now()::timestamp without time zone)
       ON CONFLICT(id) do nothing;

  INSERT INTO public.test_scenario_table(id, nameCh,nameEn, descriptionCh,descriptionEn,label,createTime)
       VALUES ('4d203111-1111-4f62-aabb-8ebcec357f87','社区通用场景','EdgeGallery Community Common Scenario','适用于社区场景的通用的测试','suite for EdgeGallery community common test','EdgeGallery', now()::timestamp without time zone),
          ('e71718a5-864a-49e5-855a-5805a5e9f97d','A运营商','A Operator','适用于A运营商场景的测试','suite for A Operator test scenario','', now()::timestamp without time zone),
          ('6fe8581c-b83f-40c2-8f5b-505478f9e30b','B运营商','B Operator','适用于B运营商场景的测试','suite for B Operator test scenario','', now()::timestamp without time zone),
          ('96a82e85-d40d-4ce5-beec-2dd1c9a3d41d','C运营商','C Operator','适用于C运营商场景的测试','suite for C Operator test scenario','', now()::timestamp without time zone),
          ('96a82e85-d40d-1111-beec-2dd1c9a3d41d','手工场景','Manual Scenario','适用于需要手工测试的场景','suite for manual test scenario','', now()::timestamp without time zone)
       ON CONFLICT(id) do nothing;

  INSERT INTO public.test_suite_table(id, nameCh,nameEn, descriptionCh,descriptionEn,scenarioIdList,createTime)
       VALUES ('522684bd-d6df-4b47-aab8-b43f1b4c19c0','通用遵从性测试','Common Compliance Test','遵从社区APPD标准、ETSI标准对应用包结构进行校验','Validate app package structure according to commnunity and ETSI standard','4d203111-1111-4f62-aabb-8ebcec357f87', now()::timestamp without time zone),
          ('6d04da1b-1f36-4295-920a-8074f7f9d942','通用沙箱测试','Common Sandbox Test','应用包部署测试','App package deployment test','4d203111-1111-4f62-aabb-8ebcec357f87', now()::timestamp without time zone),
          ('743abd93-57a3-499d-9591-fa7db86a4778','通用安全性测试','Common Security Test','应用包安全测试','App package security test','4d203111-1111-4f62-aabb-8ebcec357f87', now()::timestamp without time zone),
          ('743abd93-1111-499d-9591-fa7db86a4778','通用性能测试','Common Performance Test','应用性能测试','App performance test','4d203111-1111-4f62-aabb-8ebcec357f87', now()::timestamp without time zone),
          ('222684bd-d6df-4b47-aab8-b43f1b4c19c0','A运营商遵从性测试','A Operator Compliance Test','遵从A运营商标准对应用包结构进行校验','Validate app package structure according to A Operator standard','e71718a5-864a-49e5-855a-5805a5e9f97d', now()::timestamp without time zone),
          ('2224da1b-1f36-4295-920a-8074f7f9d942','A运营商安全性测试','A Operator Security Test','安全性测试A运营商版','App package security test A Operator version','e71718a5-864a-49e5-855a-5805a5e9f97d', now()::timestamp without time zone),
          ('111684bd-d6df-4b47-aab8-b43f1b4c19c0','B运营商遵从性测试','B Operator Compliance Test','遵从B运营商标准对应用包结构进行校验','Validate app package structure according to B Operator standard','6fe8581c-b83f-40c2-8f5b-505478f9e30b', now()::timestamp without time zone),
          ('1114da1b-1f36-4295-920a-8074f7f9d942','B运营商沙箱测试','B Operator Sandbox Test','应用包部署测试B运营商版','App package deployment test B Operator version','6fe8581c-b83f-40c2-8f5b-505478f9e30b', now()::timestamp without time zone),
          ('333684bd-d6df-4b47-aab8-b43f1b4c19c0','C运营商遵从性测试','C Operator Compliance Test','遵从C运营商标准对应用包结构进行校验','Validate app package structure according to C Operator standard','96a82e85-d40d-4ce5-beec-2dd1c9a3d41d', now()::timestamp without time zone),
          ('3334da1b-1f36-4295-920a-8074f7f9d942','C运营商沙箱测试','C Operator Sandbox Test','应用包部署测试C运营商版','App package deployment test C Operator version','96a82e85-d40d-4ce5-beec-2dd1c9a3d41d', now()::timestamp without time zone),
          ('3334da1b-1f36-1111-920a-8074f7f9d942','手工测试套','Manual Test Suite','关于应用包的手工测试','Manual test of App package','96a82e85-d40d-1111-beec-2dd1c9a3d41d', now()::timestamp without time zone)
       ON CONFLICT(id) do nothing;

INSERT INTO public.test_case_table(id, nameCh,nameEn,configIdList, hashCode,type, classname,  descriptionCh,descriptionEn,filePath,codeLanguage,expectResultCh,expectResultEn,testStepCh,testStepEn,testSuiteIdList,createTime)
       VALUES ('4d203173-5555-4f62-aabb-8ebcec357f87','应用实例化','Application Instantiation','4353982a-abb0-4cf6-9cae-a468a5318c67','','automatic','InstantiateAppTestCaseInner','将应用包部署到边缘节点','Instantiate application and its dependency application on one edge host','','jar','应用包可以成功部署','app can instantiate successfully.','部署应用包到对应的边缘节点','Deploy application package to edge node','6d04da1b-1f36-4295-920a-8074f7f9d942', now()::timestamp without time zone)
       ON CONFLICT(id) do nothing;
INSERT INTO public.test_case_table(id, nameCh,nameEn,configIdList, hashCode,type, classname,  descriptionCh,descriptionEn,filePath,codeLanguage,expectResultCh,expectResultEn,testStepCh,testStepEn,testSuiteIdList,createTime)
       VALUES ('4d203173-1025-4f62-aabb-8ebcec357f87','MEP服务注册校验','Register Service To Mep Validation','','','automatic','RegisterService2Mep','向MEP平台注册服务','Register service to mep','','jar','MEP服务注册成功','register service to mep successfully.','1.获取ak/sk token 2.调用mep服务注册接口 3.看接口返回结果','1.get ak/sk token 2.call mep register service interface 3.validate response','6d04da1b-1f36-4295-920a-8074f7f9d942', now()::timestamp without time zone)
       ON CONFLICT(id) do nothing;

INSERT INTO public.test_case_table(id, nameCh,nameEn,configIdList, hashCode,type, classname,  descriptionCh,descriptionEn,filePath,codeLanguage,expectResultCh,expectResultEn,testStepCh,testStepEn,testSuiteIdList,createTime)
       VALUES ('4d203173-1111-4f62-aabb-8ebcec357f87','MF文件路径校验','Manifest File Path Validation','','','automatic','SuffixTestCaseInner','根目录必须包含以.mf结尾的文件','Root path must contain the file which name ends of .mf','','java','根目录存在以.mf结尾的文件','there has .mf file in root path.','1.打开csar包 2.检查根目录存在以.mf结尾的文件','1.open csar package 2.there has .mf file in root path','522684bd-d6df-4b47-aab8-b43f1b4c19c0,111684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-2222-4f62-aabb-8ebcec357f87','MF文件必填字段校验','Manifest File Field Validation','','','automatic','MFContentTestCaseInner','.mf文件必须包含如下字段： app_product_name, app_provider_id, app_package_version, app_release_date_time, app_class and app_package_description','.mf file must contain the following field: app_product_name, app_provider_id, app_package_version, app_release_date_time, app_class and app_package_description','','java','必填字段都存在','the requirement fileds must exist.','1.打开csar包 2.打开.mf文件 3.校验必填字段是否都存在','1.open csar package 2.open .mf file 3.validate the requirement fields exist','522684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-3333-4f62-aabb-8ebcec357f87','MF文件Source路径校验','Manifest File Source Path Validation','','','automatic','SourcePathTestCaseInner','Source字段的值必须是正确的文件路径，文件必须存在','The value of Source filed must be right path, the corresponding file must exist','','java','Source字段的值必须是正确的路径，路径中的文件必须存在','the value of source must right path.','1.打开csar包 2.打开.mf文件 3.查看Source字段的值对应的文件路径是否存在','1.open csar package 2.open .mf file 3.validate the value of source must right path','522684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-4444-4f62-aabb-8ebcec357f87','TOSCA文件校验','Tosca File Validation','','','automatic','TOSCAFileTestCaseInner','TOSCA.meta文件必须存在，该文件必须包含字段Entry-Definitions，且其值对应的路径必须是正确的路径，路径中的文件必须存在','ToscaFileValidation  TOSCA.meta file must exist, and it must contain the field: Entry-Definitions, and the the value of the filed must be right path, the corresponding file must exist.','','java','tosca文件存在，且必填字段及内容正确','tosca file exists and field is right.','1.打开csar包 2.校验TOSCA.meta文件是否存在 3.校验必填字段是否存在 4.校验字段Entry-Definitions对应的值路径正确性','1.open csar package 2.validate the existence of TOSCA.meta file 3.validate the requirement field 4.validate the value of Entry-Definitions field','522684bd-d6df-4b47-aab8-b43f1b4c19c0,333684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-5555-4f62-aabb-8ebcec357f87','应用实例化','Application Instantiation','4353982a-abb0-4cf6-9cae-a468a5318c67','','automatic','InstantiateAppTestCaseInner','将应用包部署到边缘节点','Instantiate application and its dependency application on one edge host','','jar','应用包可以成功部署','app can instantiate successfully.','部署应用包到对应的边缘节点','Deploy application package to edge node','6d04da1b-1f36-4295-920a-8074f7f9d942', now()::timestamp without time zone),
              ('4d203173-7777-4f62-aabb-8ebcec357f87','病毒扫描','Virus Scanning','','','manual','','使用开源工具对应用包进行病毒扫描','scan application package virus by open source tool','','','应用包中未扫描出病毒','app has no virus.','1.安装病毒扫描三方件 2.扫描应用包','1.install third-party software 2.scan application package','3334da1b-1f36-1111-920a-8074f7f9d942', now()::timestamp without time zone),
              ('4d203173-9999-4f62-aabb-8ebcec357f87','APPD文件目录校验','APPD File Dir Validation','','','automatic','APPDValidation','根目录下必须包含APPD文件目录','Root directory must contain APPD file dir','','java','根目录下存在APPD文件目录','Root directory contains APPD file dir','1.打开csar包 2.校验根目录下存在APPD目录','1.open csar package 2.validate root directory contains APPD directory','522684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1000-4f62-aabb-8ebcec357f87','Artifacts文件目录校验','Artifacts File Dir Validation','','','automatic','ArtifactsValidation','根目录下必须包含Artifacts文件目录','Root directory must contain Artifacts file dir','','java','根目录下存在Artifacts文件目录','Root directory contains Artifacts file dir','1.打开csar包 2.校验根目录下存在Artifacts目录','1.open csar package 2.validate root directory contains Artifacts directory','522684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1001-4f62-aabb-8ebcec357f87','TOSCA-Metadata文件目录校验','Tosca Metadata File Dir Validation','','','automatic','ToscaMetadataValidation','根目录下必须包含TOSCA-Metadata文件目录','Root directory must contain TOSCA-Metadata file dir','','java','根目录下存在TOSCA-Metadata文件目录','Root directory contains TOSCA-Metadata file dir','1.打开csar包 2.校验根目录下存在TOSCA-Metadata目录','1.open csar package 2.validate root directory contains TOSCA-Metadata directory','522684bd-d6df-4b47-aab8-b43f1b4c19c0,111684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1002-4f62-aabb-8ebcec357f87','yaml描述文件校验','Yaml Description File Validation','','','automatic','YamlDescriptionFileValidation','APPD/Definition/目录下必须存在yaml描述文件','There must contain yaml file in APPD/Definition/ dir','','java','APPD/Definition/目录下包含yaml描述文件','APPD/Definition/ dir contains yaml file','1.打开csar包 2.校验APPD/Definition/目录下包含yaml描述文件','1.open csar package 2.validate APPD/Definition/ dir contains yaml file','522684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1003-4f62-aabb-8ebcec357f87','mf文件hash值列表校验','Manifest File Hash List Validation','','','automatic','ManifestFileHashListValidation','.mf文件中，每个文件必须有对应的hash值描述','Every Source file must has Hash field in manifest file','','java','.mf文件中每个文件都有对应的hash值描述','Every Source file has Hash field in manifest file','1.打开csar包 2.打开.mf文件 3.校验每个Source字段对应的文件都有Hash字段的描述','1.open csar package 2.open .mf file 3.validate every Source file has Hash field in manifest file','743abd93-57a3-499d-9591-fa7db86a4778', now()::timestamp without time zone),
              ('4d203173-1004-4f62-aabb-8ebcec357f87','CPU数量描述字段校验','CPU Number Description Validation','','','automatic','CPUNumberDescriptionValidation','yaml描述文件中必须有对cpu数量的描述字段：num_virtual_cpu','There must contain cpu number description in yaml file','','java','yaml描述文件中包含对cpu数量的描述','There contains cpu number description in yaml file','1.打开csar包 2.打开yaml描述文件 3.校验有num_virtual_cpu字段','1.open csar package 2.open yaml description file 3.validate existence of num_virtual_cpu field','522684bd-d6df-4b47-aab8-b43f1b4c19c0,111684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1005-4f62-aabb-8ebcec357f87','虚拟内存描述字段校验','Virtual Memory Description Validation','','','automatic','VirtualMemoryDescriptionValidation','yaml文件中有对虚拟内存大小的描述字段：virtual_mem_size','There must contain virtual memory size description in yaml file','','java','yaml描述文件中包含对虚拟内存大小的描述','There contains virtual memory size description in yaml file','1.打开csar包 2.打开yaml描述文件 3.校验有virtual_mem_size字段','1.open csar package 2.open yaml description file 3.validate existence of virtual_mem_size','522684bd-d6df-4b47-aab8-b43f1b4c19c0,333684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1006-4f62-aabb-8ebcec357f87','文件大小校验','File Size Validation','','','automatic','FileSizeValidation','应用包大小校验','The size of app package validation','','java','应用包大小不超过5G','The size of app package no more than 5G','应用包大小不超过5G','The size of app package no more than 5G','743abd93-57a3-499d-9591-fa7db86a4778', now()::timestamp without time zone),
              ('4d203173-1007-4f62-aabb-8ebcec357f87','文件数量校验','File Number Validation','','','automatic','FileNumberValidation','应用包包含的文件数量校验','The number of files app package contains validation','','java','应用包包含的文件数量不大于1024个','The number of files app package contains no more than 1024','1.解压应用包 2.获取包含文件数量 3.数量不多于1024','1. unzip app package 2. get file number 3. the number of file no more than 1024','743abd93-57a3-499d-9591-fa7db86a4778', now()::timestamp without time zone),
              ('4d203173-1008-4f62-aabb-8ebcec357f87','解压后文件大小校验','Unzip File Size Validation','','','automatic','UnzipFileSizeValidation','解压后文件大小校验','The size of unzip app package validation','','java','解压后文件大小不超过10G','The size of unzip app package no more than 10G','1.解压应用包 2.校验解压后应用包大小不超过10G','1. unzip app package 2. checking the size of unzip app package no more than 10G','743abd93-57a3-499d-9591-fa7db86a4778', now()::timestamp without time zone),
              ('4d203173-1009-4f62-aabb-8ebcec357f87','文件目录层深校验','File Dir Depth Validation','','','automatic','FileDirDepthValidation','文件目录层深校验','File Dir Depth Validation','','java','应用包包含文件的目录层级不超过10层','The depth of file dir no more than 10','1.解压应用包 2.遍历文件目录，没有超过10层的文件存在','1. unzip app package 2. traverse file dir, no file dir depth more than 10','743abd93-57a3-499d-9591-fa7db86a4778', now()::timestamp without time zone),
              ('4d203173-1010-4f62-aabb-8ebcec357f87','依赖服务存在性校验','Dependency Service Existence Validation','','','automatic','DependencyServiceExistenceValidation','依赖服务存在性校验','Dependency Service Existence Validation','','jar','如果在yaml文件中定义了依赖服务的appId和packageId,则这2个id要在appstore中真实存在','If there are definitions of dependency service, the appId and packageId must exist in appstore','1.解压应用包 2.解析yaml文件 3.若存在依赖，则appId和packageId要存在','1. unzip app package 2. analysize yaml file 3. if there are definitions of dependency service, appId and packageId must exist.','6d04da1b-1f36-4295-920a-8074f7f9d942', now()::timestamp without time zone),
              ('4d203173-1011-4f62-aabb-8ebcec357f87','A运营商MEAD文件目录校验','A Operator MEAD File Dir Validation','','','automatic','MEADFileDirValidation','MEAD文件在根目录的存在性校验','MEAD file dir existence in root path Validation','','java','应用包根目录下必须存在MEAD文件夹','There must exist MEAD file dir in app package root path','1.解压应用包 2.检查根目录是否存在MEAD文件夹','1. unzip app package 2. check the existence of MEAD file in root path','222684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1012-4f62-aabb-8ebcec357f87','A运营商MF文件必填字段校验','A Operator MF Field Validation','','','automatic','CUMFFieldValidation','MF文件必填字段校验','MF file required field validation','','java','MF文件必须包含如下字段：app_name,app_provider,app_package_version,app_release_data_time,app_type or app_package_description','MF file must contain the following fields: app_name,app_provider,app_package_version,app_release_data_time,app_type or app_package_description','1.解压应用包 2.解析mf文件是否包含必填字段','1. unzip app package 2. validate the required fields in mf file','222684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1013-4f62-aabb-8ebcec357f87','A运营商Swagger文件目录校验','A Operator Swagger File Dir Validation','','','automatic','SwaggerFileDirValidation','Swagger文件在根目录的存在性校验','Swagger file dir existence in root path Validation','','java','应用包根目录下必须存在Swagger文件夹','There must exist Swagger file dir in app package root path','1.解压应用包 2.检查根目录是否存在Swagger文件夹','1. unzip app package 2. check the existence of Swagger file in root path','222684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1014-4f62-aabb-8ebcec357f87','A运营商MF文件Hash值校验','A Operator MF File Hash Value Validation','','','automatic','MFHashValueValidation','MF文件Hash值正确性校验','MF file hash value validation','','jar','MF文件中每个Source对应的文件的Hash值必须是正确的','Each hash value of Source field file must be right','1.解压应用包 2.查看Source文件对应的hash值是否正确','1. unzip app package 2. validate the hash of Source file is right','2224da1b-1f36-4295-920a-8074f7f9d942', now()::timestamp without time zone),
              ('4d203173-1015-4f62-aabb-8ebcec357f87','容器helm模板镜像文件校验','Container Helm Tgz File Validation','','','automatic','ContainerHelmTgzFileValidation','容器的应用包，必须存在helm模板的镜像文件','There must exist .tgz file of helm for container app package','','java','容器应用包的Artifacts/Deployment/Charts目录下必须存在.tgz文件','There must exist .tgz file in Artifacts/Deployment/Charts dir for container app package.','1.打开csar包 2.Artifacts/Deployment/Charts目录下是否存在.tgz文件','1.open csar package 2.validate the existence of .tgz file in Artifacts/Deployment/Charts dir','522684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1016-4f62-aabb-8ebcec357f87','容器tgz中templates文件夹校验','Container Tgz Templates Validaion','','','automatic','ContainerTgzTemplateValidaion','容器的应用包，在helm模板的.tgz文件中，必须存在templates文件夹','There must exist templates file dir in .tgz file of helm for container app package','','jar','容器应用包的Artifacts/Deployment/Charts目录下的.tgz文件中必须存在templates文件目录','There must exist templates file dir in .tgz file of Artifacts/Deployment/Charts dir for container app package.','1.打开csar包 2.打开Artifacts/Deployment/Charts目录下的.tgz文件 3.是否存在templates目录','1.open csar package 2.open .tgz file in Artifacts/Deployment/Charts dir 3.validate the existence of templates dir','522684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1017-4f62-aabb-8ebcec357f87','容器tgz中chart文件校验','Container Tgz Chart Validaion','','','automatic','ContainerTgzChartValidaion','容器的应用包，在helm模板的.tgz文件中，必须存在chart.yaml文件','There must exist chart.yaml file in .tgz file of helm for container app package','','jar','容器应用包的Artifacts/Deployment/Charts目录下的.tgz文件中必须存在chart.yaml文件','There must exist chart.yaml file in .tgz file of Artifacts/Deployment/Charts dir for container app package.','1.打开csar包 2.打开Artifacts/Deployment/Charts目录下的.tgz文件 3.是否存在chart.yaml文件','1.open csar package 2.open .tgz file in Artifacts/Deployment/Charts dir 3.validate the existence of chart.yaml file','522684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1018-4f62-aabb-8ebcec357f87','容器tgz中values文件校验','Container Tgz Values Validaion','','','automatic','ContainerTgzValuesValidaion','容器的应用包，在helm模板的.tgz文件中，必须存在values.yaml文件','There must exist values.yaml file in .tgz file of helm for container app package','','jar','容器应用包的Artifacts/Deployment/Charts目录下的.tgz文件中必须存在values.yaml文件','There must exist values.yaml file in .tgz file of Artifacts/Deployment/Charts dir for container app package.','1.打开csar包 2.打开Artifacts/Deployment/Charts目录下的.tgz文件 3.是否存在values.yaml文件','1.open csar package 2.open .tgz file in Artifacts/Deployment/Charts dir 3.validate the existence of values.yaml file','522684bd-d6df-4b47-aab8-b43f1b4c19c0', now()::timestamp without time zone),
              ('4d203173-1019-4f62-aabb-8ebcec357f87','签名校验','Signature Validation','','','automatic','SignatureValidation','mf文件中签名值校验','Signature value in mf file validation','','java','mf文件中CMS签名值正确','The cms signature value in mf file is right','1.解压应用包 2.打开mf文件 3.找到cms签名值并验证','1. unzip app package 2. open mf file 3. find cms signature value and validate it','743abd93-57a3-499d-9591-fa7db86a4778', now()::timestamp without time zone),
              ('4d203173-1020-4f62-aabb-8ebcec357f87','端口扫描','Port Scanning Validation','','','automatic','PortScanningValidation','检查应用部署的边缘节点端口号使用情况','Validate the usage of port in app instantiated edge node','','java','校验边缘节点是否含有被防火墙过滤的端口号或者情况不明端口号','Validate the edge node whether contains port of filtered by firewall or not','1.使用开源工具扫描边缘节点 2.查看扫描结果是否有异常','1.scan edge node by open source tool 2.validate the result','743abd93-57a3-499d-9591-fa7db86a4778', now()::timestamp without time zone),
              ('4d203173-1021-4f62-aabb-8ebcec357f87','边缘节点连通性校验','Mec Host Access Validation','4353982a-0002-4cf6-9cae-a468a5318c67','','automatic','MecHostAccessValidation','检查MEC平台的边缘站点连通性','Validate mec host accessible in MEC platform','','java','校验MEC平台上边缘站点的可访问性','Validate the accessible of mec host in MEC platform','1.获取MEC平台的边缘站点 2.校验其可访问性','1.get mec hosts from MEC platform 2.validate the accessible of them','6d04da1b-1f36-4295-920a-8074f7f9d942', now()::timestamp without time zone),
              ('4d203173-1022-4f62-aabb-8ebcec357f87','网络延时校验','Network Delay Validation','','','automatic','NetworkDelayValidation','检查边缘站点的网络延时','Validate mec host network delay time','','java','边缘站点网络延时不能超过50ms','The network delay time of mec host can not more than 50 ms','1.获取应用部署的边缘站点 2.校验网络延时','1.get mec host which app instantiated in 2.validate network delay','743abd93-1111-499d-9591-fa7db86a4778', now()::timestamp without time zone),
              ('4d203173-1023-4f62-aabb-8ebcec357f87','Cpu使用率校验','Cpu Usage Validation','','','automatic','CpuUsageValidation','校验应用部署后的cpu使用率','Validate cpu usage after app instantiated','','jar','应用cpu使用率不超过50%','app cpu usage no more than 50%.','1.查看应用部署前边缘节点cpu占用情况 2.查看应用部署后边缘节点cpu占用情况 3.计算应用的cpu占用率','1.check cpu usage of edge node before app instantiated 2.check cpu usage of edge node after app instantiated 3.calculate app cpu usage','743abd93-1111-499d-9591-fa7db86a4778', now()::timestamp without time zone),
              ('4d203173-1024-4f62-aabb-8ebcec357f87','内存使用率校验','Memory Usage Validation','','','automatic','MemUsageValidation','校验应用部署后的内存使用率','Validate memory usage after app instantiated','','jar','应用内存使用率不超过50%','app memory usage no more than 50%.','1.查看应用部署前边缘节点内存占用情况 2.查看应用部署后边缘节点内存占用情况 3.计算应用的内存占用率','1.check memory usage of edge node before app instantiated 2.check memory usage of edge node after app instantiated 3.calculate app memory usage','743abd93-1111-499d-9591-fa7db86a4778', now()::timestamp without time zone),
              ('4d203173-1026-4f62-aabb-8ebcec357f87','MEP服务二次注册校验','Secondary Register Service To Mep Validation','','','automatic','SecondaryRegisterService2Mep','向MEP平台二次注册服务','Secondary Register service to mep','','jar','向MEP服务注册成功','register service to mep successfully.','1.获取ak/sk token 2.调用mep服务注册接口，构造失败场景 3.再次调用mep服务注册接口 4.看接口返回结果','1.get ak/sk token 2.call mep register service interface and construct failed scenario 3.call mep register service interface again 4.validate response','6d04da1b-1f36-4295-920a-8074f7f9d942', now()::timestamp without time zone),
              ('4d203173-1027-4f62-aabb-8ebcec357f87','向MEP更新已注册的服务校验','Update Registered Service To Mep Validation','','','automatic','updateRegisteredService2Mep','向MEP平台更新已注册的服务','update registered service to mep','','jar','向MEP更新已注册的服务成功','update registered service to mep successfully.','1.调用mep更新已注册服务接口 2.看接口返回结果','1.call mep update registered service interface 2.validate response','6d04da1b-1f36-4295-920a-8074f7f9d942', now()::timestamp without time zone),
              ('4d203173-1029-4f62-aabb-8ebcec357f87','漏洞扫描','Vulnerable Scanning Validation','','','automatic','VulnerableScanningValidation','检查应用部署的边缘节点是否包含常见漏洞','Validate common vulnerables in app instantiated edge node','','java','校验应用是否含有常见漏洞','Validate app whether has common vulnerables','1.使用开源漏洞扫描工具扫描应用部署节点 2.查看扫描结果是否有异常','1.scan edge node by open source vulnerable scanning tool 2.validate the result','743abd93-57a3-499d-9591-fa7db86a4778', now()::timestamp without time zone)
              ON CONFLICT(id) do nothing;
INSERT INTO public.test_case_table(id, nameCh,nameEn,configIdList, hashCode,type, classname,  descriptionCh,descriptionEn,filePath,codeLanguage,expectResultCh,expectResultEn,testStepCh,testStepEn,testSuiteIdList,createTime)
       VALUES ('4d203173-1028-4f62-aabb-8ebcec357f87','MEP服务取消注册校验','Unregister Service To Mep Validation','','','automatic','unregisterService2Mep','向MEP平台取消已注册的服务','unregister service to mep','','jar','向MEP取消已注册的服务成功','unregister service to mep successfully.','1.调用mep服务取消注册接口 2.看接口返回结果','1.call mep unregister service interface 2.validate response','6d04da1b-1f36-4295-920a-8074f7f9d942', now()::timestamp without time zone)
       ON CONFLICT(id) do nothing;
INSERT INTO public.test_case_table(id, nameCh,nameEn,configIdList, hashCode,type, classname,  descriptionCh,descriptionEn,filePath,codeLanguage,expectResultCh,expectResultEn,testStepCh,testStepEn,testSuiteIdList,createTime)
       VALUES ('4d203173-6666-4f62-aabb-8ebcec357f87','应用实例化终止','Application Termination','','','automatic','UninstantiateAppTestCaseInner','将实例化后的应用包卸载','Uninstantiate application and its dependency application on one edge host','','jar','实例化后的应用包成功卸载','app can uninstantiate successfully.','卸载实例化后的应用','Terminate the application instance','743abd93-1111-499d-9591-fa7db86a4778', now()::timestamp without time zone)
       ON CONFLICT(id) do nothing;