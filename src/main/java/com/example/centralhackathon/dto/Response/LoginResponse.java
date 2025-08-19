package com.example.centralhackathon.dto.Response;

import com.example.centralhackathon.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private String token;
    private Role role;
}
