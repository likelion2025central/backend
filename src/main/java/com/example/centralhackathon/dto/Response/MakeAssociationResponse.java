package com.example.centralhackathon.dto.Response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MakeAssociationResponse {
    private int createdCount;
    public MakeAssociationResponse(int createdCount) { this.createdCount = createdCount; }
}