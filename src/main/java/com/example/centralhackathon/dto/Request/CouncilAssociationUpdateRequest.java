package com.example.centralhackathon.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouncilAssociationUpdateRequest {
    private String industry;
    private String boon;
    private String period;
    private Integer num;
    private String targetSchool;
    private String targetCollege;
    private String targetDepartment;
    private String significant;
}
