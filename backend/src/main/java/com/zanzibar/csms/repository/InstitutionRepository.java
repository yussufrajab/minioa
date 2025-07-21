package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, String> {

    Optional<Institution> findByName(String name);

    boolean existsByName(String name);

    // Email field doesn't exist in Prisma schema - using Transient field
    // This method won't work with database queries, keeping for interface compatibility
    default Optional<Institution> findByEmail(String email) {
        return Optional.empty(); // All institutions are considered active in Prisma
    }

    @Query("SELECT i FROM Institution i WHERE i.name LIKE CONCAT('%', :searchTerm, '%')")
    Page<Institution> searchInstitutions(@Param("searchTerm") String searchTerm, Pageable pageable);

    // All institutions are considered active in Prisma schema
    default List<Institution> findActiveInstitutions() {
        return findAll(); // Return all institutions as they're all active
    }

    // VoteNumber field doesn't exist in Prisma schema
    default List<Institution> findByVoteNumber(String voteNumber) {
        return List.of(); // Return empty list for compatibility
    }

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.institution.id = :institutionId")
    Long countEmployeesByInstitution(@Param("institutionId") String institutionId);

    // All institutions are considered active in Prisma schema
    default Long countByIsActive(boolean isActive) {
        return isActive ? count() : 0L; // Return total count if asking for active, 0 for inactive
    }
}