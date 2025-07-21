package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.complaint.*;
import com.zanzibar.csms.entity.enums.ComplaintSeverity;
import com.zanzibar.csms.entity.enums.ComplaintStatus;
import com.zanzibar.csms.entity.enums.ComplaintType;
import com.zanzibar.csms.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaint Management", description = "Endpoints for managing complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @Operation(summary = "Create a new complaint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Complaint created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR', 'EMPLOYEE')")
    public ResponseEntity<ComplaintResponseDto> createComplaint(
            @Valid @RequestBody ComplaintCreateDto createDto,
            Authentication authentication) {
        ComplaintResponseDto response = complaintService.createComplaint(createDto, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing complaint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaint updated successfully"),
        @ApiResponse(responseCode = "404", description = "Complaint not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR')")
    public ResponseEntity<ComplaintResponseDto> updateComplaint(
            @PathVariable String id,
            @Valid @RequestBody ComplaintUpdateDto updateDto,
            Authentication authentication) {
        ComplaintResponseDto response = complaintService.updateComplaint(id, updateDto, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complaint by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaint found"),
        @ApiResponse(responseCode = "404", description = "Complaint not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR', 'EMPLOYEE')")
    public ResponseEntity<ComplaintResponseDto> getComplaintById(@PathVariable String id) {
        ComplaintResponseDto response = complaintService.getComplaintById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{complaintNumber}")
    @Operation(summary = "Get complaint by complaint number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaint found"),
        @ApiResponse(responseCode = "404", description = "Complaint not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR', 'EMPLOYEE')")
    public ResponseEntity<ComplaintResponseDto> getComplaintByNumber(@PathVariable String complaintNumber) {
        ComplaintResponseDto response = complaintService.getComplaintByNumber(complaintNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all complaints with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaints retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR')")
    public ResponseEntity<Page<ComplaintResponseDto>> getComplaints(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "submissionDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<ComplaintResponseDto> response = complaintService.getComplaints(pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/complainant/{complainantId}")
    @Operation(summary = "Get complaints by complainant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaints retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR', 'EMPLOYEE')")
    public ResponseEntity<Page<ComplaintResponseDto>> getComplaintsByComplainant(
            @PathVariable String complainantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submissionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<ComplaintResponseDto> response = complaintService.getComplaintsByComplainant(complainantId, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get complaints by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaints retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR')")
    public ResponseEntity<Page<ComplaintResponseDto>> getComplaintsByStatus(
            @PathVariable ComplaintStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submissionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<ComplaintResponseDto> response = complaintService.getComplaintsByStatus(status, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/investigator/{investigatorId}")
    @Operation(summary = "Get complaints by assigned investigator")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaints retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR')")
    public ResponseEntity<Page<ComplaintResponseDto>> getComplaintsByInvestigator(
            @PathVariable String investigatorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submissionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<ComplaintResponseDto> response = complaintService.getComplaintsByInvestigator(investigatorId, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search complaints")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR')")
    public ResponseEntity<Page<ComplaintResponseDto>> searchComplaints(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submissionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<ComplaintResponseDto> response = complaintService.searchComplaints(searchTerm, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter complaints with multiple criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaints filtered successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR')")
    public ResponseEntity<Page<ComplaintResponseDto>> getComplaintsWithFilters(
            @RequestParam(required = false) ComplaintType complaintType,
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) ComplaintSeverity severity,
            @RequestParam(required = false) String complainantId,
            @RequestParam(required = false) String respondentId,
            @RequestParam(required = false) String investigatorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submissionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        Page<ComplaintResponseDto> response = complaintService.getComplaintsWithFilters(
            complaintType, status, severity, complainantId, respondentId, 
            investigatorId, startDate, endDate, pageRequest);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/assign-investigator")
    @Operation(summary = "Assign investigator to complaint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Investigator assigned successfully"),
        @ApiResponse(responseCode = "404", description = "Complaint or investigator not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'HEAD_HR')")
    public ResponseEntity<ComplaintResponseDto> assignInvestigator(
            @PathVariable String id,
            @RequestParam String investigatorId,
            Authentication authentication) {
        ComplaintResponseDto response = complaintService.assignInvestigator(id, investigatorId, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue complaints")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue complaints retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR')")
    public ResponseEntity<?> getOverdueComplaints() {
        return ResponseEntity.ok(complaintService.getOverdueComplaints());
    }

    @GetMapping("/follow-up")
    @Operation(summary = "Get complaints requiring follow-up")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Follow-up complaints retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR')")
    public ResponseEntity<?> getComplaintsRequiringFollowUp() {
        return ResponseEntity.ok(complaintService.getComplaintsRequiringFollowUp());
    }
}