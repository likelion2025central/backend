// AssociationPaperRequest.java
package com.example.centralhackathon.dto.Request;

import com.example.centralhackathon.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class AssociationPaperRequest {
    private String councilInfo;
    private String storeName;
    private String boon;
    private LocalDate startDate;
    private LocalDate endDate;
    private String targetSchool;
    private String targetCollege;
    private String targetDepartment;
    private Role requester;
}
