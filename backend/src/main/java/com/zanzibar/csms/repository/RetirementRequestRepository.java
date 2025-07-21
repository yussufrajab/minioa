package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.RetirementRequest;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RetirementType;
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
public interface RetirementRequestRepository extends JpaRepository<RetirementRequest, String> {

    // Basic queries
    Page<RetirementRequest> findByStatus(RequestStatus status, Pageable pageable);
    
    Page<RetirementRequest> findByEmployeeId(String employeeId, Pageable pageable);
    
    Page<RetirementRequest> findBySubmittedById(String submittedById, Pageable pageable);

    Page<RetirementRequest> findByRetirementType(RetirementType retirementType, Pageable pageable);
    
    // Institution-based queries
    @Query("SELECT r FROM RetirementRequest r WHERE r.employee.institution.id = :institutionId")
    Page<RetirementRequest> findByInstitutionId(@Param("institutionId") String institutionId, Pageable pageable);
    
    // Date-based queries
    @Query("SELECT r FROM RetirementRequest r WHERE r.retirementDate BETWEEN :startDate AND :endDate")
    Page<RetirementRequest> findByRetirementDateBetween(@Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate, 
                                                       Pageable pageable);
    
    @Query("SELECT r FROM RetirementRequest r WHERE r.lastWorkingDate BETWEEN :startDate AND :endDate")
    Page<RetirementRequest> findByLastWorkingDateBetween(@Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate, 
                                                        Pageable pageable);
    
    // Search queries
    @Query("SELECT r FROM RetirementRequest r WHERE " +
           "LOWER(r.employee.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.employee.payrollNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.employee.zanzibarId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.requestNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<RetirementRequest> searchRetirementRequests(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Complex queries
    @Query("SELECT r FROM RetirementRequest r WHERE r.status = 'SUBMITTED' AND r.submissionDate < :cutoffDate")
    Page<RetirementRequest> findOverdueRetirementRequests(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);
    
    @Query("SELECT r FROM RetirementRequest r WHERE r.retirementDate <= :targetDate AND r.status = 'APPROVED'")
    List<RetirementRequest> findUpcomingRetirements(@Param("targetDate") LocalDate targetDate);
    
    @Query("SELECT r FROM RetirementRequest r WHERE r.pensionEligibilityConfirmed = true AND r.status = 'SUBMITTED'")
    Page<RetirementRequest> findPensionEligibleRequests(Pageable pageable);
    
    @Query("SELECT r FROM RetirementRequest r WHERE r.clearanceCompleted = false AND r.status = 'APPROVED'")
    List<RetirementRequest> findPendingClearanceRequests();
    
    // Statistics queries
    @Query("SELECT COUNT(r) FROM RetirementRequest r WHERE r.status = :status")
    long countByStatus(@Param("status") RequestStatus status);
    
    @Query("SELECT COUNT(r) FROM RetirementRequest r WHERE r.retirementType = :retirementType")
    long countByRetirementType(@Param("retirementType") RetirementType retirementType);
    
    @Query("SELECT COUNT(r) FROM RetirementRequest r WHERE r.submissionDate BETWEEN :startDate AND :endDate")
    long countBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(r) FROM RetirementRequest r WHERE r.employee.institution.id = :institutionId")
    long countByInstitutionId(@Param("institutionId") String institutionId);
    
    @Query("SELECT COUNT(r) FROM RetirementRequest r WHERE r.retirementDate BETWEEN :startDate AND :endDate")
    long countByRetirementDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Validation queries
    @Query("SELECT COUNT(r) > 0 FROM RetirementRequest r WHERE r.employee.id = :employeeId AND r.status IN ('SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW')")
    Boolean hasActivePendingRetirement(@Param("employeeId") String employeeId);
    
    // Document-related queries
    @Query("SELECT r FROM RetirementRequest r WHERE SIZE(r.retirementDocuments) < :minDocuments")
    Page<RetirementRequest> findWithInsufficientDocuments(@Param("minDocuments") int minDocuments, Pageable pageable);
    
    // Approval workflow queries
    @Query("SELECT r FROM RetirementRequest r WHERE r.approver.id = :approverId")
    Page<RetirementRequest> findByApproverId(@Param("approverId") String approverId, Pageable pageable);
    
    @Query("SELECT r FROM RetirementRequest r WHERE r.approvalDate BETWEEN :startDate AND :endDate")
    Page<RetirementRequest> findByApprovalDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate, 
                                                     Pageable pageable);

    // Retirement type specific queries
    @Query("SELECT r FROM RetirementRequest r WHERE r.retirementType = 'COMPULSORY' AND r.status = 'APPROVED'")
    List<RetirementRequest> findApprovedCompulsoryRetirements();
    
    @Query("SELECT r FROM RetirementRequest r WHERE r.retirementType = 'VOLUNTARY' AND r.status = 'APPROVED'")
    List<RetirementRequest> findApprovedVoluntaryRetirements();
    
    @Query("SELECT r FROM RetirementRequest r WHERE r.retirementType = 'ILLNESS' AND r.status = 'APPROVED'")
    List<RetirementRequest> findApprovedMedicalRetirements();

    // Age-based eligibility queries  
    @Query("SELECT r FROM RetirementRequest r WHERE r.employee.dateOfBirth <= :eligibilityDate AND r.retirementType = 'COMPULSORY'")
    Page<RetirementRequest> findEligibleForCompulsoryRetirement(@Param("eligibilityDate") LocalDate eligibilityDate, Pageable pageable);
}