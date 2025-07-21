package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.RequestWorkflow;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestWorkflowRepository extends JpaRepository<RequestWorkflow, String> {

    List<RequestWorkflow> findByRequestIdOrderByStepNumber(String requestId);

    Optional<RequestWorkflow> findByRequestIdAndIsCurrentStepTrue(String requestId);

    Optional<RequestWorkflow> findByRequestIdAndStepNumber(String requestId, Integer stepNumber);

    List<RequestWorkflow> findByReviewerIdAndStatus(String reviewerId, RequestStatus status);

    @Query("SELECT rw FROM RequestWorkflow rw WHERE rw.requiredRole = :role AND rw.status = :status")
    List<RequestWorkflow> findByRequiredRoleAndStatus(@Param("role") UserRole role, @Param("status") RequestStatus status);

    @Query("SELECT rw FROM RequestWorkflow rw WHERE rw.request.id = :requestId AND rw.stepNumber = (SELECT MAX(rw2.stepNumber) FROM RequestWorkflow rw2 WHERE rw2.request.id = :requestId)")
    Optional<RequestWorkflow> findLastStepForRequest(@Param("requestId") String requestId);

    @Query("SELECT AVG(rw.daysInStep) FROM RequestWorkflow rw WHERE rw.requiredRole = :role AND rw.status = 'APPROVED'")
    Double getAverageProcessingTimeByRole(@Param("role") UserRole role);
}