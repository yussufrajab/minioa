package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.dashboard.DashboardMetricsDto;
import com.zanzibar.csms.dto.dashboard.UserActivityDto;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.*;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final RequestRepository requestRepository;
    private final ComplaintRepository complaintRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public DashboardMetricsDto getDashboardMetrics(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return switch (user.getRole()) {
            case ADMIN, CSCS -> getAdminDashboardMetrics();
            case HHRMD -> getHHRMDDashboardMetrics();
            case HRMO -> getHRMODashboardMetrics();
            case DO -> getDODashboardMetrics();
            case HRO -> getHRODashboardMetrics(user);
            case HRRP -> getHRRPDashboardMetrics(user);
            case PO -> getPlanningOfficerMetrics();
            case EMP -> getEmployeeDashboardMetrics(user);
            default -> throw new IllegalArgumentException("Invalid user role for dashboard access");
        };
    }

    private DashboardMetricsDto getAdminDashboardMetrics() {
        return DashboardMetricsDto.builder()
            .employeeMetrics(buildEmployeeMetrics(null))
            .requestMetrics(buildRequestMetrics(null))
            .complaintMetrics(buildComplaintMetrics(null))
            .workflowMetrics(buildWorkflowMetrics())
            .institutionMetrics(buildInstitutionMetrics())
            .build();
    }

    private DashboardMetricsDto getHHRMDDashboardMetrics() {
        return DashboardMetricsDto.builder()
            .employeeMetrics(buildEmployeeMetrics(null))
            .requestMetrics(buildRequestMetricsForRole(UserRole.HHRMD))
            .complaintMetrics(buildComplaintMetrics(null))
            .workflowMetrics(buildWorkflowMetricsForRole(UserRole.HHRMD))
            .institutionMetrics(buildInstitutionMetrics())
            .build();
    }

    private DashboardMetricsDto getHRMODashboardMetrics() {
        return DashboardMetricsDto.builder()
            .employeeMetrics(buildEmployeeMetrics(null))
            .requestMetrics(buildRequestMetricsForRole(UserRole.HRMO))
            .workflowMetrics(buildWorkflowMetricsForRole(UserRole.HRMO))
            .institutionMetrics(buildInstitutionMetrics())
            .build();
    }

    private DashboardMetricsDto getDODashboardMetrics() {
        return DashboardMetricsDto.builder()
            .complaintMetrics(buildComplaintMetrics(null))
            .workflowMetrics(buildWorkflowMetricsForRole(UserRole.DO))
            .build();
    }

    private DashboardMetricsDto getHRODashboardMetrics(User user) {
        String institutionId = user.getEmployee() != null ? user.getEmployee().getInstitution().getId() : null;
        
        return DashboardMetricsDto.builder()
            .employeeMetrics(buildEmployeeMetrics(institutionId))
            .requestMetrics(buildRequestMetrics(institutionId))
            .institutionMetrics(buildInstitutionMetricsForInstitution(institutionId))
            .build();
    }

    private DashboardMetricsDto getHRRPDashboardMetrics(User user) {
        String institutionId = user.getEmployee() != null ? user.getEmployee().getInstitution().getId() : null;
        
        return DashboardMetricsDto.builder()
            .employeeMetrics(buildEmployeeMetrics(institutionId))
            .requestMetrics(buildRequestMetrics(institutionId))
            .complaintMetrics(buildComplaintMetrics(institutionId))
            .institutionMetrics(buildInstitutionMetricsForInstitution(institutionId))
            .build();
    }

    private DashboardMetricsDto getPlanningOfficerMetrics() {
        return DashboardMetricsDto.builder()
            .employeeMetrics(buildEmployeeMetrics(null))
            .requestMetrics(buildRequestMetrics(null))
            .complaintMetrics(buildComplaintMetrics(null))
            .institutionMetrics(buildInstitutionMetrics())
            .build();
    }

    private DashboardMetricsDto getEmployeeDashboardMetrics(User user) {
        String employeeId = user.getEmployee() != null ? user.getEmployee().getId() : null;
        
        return DashboardMetricsDto.builder()
            .requestMetrics(buildPersonalRequestMetrics(employeeId))
            .complaintMetrics(buildPersonalComplaintMetrics(employeeId))
            .build();
    }

    private DashboardMetricsDto.EmployeeMetrics buildEmployeeMetrics(String institutionId) {
        long totalEmployees = institutionId != null ? 
            employeeRepository.countByInstitutionId(institutionId) : 
            employeeRepository.count();

        long activeEmployees = institutionId != null ?
            employeeRepository.countByInstitutionIdAndIsActive(institutionId, true) :
            employeeRepository.countByIsActive(true);

        long inactiveEmployees = totalEmployees - activeEmployees;

        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long newHiresThisMonth = institutionId != null ?
            employeeRepository.countByInstitutionIdAndCreatedAtAfter(institutionId, monthStart) :
            employeeRepository.countByCreatedAtAfter(monthStart);

        Map<String, Long> employeesByInstitution = new HashMap<>();
        Map<String, Long> employeesByGrade = new HashMap<>();

        if (institutionId == null) {
            employeesByInstitution = institutionRepository.findAll().stream()
                .collect(Collectors.toMap(
                    institution -> institution.getName(),
                    institution -> employeeRepository.countByInstitutionId(institution.getId())
                ));
        }

        return DashboardMetricsDto.EmployeeMetrics.builder()
            .totalEmployees(totalEmployees)
            .activeEmployees(activeEmployees)
            .inactiveEmployees(inactiveEmployees)
            .newHiresThisMonth(newHiresThisMonth)
            .employeesByInstitution(employeesByInstitution)
            .employeesByGrade(employeesByGrade)
            .build();
    }

    private DashboardMetricsDto.RequestMetrics buildRequestMetrics(String institutionId) {
        long totalRequests = institutionId != null ?
            requestRepository.countByEmployeeInstitutionId(institutionId) :
            requestRepository.count();

        long pendingRequests = institutionId != null ?
            requestRepository.countByEmployeeInstitutionIdAndStatus(institutionId, RequestStatus.SUBMITTED) :
            requestRepository.countByStatus(RequestStatus.SUBMITTED);

        long approvedRequests = institutionId != null ?
            requestRepository.countByEmployeeInstitutionIdAndStatus(institutionId, RequestStatus.APPROVED) :
            requestRepository.countByStatus(RequestStatus.APPROVED);

        long rejectedRequests = institutionId != null ?
            requestRepository.countByEmployeeInstitutionIdAndStatus(institutionId, RequestStatus.REJECTED) :
            requestRepository.countByStatus(RequestStatus.REJECTED);

        Map<String, Long> requestsByType = Arrays.stream(RequestType.values())
            .collect(Collectors.toMap(
                RequestType::name,
                type -> institutionId != null ?
                    requestRepository.countByEmployeeInstitutionIdAndRequestType(institutionId, type) :
                    requestRepository.countByRequestType(type)
            ));

        Map<String, Long> requestsByStatus = Arrays.stream(RequestStatus.values())
            .collect(Collectors.toMap(
                RequestStatus::name,
                status -> institutionId != null ?
                    requestRepository.countByEmployeeInstitutionIdAndStatus(institutionId, status) :
                    requestRepository.countByStatus(status)
            ));

        return DashboardMetricsDto.RequestMetrics.builder()
            .totalRequests(totalRequests)
            .pendingRequests(pendingRequests)
            .approvedRequests(approvedRequests)
            .rejectedRequests(rejectedRequests)
            .requestsByType(requestsByType)
            .requestsByStatus(requestsByStatus)
            .averageProcessingTime(0.0)
            .build();
    }

    private DashboardMetricsDto.RequestMetrics buildRequestMetricsForRole(UserRole role) {
        Map<String, Long> pendingByType = new HashMap<>();
        
        return DashboardMetricsDto.RequestMetrics.builder()
            .pendingRequests(0L)
            .requestsByType(pendingByType)
            .build();
    }

    private DashboardMetricsDto.ComplaintMetrics buildComplaintMetrics(String institutionId) {
        long totalComplaints = institutionId != null ?
            complaintRepository.countByComplainantInstitutionId(institutionId) :
            complaintRepository.count();

        long pendingComplaints = complaintRepository.countByStatus(ComplaintStatus.SUBMITTED);
        long underInvestigation = complaintRepository.countByStatus(ComplaintStatus.UNDER_INVESTIGATION);
        long resolvedComplaints = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);

        Map<String, Long> complaintsByType = Arrays.stream(ComplaintType.values())
            .collect(Collectors.toMap(
                ComplaintType::name,
                type -> complaintRepository.countByComplaintType(type)
            ));

        Map<String, Long> complaintsBySeverity = Arrays.stream(ComplaintSeverity.values())
            .collect(Collectors.toMap(
                ComplaintSeverity::name,
                severity -> complaintRepository.countBySeverity(severity)
            ));

        return DashboardMetricsDto.ComplaintMetrics.builder()
            .totalComplaints(totalComplaints)
            .pendingComplaints(pendingComplaints)
            .underInvestigation(underInvestigation)
            .resolvedComplaints(resolvedComplaints)
            .complaintsByType(complaintsByType)
            .complaintsBySeverity(complaintsBySeverity)
            .averageResolutionTime(0.0)
            .averageSatisfactionRating(0.0)
            .build();
    }

    private DashboardMetricsDto.WorkflowMetrics buildWorkflowMetrics() {
        return DashboardMetricsDto.WorkflowMetrics.builder()
            .pendingHRMOApprovals(0L)
            .pendingHHRMDApprovals(0L)
            .pendingDOApprovals(0L)
            .workflowStepsByUser(new HashMap<>())
            .bottlenecksByStep(new HashMap<>())
            .build();
    }

    private DashboardMetricsDto.WorkflowMetrics buildWorkflowMetricsForRole(UserRole role) {
        return DashboardMetricsDto.WorkflowMetrics.builder()
            .workflowStepsByUser(new HashMap<>())
            .bottlenecksByStep(new HashMap<>())
            .build();
    }

    private DashboardMetricsDto.InstitutionMetrics buildInstitutionMetrics() {
        long totalInstitutions = institutionRepository.count();
        long activeInstitutions = institutionRepository.countByIsActive(true);

        Map<String, Long> employeeCountByInstitution = institutionRepository.findAll().stream()
            .collect(Collectors.toMap(
                institution -> institution.getName(),
                institution -> employeeRepository.countByInstitutionId(institution.getId())
            ));

        return DashboardMetricsDto.InstitutionMetrics.builder()
            .totalInstitutions(totalInstitutions)
            .activeInstitutions(activeInstitutions)
            .employeeCountByInstitution(employeeCountByInstitution)
            .requestCountByInstitution(new HashMap<>())
            .complaintCountByInstitution(new HashMap<>())
            .build();
    }

    private DashboardMetricsDto.InstitutionMetrics buildInstitutionMetricsForInstitution(String institutionId) {
        if (institutionId == null) return null;

        return DashboardMetricsDto.InstitutionMetrics.builder()
            .totalInstitutions(1L)
            .activeInstitutions(1L)
            .employeeCountByInstitution(Map.of("Current Institution", 
                employeeRepository.countByInstitutionId(institutionId)))
            .build();
    }

    private DashboardMetricsDto.RequestMetrics buildPersonalRequestMetrics(String employeeId) {
        if (employeeId == null) return null;

        long totalRequests = requestRepository.countByEmployeeId(employeeId);
        long pendingRequests = requestRepository.countByEmployeeIdAndStatus(employeeId, RequestStatus.SUBMITTED);
        long approvedRequests = requestRepository.countByEmployeeIdAndStatus(employeeId, RequestStatus.APPROVED);
        long rejectedRequests = requestRepository.countByEmployeeIdAndStatus(employeeId, RequestStatus.REJECTED);

        return DashboardMetricsDto.RequestMetrics.builder()
            .totalRequests(totalRequests)
            .pendingRequests(pendingRequests)
            .approvedRequests(approvedRequests)
            .rejectedRequests(rejectedRequests)
            .build();
    }

    private DashboardMetricsDto.ComplaintMetrics buildPersonalComplaintMetrics(String employeeId) {
        if (employeeId == null) return null;

        long totalComplaints = complaintRepository.countByComplainantId(employeeId);
        long pendingComplaints = complaintRepository.countByComplainantIdAndStatus(employeeId, ComplaintStatus.SUBMITTED);
        long resolvedComplaints = complaintRepository.countByComplainantIdAndStatus(employeeId, ComplaintStatus.RESOLVED);

        return DashboardMetricsDto.ComplaintMetrics.builder()
            .totalComplaints(totalComplaints)
            .pendingComplaints(pendingComplaints)
            .resolvedComplaints(resolvedComplaints)
            .build();
    }

    public UserActivityDto getUserActivity(String username, int limit) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<UserActivityDto.ActivityItem> recentActivities = getRecentActivities(user, limit);
        List<UserActivityDto.TaskItem> pendingTasks = getPendingTasks(user, limit);
        List<UserActivityDto.NotificationItem> notifications = getNotifications(user, limit);

        return UserActivityDto.builder()
            .recentActivities(recentActivities)
            .pendingTasks(pendingTasks)
            .notifications(notifications)
            .build();
    }

    private List<UserActivityDto.ActivityItem> getRecentActivities(User user, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        return auditLogRepository.findByUserId(user.getId(), pageRequest)
            .stream()
            .map(audit -> UserActivityDto.ActivityItem.builder()
                .id(audit.getId().toString())
                .type(audit.getAction())
                .action(audit.getAction())
                .description(audit.getAction() + " on " + audit.getEntityType())
                .entityType(audit.getEntityType())
                .entityId(audit.getEntityId())
                .userName(audit.getUsername())
                .timestamp(audit.getTimestamp())
                .status(audit.getSuccess() != null && audit.getSuccess() ? "SUCCESS" : "FAILED")
                .build())
            .collect(Collectors.toList());
    }

    private List<UserActivityDto.TaskItem> getPendingTasks(User user, int limit) {
        return new ArrayList<>();
    }

    private List<UserActivityDto.NotificationItem> getNotifications(User user, int limit) {
        return new ArrayList<>();
    }
}