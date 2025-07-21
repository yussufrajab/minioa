package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.TerminationRequest;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.TerminationScenario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TerminationRequestRepository extends JpaRepository<TerminationRequest, String> {

    @Query("SELECT tr FROM TerminationRequest tr WHERE tr.employee.id = :employeeId")
    List<TerminationRequest> findByEmployeeId(@Param("employeeId") String employeeId);

    @Query("SELECT tr FROM TerminationRequest tr WHERE tr.employee.id = :employeeId")
    Page<TerminationRequest> findByEmployeeId(@Param("employeeId") String employeeId, Pageable pageable);

    @Query("SELECT tr FROM TerminationRequest tr WHERE tr.submittedBy.id = :submittedById")
    Page<TerminationRequest> findBySubmittedById(@Param("submittedById") String submittedById, Pageable pageable);

    @Query("SELECT tr FROM TerminationRequest tr WHERE tr.status = :status")
    Page<TerminationRequest> findByStatus(@Param("status") RequestStatus status, Pageable pageable);

    @Query("SELECT tr FROM TerminationRequest tr WHERE tr.scenario = :scenario")
    Page<TerminationRequest> findByScenario(@Param("scenario") TerminationScenario scenario, Pageable pageable);

    @Query("SELECT tr FROM TerminationRequest tr WHERE tr.employee.institution.id = :institutionId")
    Page<TerminationRequest> findByInstitutionId(@Param("institutionId") String institutionId, Pageable pageable);

    @Query("SELECT tr FROM TerminationRequest tr WHERE tr.approver.id = :approverId")
    Page<TerminationRequest> findByApproverId(@Param("approverId") String approverId, Pageable pageable);

    @Query("SELECT tr FROM TerminationRequest tr WHERE tr.submissionDate BETWEEN :startDate AND :endDate")
    Page<TerminationRequest> findBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate, 
                                                        Pageable pageable);

    @Query("SELECT tr FROM TerminationRequest tr WHERE " +
           "(:status IS NULL OR tr.status = :status) AND " +
           "(:scenario IS NULL OR tr.scenario = :scenario) AND " +
           "(:institutionId IS NULL OR tr.employee.institution.id = :institutionId) AND " +
           "(:submittedById IS NULL OR tr.submittedBy.id = :submittedById) AND " +
           "(:startDate IS NULL OR tr.submissionDate >= :startDate) AND " +
           "(:endDate IS NULL OR tr.submissionDate <= :endDate)")
    Page<TerminationRequest> findWithFilters(@Param("status") RequestStatus status,
                                           @Param("scenario") TerminationScenario scenario,
                                           @Param("institutionId") String institutionId,
                                           @Param("submittedById") String submittedById,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);

    @Query("SELECT tr FROM TerminationRequest tr WHERE " +
           "LOWER(tr.employee.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tr.employee.payrollNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tr.requestNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tr.reason) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<TerminationRequest> searchTerminationRequests(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Statistics queries
    @Query("SELECT COUNT(tr) FROM TerminationRequest tr WHERE tr.status = :status")
    Long countByStatus(@Param("status") RequestStatus status);

    @Query("SELECT COUNT(tr) FROM TerminationRequest tr WHERE tr.scenario = :scenario")
    Long countByScenario(@Param("scenario") TerminationScenario scenario);

    @Query("SELECT COUNT(tr) FROM TerminationRequest tr WHERE tr.submissionDate BETWEEN :startDate AND :endDate")
    Long countBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(tr) FROM TerminationRequest tr WHERE tr.employee.institution.id = :institutionId AND tr.status = :status")
    Long countByInstitutionIdAndStatus(@Param("institutionId") String institutionId, @Param("status") RequestStatus status);

    @Query("SELECT tr.scenario, COUNT(tr) FROM TerminationRequest tr " +
           "WHERE tr.submissionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY tr.scenario ORDER BY COUNT(tr) DESC")
    List<Object[]> getTerminationScenarioStatistics(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT tr.status, COUNT(tr) FROM TerminationRequest tr " +
           "WHERE tr.submissionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY tr.status ORDER BY COUNT(tr) DESC")
    List<Object[]> getTerminationStatusStatistics(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);

    // Check if employee has pending termination requests
    @Query("SELECT COUNT(tr) > 0 FROM TerminationRequest tr WHERE tr.employee.id = :employeeId AND tr.status IN ('PENDING', 'UNDER_REVIEW')")
    Boolean hasActivePendingTermination(@Param("employeeId") String employeeId);

    // Find overdue termination requests (no action taken within SLA)
    @Query("SELECT tr FROM TerminationRequest tr WHERE tr.status = 'PENDING' AND tr.submissionDate < :slaDeadline")
    List<TerminationRequest> findOverdueTerminationRequests(@Param("slaDeadline") LocalDateTime slaDeadline);
}