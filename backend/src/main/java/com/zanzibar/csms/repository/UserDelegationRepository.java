package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.UserDelegation;
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
public interface UserDelegationRepository extends JpaRepository<UserDelegation, String> {

    /**
     * Find active delegation by delegator at specific time
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.delegator.id = :delegatorId AND d.isActive = true AND d.startDate <= :currentTime AND d.endDate >= :currentTime")
    Optional<UserDelegation> findActiveDelegationByDelegator(@Param("delegatorId") String delegatorId, 
                                                            @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find all active delegations by delegator
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.delegator.id = :delegatorId AND d.isActive = true AND d.startDate <= :currentTime AND d.endDate >= :currentTime")
    List<UserDelegation> findActiveDelegationsByDelegator(@Param("delegatorId") String delegatorId, 
                                                          @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find all active delegations by delegate
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.delegate.id = :delegateId AND d.isActive = true AND d.startDate <= :currentTime AND d.endDate >= :currentTime")
    List<UserDelegation> findActiveDelegationsByDelegate(@Param("delegateId") String delegateId, 
                                                         @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find overlapping delegations for a delegator
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.delegator.id = :delegatorId AND d.isActive = true AND ((d.startDate <= :endDate AND d.endDate >= :startDate))")
    List<UserDelegation> findOverlappingDelegations(@Param("delegatorId") String delegatorId, 
                                                    @Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);

    /**
     * Find delegations by delegator or delegate
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.delegator.id = :delegatorId OR d.delegate.id = :delegateId ORDER BY d.startDate DESC")
    Page<UserDelegation> findByDelegatorIdOrDelegateId(@Param("delegatorId") String delegatorId, 
                                                       @Param("delegateId") String delegateId, 
                                                       Pageable pageable);

    /**
     * Find delegations that should be activated
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.isActive = true AND d.startDate <= :currentTime AND d.startDate > :lastCheck")
    List<UserDelegation> findDelegationsToActivate(@Param("currentTime") LocalDateTime currentTime, 
                                                   @Param("lastCheck") LocalDateTime lastCheck);

    /**
     * Find delegations that should be deactivated (expired)
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.isActive = true AND d.endDate < :currentTime")
    List<UserDelegation> findDelegationsToDeactivate(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find active delegations
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.isActive = true AND d.startDate <= :currentTime AND d.endDate >= :currentTime")
    List<UserDelegation> findActiveDelegations(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find expired delegations
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.isActive = true AND d.endDate < :currentTime")
    List<UserDelegation> findExpiredDelegations(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find delegations by date range
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.startDate >= :startDate AND d.endDate <= :endDate")
    List<UserDelegation> findDelegationsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);

    /**
     * Find emergency delegations
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.reason LIKE 'EMERGENCY:%' AND d.isActive = true")
    List<UserDelegation> findEmergencyDelegations();

    /**
     * Find extended delegations
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.extendedAt IS NOT NULL")
    List<UserDelegation> findExtendedDelegations();

    /**
     * Find revoked delegations
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.revokedAt IS NOT NULL")
    List<UserDelegation> findRevokedDelegations();

    /**
     * Count active delegations by delegator
     */
    @Query("SELECT COUNT(d) FROM UserDelegation d WHERE d.delegator.id = :delegatorId AND d.isActive = true AND d.startDate <= :currentTime AND d.endDate >= :currentTime")
    long countActiveDelegationsByDelegator(@Param("delegatorId") String delegatorId, 
                                          @Param("currentTime") LocalDateTime currentTime);

    /**
     * Count active delegations by delegate
     */
    @Query("SELECT COUNT(d) FROM UserDelegation d WHERE d.delegate.id = :delegateId AND d.isActive = true AND d.startDate <= :currentTime AND d.endDate >= :currentTime")
    long countActiveDelegationsByDelegate(@Param("delegateId") String delegateId, 
                                         @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find delegations that will expire soon
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.isActive = true AND d.endDate BETWEEN :currentTime AND :warningTime")
    List<UserDelegation> findDelegationsExpiringSoon(@Param("currentTime") LocalDateTime currentTime, 
                                                     @Param("warningTime") LocalDateTime warningTime);

    /**
     * Find delegations by institution (if users belong to institutions)
     */
    @Query("SELECT d FROM UserDelegation d WHERE d.delegator.institution.id = :institutionId OR d.delegate.institution.id = :institutionId")
    List<UserDelegation> findDelegationsByInstitution(@Param("institutionId") String institutionId);

    // Additional method to simplify delegation activation check
    default List<UserDelegation> findDelegationsToActivate(LocalDateTime currentTime) {
        return findDelegationsToActivate(currentTime, currentTime.minusHours(1));
    }
}