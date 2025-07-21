package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employee_certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCertificate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "certificate_type", nullable = false)
    private String certificateType;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "field_of_study")
    private String fieldOfStudy;

    @Column(name = "date_obtained")
    private LocalDate dateObtained;

    @Column(name = "certificate_number")
    private String certificateNumber;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;
}