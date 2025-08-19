package com.example.centralhackathon.entity;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Setter
@DiscriminatorValue("STUDENT")
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id") // users.id를 PK=FK로
public class NormalUser extends Users {
    private String name;
    private String schoolName;
    private String major;

    @Override
    public Role getRole() { return Role.STUDENT; }
}