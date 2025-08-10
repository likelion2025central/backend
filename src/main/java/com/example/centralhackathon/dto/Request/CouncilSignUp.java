package com.example.centralhackathon.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouncilSignUp {
    private String username;
    private String password;
    private String department;
    private String email;
    private String phone;
}
