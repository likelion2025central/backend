package com.example.centralhackathon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@DiscriminatorValue("BOSS")
@Table(name = "bosses")
@PrimaryKeyJoinColumn(name = "user_id")
public class Boss extends Users {
    private String storeName;
    @Column(unique = true)
    private String bizRegNo;
    private String phone;
    private String email;

    @Override
    public Role getRole() { return Role.BOSS; }
}
