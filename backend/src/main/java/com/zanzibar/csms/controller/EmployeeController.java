package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.EmployeeDto;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.security.CurrentUser;
import com.zanzibar.csms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Employee management endpoints")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all employees", description = "Get paginated list of employees")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO') or hasRole('CSCS')")
    public ResponseEntity<Page<EmployeeDto>> getAllEmployees(
            @PageableDefault(size = 20, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeDto> employees = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Get employee details by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO') or hasRole('HRO') or hasRole('CSCS')")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable String id) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/payroll/{payrollNumber}")
    @Operation(summary = "Get employee by payroll number", description = "Get employee details by payroll number")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO') or hasRole('HRO')")
    public ResponseEntity<EmployeeDto> getEmployeeByPayrollNumber(@PathVariable String payrollNumber) {
        EmployeeDto employee = employeeService.getEmployeeByPayrollNumber(payrollNumber);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/zanzibar-id/{zanzibarId}")
    @Operation(summary = "Get employee by Zanzibar ID", description = "Get employee details by Zanzibar ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO') or hasRole('HRO')")
    public ResponseEntity<EmployeeDto> getEmployeeByZanzibarId(@PathVariable String zanzibarId) {
        EmployeeDto employee = employeeService.getEmployeeByZanzibarId(zanzibarId);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Get employees by institution", description = "Get employees belonging to a specific institution")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO') or hasRole('HRO') or hasRole('HRRP')")
    public ResponseEntity<Page<EmployeeDto>> getEmployeesByInstitution(
            @PathVariable String institutionId,
            @PageableDefault(size = 20, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeDto> employees = employeeService.getEmployeesByInstitution(institutionId, pageable);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get employees by status", description = "Get employees with a specific employment status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO') or hasRole('CSCS')")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByStatus(@PathVariable EmploymentStatus status) {
        List<EmployeeDto> employees = employeeService.getEmployeesByStatus(status);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees", description = "Search employees by name, payroll number, or Zanzibar ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO') or hasRole('HRO')")
    public ResponseEntity<Page<EmployeeDto>> searchEmployees(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeDto> employees = employeeService.searchEmployees(searchTerm, pageable);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/institution/{institutionId}/search")
    @Operation(summary = "Search employees by institution", description = "Search employees within a specific institution")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO') or hasRole('HRO') or hasRole('HRRP')")
    public ResponseEntity<Page<EmployeeDto>> searchEmployeesByInstitution(
            @PathVariable String institutionId,
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeDto> employees = employeeService.searchEmployeesByInstitution(institutionId, searchTerm, pageable);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/eligible-for-confirmation")
    @Operation(summary = "Get employees eligible for confirmation", description = "Get employees who are eligible for employment confirmation")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<EmployeeDto>> getEligibleForConfirmation() {
        List<EmployeeDto> employees = employeeService.getUnconfirmedEmployeesEligibleForConfirmation();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/eligible-for-retirement")
    @Operation(summary = "Get employees eligible for retirement", description = "Get employees who are eligible for retirement")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<EmployeeDto>> getEligibleForRetirement() {
        List<EmployeeDto> employees = employeeService.getEmployeesEligibleForRetirement();
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @Operation(summary = "Create new employee", description = "Create a new employee record")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDto> createEmployee(
            @Valid @RequestBody EmployeeDto employeeDto,
            @CurrentUser User currentUser) {
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto, currentUser.getId(), currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee", description = "Update an existing employee record")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD')")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable String id,
            @Valid @RequestBody EmployeeDto employeeDto,
            @CurrentUser User currentUser) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto, currentUser.getId(), currentUser.getUsername());
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee", description = "Soft delete an employee record")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id, @CurrentUser User currentUser) {
        employeeService.deleteEmployee(id, currentUser.getId(), currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update employment status", description = "Update employee's employment status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Void> updateEmploymentStatus(
            @PathVariable String id,
            @RequestParam EmploymentStatus status,
            @CurrentUser User currentUser) {
        employeeService.updateEmploymentStatus(id, status, currentUser.getId(), currentUser.getUsername());
        return ResponseEntity.ok().build();
    }
}