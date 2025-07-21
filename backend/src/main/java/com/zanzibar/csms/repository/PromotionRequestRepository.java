package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.PromotionRequest;
import com.zanzibar.csms.entity.enums.PromotionType;
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
public interface PromotionRequestRepository extends JpaRepository<PromotionRequest, String> {

    // Basic queries
    Page<PromotionRequest> findByStatus(RequestStatus status, Pageable pageable);
    
    Page<PromotionRequest> findByEmployeeId(String employeeId, Pageable pageable);
    
    Page<PromotionRequest> findBySubmittedById(String submittedById, Pageable pageable);

    Page<PromotionRequest> findByPromotionType(PromotionType promotionType, Pageable pageable);
    
    // Institution-based queries
    @Query("SELECT p FROM PromotionRequest p WHERE p.employee.institution.id = :institutionId")
    Page<PromotionRequest> findByInstitutionId(@Param("institutionId") String institutionId, Pageable pageable);
    
    // Grade-based queries
    @Query("SELECT p FROM PromotionRequest p WHERE p.currentGrade = :grade")
    Page<PromotionRequest> findByCurrentGrade(@Param("grade") String grade, Pageable pageable);
    
    @Query("SELECT p FROM PromotionRequest p WHERE p.proposedGrade = :grade")
    Page<PromotionRequest> findByProposedGrade(@Param("grade") String grade, Pageable pageable);
    
    // Position-based queries
    @Query("SELECT p FROM PromotionRequest p WHERE p.currentPosition = :position")
    Page<PromotionRequest> findByCurrentPosition(@Param("position") String position, Pageable pageable);
    
    @Query("SELECT p FROM PromotionRequest p WHERE p.proposedPosition = :position")
    Page<PromotionRequest> findByProposedPosition(@Param("position") String position, Pageable pageable);
    
    // Date-based queries
    @Query("SELECT p FROM PromotionRequest p WHERE p.effectiveDate BETWEEN :startDate AND :endDate")
    Page<PromotionRequest> findByEffectiveDateBetween(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate, 
                                                     Pageable pageable);
    
    @Query("SELECT p FROM PromotionRequest p WHERE p.effectiveDate <= :targetDate AND p.status = 'APPROVED'")
    List<PromotionRequest> findUpcomingPromotions(@Param("targetDate") LocalDate targetDate);
    
