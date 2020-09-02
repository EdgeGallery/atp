package org.edgegallery.atp.interfaces.testcase.facade;

import org.edgegallery.atp.application.TestCaseService;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.domain.shared.PageCriteria;
import org.edgegallery.atp.interfaces.testcase.facade.dto.TestCaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service("TestCaseServiceFacade")
public class TestCaseServiceFacade {

    @Autowired
    private TestCaseService testCaseService;

    public void testCaseUpload(User user, List<MultipartFile> uploadFiles){

    }

    public ResponseEntity<List<TestCaseDto>> queryAll() {
        return ResponseEntity.ok(testCaseService.queryAll(new PageCriteria(100, 0, ""))
                .map(TestCaseDto::of)
                .getResults());
    }

    public ResponseEntity<TestCaseDto> queryByTestCaseId(String testCaseId) {
        return ResponseEntity.ok(TestCaseDto.of(testCaseService.queryByTestCaseId(testCaseId)));
    }


    public void deleteTestCase(String testCaseId, User user) {
        testCaseService.deleteTestCase(testCaseId, user.getUserId());
    }
}
