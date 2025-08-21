package com.example.centralhackathon.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@DiscriminatorValue("COUNCIL")
@Table(name = "student_council")
@PrimaryKeyJoinColumn(name = "user_id")
public class StudentCouncil extends Users {
    private String schoolName;
    private String college;
    private String department;
    private String email;
    private String phone;

    @Override
    public Role getRole() { return Role.COUNCIL; }
}
