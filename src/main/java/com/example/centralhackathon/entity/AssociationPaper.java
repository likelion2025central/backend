package com.example.centralhackathon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AssociationPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assoc_id")
    private Association association;
    private String councilInfo;
    private String storeName;
    private String boon;
    private LocalDate startDate;
    private LocalDate endDate;
    private String targetSchool;
    private String targetCollege;
    private String targetDepartment;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role requester;
}
