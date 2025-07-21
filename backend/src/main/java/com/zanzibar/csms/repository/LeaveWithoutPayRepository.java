package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.LeaveWithoutPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveWithoutPayRepository extends JpaRepository<LeaveWithoutPay, Long> {

    @Query("SELECT l FROM LeaveWithoutPay l WHERE l.employee.id = :employeeId")
    List<LeaveWithoutPay> findByEmployeeId(@Param("employeeId") String employeeId);

    @Query("SELECT l FROM LeaveWithoutPay l WHERE l.employee.id = :employeeId AND l.status = 'APPROVED'")
    List<LeaveWithoutPay> findApprovedByEmployeeId(@Param("employeeId") String employeeId);

    @Query("SELECT l FROM LeaveWithoutPay l WHERE l.employee.id = :employeeId " +
           "AND l.status = 'APPROVED' " +
           "AND ((l.leaveStartDate <= :endDate AND l.leaveEndDate >= :startDate))")
    List<LeaveWithoutPay> findOverlappingApprovedRequests(@Param("employeeId") String employeeId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(l) FROM LeaveWithoutPay l WHERE l.employee.id = :employeeId " +
           "AND l.status = 'APPROVED' " +
           "AND YEAR(l.leaveStartDate) = :year")
    long countApprovedRequestsInYear(@Param("employeeId") String employeeId, @Param("year") int year);

    @Query("SELECT l FROM LeaveWithoutPay l WHERE l.employee.id = :employeeId " +
           "AND l.status = 'APPROVED' " +
           "AND l.leaveEndDate < :currentDate")
    List<LeaveWithoutPay> findCompletedApprovedRequests(@Param("employeeId") String employeeId,
                                                        @Param("currentDate") LocalDate currentDate);

    @Query("SELECT l FROM LeaveWithoutPay l WHERE l.status = 'PENDING' " +
           "AND l.leaveStartDate BETWEEN :startDate AND :endDate")
    List<LeaveWithoutPay> findPendingRequestsInPeriod(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT l FROM LeaveWithoutPay l WHERE l.employee.id = :employeeId " +
           "AND l.status IN ('PENDING', 'APPROVED') " +
           "AND l.leaveStartDate > :currentDate")
    List<LeaveWithoutPay> findActiveOrFutureRequests(@Param("employeeId") String employeeId,
                                                     @Param("currentDate") LocalDate currentDate);
}