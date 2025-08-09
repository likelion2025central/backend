package com.example.centralhackathon.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("STUDENT")
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id") // users.id를 PK=FK로
class NormalUser extends Users {
    private String userName;
    private String schoolName;
    private String major;
}