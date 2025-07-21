package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.ConfirmationRequestDto;
import com.zanzibar.csms.entity.ConfirmationRequest;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.Institution;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.entity.enums.RequestType;
import com.zanzibar.csms.exception.ConfirmationEligibilityException;
import com.zanzibar.csms.exception.ConfirmationValidationException;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.RequestRepository;
import com.zanzibar.csms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationRequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ConfirmationRequestService confirmationRequestService;

    private Employee testEmployee;
    private User testUser;
    private ConfirmationRequestDto testRequestDto;
    private Institution testInstitution;

    @BeforeEach
    void setUp() {
        testInstitution = new Institution();
        testInstitution.setId("institution1");
        testInstitution.setName("Test Institution");

        testEmployee = new Employee();
        testEmployee.setId("employee1");
        testEmployee.setFullName("John Doe");
        testEmployee.setEmploymentStatus(EmploymentStatus.UNCONFIRMED);
        testEmployee.setEmploymentDate(LocalDate.now().minusMonths(13)); // 13 months ago - eligible
        testEmployee.setInstitution(testInstitution);

        testUser = new User();
        testUser.setId("user1");
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");

        testRequestDto = new ConfirmationRequestDto();
        testRequestDto.setEmployeeId("employee1");
        testRequestDto.setProbationStartDate(LocalDate.now().minusMonths(13));
        testRequestDto.setProbationEndDate(LocalDate.now().minusMonths(1));
        testRequestDto.setPerformanceRating("GOOD");
        testRequestDto.setSupervisorRecommendation("Good performance");
        testRequestDto.setHrAssessment("Recommended for confirmation");
        testRequestDto.setCurrentSalary(1000.0);
        testRequestDto.setProposedSalary(1100.0);
    }

    @Test
    void testIsEmployeeEligibleForConfirmation_EligibleEmployee_ReturnsTrue() {
        // Arrange
        when(employeeRepository.findById("employee1")).thenReturn(Optional.of(testEmployee));
        when(requestRepository.findByEmployeeId(eq("employee1"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        boolean result = confirmationRequestService.isEmployeeEligibleForConfirmation("employee1");

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsEmployeeEligibleForConfirmation_EmployeeNotUnconfirmed_ReturnsFalse() {
        // Arrange
        testEmployee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        when(employeeRepository.findById("employee1")).thenReturn(Optional.of(testEmployee));

        // Act
        boolean result = confirmationRequestService.isEmployeeEligibleForConfirmation("employee1");

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsEmployeeEligibleForConfirmation_EmployedLessThan12Months_ReturnsFalse() {
        // Arrange
        testEmployee.setEmploymentDate(LocalDate.now().minusMonths(6)); // Only 6 months
        when(employeeRepository.findById("employee1")).thenReturn(Optional.of(testEmployee));

        // Act
        boolean result = confirmationRequestService.isEmployeeEligibleForConfirmation("employee1");

        // Assert
        assertFalse(result);
    }

    @Test
    void testCreateConfirmationRequest_ValidRequest_Success() {
        // Arrange
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(employeeRepository.findById("employee1")).thenReturn(Optional.of(testEmployee));
        when(requestRepository.findByEmployeeId(eq("employee1"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        ConfirmationRequest savedRequest = new ConfirmationRequest();
        savedRequest.setId("request1");
        savedRequest.setEmployee(testEmployee);
        savedRequest.setSubmittedBy(testUser);
        savedRequest.setRequestType(RequestType.CONFIRMATION);
        
        when(requestRepository.save(any(ConfirmationRequest.class))).thenReturn(savedRequest);

        // Act
        ConfirmationRequestDto result = confirmationRequestService.createConfirmationRequest(testRequestDto, "user1");

        // Assert
        assertNotNull(result);
        verify(requestRepository).save(any(ConfirmationRequest.class));
        verify(auditService).logAction(eq("user1"), eq("testuser"), eq("CREATE_CONFIRMATION_REQUEST"), 
                eq("ConfirmationRequest"), any(), any(), any(), eq(true), any());
    }

    @Test
    void testCreateConfirmationRequest_IneligibleEmployee_ThrowsException() {
        // Arrange
        testEmployee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(employeeRepository.findById("employee1")).thenReturn(Optional.of(testEmployee));

        // Act & Assert
        assertThrows(ConfirmationEligibilityException.class, () -> {
            confirmationRequestService.createConfirmationRequest(testRequestDto, "user1");
        });
    }

    @Test
    void testValidateConfirmationRequest_InvalidSalaryIncrease_ThrowsException() {
        // Arrange
        testRequestDto.setCurrentSalary(1000.0);
        testRequestDto.setProposedSalary(1600.0); // 60% increase - exceeds 50% limit

        // Act & Assert
        assertThrows(ConfirmationValidationException.class, () -> {
            confirmationRequestService.validateConfirmationRequest(testRequestDto);
        });
    }

    @Test
    void testValidateConfirmationRequest_UnsatisfactoryPerformance_ThrowsException() {
        // Arrange
        testRequestDto.setPerformanceRating("UNSATISFACTORY");

        // Act & Assert
        assertThrows(ConfirmationValidationException.class, () -> {
            confirmationRequestService.validateConfirmationRequest(testRequestDto);
        });
    }

    @Test
    void testValidateConfirmationRequest_InvalidProbationPeriod_ThrowsException() {
        // Arrange
        testRequestDto.setProbationStartDate(LocalDate.now().minusMonths(1));
        testRequestDto.setProbationEndDate(LocalDate.now().minusMonths(2)); // End before start

        // Act & Assert
        assertThrows(ConfirmationValidationException.class, () -> {
            confirmationRequestService.validateConfirmationRequest(testRequestDto);
        });
    }

    @Test
    void testValidateConfirmationRequest_InsufficientProbationPeriod_ThrowsException() {
        // Arrange
        testRequestDto.setProbationStartDate(LocalDate.now().minusMonths(6));
        testRequestDto.setProbationEndDate(LocalDate.now().minusMonths(1)); // Only 5 months

        // Act & Assert
        assertThrows(ConfirmationValidationException.class, () -> {
            confirmationRequestService.validateConfirmationRequest(testRequestDto);
        });
    }

    @Test
    void testValidateConfirmationRequest_MissingRequiredFields_ThrowsException() {
        // Arrange
        testRequestDto.setSupervisorRecommendation(null);

        // Act & Assert
        assertThrows(ConfirmationValidationException.class, () -> {
            confirmationRequestService.validateConfirmationRequest(testRequestDto);
        });
    }

    @Test
    void testGetConfirmationEligibilityReason_IneligibleEmployee_ReturnsReason() {
        // Arrange
        testEmployee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        when(employeeRepository.findById("employee1")).thenReturn(Optional.of(testEmployee));

        // Act
        String reason = confirmationRequestService.getConfirmationEligibilityReason("employee1");

        // Assert
        assertEquals("Employee status is ACTIVE, not UNCONFIRMED", reason);
    }

    @Test
    void testGetConfirmationEligibilityReason_EligibleEmployee_ReturnsEligibleMessage() {
        // Arrange
        when(employeeRepository.findById("employee1")).thenReturn(Optional.of(testEmployee));
        when(requestRepository.findByEmployeeId(eq("employee1"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        String reason = confirmationRequestService.getConfirmationEligibilityReason("employee1");

        // Assert
        assertEquals("Employee is eligible for confirmation", reason);
    }
}