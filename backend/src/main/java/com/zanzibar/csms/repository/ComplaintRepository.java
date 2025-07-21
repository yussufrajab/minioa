package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.Complaint;
import com.zanzibar.csms.entity.enums.ComplaintSeverity;
import com.zanzibar.csms.entity.enums.ComplaintStatus;
import com.zanzibar.csms.entity.enums.ComplaintType;
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
public interface ComplaintRepository extends JpaRepository<Complaint, String> {

    Optional<Complaint> findByComplaintNumber(String complaintNumber);

    Page<Complaint> findByComplainantId(String complainantId, Pageable pageable);

    Page<Complaint> findByRespondentId(String respondentId, Pageable pageable);

    Page<Complaint> findByStatus(ComplaintStatus status, Pageable pageable);

    Page<Complaint> findByComplaintType(ComplaintType complaintType, Pageable pageable);

    Page<Complaint> findBySeverity(ComplaintSeverity severity, Pageable pageable);

    Page<Complaint> findByAssignedInvestigatorId(String investigatorId, Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE c.submissionDate BETWEEN :startDate AND :endDate")
    Page<Complaint> findBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate, 
                                               Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE c.targetResolutionDate < :now AND c.status NOT IN :terminalStatuses")
    List<Complaint> findOverdueComplaints(@Param("now") LocalDateTime now, 
                                         @Param("terminalStatuses") List<ComplaintStatus> terminalStatuses);

    @Query("SELECT c FROM Complaint c WHERE c.followUpRequired = true AND c.followUpDate <= :date")
    List<Complaint> findComplaintsRequiringFollowUp(@Param("date") LocalDateTime date);

    @Query("SELECT c FROM Complaint c WHERE c.status = :status AND c.assignedInvestigator IS NULL")
    List<Complaint> findUnassignedComplaintsByStatus(@Param("status") ComplaintStatus status);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = :status")
    Long countByStatus(@Param("status") ComplaintStatus status);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.complaintType = :type")
    Long countByComplaintType(@Param("type") ComplaintType type);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.severity = :severity")
    Long countBySeverity(@Param("severity") ComplaintSeverity severity);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.submissionDate BETWEEN :startDate AND :endDate")
    Long countBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(CAST((EXTRACT(EPOCH FROM c.actualResolutionDate) - EXTRACT(EPOCH FROM c.submissionDate)) / 86400 AS double)) " +
           "FROM Complaint c WHERE c.actualResolutionDate IS NOT NULL AND c.submissionDate BETWEEN :startDate AND :endDate")
    Double getAverageResolutionTimeInDays(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(c.complainantSatisfactionRating) FROM Complaint c " +
           "WHERE c.complainantSatisfactionRating IS NOT NULL AND c.submissionDate BETWEEN :startDate AND :endDate")
    Double getAverageSatisfactionRating(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.complaintType, COUNT(c) FROM Complaint c " +
           "WHERE c.submissionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY c.complaintType ORDER BY COUNT(c) DESC")
    List<Object[]> getComplaintTypeStatistics(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.status, COUNT(c) FROM Complaint c " +
           "WHERE c.submissionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY c.status ORDER BY COUNT(c) DESC")
    List<Object[]> getComplaintStatusStatistics(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c FROM Complaint c WHERE " +
           "(:complaintType IS NULL OR c.complaintType = :complaintType) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:severity IS NULL OR c.severity = :severity) AND " +
           "(:complainantId IS NULL OR c.complainant.id = :complainantId) AND " +
           "(:respondentId IS NULL OR c.respondent.id = :respondentId) AND " +
           "(:investigatorId IS NULL OR c.assignedInvestigator.id = :investigatorId) AND " +
           "(:startDate IS NULL OR c.submissionDate >= :startDate) AND " +
           "(:endDate IS NULL OR c.submissionDate <= :endDate)")
    Page<Complaint> findComplaintsWithFilters(@Param("complaintType") ComplaintType complaintType,
                                             @Param("status") ComplaintStatus status,
                                             @Param("severity") ComplaintSeverity severity,
                                             @Param("complainantId") String complainantId,
                                             @Param("respondentId") String respondentId,
                                             @Param("investigatorId") String investigatorId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.complaintNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Complaint> searchComplaints(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = :status AND c.submissionDate BETWEEN :startDate AND :endDate")
    Long countByStatusAndSubmissionDateBetween(@Param("status") ComplaintStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.complainant.institution.id = :institutionId AND c.submissionDate BETWEEN :startDate AND :endDate")
    Long countByComplainantInstitutionIdAndSubmissionDateBetween(@Param("institutionId") String institutionId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.complainant.id = :complainantId")
    Long countByComplainantId(@Param("complainantId") String complainantId);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.complainant.id = :complainantId AND c.status = :status")
    Long countByComplainantIdAndStatus(@Param("complainantId") String complainantId, @Param("status") ComplaintStatus status);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.complainant.institution.id = :institutionId")
    Long countByComplainantInstitutionId(@Param("institutionId") String institutionId);
}