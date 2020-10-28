package org.edgegallery.atp.constant;

public interface ExceptionConstant {

    interface MFContentTestCase {

        String LOSS_FIELD =
                ".mf file may lost the following fileds:app_name,app_provider,app_archive_version,app_release_date_time or app_contact.";

        String FILE_NOT_EXIST = ".mf file may not exist or it do not in the root directory.";
    }

    interface SourcePathTestCase {
        String SOURCE_PATH_FILE_NOT_EXISTS = "some source path file in .mf may not exist.";
    }

    interface TOSCAFileTestCase {
        String TOSCA_FILE_NOT_EXISTS = "tosca.meta not exists.";
        String LOSS_FIELD =
                "tosca.meta file may lost the following fileds:Entry-Definitions,ETSI-Entry-Manifest,Entry-Tests,ETSI-Entry-Change-Log or Entry-Helm-Package.";
        String FILE_NOT_EXIT =
                "the file must exists corresponding the value of the following field:Entry-Definitions,ETSI-Entry-Manifest,Entry-Tests,ETSI-Entry-Change-Log and Entry-Helm-Package.";
    }

    interface InstantiateAppTestCase {
        String RESPONSE_FROM_APM_FAILED = "upload csar file to apm failed, and the response code is: ";
        String INSTANTIATE_DEPENDENCE_APP_FAILED =
                "instantiate dependence app failed, the name of failed dependenced app are: ";
        String INSTANTIATE_APP_FAILED = "instantiate app from appo failed.";
    }

    interface UninstantiateAppTestCase {
        String UNINSTANTIATE_DEPENDENCE_APP_FAILED =
                "delete instantiate dependence app failed, the appInstanceId of failed dependenced app are: ";
        String UNINSTANTIATE_APP_FAILED = "delete instantiate app from appo failed, the appInstanceId is: ";
    }

    interface VirusScanTestCase {
        String FIND_VIRUS = "The file contains virus, the number of virus is: %s";
    }

    String INNER_EXCEPTION = "inner exception, please check the log.";
}
