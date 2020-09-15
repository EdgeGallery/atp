package org.edgegallery.atp.interfaces.testcase.facade.dto;

import org.edgegallery.atp.model.TestCase;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestCaseDto {

    private String id;

    private String type;

    private String name;

    private String desc;

    private String content;

    public TestCaseDto() {

    }

    public TestCaseDto(String id, String type, String name, String desc, String content) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.content = content;
    }

    public static TestCaseDto of(TestCase testCase) {
        return new TestCaseDto();
    }
}
