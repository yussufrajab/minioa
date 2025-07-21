package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.LeaveWithoutPayRequestDto;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.LeaveWithoutPay;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.exception.BusinessRuleViolationException;
import com.zanzibar.csms.mapper.LeaveWithoutPayMapper;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.LeaveWithoutPayRepository;
import com.zanzibar.csms.service.BusinessRuleEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveWithoutPayService {

    private final LeaveWithoutPayRepository leaveWithoutPayRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveWithoutPayMapper leaveWithoutPayMapper;
    private final BusinessRuleEngine businessRuleEngine;

    @Transactional
    public LeaveWithoutPay createRequest(LeaveWithoutPayRequestDto requestDto, String createdBy) {
        log.info("Creating LWOP request for employee: {}", requestDto.getEmployeeId());

        // Get employee
        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new BusinessRuleViolationException("Employee not found: " + requestDto.getEmployeeId()));

        // Validate business rules
        validateBusinessRules(requestDto, employee);

        // Create entity
        LeaveWithoutPay leaveWithoutPay = leaveWithoutPayMapper.toEntity(requestDto);
        leaveWithoutPay.setEmployee(employee);
        leaveWithoutPay.setStatus(RequestStatus.SUBMITTED);
        // createdBy and createdAt are handled by BaseEntity auditing

        return leaveWithoutPayRepository.save(leaveWithoutPay);
    }

    private void validateBusinessRules(LeaveWithoutPayRequestDto requestDto, Employee employee) {
        // Check maximum 2 periods rule (FR5.1)
        List<LeaveWithoutPay> completedRequests = leaveWithoutPayRepository
                .findCompletedApprovedRequests(requestDto.getEmployeeId(), LocalDate.now());
        
        if (completedRequests.size() >= 2) {
            throw new BusinessRuleViolationException("Employee has already exhausted the maximum 2 LWOP periods allowed");
        }

        // Check for overlapping requests
        List<LeaveWithoutPay> overlappingRequests = leaveWithoutPayRepository
                .findOverlappingApprovedRequests(requestDto.getEmployeeId(), 
                        requestDto.getLeaveStartDate(), 
                        requestDto.getLeaveEndDate());
        
        if (!overlappingRequests.isEmpty()) {
            throw new BusinessRuleViolationException("LWOP request overlaps with existing approved request");
        }

        // Check loan guarantee requirements (FR5.2)
        if (requestDto.getHasLoanGuarantee() && !requestDto.getLoanGuaranteeConfirmed()) {
            throw new BusinessRuleViolationException("Loan guarantee confirmation is required when employee has loan guarantees");
        }

        // Check duration limits (already handled by validation annotation)
        Period period = Period.between(requestDto.getLeaveStartDate(), requestDto.getLeaveEndDate());
        int totalMonths = period.getYears() * 12 + period.getMonths();
        
        if (totalMonths < 1 || totalMonths > 36) {
            throw new BusinessRuleViolationException("LWOP duration must be between 1 month and 3 years");
        }

        // Check if employee has active or future requests
        List<LeaveWithoutPay> activeRequests = leaveWithoutPayRepository
                .findActiveOrFutureRequests(requestDto.getEmployeeId(), LocalDate.now());
        
        if (!activeRequests.isEmpty()) {
            throw new BusinessRuleViolationException("Employee has active or future LWOP requests");
        }
    }

    @Transactional(readOnly = true)
    public List<LeaveWithoutPay> findByEmployeeId(String employeeId) {
        return leaveWithoutPayRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public Optional<LeaveWithoutPay> findById(Long id) {
        return leaveWithoutPayRepository.findById(id);
    }

    @Transactional
    public LeaveWithoutPay updateStatus(Long id, RequestStatus status, String updatedBy) {
        LeaveWithoutPay request = leaveWithoutPayRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException("LWOP request not found: " + id));

        request.setStatus(status);
        // updatedBy and updatedAt are handled by BaseEntity auditing

        return leaveWithoutPayRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<LeaveWithoutPay> findPendingRequestsInPeriod(LocalDate startDate, LocalDate endDate) {
        return leaveWithoutPayRepository.findPendingRequestsInPeriod(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<LeaveWithoutPay> findApprovedByEmployeeId(String employeeId) {
        return leaveWithoutPayRepository.findApprovedByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public boolean hasExceededMaxPeriods(String employeeId) {
        List<LeaveWithoutPay> completedRequests = leaveWithoutPayRepository
                .findCompletedApprovedRequests(employeeId, LocalDate.now());
        return completedRequests.size() >= 2;
    }

    @Transactional
    public void deleteRequest(Long id) {
        LeaveWithoutPay request = leaveWithoutPayRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException("LWOP request not found: " + id));
        
        if (request.getStatus() == RequestStatus.APPROVED) {
            throw new BusinessRuleViolationException("Cannot delete approved LWOP request");
        }
        
        leaveWithoutPayRepository.delete(request);
    }
}