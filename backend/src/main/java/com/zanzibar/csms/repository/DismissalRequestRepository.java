package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.DismissalRequest;
import com.zanzibar.csms.entity.enums.DismissalReason;
import com.zanzibar.csms.entity.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DismissalRequestRepository extends JpaRepository<DismissalRequest, String> {

    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.employee.id = :employeeId")
    List<DismissalRequest> findByEmployeeId(@Param("employeeId") String employeeId);

    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.employee.id = :employeeId")
    Page<DismissalRequest> findByEmployeeId(@Param("employeeId") String employeeId, Pageable pageable);

    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.submittedBy.id = :submittedById")
    Page<DismissalRequest> findBySubmittedById(@Param("submittedById") String submittedById, Pageable pageable);

    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.status = :status")
    Page<DismissalRequest> findByStatus(@Param("status") RequestStatus status, Pageable pageable);

    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.dismissalReason = :reason")
    Page<DismissalRequest> findByDismissalReason(@Param("reason") DismissalReason reason, Pageable pageable);

    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.employee.institution.id = :institutionId")
    Page<DismissalRequest> findByInstitutionId(@Param("institutionId") String institutionId, Pageable pageable);

    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.approver.id = :approverId")
    Page<DismissalRequest> findByApproverId(@Param("approverId") String approverId, Pageable pageable);

    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.submissionDate BETWEEN :startDate AND :endDate")
    Page<DismissalRequest> findBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate, 
                                                       Pageable pageable);

    @Query("SELECT dr FROM DismissalRequest dr WHERE " +
           "(:status IS NULL OR dr.status = :status) AND " +
           "(:reason IS NULL OR dr.dismissalReason = :reason) AND " +
           "(:institutionId IS NULL OR dr.employee.institution.id = :institutionId) AND " +
           "(:submittedById IS NULL OR dr.submittedBy.id = :submittedById) AND " +
           "(:startDate IS NULL OR dr.submissionDate >= :startDate) AND " +
           "(:endDate IS NULL OR dr.submissionDate <= :endDate)")
    Page<DismissalRequest> findWithFilters(@Param("status") RequestStatus status,
                                          @Param("reason") DismissalReason reason,
                                          @Param("institutionId") String institutionId,
                                          @Param("submittedById") String submittedById,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          Pageable pageable);

    @Query("SELECT dr FROM DismissalRequest dr WHERE " +
           "LOWER(dr.employee.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(dr.employee.payrollNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(dr.requestNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(dr.detailedCharges) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<DismissalRequest> searchDismissalRequests(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Investigation-related queries
    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.investigationStartDate IS NOT NULL AND dr.investigationEndDate IS NULL")
    List<DismissalRequest> findActiveInvestigations();

    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.investigationOfficer = :officer")
    Page<DismissalRequest> findByInvestigationOfficer(@Param("officer") String officer, Pageable pageable);

    // Legal consultation queries
    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.legalConsultation = true")
    Page<DismissalRequest> findRequestsWithLegalConsultation(Pageable pageable);

    // Appeal period queries
    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.appealPeriodExpires < :now AND dr.status = 'APPROVED'")
    List<DismissalRequest> findRequestsWithExpiredAppealPeriod(@Param("now") LocalDateTime now);

    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.appealPeriodExpires BETWEEN :now AND :alertTime AND dr.status = 'APPROVED'")
    List<DismissalRequest> findRequestsWithAppealPeriodExpiringSoon(@Param("now") LocalDateTime now, 
                                                                   @Param("alertTime") LocalDateTime alertTime);

    // Statistics queries
    @Query("SELECT COUNT(dr) FROM DismissalRequest dr WHERE dr.status = :status")
    Long countByStatus(@Param("status") RequestStatus status);

    @Query("SELECT COUNT(dr) FROM DismissalRequest dr WHERE dr.dismissalReason = :reason")
    Long countByDismissalReason(@Param("reason") DismissalReason reason);

    @Query("SELECT COUNT(dr) FROM DismissalRequest dr WHERE dr.submissionDate BETWEEN :startDate AND :endDate")
    Long countBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(dr) FROM DismissalRequest dr WHERE dr.employee.institution.id = :institutionId AND dr.status = :status")
    Long countByInstitutionIdAndStatus(@Param("institutionId") String institutionId, @Param("status") RequestStatus status);

    @Query("SELECT dr.dismissalReason, COUNT(dr) FROM DismissalRequest dr " +
           "WHERE dr.submissionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY dr.dismissalReason ORDER BY COUNT(dr) DESC")
    List<Object[]> getDismissalReasonStatistics(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT dr.status, COUNT(dr) FROM DismissalRequest dr " +
           "WHERE dr.submissionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY dr.status ORDER BY COUNT(dr) DESC")
    List<Object[]> getDismissalStatusStatistics(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    // Check if employee has pending dismissal requests
    @Query("SELECT COUNT(dr) > 0 FROM DismissalRequest dr WHERE dr.employee.id = :employeeId AND dr.status IN ('PENDING', 'UNDER_REVIEW')")
    Boolean hasActivePendingDismissal(@Param("employeeId") String employeeId);

    // Find overdue dismissal requests (no action taken within SLA)
    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.status = 'PENDING' AND dr.submissionDate < :slaDeadline")
    List<DismissalRequest> findOverdueDismissalRequests(@Param("slaDeadline") LocalDateTime slaDeadline);

    // Hearing-related queries
    @Query("SELECT dr FROM DismissalRequest dr WHERE dr.hearingDate BETWEEN :startDate AND :endDate")
    List<DismissalRequest> findRequestsWithHearingsBetween(@Param("startDate") LocalDateTime startDate, 
                                                          @Param("endDate") LocalDateTime endDate);

    // Settlement queries
    @Query("SELECT AVG(dr.finalSettlementAmount) FROM DismissalRequest dr WHERE dr.finalSettlementAmount IS NOT NULL AND dr.submissionDate BETWEEN :startDate AND :endDate")
    Double getAverageSettlementAmount(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(dr) FROM DismissalRequest dr WHERE dr.finalSettlementAmount > 0 AND dr.submissionDate BETWEEN :startDate AND :endDate")
    Long countRequestsWithSettlement(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
}