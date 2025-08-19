package com.example.centralhackathon.entity;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Setter
@DiscriminatorValue("BOSS")
@Table(name = "bosses")
@PrimaryKeyJoinColumn(name = "user_id")
public class Boss extends Users {
    private String storeName;
    @Column(unique = true)
    private String bizRegNo;
    private String phone;

    @Override
    public Role getRole() { return Role.BOSS; }
}
