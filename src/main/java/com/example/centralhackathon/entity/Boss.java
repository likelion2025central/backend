package com.example.centralhackathon.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("OWNER")
@Table(name = "owners")
@PrimaryKeyJoinColumn(name = "user_id")
public class Boss extends Users {
    private String storeName;
    @Column(unique = true)
    private String bizRegNo;
    private String phone;
}
