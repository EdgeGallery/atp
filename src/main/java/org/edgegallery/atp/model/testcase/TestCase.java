package org.edgegallery.atp.model.testcase;

import org.edgegallery.atp.model.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

    public TestCase() {

    }

    public TestCase(String id, String name, String type, String className, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.className = className;
    }
}
