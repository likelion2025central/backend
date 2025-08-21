package com.example.centralhackathon.dto.Request;

import com.example.centralhackathon.entity.AssociationCondition;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateAssociationStatusRequest {
    @NotNull
    private AssociationCondition status; // WAITING/NEGOTIATING/CONFIRMED/REJECTED
}