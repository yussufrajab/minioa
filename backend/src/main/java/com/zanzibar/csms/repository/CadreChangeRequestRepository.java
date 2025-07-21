package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.CadreChangeRequest;
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
import java.util.Optional;

@Repository
public interface CadreChangeRequestRepository extends JpaRepository<CadreChangeRequest, String> {

    // Find by status
    Page<CadreChangeRequest> findByStatus(RequestStatus status, Pageable pageable);

    // Find by employee
    Page<CadreChangeRequest> findByEmployeeId(String employeeId, Pageable pageable);

    // Find by institution
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.employee.institution.id = :institutionId")
    Page<CadreChangeRequest> findByInstitutionId(@Param("institutionId") String institutionId, Pageable pageable);

    // Find by submitter
    Page<CadreChangeRequest> findBySubmittedById(String submittedById, Pageable pageable);

    // Find by approver
    Page<CadreChangeRequest> findByApproverId(String approverId, Pageable pageable);

    // Find by date range
    Page<CadreChangeRequest> findBySubmissionDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Find by current cadre
    Page<CadreChangeRequest> findByCurrentCadre(String currentCadre, Pageable pageable);

    // Find by proposed cadre
    Page<CadreChangeRequest> findByProposedCadre(String proposedCadre, Pageable pageable);

    // Find by education level
    Page<CadreChangeRequest> findByEducationLevel(String educationLevel, Pageable pageable);

    // Find requests requiring TCU verification
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.tcuVerificationRequired = true AND c.tcuVerificationStatus != 'VERIFIED'")
    Page<CadreChangeRequest> findRequestsRequiringTcuVerification(Pageable pageable);

    // Find overdue requests
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.status IN ('SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW') AND c.submissionDate < :slaDeadline")
    List<CadreChangeRequest> findOverdueCadreChangeRequests(@Param("slaDeadline") LocalDateTime slaDeadline);

    // Search cadre change requests
    @Query("SELECT c FROM CadreChangeRequest c WHERE " +
           "LOWER(c.employee.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.employee.payrollNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.requestNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.currentCadre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.proposedCadre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.educationLevel) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<CadreChangeRequest> searchCadreChangeRequests(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Find requests with filters
    @Query("SELECT c FROM CadreChangeRequest c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:institutionId IS NULL OR c.employee.institution.id = :institutionId) AND " +
           "(:currentCadre IS NULL OR c.currentCadre = :currentCadre) AND " +
           "(:proposedCadre IS NULL OR c.proposedCadre = :proposedCadre) AND " +
           "(:educationLevel IS NULL OR c.educationLevel = :educationLevel) AND " +
           "(:startDate IS NULL OR c.submissionDate >= :startDate) AND " +
           "(:endDate IS NULL OR c.submissionDate <= :endDate)")
    Page<CadreChangeRequest> findWithFilters(
        @Param("status") RequestStatus status,
        @Param("institutionId") String institutionId,
        @Param("currentCadre") String currentCadre,
        @Param("proposedCadre") String proposedCadre,
        @Param("educationLevel") String educationLevel,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );

    // Check if employee has pending cadre change request
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CadreChangeRequest c " +
           "WHERE c.employee.id = :employeeId AND c.status IN ('SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW')")
    Boolean hasActivePendingCadreChange(@Param("employeeId") String employeeId);

    // Statistics queries
    @Query("SELECT COUNT(c) FROM CadreChangeRequest c WHERE c.status = :status")
    Long countByStatus(@Param("status") RequestStatus status);

    @Query("SELECT COUNT(c) FROM CadreChangeRequest c WHERE c.employee.institution.id = :institutionId AND c.status = :status")
    Long countByInstitutionIdAndStatus(@Param("institutionId") String institutionId, @Param("status") RequestStatus status);

    @Query("SELECT COUNT(c) FROM CadreChangeRequest c WHERE c.submissionDate BETWEEN :startDate AND :endDate")
    Long countBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.currentCadre, COUNT(c) FROM CadreChangeRequest c GROUP BY c.currentCadre")
    List<Object[]> getCadreChangeFromStatistics();

    @Query("SELECT c.proposedCadre, COUNT(c) FROM CadreChangeRequest c GROUP BY c.proposedCadre")
    List<Object[]> getCadreChangeToStatistics();

    @Query("SELECT c.educationLevel, COUNT(c) FROM CadreChangeRequest c GROUP BY c.educationLevel")
    List<Object[]> getEducationLevelStatistics();

    @Query("SELECT c.status, COUNT(c) FROM CadreChangeRequest c GROUP BY c.status")
    List<Object[]> getCadreChangeStatusStatistics();

    // Find effective dates approaching
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.status = 'APPROVED' AND c.effectiveDate BETWEEN :startDate AND :endDate")
    List<CadreChangeRequest> findRequestsWithEffectiveDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find requests by education completion year
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.educationCompletionYear = :year")
    Page<CadreChangeRequest> findByEducationCompletionYear(@Param("year") Integer year, Pageable pageable);

    // Find requests requiring budget approval
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.budgetaryImplications IS NOT NULL AND c.budgetaryImplications != ''")
    Page<CadreChangeRequest> findRequestsRequiringBudgetApproval(Pageable pageable);

    // Find requests by performance rating
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.performanceRating = :rating")
    Page<CadreChangeRequest> findByPerformanceRating(@Param("rating") String rating, Pageable pageable);

    // Find requests by years of experience range
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.yearsOfExperience BETWEEN :minYears AND :maxYears")
    Page<CadreChangeRequest> findByYearsOfExperienceBetween(@Param("minYears") Integer minYears, @Param("maxYears") Integer maxYears, Pageable pageable);

    // Find recent requests by employee
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.employee.id = :employeeId ORDER BY c.submissionDate DESC")
    List<CadreChangeRequest> findRecentByEmployeeId(@Param("employeeId") String employeeId, Pageable pageable);

    // Find requests by institution and cadre
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.employee.institution.id = :institutionId AND c.currentCadre = :cadre")
    Page<CadreChangeRequest> findByInstitutionIdAndCurrentCadre(@Param("institutionId") String institutionId, @Param("cadre") String cadre, Pageable pageable);

    // Find requests pending TCU verification
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.tcuVerificationRequired = true AND (c.tcuVerificationStatus IS NULL OR c.tcuVerificationStatus = 'PENDING')")
    Page<CadreChangeRequest> findPendingTcuVerification(Pageable pageable);

    // Find approved requests for implementation
    @Query("SELECT c FROM CadreChangeRequest c WHERE c.status = 'APPROVED' AND c.effectiveDate <= :currentDate")
    List<CadreChangeRequest> findApprovedRequestsForImplementation(@Param("currentDate") LocalDate currentDate);
}