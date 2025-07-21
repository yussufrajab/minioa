package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.Request;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RequestType;
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
public interface RequestRepository extends JpaRepository<Request, String> {

    Optional<Request> findByRequestNumber(String requestNumber);

    Page<Request> findByStatus(RequestStatus status, Pageable pageable);

    Page<Request> findByRequestType(RequestType requestType, Pageable pageable);

    Page<Request> findByEmployeeId(String employeeId, Pageable pageable);

    Page<Request> findBySubmittedById(String submittedById, Pageable pageable);

    Page<Request> findByCurrentReviewerId(String currentReviewerId, Pageable pageable);

    @Query("SELECT r FROM Request r WHERE r.employee.institution.id = :institutionId")
    Page<Request> findByInstitutionId(@Param("institutionId") String institutionId, Pageable pageable);

    @Query("SELECT r FROM Request r WHERE r.status IN :statuses")
    Page<Request> findByStatusIn(@Param("statuses") List<RequestStatus> statuses, Pageable pageable);

    @Query("SELECT r FROM Request r WHERE r.dueDate < :date AND r.status IN :statuses")
    List<Request> findOverdueRequests(@Param("date") LocalDateTime date, @Param("statuses") List<RequestStatus> statuses);

    @Query("SELECT r FROM Request r WHERE r.submissionDate BETWEEN :startDate AND :endDate")
    List<Request> findBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.status = :status")
    Long countByStatus(@Param("status") RequestStatus status);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.requestType = :requestType AND r.status = :status")
    Long countByRequestTypeAndStatus(@Param("requestType") RequestType requestType, @Param("status") RequestStatus status);

    @Query("SELECT r FROM Request r WHERE r.currentReviewer.id = :reviewerId AND r.status IN :statuses ORDER BY r.priority DESC, r.submissionDate ASC")
    List<Request> findByCurrentReviewerAndStatus(@Param("reviewerId") String reviewerId, @Param("statuses") List<RequestStatus> statuses);

    // Additional methods for business rule engine
    @Query("SELECT COUNT(r) FROM Request r WHERE r.employee = :employee AND r.requestType = :requestType AND r.submissionDate BETWEEN :startDate AND :endDate")
    long countByEmployeeAndRequestTypeAndSubmissionDateBetween(@Param("employee") com.zanzibar.csms.entity.Employee employee, 
                                                              @Param("requestType") RequestType requestType, 
                                                              @Param("startDate") LocalDateTime startDate, 
                                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Request r WHERE r.employee = :employee AND r.status IN ('SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW')")
    List<Request> findPendingRequestsByEmployee(@Param("employee") com.zanzibar.csms.entity.Employee employee);

    @Query("SELECT r FROM Request r WHERE r.currentReviewer.id = :reviewerId AND r.status IN ('SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW')")
    List<Request> findPendingRequestsByCurrentReviewer(@Param("reviewerId") String reviewerId);

    // SLA related methods
    @Query("SELECT r FROM Request r WHERE r.currentReviewer.id = :reviewerId AND r.dueDate < :currentTime AND r.status IN ('SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW')")
    Page<Request> findOverdueRequestsByCurrentReviewer(@Param("reviewerId") String reviewerId, 
                                                       @Param("currentTime") LocalDateTime currentTime, 
                                                       Pageable pageable);

    @Query("SELECT r FROM Request r WHERE r.dueDate BETWEEN :currentTime AND :warningThreshold AND r.status IN ('SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW')")
    Page<Request> findRequestsApproachingSLA(@Param("currentTime") LocalDateTime currentTime, 
                                            @Param("warningThreshold") LocalDateTime warningThreshold, 
                                            Pageable pageable);

    // Status-based queries
    @Query("SELECT r FROM Request r WHERE r.status IN :statuses")
    List<Request> findByStatusIn(@Param("statuses") List<RequestStatus> statuses);

    // Default method to find requests approaching SLA deadline
    default Page<Request> findRequestsApproachingSLA(LocalDateTime warningThreshold, Pageable pageable) {
        return findRequestsApproachingSLA(LocalDateTime.now(), warningThreshold, pageable);
    }

    @Query("SELECT r FROM Request r WHERE r.currentReviewer.id = :reviewerId AND r.status IN :statuses ORDER BY r.priority DESC, r.submissionDate ASC")
    List<Request> findPendingRequestsForReviewer(@Param("reviewerId") String reviewerId, @Param("statuses") List<RequestStatus> statuses);

    @Query("SELECT r FROM Request r WHERE r.employee.fullName LIKE CONCAT('%', :searchTerm, '%') OR r.requestNumber LIKE CONCAT('%', :searchTerm, '%') OR r.description LIKE CONCAT('%', :searchTerm, '%')")
    Page<Request> searchRequests(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT YEAR(r.submissionDate) as year, MONTH(r.submissionDate) as month, COUNT(r) as count " +
           "FROM Request r WHERE r.submissionDate >= :startDate GROUP BY YEAR(r.submissionDate), MONTH(r.submissionDate) ORDER BY year, month")
    List<Object[]> getRequestStatisticsByMonth(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.submissionDate BETWEEN :startDate AND :endDate")
    Long countBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.status = :status AND r.submissionDate BETWEEN :startDate AND :endDate")
    Long countByStatusAndSubmissionDateBetween(@Param("status") RequestStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.employee.institution.id = :institutionId AND r.submissionDate BETWEEN :startDate AND :endDate")
    Long countByEmployeeInstitutionIdAndSubmissionDateBetween(@Param("institutionId") String institutionId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.requestType = :requestType")
    Long countByRequestType(@Param("requestType") RequestType requestType);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.employee.institution.id = :institutionId AND r.requestType = :requestType")
    Long countByEmployeeInstitutionIdAndRequestType(@Param("institutionId") String institutionId, @Param("requestType") RequestType requestType);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.employee.institution.id = :institutionId AND r.status = :status")
    Long countByEmployeeInstitutionIdAndStatus(@Param("institutionId") String institutionId, @Param("status") RequestStatus status);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.employee.id = :employeeId")
    Long countByEmployeeId(@Param("employeeId") String employeeId);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.employee.id = :employeeId AND r.status = :status")
    Long countByEmployeeIdAndStatus(@Param("employeeId") String employeeId, @Param("status") RequestStatus status);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.employee.institution.id = :institutionId")
    Long countByEmployeeInstitutionId(@Param("institutionId") String institutionId);
}