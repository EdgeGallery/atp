package org.edgegallery.atp.model.testcase;

import org.springframework.web.multipart.MultipartFile;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class UpdateTestCaseReq extends TestCase {
    MultipartFile file;
}
