package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.ServiceExtensionRequest;
import com.zanzibar.csms.entity.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceExtensionRequestRepository extends JpaRepository<ServiceExtensionRequest, String> {

    // Basic queries
    Page<ServiceExtensionRequest> findByStatus(RequestStatus status, Pageable pageable);
    
    Page<ServiceExtensionRequest> findByEmployeeId(String employeeId, Pageable pageable);
    
    Page<ServiceExtensionRequest> findBySubmittedById(String submittedById, Pageable pageable);
    
    // Institution-based queries
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE s.employee.institution.id = :institutionId")
    Page<ServiceExtensionRequest> findByInstitutionId(@Param("institutionId") String institutionId, Pageable pageable);
    
    // Search queries
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE " +
           "LOWER(s.employee.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.employee.payrollNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.employee.zanzibarId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.requestNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ServiceExtensionRequest> searchServiceExtensionRequests(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Complex queries
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE s.status = 'SUBMITTED' AND s.submissionDate < :cutoffDate")
    Page<ServiceExtensionRequest> findOverdueServiceExtensionRequests(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);
    
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE s.status = 'APPROVED'")
    List<ServiceExtensionRequest> findActiveExtensions(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE s.expirationWarningSent = false")
    List<ServiceExtensionRequest> findRequiringExpirationNotification();
    
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE s.retirementEligibilityDate < :currentDate AND s.status = 'APPROVED'")
    List<ServiceExtensionRequest> findExpiredExtensions(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE s.expirationWarningSent = false")
    List<ServiceExtensionRequest> findExpiringExtensions(@Param("days") int days);
    
    // Statistics queries
    @Query("SELECT COUNT(s) FROM ServiceExtensionRequest s WHERE s.status = :status")
    long countByStatus(@Param("status") RequestStatus status);
    
    @Query("SELECT COUNT(s) FROM ServiceExtensionRequest s WHERE s.submissionDate BETWEEN :startDate AND :endDate")
    long countBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(s) FROM ServiceExtensionRequest s WHERE s.employee.institution.id = :institutionId")
    long countByInstitutionId(@Param("institutionId") String institutionId);
    
    // Duration-based queries
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE s.extensionDurationYears >= :minYears")
    Page<ServiceExtensionRequest> findByMinimumExtensionDuration(@Param("minYears") int minYears, Pageable pageable);
    
    @Query("SELECT AVG(s.extensionDurationYears) FROM ServiceExtensionRequest s WHERE s.status = 'APPROVED'")
    Double getAverageExtensionDuration();
    
    // Validation queries
    @Query("SELECT COUNT(s) > 0 FROM ServiceExtensionRequest s WHERE s.employee.id = :employeeId AND s.status IN ('SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW')")
    Boolean hasActivePendingServiceExtension(@Param("employeeId") String employeeId);
    
    // Document-related queries
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE SIZE(s.serviceExtensionDocuments) < :minDocuments")
    Page<ServiceExtensionRequest> findWithInsufficientDocuments(@Param("minDocuments") int minDocuments, Pageable pageable);
    
    // Approval workflow queries
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE s.approver.id = :approverId")
    Page<ServiceExtensionRequest> findByApproverId(@Param("approverId") String approverId, Pageable pageable);
    
    @Query("SELECT s FROM ServiceExtensionRequest s WHERE s.approvalDate BETWEEN :startDate AND :endDate")
    Page<ServiceExtensionRequest> findByApprovalDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                           @Param("endDate") LocalDateTime endDate, 
                                                           Pageable pageable);
}