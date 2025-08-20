// 응답 DTO
package com.example.centralhackathon.dto.Response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BossAssociationResponse {
    private Long id;
    private String industry;
    private String boon;
    private String period;
    private Integer num;
    private String targetSchool;
    private String significant;
    private String imgUrl; // 단일 이미지 URL
}
