package org.edgegallery.atp.model.testcase;

import org.edgegallery.atp.model.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(value={"filePath","className"})
public class TestCase implements Entity {

    /**
     * test case id
     */
    private String id;

    /**
     * test case name
     */
    private String name;

    /**
     * test name type, the value is enum:virus,compliance or sandbox.
     */
    private String type;

    /**
     * package path of test case class
     */
    private String className;

    /**
     * description of test case
     */
    private String description;

    /**
     * content of test case souce code. The reserved field.
     */
    private String hashCode;
    
    /**
     * file storage path
     */
    private String filePath;
    
    /**
     * test case language
     */
    private String codeLanguage;
    
    /**
     * expect test result
     */
    private String expectResult;
   
    /**
     * verification model
     */
    private String verificationModel;

    public TestCase() {

    }
}
