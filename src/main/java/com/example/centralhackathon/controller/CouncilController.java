package com.example.centralhackathon.controller;

import com.example.centralhackathon.config.ApiResponse;
import com.example.centralhackathon.dto.Request.BossSignUp;
import com.example.centralhackathon.dto.Request.CouncilAssociationRequest;
import com.example.centralhackathon.service.CouncilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/council")
public class CouncilController {
    private final CouncilService councilService;
    @PostMapping("/association/register")
    public ResponseEntity<ApiResponse> signUpBoss(@RequestBody CouncilAssociationRequest req, @AuthenticationPrincipal UserDetails userDetails) {
        councilService.registerAssociation(req, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "희망 제휴 등록 완료", null));
    }
}
