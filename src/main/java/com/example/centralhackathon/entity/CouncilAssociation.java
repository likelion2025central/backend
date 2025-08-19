package com.example.centralhackathon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class CouncilAssociation extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private Users user;
    private String industry;
    private String boon;
    private String period;
    private Integer num;
    private String targetSchool;
    private String targetCollege;
    private String targetDepartment;
    private String significant;

}
