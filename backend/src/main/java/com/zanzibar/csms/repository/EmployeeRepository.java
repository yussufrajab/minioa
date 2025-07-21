package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
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
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByPayrollNumber(String payrollNumber);

    Optional<Employee> findByZanzibarId(String zanzibarId);

    Optional<Employee> findByZssfNumber(String zssfNumber);

    boolean existsByPayrollNumber(String payrollNumber);

    boolean existsByZanzibarId(String zanzibarId);

    boolean existsByZssfNumber(String zssfNumber);

    @Query("SELECT e FROM Employee e WHERE e.institution.id = :institutionId")
    List<Employee> findByInstitutionId(@Param("institutionId") String institutionId);

    List<Employee> findByEmploymentStatus(EmploymentStatus status);

    @Query("SELECT e FROM Employee e WHERE e.institution.id = :institutionId AND e.employmentStatus = :status")
    List<Employee> findByInstitutionIdAndStatus(@Param("institutionId") String institutionId, @Param("status") EmploymentStatus status);

    @Query("SELECT e FROM Employee e WHERE e.fullName LIKE CONCAT('%', :searchTerm, '%') OR e.payrollNumber LIKE CONCAT('%', :searchTerm, '%') OR e.zanzibarId LIKE CONCAT('%', :searchTerm, '%')")
    Page<Employee> searchEmployees(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.institution.id = :institutionId AND (e.fullName LIKE CONCAT('%', :searchTerm, '%') OR e.payrollNumber LIKE CONCAT('%', :searchTerm, '%') OR e.zanzibarId LIKE CONCAT('%', :searchTerm, '%'))")
    Page<Employee> searchEmployeesByInstitution(@Param("institutionId") String institutionId, @Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.employmentDate <= :endDate AND e.confirmationDate IS NULL")
    List<Employee> findUnconfirmedEmployeesEligibleForConfirmation(@Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM Employee e WHERE e.dateOfBirth <= :retirementDate AND e.employmentStatus = 'ACTIVE'")
    List<Employee> findEmployeesEligibleForRetirement(@Param("retirementDate") LocalDate retirementDate);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.employmentStatus = :status")
    Long countByStatus(@Param("status") EmploymentStatus status);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.institution.id = :institutionId AND e.employmentStatus = :status")
    Long countByInstitutionIdAndEmploymentStatus(@Param("institutionId") String institutionId, @Param("status") EmploymentStatus status);

    @Query("SELECT e FROM Employee e WHERE e.institution.id = :institutionId")
    Page<Employee> findByInstitutionId(@Param("institutionId") String institutionId, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.rank = :rank")
    List<Employee> findByRank(@Param("rank") String rank);

    @Query("SELECT e FROM Employee e WHERE e.department = :department")
    List<Employee> findByDepartment(@Param("department") String department);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.institution.id = :institutionId")
    Long countByInstitutionId(@Param("institutionId") String institutionId);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.institution.id = :institutionId AND e.isActive = :isActive")
    Long countByInstitutionIdAndIsActive(@Param("institutionId") String institutionId, @Param("isActive") boolean isActive);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.isActive = :isActive")
    Long countByIsActive(@Param("isActive") boolean isActive);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.institution.id = :institutionId AND e.createdAt >= :startDate")
    Long countByInstitutionIdAndCreatedAtAfter(@Param("institutionId") String institutionId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.createdAt >= :startDate")
    Long countByCreatedAtAfter(@Param("startDate") LocalDateTime startDate);

    // Additional methods for confirmation eligibility
    @Query("SELECT e FROM Employee e WHERE e.employmentStatus = :status AND e.employmentDate BETWEEN :startDate AND :endDate")
    Page<Employee> findByEmploymentStatusAndEmploymentDateBetween(@Param("status") EmploymentStatus status, 
                                                                 @Param("startDate") LocalDate startDate, 
                                                                 @Param("endDate") LocalDate endDate, 
                                                                 Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.employmentStatus = :status AND e.employmentDate < :endDate")
    Page<Employee> findByEmploymentStatusAndEmploymentDateBefore(@Param("status") EmploymentStatus status, 
                                                                @Param("endDate") LocalDate endDate, 
                                                                Pageable pageable);
}