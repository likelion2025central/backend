package com.example.centralhackathon.dto.Response;

import com.example.centralhackathon.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private Role role;

    // role == COUNCIL 일 때만 채움
    private CouncilInfo council;

    // role == BOSS 일 때만 채움
    private BossInfo boss;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CouncilInfo {
        private String schoolName;
        private String college;
        private String department;
        private String email;
        private String phone;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BossInfo {
        private String storeName;
        private String bizRegNo;
        private String phone;
        private String email;
    }
}