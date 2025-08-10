package com.example.centralhackathon.dto.Request;

import com.example.centralhackathon.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private String password;
    private String username;

    public static UserInfo toDto(Users user) {
        return new UserInfo(
                user.getId(),
                user.getPassword(),
                user.getUsername()
        );
    }
}
