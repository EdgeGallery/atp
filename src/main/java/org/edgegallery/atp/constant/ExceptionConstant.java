package org.edgegallery.atp.constant;

public interface ExceptionConstant {

	interface MFContentTestCase {

		String LOSS_FIELD = ".mf file may lost the following fileds:app_name,app_provider,app_archive_version,app_release_date_time or app_contact.";

		String FILE_NOT_EXIST = ".mf file may not exist or it do not in the root directory.";
	}

	interface SourcePathTestCase {
		String SOURCE_PATH_FILE_NOT_EXISTS = "some source path file in .mf may not exist.";
	}

	interface TOSCAFileTestCase {
		String TOSCA_FILE_NOT_EXISTS = "tosca.meta not exists.";
		String LOSS_FIELD = "tosca.meta file may lost the following fileds:Entry-Definitions,ETSI-Entry-Manifest,Entry-Tests,ETSI-Entry-Change-Log or Entry-Helm-Package.";
		String FILE_NOT_EXIT = "the file must exists corresponding the value of the following field:Entry-Definitions,ETSI-Entry-Manifest,Entry-Tests,ETSI-Entry-Change-Log and Entry-Helm-Package.";
	}

	String INNER_EXCEPTION = "inner exception, please check the log.";
}
