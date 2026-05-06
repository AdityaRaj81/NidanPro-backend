package nidanpro_backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.CreateTestParameterRequest;
import nidanpro_backend.dto.CreateTestRequest;
import nidanpro_backend.model.LabTest;
import nidanpro_backend.model.RangeRuleType;
import nidanpro_backend.model.StaffUser;
import nidanpro_backend.model.TestParameter;
import nidanpro_backend.repository.LabTestRepository;
import nidanpro_backend.repository.StaffUserRepository;
import nidanpro_backend.repository.TestParameterRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

  private final LabTestRepository labTestRepository;
  private final TestParameterRepository testParameterRepository;
  private final StaffUserRepository staffUserRepository;

  public LabTest createTest(CreateTestRequest request, String staffEmail) {
    StaffUser createdBy = staffUserRepository.findByEmailIgnoreCase(staffEmail)
        .orElseThrow(() -> new IllegalArgumentException("Staff user not found"));

    LabTest test = new LabTest();
    test.setTestCode(request.testCode());
    test.setTestName(request.testName());
    test.setDescription(request.description());
    test.setPrice(request.price());
    test.setActive(request.active());
    test.setCreatedBy(createdBy);
    test.setUpdatedBy(createdBy);
    test.setLab(createdBy.getLab());
    return labTestRepository.save(test);
  }

  public LabTest updateTest(Long testId, CreateTestRequest request, String staffEmail) {
    StaffUser updatedBy = staffUserRepository.findByEmailIgnoreCase(staffEmail)
        .orElseThrow(() -> new IllegalArgumentException("Staff user not found"));

    LabTest test = labTestRepository.findById(testId)
        .orElseThrow(() -> new IllegalArgumentException("Test not found"));

    test.setTestCode(request.testCode());
    test.setTestName(request.testName());
    test.setDescription(request.description());
    test.setPrice(request.price());
    test.setActive(request.active());
    test.setUpdatedBy(updatedBy);
    if (test.getLab() == null) {
      test.setLab(updatedBy.getLab());
    }
    return labTestRepository.save(test);
  }

  public List<LabTest> listTests() {
    return labTestRepository.findAll();
  }

  public List<LabTest> listActiveTests() {
    return labTestRepository.findByActiveTrue();
  }

  public Optional<LabTest> getTestById(Long testId) {
    return labTestRepository.findById(testId);
  }

  public TestParameter addParameter(Long testId, CreateTestParameterRequest request) {
    LabTest test = labTestRepository.findById(testId)
        .orElseThrow(() -> new IllegalArgumentException("Test not found"));

    RangeRuleType ruleType = request.rangeRuleType() == null
        ? RangeRuleType.BETWEEN
        : request.rangeRuleType();

    validateRangeRequest(ruleType, request.lowerBound(), request.upperBound(), request.referenceRange());

    TestParameter parameter = new TestParameter();
    parameter.setLabTest(test);
    parameter.setParameterName(request.parameterName());
    parameter.setUnit(request.unit());
    parameter.setRangeRuleType(ruleType);
    parameter.setLowerBound(request.lowerBound());
    parameter.setUpperBound(request.upperBound());
    parameter.setReferenceRange(
        buildReferenceRange(ruleType, request.lowerBound(), request.upperBound(), request.referenceRange()));
    return testParameterRepository.save(parameter);
  }

  public List<TestParameter> listParameters(Long testId) {
    return testParameterRepository.findByLabTest_Id(testId);
  }

  private void validateRangeRequest(
      RangeRuleType ruleType,
      BigDecimal lowerBound,
      BigDecimal upperBound,
      String referenceRange) {
    switch (ruleType) {
      case BETWEEN -> {
        if (lowerBound == null || upperBound == null) {
          throw new IllegalArgumentException("Both lower and upper bounds are required for BETWEEN range.");
        }
        if (lowerBound.compareTo(upperBound) > 0) {
          throw new IllegalArgumentException("Lower bound cannot be greater than upper bound.");
        }
      }
      case LESS_THAN, LESS_THAN_OR_EQUAL -> {
        if (upperBound == null) {
          throw new IllegalArgumentException("Upper bound is required for LESS THAN range.");
        }
      }
      case GREATER_THAN, GREATER_THAN_OR_EQUAL -> {
        if (lowerBound == null) {
          throw new IllegalArgumentException("Lower bound is required for GREATER THAN range.");
        }
      }
      case CUSTOM_TEXT -> {
        if (referenceRange == null || referenceRange.isBlank()) {
          throw new IllegalArgumentException("Reference range text is required for CUSTOM_TEXT rule.");
        }
      }
      default -> throw new IllegalArgumentException("Unsupported range rule type.");
    }
  }

  private String buildReferenceRange(
      RangeRuleType ruleType,
      BigDecimal lowerBound,
      BigDecimal upperBound,
      String referenceRangeText) {
    return switch (ruleType) {
      case BETWEEN ->
        lowerBound.stripTrailingZeros().toPlainString() + " - " + upperBound.stripTrailingZeros().toPlainString();
      case LESS_THAN -> "< " + upperBound.stripTrailingZeros().toPlainString();
      case LESS_THAN_OR_EQUAL -> "<= " + upperBound.stripTrailingZeros().toPlainString();
      case GREATER_THAN -> "> " + lowerBound.stripTrailingZeros().toPlainString();
      case GREATER_THAN_OR_EQUAL -> ">= " + lowerBound.stripTrailingZeros().toPlainString();
      case CUSTOM_TEXT -> referenceRangeText;
    };
  }
}
