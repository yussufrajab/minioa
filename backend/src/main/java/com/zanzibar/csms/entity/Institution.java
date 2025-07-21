package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"Institution\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institution {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    // Institution is always active in Prisma schema - this field is for compatibility only
    @Transient
    private Boolean isActive = true;

    // Additional fields for compatibility with existing services (stored in-memory only)
    // These fields don't exist in the Prisma database schema, so they are @Transient
    @Transient
    private String email;

    @Transient
    private String address;

    @Transient
    private String telephone;

    @Transient
    private String voteNumber;

    @Transient
    private String voteDescription;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();
}