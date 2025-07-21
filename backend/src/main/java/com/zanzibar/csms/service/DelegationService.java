package com.zanzibar.csms.service;

import com.zanzibar.csms.entity.Request;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.UserDelegation;
import com.zanzibar.csms.entity.enums.UserRole;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.exception.ValidationException;
import com.zanzibar.csms.repository.RequestRepository;
import com.zanzibar.csms.repository.UserDelegationRepository;
import com.zanzibar.csms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DelegationService {

    private final UserDelegationRepository delegationRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    /**
     * Creates a delegation from one user to another
     */
    @Transactional
    public UserDelegation createDelegation(String delegatorId, String delegateId, 
                                          LocalDateTime startDate, LocalDateTime endDate, 
                                          String reason) {
        log.info("Creating delegation from user {} to user {}", delegatorId, delegateId);

        User delegator = userRepository.findById(delegatorId)
            .orElseThrow(() -> new ResourceNotFoundException("Delegator not found"));

        User delegate = userRepository.findById(delegateId)
            .orElseThrow(() -> new ResourceNotFoundException("Delegate not found"));

        // Validate delegation
        validateDelegation(delegator, delegate, startDate, endDate);

        // Check for overlapping delegations
        List<UserDelegation> overlappingDelegations = delegationRepository
            .findOverlappingDelegations(delegatorId, startDate, endDate);
        
        if (!overlappingDelegations.isEmpty()) {
            throw new ValidationException("Delegation period overlaps with existing delegation");
        }

        UserDelegation delegation = UserDelegation.builder()
            .delegator(delegator)
            .delegate(delegate)
            .startDate(startDate)
            .endDate(endDate)
            .reason(reason)
            .isActive(true)
            .build();

        UserDelegation savedDelegation = delegationRepository.save(delegation);

        // Transfer pending requests if delegation is active now
        if (startDate.isBefore(LocalDateTime.now()) || startDate.isEqual(LocalDateTime.now())) {
            transferPendingRequests(delegator, delegate);
        }

        // Send notification
        notificationService.sendDelegationNotification(savedDelegation);

        // Log audit event
        auditService.logAction(
            delegatorId,
            delegator.getUsername(),
            "DELEGATION_CREATED",
            "UserDelegation",
            savedDelegation.getId(),
            delegator.getUsername(),
            delegate.getUsername(),
            true,
            null
        );

        return savedDelegation;
    }

    /**
     * Revokes an active delegation
     */
    @Transactional
    public void revokeDelegation(String delegationId, String reason, String revokedBy) {
        log.info("Revoking delegation: {} by user: {}", delegationId, revokedBy);

        UserDelegation delegation = delegationRepository.findById(delegationId)
            .orElseThrow(() -> new ResourceNotFoundException("Delegation not found"));

        if (!delegation.getIsActive()) {
            throw new ValidationException("Delegation is not active");
        }

        delegation.setIsActive(false);
        delegation.setRevokedAt(LocalDateTime.now());
        delegation.setRevokedBy(revokedBy);
        delegation.setRevocationReason(reason);

        delegationRepository.save(delegation);

        // Transfer requests back to original user
        transferPendingRequests(delegation.getDelegate(), delegation.getDelegator());

        // Send notification
        notificationService.sendDelegationRevocationNotification(delegation);

        // Log audit event
        auditService.logAction(
            revokedBy,
            null,
            "DELEGATION_REVOKED",
            "UserDelegation",
            delegationId,
            null,
            reason,
            true,
            null
        );
    }

    /**
     * Gets effective reviewer for a request (considering active delegations)
     */
    public User getEffectiveReviewer(User originalReviewer) {
        LocalDateTime now = LocalDateTime.now();
        
        Optional<UserDelegation> activeDelegation = delegationRepository
            .findActiveDelegationByDelegator(originalReviewer.getId(), now);

        if (activeDelegation.isPresent()) {
            log.debug("Found active delegation for user {}: delegated to {}", 
                originalReviewer.getUsername(), activeDelegation.get().getDelegate().getUsername());
            return activeDelegation.get().getDelegate();
        }

        return originalReviewer;
    }

    /**
     * Gets all active delegations for a user
     */
    public List<UserDelegation> getActiveDelegations(String userId) {
        LocalDateTime now = LocalDateTime.now();
        return delegationRepository.findActiveDelegationsByDelegator(userId, now);
    }

    /**
     * Gets all delegations where user is a delegate
     */
    public List<UserDelegation> getDelegationsAsDelegate(String userId) {
        LocalDateTime now = LocalDateTime.now();
        return delegationRepository.findActiveDelegationsByDelegate(userId, now);
    }

    /**
     * Gets delegation history for a user
     */
    public Page<UserDelegation> getDelegationHistory(String userId, Pageable pageable) {
        return delegationRepository.findByDelegatorIdOrDelegateId(userId, userId, pageable);
    }

    /**
     * Checks if a user is currently delegated
     */
    public boolean isDelegated(String userId) {
        LocalDateTime now = LocalDateTime.now();
        return delegationRepository.findActiveDelegationByDelegator(userId, now).isPresent();
    }

    /**
     * Checks if a user is currently acting as delegate
     */
    public boolean isActingAsDelegate(String userId) {
        LocalDateTime now = LocalDateTime.now();
        return !delegationRepository.findActiveDelegationsByDelegate(userId, now).isEmpty();
    }

    /**
     * Automatically activates and deactivates delegations based on date
     */
    @Transactional
    public void processDelegationSchedules() {
        LocalDateTime now = LocalDateTime.now();

        // Activate delegations that should start now
        List<UserDelegation> toActivate = delegationRepository.findDelegationsToActivate(now);
        for (UserDelegation delegation : toActivate) {
            transferPendingRequests(delegation.getDelegator(), delegation.getDelegate());
            notificationService.sendDelegationActivationNotification(delegation);
        }

        // Deactivate expired delegations
        List<UserDelegation> toDeactivate = delegationRepository.findDelegationsToDeactivate(now);
        for (UserDelegation delegation : toDeactivate) {
            delegation.setIsActive(false);
            transferPendingRequests(delegation.getDelegate(), delegation.getDelegator());
            notificationService.sendDelegationExpirationNotification(delegation);
        }

        if (!toActivate.isEmpty()) {
            delegationRepository.saveAll(toActivate);
        }
        if (!toDeactivate.isEmpty()) {
            delegationRepository.saveAll(toDeactivate);
        }
    }

    /**
     * Creates emergency delegation for immediate coverage
     */
    @Transactional
    public UserDelegation createEmergencyDelegation(String delegatorId, String delegateId, 
                                                   int durationHours, String reason) {
        log.info("Creating emergency delegation from {} to {} for {} hours", 
            delegatorId, delegateId, durationHours);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusHours(durationHours);

        UserDelegation delegation = createDelegation(delegatorId, delegateId, startDate, endDate, 
            "EMERGENCY: " + reason);

        // Send urgent notification
        notificationService.sendEmergencyDelegationNotification(delegation);

        return delegation;
    }

    /**
     * Extends an existing delegation
     */
    @Transactional
    public UserDelegation extendDelegation(String delegationId, LocalDateTime newEndDate, 
                                         String extensionReason, String extendedBy) {
        log.info("Extending delegation: {} to {}", delegationId, newEndDate);

        UserDelegation delegation = delegationRepository.findById(delegationId)
            .orElseThrow(() -> new ResourceNotFoundException("Delegation not found"));

        if (!delegation.getIsActive()) {
            throw new ValidationException("Cannot extend inactive delegation");
        }

        if (newEndDate.isBefore(delegation.getEndDate())) {
            throw new ValidationException("New end date must be after current end date");
        }

        delegation.setEndDate(newEndDate);
        delegation.setExtensionReason(extensionReason);
        delegation.setExtendedBy(extendedBy);
        delegation.setExtendedAt(LocalDateTime.now());

        UserDelegation savedDelegation = delegationRepository.save(delegation);

        // Send notification
        notificationService.sendDelegationExtensionNotification(savedDelegation);

        // Log audit event
        auditService.logAction(
            extendedBy,
            null,
            "DELEGATION_EXTENDED",
            "UserDelegation",
            delegationId,
            delegation.getEndDate().toString(),
            newEndDate.toString(),
            true,
            null
        );

        return savedDelegation;
    }

    private void validateDelegation(User delegator, User delegate, 
                                   LocalDateTime startDate, LocalDateTime endDate) {
        // Same user cannot delegate to themselves
        if (delegator.getId().equals(delegate.getId())) {
            throw new ValidationException("Cannot delegate to yourself");
        }

        // End date must be after start date
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date must be after start date");
        }

        // Both users must have compatible roles
        if (!areRolesCompatible(delegator.getRole(), delegate.getRole())) {
            throw new ValidationException("Delegate must have compatible role with delegator");
        }

        // Maximum delegation period (e.g., 30 days)
        if (java.time.Duration.between(startDate, endDate).toDays() > 30) {
            throw new ValidationException("Delegation period cannot exceed 30 days");
        }
    }

    private boolean areRolesCompatible(UserRole delegatorRole, UserRole delegateRole) {
        // Define role compatibility rules
        return switch (delegatorRole) {
            case HHRMD -> delegateRole == UserRole.HHRMD || delegateRole == UserRole.HRMO;
            case HRMO -> delegateRole == UserRole.HRMO || delegateRole == UserRole.HHRMD;
            case DO -> delegateRole == UserRole.DO || delegateRole == UserRole.HHRMD;
            case HRO -> delegateRole == UserRole.HRO;
            default -> false;
        };
    }

    private void transferPendingRequests(User fromUser, User toUser) {
        List<Request> pendingRequests = requestRepository.findPendingRequestsByCurrentReviewer(fromUser.getId());
        
        for (Request request : pendingRequests) {
            request.setCurrentReviewer(toUser);
        }
        
        if (!pendingRequests.isEmpty()) {
            requestRepository.saveAll(pendingRequests);
            log.info("Transferred {} pending requests from {} to {}", 
                pendingRequests.size(), fromUser.getUsername(), toUser.getUsername());
        }
    }
}