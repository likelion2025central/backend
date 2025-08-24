package com.example.centralhackathon.dto.Response;

import com.example.centralhackathon.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class AssocForStudentResponse {
    private Long id;
    private Long associationId;
    private String councilInfo;
    private String storeName;
    private String boon;
    private LocalDate startDate;
    private LocalDate endDate;
    private String targetSchool;
    private String targetCollege;
    private String targetDepartment;
    private String imgUrl;
    public AssocForStudentResponse(
            Long id, Long associationId, String councilInfo, String storeName, String boon,
            LocalDate startDate, LocalDate endDate, String targetSchool, String targetCollege,
            String targetDepartment, String imgUrl
    ) {
        this.id = id;
        this.associationId = associationId;
        this.councilInfo = councilInfo;
        this.storeName = storeName;
        this.boon = boon;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetSchool = targetSchool;
        this.targetCollege = targetCollege;
        this.targetDepartment = targetDepartment;
        this.imgUrl = imgUrl;
    }

    public AssocForStudentResponse() {}
}

