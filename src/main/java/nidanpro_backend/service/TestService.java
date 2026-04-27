package nidanpro_backend.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.CreateTestParameterRequest;
import nidanpro_backend.dto.CreateTestRequest;
import nidanpro_backend.model.LabTest;
import nidanpro_backend.model.TestParameter;
import nidanpro_backend.repository.LabTestRepository;
import nidanpro_backend.repository.TestParameterRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

  private final LabTestRepository labTestRepository;
  private final TestParameterRepository testParameterRepository;

  public LabTest createTest(CreateTestRequest request) {
    LabTest test = new LabTest();
    test.setTestCode(request.testCode());
    test.setTestName(request.testName());
    test.setDescription(request.description());
    test.setPrice(request.price());
    test.setActive(request.active());
    return labTestRepository.save(test);
  }

  public List<LabTest> listTests() {
    return labTestRepository.findAll();
  }

  public TestParameter addParameter(Long testId, CreateTestParameterRequest request) {
    LabTest test = labTestRepository.findById(testId)
        .orElseThrow(() -> new IllegalArgumentException("Test not found"));

    TestParameter parameter = new TestParameter();
    parameter.setLabTest(test);
    parameter.setParameterName(request.parameterName());
    parameter.setUnit(request.unit());
    parameter.setReferenceRange(request.referenceRange());
    return testParameterRepository.save(parameter);
  }

  public List<TestParameter> listParameters(Long testId) {
    return testParameterRepository.findByLabTest_Id(testId);
  }
}
