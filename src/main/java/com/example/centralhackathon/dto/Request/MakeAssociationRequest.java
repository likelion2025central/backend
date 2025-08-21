package com.example.centralhackathon.dto.Request;

import com.example.centralhackathon.entity.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class MakeAssociationRequest {
    @NotNull
    private Role requesterType; // COUNCIL 또는 BOSS만 허용

    @NotNull
    private Long requesterId;

    @NotEmpty
    private List<Long> targetIds;
}

