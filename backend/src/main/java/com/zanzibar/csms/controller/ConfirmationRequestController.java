package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.ConfirmationRequestDto;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.security.CurrentUser;
import com.zanzibar.csms.service.ConfirmationRequestService;
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

@RestController
@RequestMapping("/requests/confirmation")
@RequiredArgsConstructor
@Tag(name = "Confirmation Requests", description = "Employee Confirmation Request Management")
public class ConfirmationRequestController {

    private final ConfirmationRequestService confirmationRequestService;

    @GetMapping
    @Operation(summary = "Get all confirmation requests", description = "Get paginated list of confirmation requests")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<Page<ConfirmationRequestDto>> getAllConfirmationRequests(
            @PageableDefault(sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ConfirmationRequestDto> requests = confirmationRequestService.getAllConfirmationRequests(pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get confirmation request by ID", description = "Get confirmation request details by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<ConfirmationRequestDto> getConfirmationRequestById(@PathVariable String id) {
        ConfirmationRequestDto request = confirmationRequestService.getConfirmationRequestById(id);
        return ResponseEntity.ok(request);
    }

    @PostMapping
    @Operation(summary = "Create confirmation request", description = "Create a new employee confirmation request")
    @PreAuthorize("hasAnyRole('ADMIN', 'HRO')")
    public ResponseEntity<ConfirmationRequestDto> createConfirmationRequest(
            @Valid @RequestBody ConfirmationRequestDto requestDto,
            @CurrentUser User currentUser) {
        ConfirmationRequestDto createdRequest = confirmationRequestService.createConfirmationRequest(requestDto, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update confirmation request", description = "Update a confirmation request in draft status")
    @PreAuthorize("hasAnyRole('ADMIN', 'HRO')")
    public ResponseEntity<ConfirmationRequestDto> updateConfirmationRequest(
            @PathVariable String id,
            @Valid @RequestBody ConfirmationRequestDto requestDto,
            @CurrentUser User currentUser) {
        ConfirmationRequestDto updatedRequest = confirmationRequestService.updateConfirmationRequest(id, requestDto, currentUser.getId());
        return ResponseEntity.ok(updatedRequest);
    }

    @GetMapping("/employee/{employeeId}/eligible")
    @Operation(summary = "Check confirmation eligibility", description = "Check if employee is eligible for confirmation")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<Boolean> isEmployeeEligibleForConfirmation(@PathVariable String employeeId) {
        boolean eligible = confirmationRequestService.isEmployeeEligibleForConfirmation(employeeId);
        return ResponseEntity.ok(eligible);
    }

    @GetMapping("/pending-confirmations")
    @Operation(summary = "Get pending confirmations", description = "Get employees with upcoming confirmation eligibility")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<Page<ConfirmationRequestDto>> getPendingConfirmations(
            @PageableDefault(sort = "probationEndDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ConfirmationRequestDto> requests = confirmationRequestService.getPendingConfirmations(pageable);
        return ResponseEntity.ok(requests);
    }
}