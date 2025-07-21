package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.EmployeeDto;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.Institution;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final InstitutionRepository institutionRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        return employees.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return convertToDto(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeByPayrollNumber(String payrollNumber) {
        Employee employee = employeeRepository.findByPayrollNumber(payrollNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with payroll number: " + payrollNumber));
        return convertToDto(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeByZanzibarId(String zanzibarId) {
        Employee employee = employeeRepository.findByZanzibarId(zanzibarId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with Zanzibar ID: " + zanzibarId));
        return convertToDto(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> getEmployeesByInstitution(String institutionId, Pageable pageable) {
        Page<Employee> employees = employeeRepository.findByInstitutionId(institutionId, pageable);
        return employees.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByStatus(EmploymentStatus status) {
        List<Employee> employees = employeeRepository.findByEmploymentStatus(status);
        return employees.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> searchEmployees(String searchTerm, Pageable pageable) {
        Page<Employee> employees = employeeRepository.searchEmployees(searchTerm, pageable);
        return employees.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> searchEmployeesByInstitution(String institutionId, String searchTerm, Pageable pageable) {
        Page<Employee> employees = employeeRepository.searchEmployeesByInstitution(institutionId, searchTerm, pageable);
        return employees.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getUnconfirmedEmployeesEligibleForConfirmation() {
        LocalDate endDate = LocalDate.now().minusMonths(12);
        List<Employee> employees = employeeRepository.findUnconfirmedEmployeesEligibleForConfirmation(endDate);
        return employees.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesEligibleForRetirement() {
        LocalDate retirementDate = LocalDate.now().minusYears(60);
        List<Employee> employees = employeeRepository.findEmployeesEligibleForRetirement(retirementDate);
        return employees.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeDto employeeDto, String currentUserId, String currentUsername) {
        if (employeeRepository.existsByPayrollNumber(employeeDto.getPayrollNumber())) {
            throw new IllegalArgumentException("Employee with payroll number already exists");
        }

        if (employeeRepository.existsByZanzibarId(employeeDto.getZanzibarId())) {
            throw new IllegalArgumentException("Employee with Zanzibar ID already exists");
        }

        if (employeeRepository.existsByZssfNumber(employeeDto.getZssfNumber())) {
            throw new IllegalArgumentException("Employee with ZSSF number already exists");
        }

        Institution institution = institutionRepository.findById(employeeDto.getInstitutionId())
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + employeeDto.getInstitutionId()));

        Employee employee = Employee.builder()
                .fullName(employeeDto.getFullName())
                .dateOfBirth(employeeDto.getDateOfBirth())
                .placeOfBirth(employeeDto.getPlaceOfBirth())
                .region(employeeDto.getRegion())
                .countryOfBirth(employeeDto.getCountryOfBirth())
                .payrollNumber(employeeDto.getPayrollNumber())
                .zanzibarId(employeeDto.getZanzibarId())
                .zssfNumber(employeeDto.getZssfNumber())
                .rank(employeeDto.getRank())
                .ministry(employeeDto.getMinistry())
                .institution(institution)
                .department(employeeDto.getDepartment())
                .appointmentType(employeeDto.getAppointmentType())
                .contractType(employeeDto.getContractType())
                .recentTitleDate(employeeDto.getRecentTitleDate())
                .currentReportingOffice(employeeDto.getCurrentReportingOffice())
                .currentWorkplace(employeeDto.getCurrentWorkplace())
                .employmentDate(employeeDto.getEmploymentDate())
                .confirmationDate(employeeDto.getConfirmationDate())
                .loanGuaranteeStatus(employeeDto.getLoanGuaranteeStatus())
                .employmentStatus(employeeDto.getEmploymentStatus())
                .phoneNumber(employeeDto.getPhoneNumber())
                .gender(employeeDto.getGender())
                .contactAddress(employeeDto.getContactAddress())
                .build();

        employee.setCreatedBy(currentUserId);
        Employee savedEmployee = employeeRepository.save(employee);

        auditService.logAction(currentUserId, currentUsername, "CREATE_EMPLOYEE", "Employee", savedEmployee.getId(), 
                null, savedEmployee.toString(), true, null);

        return convertToDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployee(String id, EmployeeDto employeeDto, String currentUserId, String currentUsername) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        String beforeValue = employee.toString();

        employee.setFullName(employeeDto.getFullName());
        employee.setDateOfBirth(employeeDto.getDateOfBirth());
        employee.setPlaceOfBirth(employeeDto.getPlaceOfBirth());
        employee.setRegion(employeeDto.getRegion());
        employee.setCountryOfBirth(employeeDto.getCountryOfBirth());
        employee.setRank(employeeDto.getRank());
        employee.setMinistry(employeeDto.getMinistry());
        employee.setDepartment(employeeDto.getDepartment());
        employee.setAppointmentType(employeeDto.getAppointmentType());
        employee.setContractType(employeeDto.getContractType());
        employee.setRecentTitleDate(employeeDto.getRecentTitleDate());
        employee.setCurrentReportingOffice(employeeDto.getCurrentReportingOffice());
        employee.setCurrentWorkplace(employeeDto.getCurrentWorkplace());
        employee.setConfirmationDate(employeeDto.getConfirmationDate());
        employee.setLoanGuaranteeStatus(employeeDto.getLoanGuaranteeStatus());
        employee.setEmploymentStatus(employeeDto.getEmploymentStatus());
        employee.setPhoneNumber(employeeDto.getPhoneNumber());
        employee.setGender(employeeDto.getGender());
        employee.setContactAddress(employeeDto.getContactAddress());
        employee.setUpdatedBy(currentUserId);

        Employee updatedEmployee = employeeRepository.save(employee);

        auditService.logAction(currentUserId, currentUsername, "UPDATE_EMPLOYEE", "Employee", updatedEmployee.getId(), 
                beforeValue, updatedEmployee.toString(), true, null);

        return convertToDto(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(String id, String currentUserId, String currentUsername) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        String beforeValue = employee.toString();
        employee.setIsActive(false);
        employee.setUpdatedBy(currentUserId);
        employeeRepository.save(employee);

        auditService.logAction(currentUserId, currentUsername, "DELETE_EMPLOYEE", "Employee", id, 
                beforeValue, null, true, null);
    }

    @Transactional
    public void updateEmploymentStatus(String id, EmploymentStatus status, String currentUserId, String currentUsername) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        EmploymentStatus oldStatus = employee.getEmploymentStatus();
        employee.setEmploymentStatus(status);
        employee.setUpdatedBy(currentUserId);
        employeeRepository.save(employee);

        auditService.logAction(currentUserId, currentUsername, "UPDATE_EMPLOYMENT_STATUS", "Employee", id, 
                oldStatus.toString(), status.toString(), true, null);
    }

    private EmployeeDto convertToDto(Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .profileImage(employee.getProfileImage())
                .dateOfBirth(employee.getDateOfBirth())
                .placeOfBirth(employee.getPlaceOfBirth())
                .region(employee.getRegion())
                .countryOfBirth(employee.getCountryOfBirth())
                .payrollNumber(employee.getPayrollNumber())
                .zanzibarId(employee.getZanzibarId())
                .zssfNumber(employee.getZssfNumber())
                .rank(employee.getRank())
                .ministry(employee.getMinistry())
                .institutionId(employee.getInstitution().getId())
                .institutionName(employee.getInstitution().getName())
                .department(employee.getDepartment())
                .appointmentType(employee.getAppointmentType())
                .contractType(employee.getContractType())
                .recentTitleDate(employee.getRecentTitleDate())
                .currentReportingOffice(employee.getCurrentReportingOffice())
                .currentWorkplace(employee.getCurrentWorkplace())
                .employmentDate(employee.getEmploymentDate())
                .confirmationDate(employee.getConfirmationDate())
                .loanGuaranteeStatus(employee.getLoanGuaranteeStatus())
                .employmentStatus(employee.getEmploymentStatus())
                .phoneNumber(employee.getPhoneNumber())
                .gender(employee.getGender())
                .contactAddress(employee.getContactAddress())
                .build();
    }
}