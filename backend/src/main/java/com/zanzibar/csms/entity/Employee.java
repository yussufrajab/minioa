package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "region")
    private String region;

    @Column(name = "country_of_birth")
    private String countryOfBirth;

    @Column(name = "payroll_number", unique = true, nullable = false)
    private String payrollNumber;

    @Column(name = "zanzibar_id", unique = true, nullable = false)
    private String zanzibarId;

    @Column(name = "zssf_number", unique = true, nullable = false)
    private String zssfNumber;

    @Column(name = "rank")
    private String rank;

    @Column(name = "ministry")
    private String ministry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "department")
    private String department;

    @Column(name = "appointment_type")
    private String appointmentType;

    @Column(name = "contract_type")
    private String contractType;

    @Column(name = "recent_title_date")
    private LocalDate recentTitleDate;

    @Column(name = "current_reporting_office")
    private String currentReportingOffice;

    @Column(name = "current_workplace")
    private String currentWorkplace;

    @Column(name = "employment_date", nullable = false)
    private LocalDate employmentDate;

    @Column(name = "confirmation_date")
    private LocalDate confirmationDate;

    @Column(name = "loan_guarantee_status")
    private Boolean loanGuaranteeStatus = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false)
    private EmploymentStatus employmentStatus;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "gender")
    private String gender;

    @Column(name = "contact_address")
    private String contactAddress;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmployeeDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmployeeCertificate> certificates = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Request> requests = new ArrayList<>();

    @OneToMany(mappedBy = "complainant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Complaint> filedComplaints = new ArrayList<>();

    @OneToMany(mappedBy = "respondent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Complaint> receivedComplaints = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LeaveWithoutPay> leaveRecords = new ArrayList<>();
}