    // Search queries
    @Query("SELECT p FROM PromotionRequest p WHERE " +
           "LOWER(p.employee.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.employee.payrollNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.employee.zanzibarId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.requestNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.currentPosition) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.proposedPosition) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<PromotionRequest> searchPromotionRequests(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Complex queries
    @Query("SELECT p FROM PromotionRequest p WHERE p.status = 'SUBMITTED' AND p.submissionDate < :cutoffDate")
    Page<PromotionRequest> findOverduePromotionRequests(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);
    
    @Query("SELECT p FROM PromotionRequest p WHERE p.performanceRating = :rating")
    Page<PromotionRequest> findByPerformanceRating(@Param("rating") String rating, Pageable pageable);
    
    @Query("SELECT p FROM PromotionRequest p WHERE p.yearsInCurrentPosition >= :minYears")
    Page<PromotionRequest> findByMinimumYearsInPosition(@Param("minYears") Integer minYears, Pageable pageable);
    
    // Salary-based queries
    @Query("SELECT p FROM PromotionRequest p WHERE p.currentSalary BETWEEN :minSalary AND :maxSalary")
    Page<PromotionRequest> findByCurrentSalaryRange(@Param("minSalary") Double minSalary, 
                                                   @Param("maxSalary") Double maxSalary, 
                                                   Pageable pageable);
    
    @Query("SELECT p FROM PromotionRequest p WHERE p.proposedSalary BETWEEN :minSalary AND :maxSalary")
    Page<PromotionRequest> findByProposedSalaryRange(@Param("minSalary") Double minSalary, 
                                                    @Param("maxSalary") Double maxSalary, 
                                                    Pageable pageable);
    
    @Query("SELECT p FROM PromotionRequest p WHERE (p.proposedSalary - p.currentSalary) / p.currentSalary * 100 >= :minPercentage")
    Page<PromotionRequest> findBySalaryIncreasePercentage(@Param("minPercentage") Double minPercentage, Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(p) FROM PromotionRequest p WHERE p.status = :status")
    long countByStatus(@Param("status") RequestStatus status);
    
    @Query("SELECT COUNT(p) FROM PromotionRequest p WHERE p.promotionType = :promotionType")
    long countByPromotionType(@Param("promotionType") PromotionType promotionType);
    
    @Query("SELECT COUNT(p) FROM PromotionRequest p WHERE p.submissionDate BETWEEN :startDate AND :endDate")
    long countBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM PromotionRequest p WHERE p.employee.institution.id = :institutionId")
    long countByInstitutionId(@Param("institutionId") String institutionId);
    
    @Query("SELECT COUNT(p) FROM PromotionRequest p WHERE p.effectiveDate BETWEEN :startDate AND :endDate")
    long countByEffectiveDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Validation queries
    @Query("SELECT COUNT(p) > 0 FROM PromotionRequest p WHERE p.employee.id = :employeeId AND p.status IN ('SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW')")
    Boolean hasActivePendingPromotion(@Param("employeeId") String employeeId);
    
    @Query("SELECT COUNT(p) > 0 FROM PromotionRequest p WHERE p.employee.id = :employeeId AND p.status = 'APPROVED' AND p.effectiveDate > :currentDate")
    Boolean hasApprovedPromotionPending(@Param("employeeId") String employeeId, @Param("currentDate") LocalDate currentDate);
    
    // Approval workflow queries
    @Query("SELECT p FROM PromotionRequest p WHERE p.approver.id = :approverId")
    Page<PromotionRequest> findByApproverId(@Param("approverId") String approverId, Pageable pageable);
    
    @Query("SELECT p FROM PromotionRequest p WHERE p.approvalDate BETWEEN :startDate AND :endDate")
    Page<PromotionRequest> findByApprovalDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate, 
                                                    Pageable pageable);

    // Promotion type specific queries
    @Query("SELECT p FROM PromotionRequest p WHERE p.promotionType = 'EDUCATIONAL' AND p.status = 'APPROVED'")
    List<PromotionRequest> findApprovedEducationalPromotions();
    
    @Query("SELECT p FROM PromotionRequest p WHERE p.promotionType = 'PERFORMANCE' AND p.status = 'APPROVED'")
    List<PromotionRequest> findApprovedPerformancePromotions();
    
    // Grade progression queries
    @Query("SELECT p FROM PromotionRequest p WHERE p.currentGrade = :currentGrade AND p.proposedGrade = :proposedGrade")
    Page<PromotionRequest> findByGradeProgression(@Param("currentGrade") String currentGrade, 
                                                 @Param("proposedGrade") String proposedGrade, 
                                                 Pageable pageable);
    
    // Employee promotion history
    @Query("SELECT p FROM PromotionRequest p WHERE p.employee.id = :employeeId AND p.status = 'APPROVED' ORDER BY p.effectiveDate DESC")
    List<PromotionRequest> findEmployeePromotionHistory(@Param("employeeId") String employeeId);
    
    // Summary queries for reporting
    @Query("SELECT AVG(p.proposedSalary - p.currentSalary) FROM PromotionRequest p WHERE p.status = 'APPROVED' AND p.currentSalary IS NOT NULL AND p.proposedSalary IS NOT NULL")
    Double getAverageSalaryIncrease();
    
    @Query("SELECT SUM(p.proposedSalary - p.currentSalary) FROM PromotionRequest p WHERE p.status = 'APPROVED' AND p.currentSalary IS NOT NULL AND p.proposedSalary IS NOT NULL")
    Double getTotalSalaryIncrease();
    
    @Query("SELECT AVG(p.yearsInCurrentPosition) FROM PromotionRequest p WHERE p.status = 'APPROVED' AND p.yearsInCurrentPosition IS NOT NULL")
    Double getAverageYearsInPositionForPromotions();
}