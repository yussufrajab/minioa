package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    // Email methods removed - Prisma schema uses username instead of email
    
    boolean existsByUsername(String username);

    List<User> findByRole(UserRole role);

    @Query("SELECT u FROM User u WHERE u.institution.id = :institutionId")
    List<User> findByInstitutionId(@Param("institutionId") String institutionId);

    @Query("SELECT u FROM User u WHERE u.institution.id = :institutionId AND u.role = :role")
    List<User> findByInstitutionIdAndRole(@Param("institutionId") String institutionId, @Param("role") UserRole role);

    @Query("SELECT u FROM User u WHERE u.name LIKE CONCAT('%', :searchTerm, '%') OR u.username LIKE CONCAT('%', :searchTerm, '%')")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.active = true")
    List<User> findActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") UserRole role);

    List<User> findByActive(boolean active);

    List<User> findByRoleAndActiveTrue(UserRole role);
}