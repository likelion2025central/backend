package com.example.centralhackathon.dto.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouncilAssociationResponse {
    private Long id;
    private String industry;
    private String boon;
    private String period;
    private String targetSchool;
    private String targetCollege;
    private String targetDepartment;
    private String significant;
}

