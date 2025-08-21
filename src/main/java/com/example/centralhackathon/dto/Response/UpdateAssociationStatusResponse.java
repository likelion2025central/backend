package com.example.centralhackathon.dto.Response;

import com.example.centralhackathon.entity.AssociationCondition;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UpdateAssociationStatusResponse {
    private Long id;
    private AssociationCondition status;

    public UpdateAssociationStatusResponse(Long id, @NotNull AssociationCondition status) {
    }
}