package org.edgegallery.atp.domain.model.testcase;

import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.domain.shared.Entity;

@Setter
@Getter
public class TestCase implements Entity {

    private String id;

    private String type;

    private String name;

    private String desc;

    private String content;

    public TestCase() {

    }

    public TestCase(String id, String type, String name, String desc, String content) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.content = content;
    }
}
