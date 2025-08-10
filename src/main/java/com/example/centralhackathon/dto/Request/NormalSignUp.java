package com.example.centralhackathon.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NormalSignUp {
    private String username;
    private String name;
    private String schoolName;
    private String major;
    private String password;
}
