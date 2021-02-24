package org.edgegallery.atp.model.task.testScenarios;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskTestCase {

    /**
     * test case id
     */
    String id;

    /**
     * test case chinese name
     */
    String nameCh;

    /**
     * test case english name
     */
    String nameEn;

    /**
     * test case chinese description
     */
    String descriptionCh;

    /**
     * test case english description
     */
    String descriptionEn;

    /**
     * test case type: automatic or manual
     */
    String type;

    /**
     * test case execute result. The value is enum:success,failed or running.
     */
    String result;

    /**
     * test case fail reason,it can be empty when the result is not failed.
     */
    String reason;
}
