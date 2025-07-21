package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.report.ReportDto;
import com.zanzibar.csms.dto.report.ReportRequestDto;
import com.zanzibar.csms.dto.report.StandardReportDto;
import com.zanzibar.csms.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Report generation and analytics endpoints")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate")
    @Operation(summary = "Generate a standard report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'HRO', 'HRRP', 'PO')")
    public ResponseEntity<ReportDto> generateReport(
            @Valid @RequestBody ReportRequestDto request,
            Authentication authentication) {
        
        ReportDto report = reportService.generateStandardReport(request, authentication.getName());
        return ResponseEntity.ok(report);
    }

    @GetMapping("/employee")
    @Operation(summary = "Generate employee report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'HRO', 'HRRP', 'PO')")
    public ResponseEntity<List<StandardReportDto.EmployeeReportDto>> generateEmployeeReport(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            @Parameter(description = "Department filter") @RequestParam(required = false) String department,
            @Parameter(description = "Employment status filter") @RequestParam(required = false) String employmentStatus) {
        
        ReportRequestDto request = ReportRequestDto.builder()
            .reportType("EMPLOYEE")
            .reportName("Employee Report")
            .periodStart(periodStart)
            .periodEnd(periodEnd)
            .institutionId(institutionId)
            .build();
        
        List<StandardReportDto.EmployeeReportDto> report = reportService.generateEmployeeReport(request);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/requests")
    @Operation(summary = "Generate requests report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Requests report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'HRO', 'HRRP', 'PO')")
    public ResponseEntity<List<StandardReportDto.RequestReportDto>> generateRequestsReport(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            @Parameter(description = "Request type filter") @RequestParam(required = false) String requestType,
            @Parameter(description = "Request status filter") @RequestParam(required = false) String requestStatus) {
        
        ReportRequestDto request = ReportRequestDto.builder()
            .reportType("REQUEST")
            .reportName("Requests Report")
            .periodStart(periodStart)
            .periodEnd(periodEnd)
            .institutionId(institutionId)
            .requestType(requestType)
            .requestStatus(requestStatus)
            .build();
        
        List<StandardReportDto.RequestReportDto> report = reportService.generateRequestReport(request);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/complaints")
    @Operation(summary = "Generate complaints report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complaints report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'DO', 'HRO', 'HRRP', 'PO')")
    public ResponseEntity<List<StandardReportDto.ComplaintReportDto>> generateComplaintsReport(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            @Parameter(description = "Complaint type filter") @RequestParam(required = false) String complaintType,
            @Parameter(description = "Complaint status filter") @RequestParam(required = false) String complaintStatus) {
        
        ReportRequestDto request = ReportRequestDto.builder()
            .reportType("COMPLAINT")
            .reportName("Complaints Report")
            .periodStart(periodStart)
            .periodEnd(periodEnd)
            .institutionId(institutionId)
            .complaintType(complaintType)
            .complaintStatus(complaintStatus)
            .build();
        
        List<StandardReportDto.ComplaintReportDto> report = reportService.generateComplaintReport(request);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/institutions")
    @Operation(summary = "Generate institutional report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Institutional report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'PO')")
    public ResponseEntity<List<StandardReportDto.InstitutionalReportDto>> generateInstitutionalReport(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution IDs filter") @RequestParam(required = false) List<String> institutionIds) {
        
        ReportRequestDto request = ReportRequestDto.builder()
            .reportType("INSTITUTIONAL")
            .reportName("Institutional Report")
            .periodStart(periodStart)
            .periodEnd(periodEnd)
            .institutionIds(institutionIds)
            .build();
        
        List<StandardReportDto.InstitutionalReportDto> report = reportService.generateInstitutionalReport(request);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/workflow")
    @Operation(summary = "Generate workflow report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Workflow report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'PO')")
    public ResponseEntity<List<StandardReportDto.WorkflowReportDto>> generateWorkflowReport(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            @Parameter(description = "Request type filter") @RequestParam(required = false) String requestType,
            @Parameter(description = "Current reviewer filter") @RequestParam(required = false) String currentReviewer) {
        
        ReportRequestDto request = ReportRequestDto.builder()
            .reportType("WORKFLOW")
            .reportName("Workflow Report")
            .periodStart(periodStart)
            .periodEnd(periodEnd)
            .institutionId(institutionId)
            .requestType(requestType)
            .build();
        
        List<StandardReportDto.WorkflowReportDto> report = reportService.generateWorkflowReport(request);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/performance")
    @Operation(summary = "Generate performance report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Performance report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'PO')")
    public ResponseEntity<List<StandardReportDto.PerformanceReportDto>> generatePerformanceReport(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            @Parameter(description = "Department filter") @RequestParam(required = false) String department,
            @Parameter(description = "Metric category filter") @RequestParam(required = false) String category) {
        
        ReportRequestDto request = ReportRequestDto.builder()
            .reportType("PERFORMANCE")
            .reportName("Performance Report")
            .periodStart(periodStart)
            .periodEnd(periodEnd)
            .institutionId(institutionId)
            .departmentId(department)
            .build();
        
        List<StandardReportDto.PerformanceReportDto> report = reportService.generatePerformanceReport(request);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/audit")
    @Operation(summary = "Generate audit report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'PO')")
    public ResponseEntity<List<StandardReportDto.AuditReportDto>> generateAuditReport(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "User ID filter") @RequestParam(required = false) String userId,
            @Parameter(description = "Action filter") @RequestParam(required = false) String action,
            @Parameter(description = "Entity type filter") @RequestParam(required = false) String entityType) {
        
        ReportRequestDto request = ReportRequestDto.builder()
            .reportType("AUDIT")
            .reportName("Audit Report")
            .periodStart(periodStart)
            .periodEnd(periodEnd)
            .userId(userId)
            .build();
        
        List<StandardReportDto.AuditReportDto> report = reportService.generateAuditReport(request);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/sla")
    @Operation(summary = "Generate SLA report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "SLA report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'PO')")
    public ResponseEntity<List<StandardReportDto.SLAReportDto>> generateSLAReport(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            @Parameter(description = "Request type filter") @RequestParam(required = false) String requestType,
            @Parameter(description = "SLA compliance filter") @RequestParam(required = false) Boolean withinSLA) {
        
        ReportRequestDto request = ReportRequestDto.builder()
            .reportType("SLA")
            .reportName("SLA Compliance Report")
            .periodStart(periodStart)
            .periodEnd(periodEnd)
            .institutionId(institutionId)
            .requestType(requestType)
            .build();
        
        List<StandardReportDto.SLAReportDto> report = reportService.generateSLAReport(request);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/download/{filename}")
    @Operation(summary = "Download generated report file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report file downloaded successfully"),
        @ApiResponse(responseCode = "404", description = "Report file not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadReport(
            @Parameter(description = "Report filename") @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get("./reports").resolve(filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(filePath);
            String contentType = Files.probeContentType(filePath);
            
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/types")
    @Operation(summary = "Get available report types")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report types retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getReportTypes() {
        List<String> reportTypes = List.of(
            "EMPLOYEE",
            "REQUEST", 
            "COMPLAINT",
            "INSTITUTIONAL",
            "WORKFLOW",
            "PERFORMANCE",
            "AUDIT",
            "SLA"
        );
        return ResponseEntity.ok(reportTypes);
    }

    @GetMapping("/formats")
    @Operation(summary = "Get available report formats")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report formats retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getReportFormats() {
        List<String> formats = List.of("PDF", "EXCEL", "CSV");
        return ResponseEntity.ok(formats);
    }

    @GetMapping("/templates")
    @Operation(summary = "Get available report templates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report templates retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'PO')")
    public ResponseEntity<List<ReportRequestDto>> getReportTemplates() {
        List<ReportRequestDto> templates = List.of(
            ReportRequestDto.builder()
                .reportType("EMPLOYEE")
                .reportName("Monthly Employee Report")
                .description("Monthly summary of all employees")
                .format("PDF")
                .build(),
            ReportRequestDto.builder()
                .reportType("REQUEST")
                .reportName("Weekly Request Status Report")
                .description("Weekly status of all requests")
                .format("EXCEL")
                .build(),
            ReportRequestDto.builder()
                .reportType("COMPLAINT")
                .reportName("Monthly Complaint Resolution Report")
                .description("Monthly complaint resolution statistics")
                .format("PDF")
                .build(),
            ReportRequestDto.builder()
                .reportType("PERFORMANCE")
                .reportName("Quarterly Performance Report")
                .description("Quarterly system performance metrics")
                .format("PDF")
                .build(),
            ReportRequestDto.builder()
                .reportType("SLA")
                .reportName("Monthly SLA Compliance Report")
                .description("Monthly SLA compliance statistics")
                .format("EXCEL")
                .build()
        );
        return ResponseEntity.ok(templates);
    }
}