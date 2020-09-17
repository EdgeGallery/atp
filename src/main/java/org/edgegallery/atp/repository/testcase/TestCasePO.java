package org.edgegallery.atp.repository.testcase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.PersistenceObject;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCasePO implements PersistenceObject<TestCase> {
    private String id;

    private String type;

    private String name;

    private String desc;

    private String content;

    // type means
    private String types;

    public TestCasePO() {}

    public TestCasePO(String id, String type, String name, String desc, String content) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.content = content;
    }

    @Override
    public TestCase toDomainModel() {
        return new TestCase();
    }


}
