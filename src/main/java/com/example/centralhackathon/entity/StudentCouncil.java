package com.example.centralhackathon.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Setter;

@Entity
@Setter
@DiscriminatorValue("COUNCIL")
@Table(name = "student_council")
@PrimaryKeyJoinColumn(name = "user_id")
public class StudentCouncil extends Users {
    private String department;
    private String email;
    private String phone;
}
