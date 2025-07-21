package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "leave_without_pay_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveWithoutPay extends Request {

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "leave_start_date", nullable = false)
    private LocalDate leaveStartDate;

    @Column(name = "leave_end_date", nullable = false)
    private LocalDate leaveEndDate;

    @Column(name = "has_loan_guarantee")
    private Boolean hasLoanGuarantee = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;
}