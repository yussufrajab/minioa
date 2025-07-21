package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.LeaveWithoutPayRequestDto;
import com.zanzibar.csms.entity.LeaveWithoutPay;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.service.LeaveWithoutPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lwop")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Leave Without Pay", description = "Leave Without Pay management endpoints")
public class LeaveWithoutPayController {

    private final LeaveWithoutPayService leaveWithoutPayService;

    @PostMapping
    @Operation(summary = "Create a new LWOP request")
    public ResponseEntity<LeaveWithoutPay> createRequest(
            @Valid @RequestBody LeaveWithoutPayRequestDto requestDto,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Creating LWOP request for employee: {}", requestDto.getEmployeeId());
        
        LeaveWithoutPay createdRequest = leaveWithoutPayService.createRequest(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get LWOP request by ID")
    public ResponseEntity<LeaveWithoutPay> getRequest(@PathVariable Long id) {
        return leaveWithoutPayService.findById(id)
                .map(request -> ResponseEntity.ok().body(request))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get all LWOP requests for an employee")
    public ResponseEntity<List<LeaveWithoutPay>> getRequestsByEmployeeId(
            @PathVariable String employeeId) {
        
        List<LeaveWithoutPay> requests = leaveWithoutPayService.findByEmployeeId(employeeId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/employee/{employeeId}/approved")
    @Operation(summary = "Get approved LWOP requests for an employee")
    public ResponseEntity<List<LeaveWithoutPay>> getApprovedRequestsByEmployeeId(
            @PathVariable String employeeId) {
        
        List<LeaveWithoutPay> requests = leaveWithoutPayService.findApprovedByEmployeeId(employeeId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending LWOP requests in a period")
    public ResponseEntity<List<LeaveWithoutPay>> getPendingRequestsInPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<LeaveWithoutPay> requests = leaveWithoutPayService.findPendingRequestsInPeriod(startDate, endDate);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update LWOP request status")
    public ResponseEntity<LeaveWithoutPay> updateStatus(
            @PathVariable Long id,
            @RequestParam RequestStatus status,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Updating LWOP request {} status to {}", id, status);
        
        LeaveWithoutPay updatedRequest = leaveWithoutPayService.updateStatus(id, status, userId);
        return ResponseEntity.ok(updatedRequest);
    }

    @GetMapping("/employee/{employeeId}/exceeded-max-periods")
    @Operation(summary = "Check if employee has exceeded maximum LWOP periods")
    public ResponseEntity<Boolean> hasExceededMaxPeriods(@PathVariable String employeeId) {
        boolean exceeded = leaveWithoutPayService.hasExceededMaxPeriods(employeeId);
        return ResponseEntity.ok(exceeded);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete LWOP request")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Deleting LWOP request: {}", id);
        
        leaveWithoutPayService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}