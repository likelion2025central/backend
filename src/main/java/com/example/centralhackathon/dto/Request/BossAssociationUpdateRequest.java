package com.example.centralhackathon.dto.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter @NoArgsConstructor
public class BossAssociationUpdateRequest {
    private String industry;
    private String boon;
    private String period;
    private Integer num;
    private String targetSchool;
    private String significant;
    private MultipartFile image;
}
