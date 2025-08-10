package com.example.centralhackathon.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BossSignUp {
    private String username;
    private String password;
    private String storeName;
    private String bizRegNo;
    private String phone;
}
