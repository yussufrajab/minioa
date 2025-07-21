package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.promotion.PromotionRequestCreateDto;
import com.zanzibar.csms.dto.promotion.PromotionRequestResponseDto;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.PromotionRequest;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.entity.enums.PromotionType;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.exception.PromotionEligibilityException;
import com.zanzibar.csms.exception.PromotionValidationException;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.mapper.PromotionRequestMapper;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.PromotionRequestRepository;
import com.zanzibar.csms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionRequestServiceTest {

    @Mock
    private PromotionRequestRepository promotionRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PromotionRequestMapper promotionRequestMapper;

    @Mock
    private AuditService auditService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PromotionRequestService promotionRequestService;

    private Employee employee;
    private User submittedBy;
    private PromotionRequestCreateDto createDto;
    private PromotionRequest promotionRequest;
    private PromotionRequestResponseDto responseDto;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId("emp-123");
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);

        submittedBy = new User();
        submittedBy.setId("user-123");

        createDto = PromotionRequestCreateDto.builder()
            .employeeId("emp-123")
            .promotionType(PromotionType.PERFORMANCE)
            .currentPosition("Software Engineer")
            .currentGrade("G7")
            .proposedPosition("Senior Software Engineer")
            .proposedGrade("G8")
            .currentSalary(50000.0)
            .proposedSalary(60000.0)
            .effectiveDate(LocalDate.now().plusDays(30))
            .justification("Excellent performance and leadership skills")
            .performanceRating("EXCELLENT")
            .yearsInCurrentPosition(3)
            .qualificationsMet("Bachelor's degree in Computer Science")
            .supervisorRecommendation("Highly recommended for promotion")
            .build();

        promotionRequest = new PromotionRequest();
        promotionRequest.setId("prom-123");
        promotionRequest.setStatus(RequestStatus.SUBMITTED);

        responseDto = new PromotionRequestResponseDto();
        responseDto.setId("prom-123");
        responseDto.setStatus(RequestStatus.SUBMITTED);
    }

    @Test
    @WithMockUser(roles = "HRO")
    void createPromotionRequest_Success() {
        // Given
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.of(employee));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(submittedBy));
        when(promotionRequestRepository.hasActivePendingPromotion("emp-123")).thenReturn(false);
        when(promotionRequestRepository.hasApprovedPromotionPending("emp-123", any(LocalDate.class))).thenReturn(false);
        when(promotionRequestRepository.save(any(PromotionRequest.class))).thenReturn(promotionRequest);
        when(promotionRequestMapper.toResponseDto(promotionRequest)).thenReturn(responseDto);

        // When
        PromotionRequestResponseDto result = promotionRequestService.createPromotionRequest(createDto, "user-123");

        // Then
        assertNotNull(result);
        assertEquals("prom-123", result.getId());
        assertEquals(RequestStatus.SUBMITTED, result.getStatus());
        verify(promotionRequestRepository).save(any(PromotionRequest.class));
        verify(auditService).logAction(any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(notificationService).sendWorkflowNotification(any(), any(), any());
    }

    @Test
    void createPromotionRequest_EmployeeNotFound() {
        // Given
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            promotionRequestService.createPromotionRequest(createDto, "user-123");
        });
    }

    @Test
    void createPromotionRequest_EmployeeInactive() {
        // Given
        employee.setEmploymentStatus(EmploymentStatus.TERMINATED);
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.of(employee));

        // When & Then
        assertThrows(PromotionEligibilityException.class, () -> {
            promotionRequestService.createPromotionRequest(createDto, "user-123");
        });
    }

    @Test
    void createPromotionRequest_EmployeeHasPendingPromotion() {
        // Given
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.of(employee));
        when(promotionRequestRepository.hasActivePendingPromotion("emp-123")).thenReturn(true);

        // When & Then
        assertThrows(PromotionEligibilityException.class, () -> {
            promotionRequestService.createPromotionRequest(createDto, "user-123");
        });
    }

    @Test
    void createPromotionRequest_InsufficientYearsInPosition() {
        // Given
        createDto.setYearsInCurrentPosition(1);
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.of(employee));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(submittedBy));
        when(promotionRequestRepository.hasActivePendingPromotion("emp-123")).thenReturn(false);
        when(promotionRequestRepository.hasApprovedPromotionPending("emp-123", any(LocalDate.class))).thenReturn(false);

        // When & Then
        assertThrows(PromotionEligibilityException.class, () -> {
            promotionRequestService.createPromotionRequest(createDto, "user-123");
        });
    }

    @Test
    void createPromotionRequest_EducationalPromotionWithoutQualifications() {
        // Given
        createDto.setPromotionType(PromotionType.EDUCATIONAL);
        createDto.setQualificationsMet(null);
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.of(employee));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(submittedBy));
        when(promotionRequestRepository.hasActivePendingPromotion("emp-123")).thenReturn(false);
        when(promotionRequestRepository.hasApprovedPromotionPending("emp-123", any(LocalDate.class))).thenReturn(false);

        // When & Then
        assertThrows(PromotionValidationException.class, () -> {
            promotionRequestService.createPromotionRequest(createDto, "user-123");
        });
    }

    @Test
    void createPromotionRequest_PerformancePromotionWithoutRating() {
        // Given
        createDto.setPerformanceRating(null);
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.of(employee));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(submittedBy));
        when(promotionRequestRepository.hasActivePendingPromotion("emp-123")).thenReturn(false);
        when(promotionRequestRepository.hasApprovedPromotionPending("emp-123", any(LocalDate.class))).thenReturn(false);

        // When & Then
        assertThrows(PromotionValidationException.class, () -> {
            promotionRequestService.createPromotionRequest(createDto, "user-123");
        });
    }

    @Test
    void createPromotionRequest_PerformancePromotionWithPoorRating() {
        // Given
        createDto.setPerformanceRating("UNSATISFACTORY");
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.of(employee));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(submittedBy));
        when(promotionRequestRepository.hasActivePendingPromotion("emp-123")).thenReturn(false);
        when(promotionRequestRepository.hasApprovedPromotionPending("emp-123", any(LocalDate.class))).thenReturn(false);

        // When & Then
        assertThrows(PromotionEligibilityException.class, () -> {
            promotionRequestService.createPromotionRequest(createDto, "user-123");
        });
    }

    @Test
    void createPromotionRequest_SalaryIncreaseExceedsLimit() {
        // Given
        createDto.setCurrentSalary(50000.0);
        createDto.setProposedSalary(80000.0); // 60% increase
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.of(employee));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(submittedBy));
        when(promotionRequestRepository.hasActivePendingPromotion("emp-123")).thenReturn(false);
        when(promotionRequestRepository.hasApprovedPromotionPending("emp-123", any(LocalDate.class))).thenReturn(false);

        // When & Then
        assertThrows(PromotionValidationException.class, () -> {
            promotionRequestService.createPromotionRequest(createDto, "user-123");
        });
    }

    @Test
    void createPromotionRequest_SamePositionAndGrade() {
        // Given
        createDto.setCurrentPosition("Software Engineer");
        createDto.setProposedPosition("Software Engineer");
        createDto.setCurrentGrade("G7");
        createDto.setProposedGrade("G7");
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.of(employee));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(submittedBy));
        when(promotionRequestRepository.hasActivePendingPromotion("emp-123")).thenReturn(false);
        when(promotionRequestRepository.hasApprovedPromotionPending("emp-123", any(LocalDate.class))).thenReturn(false);

        // When & Then
        assertThrows(PromotionValidationException.class, () -> {
            promotionRequestService.createPromotionRequest(createDto, "user-123");
        });
    }

    @Test
    void createPromotionRequest_EffectiveDateInPast() {
        // Given
        createDto.setEffectiveDate(LocalDate.now().minusDays(1));
        when(employeeRepository.findById("emp-123")).thenReturn(Optional.of(employee));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(submittedBy));
        when(promotionRequestRepository.hasActivePendingPromotion("emp-123")).thenReturn(false);
        when(promotionRequestRepository.hasApprovedPromotionPending("emp-123", any(LocalDate.class))).thenReturn(false);

        // When & Then
        assertThrows(PromotionValidationException.class, () -> {
            promotionRequestService.createPromotionRequest(createDto, "user-123");
        });
    }

    @Test
    @WithMockUser(roles = "HHRMD")
    void approvePromotionRequest_Success() {
        // Given
        when(promotionRequestRepository.findById("prom-123")).thenReturn(Optional.of(promotionRequest));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(submittedBy));
        when(promotionRequestRepository.save(any(PromotionRequest.class))).thenReturn(promotionRequest);
        when(promotionRequestMapper.toResponseDto(promotionRequest)).thenReturn(responseDto);

        // When
        PromotionRequestResponseDto result = promotionRequestService.approvePromotionRequest("prom-123", "user-123", "Approved");

        // Then
        assertNotNull(result);
        verify(promotionRequestRepository).save(any(PromotionRequest.class));
        verify(auditService).logAction(any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(notificationService).sendWorkflowNotification(any(), any(), any());
    }

    @Test
    void approvePromotionRequest_RequestNotFound() {
        // Given
        when(promotionRequestRepository.findById("prom-123")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            promotionRequestService.approvePromotionRequest("prom-123", "user-123", "Approved");
        });
    }
}