package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.ResignationRequest;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.ResignationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResignationRequestRepository extends JpaRepository<ResignationRequest, String> {

    // Basic queries
    Page<ResignationRequest> findByStatus(RequestStatus status, Pageable pageable);
    
    Page<ResignationRequest> findByEmployeeId(String employeeId, Pageable pageable);
    
    Page<ResignationRequest> findBySubmittedById(String submittedById, Pageable pageable);

    Page<ResignationRequest> findByResignationType(ResignationType resignationType, Pageable pageable);
    
    // Institution-based queries
    @Query("SELECT r FROM ResignationRequest r WHERE r.employee.institution.id = :institutionId")
    Page<ResignationRequest> findByInstitutionId(@Param("institutionId") String institutionId, Pageable pageable);
    
    // Date-based queries
    @Query("SELECT r FROM ResignationRequest r WHERE r.resignationDate BETWEEN :startDate AND :endDate")
    Page<ResignationRequest> findByResignationDateBetween(@Param("startDate") LocalDate startDate, 
                                                         @Param("endDate") LocalDate endDate, 
                                                         Pageable pageable);
    
    @Query("SELECT r FROM ResignationRequest r WHERE r.lastWorkingDate BETWEEN :startDate AND :endDate")
    Page<ResignationRequest> findByLastWorkingDateBetween(@Param("startDate") LocalDate startDate, 
                                                         @Param("endDate") LocalDate endDate, 
                                                         Pageable pageable);
    
    // Search queries
    @Query("SELECT r FROM ResignationRequest r WHERE " +
           "LOWER(r.employee.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.employee.payrollNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.employee.zanzibarId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.requestNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ResignationRequest> searchResignationRequests(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Complex queries
    @Query("SELECT r FROM ResignationRequest r WHERE r.status = 'SUBMITTED' AND r.submissionDate < :cutoffDate")
    Page<ResignationRequest> findOverdueResignationRequests(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);
    
    @Query("SELECT r FROM ResignationRequest r WHERE r.lastWorkingDate <= :targetDate AND r.status = 'APPROVED'")
    List<ResignationRequest> findUpcomingResignations(@Param("targetDate") LocalDate targetDate);
    
    @Query("SELECT r FROM ResignationRequest r WHERE r.clearanceCompleted = false AND r.status = 'APPROVED'")
    List<ResignationRequest> findPendingClearanceRequests();
    
    @Query("SELECT r FROM ResignationRequest r WHERE r.handoverCompleted = false AND r.status = 'APPROVED'")
    List<ResignationRequest> findPendingHandoverRequests();
    
    // Payment-related queries
    @Query("SELECT r FROM ResignationRequest r WHERE r.resignationType = 'TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT' AND r.paymentConfirmed = false")
    Page<ResignationRequest> findPendingPaymentRequests(Pageable pageable);
    
    @Query("SELECT r FROM ResignationRequest r WHERE r.resignationType = 'TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT' AND r.paymentConfirmed = true")
    Page<ResignationRequest> findConfirmedPaymentRequests(Pageable pageable);
    
    @Query("SELECT r FROM ResignationRequest r WHERE r.paymentAmount > :amount")
    Page<ResignationRequest> findByPaymentAmountGreaterThan(@Param("amount") BigDecimal amount, Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(r) FROM ResignationRequest r WHERE r.status = :status")
    long countByStatus(@Param("status") RequestStatus status);
    
    @Query("SELECT COUNT(r) FROM ResignationRequest r WHERE r.resignationType = :resignationType")
    long countByResignationType(@Param("resignationType") ResignationType resignationType);
    
    @Query("SELECT COUNT(r) FROM ResignationRequest r WHERE r.submissionDate BETWEEN :startDate AND :endDate")
    long countBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(r) FROM ResignationRequest r WHERE r.employee.institution.id = :institutionId")
    long countByInstitutionId(@Param("institutionId") String institutionId);
    
    @Query("SELECT COUNT(r) FROM ResignationRequest r WHERE r.resignationDate BETWEEN :startDate AND :endDate")
    long countByResignationDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Validation queries
    @Query("SELECT COUNT(r) > 0 FROM ResignationRequest r WHERE r.employee.id = :employeeId AND r.status IN ('SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW')")
    Boolean hasActivePendingResignation(@Param("employeeId") String employeeId);
    
    // Document-related queries
    @Query("SELECT r FROM ResignationRequest r WHERE SIZE(r.resignationDocuments) < :minDocuments")
    Page<ResignationRequest> findWithInsufficientDocuments(@Param("minDocuments") int minDocuments, Pageable pageable);
    
    // Approval workflow queries
    @Query("SELECT r FROM ResignationRequest r WHERE r.approver.id = :approverId")
    Page<ResignationRequest> findByApproverId(@Param("approverId") String approverId, Pageable pageable);
    
    @Query("SELECT r FROM ResignationRequest r WHERE r.approvalDate BETWEEN :startDate AND :endDate")
    Page<ResignationRequest> findByApprovalDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate, 
                                                      Pageable pageable);

    // Resignation type specific queries
    @Query("SELECT r FROM ResignationRequest r WHERE r.resignationType = 'THREE_MONTH_NOTICE' AND r.status = 'APPROVED'")
    List<ResignationRequest> findApprovedStandardNoticeResignations();
    
    @Query("SELECT r FROM ResignationRequest r WHERE r.resignationType = 'TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT' AND r.status = 'APPROVED'")
    List<ResignationRequest> findApprovedImmediateResignations();
    
    // Notice period queries
    @Query("SELECT r FROM ResignationRequest r WHERE r.resignationType = 'THREE_MONTH_NOTICE' AND r.lastWorkingDate <= :targetDate")
    Page<ResignationRequest> findStandardNoticeNearingCompletion(@Param("targetDate") LocalDate targetDate, Pageable pageable);
    
    @Query("SELECT r FROM ResignationRequest r WHERE r.resignationType = 'TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT' AND r.lastWorkingDate <= :targetDate")
    Page<ResignationRequest> findImmediateResignationsNearingCompletion(@Param("targetDate") LocalDate targetDate, Pageable pageable);
    
    // Summary queries for reporting
    @Query("SELECT SUM(r.paymentAmount) FROM ResignationRequest r WHERE r.resignationType = 'TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT' AND r.paymentConfirmed = true")
    BigDecimal getTotalPaymentAmount();
    
    @Query("SELECT AVG(r.paymentAmount) FROM ResignationRequest r WHERE r.resignationType = 'TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT' AND r.paymentConfirmed = true")
    BigDecimal getAveragePaymentAmount();
}