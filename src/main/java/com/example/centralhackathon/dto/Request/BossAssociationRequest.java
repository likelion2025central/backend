// 요청 DTO: JSON 본문
package com.example.centralhackathon.dto.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@NoArgsConstructor
@Getter @Setter
public class BossAssociationRequest {
    private String industry;
    private String boon;
    private String period;
    private Integer num;
    private String targetSchool;
    private String significant;
    private MultipartFile image;
}
