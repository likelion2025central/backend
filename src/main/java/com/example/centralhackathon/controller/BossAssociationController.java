package com.example.centralhackathon.controller;

import com.example.centralhackathon.config.ApiResponse;
import com.example.centralhackathon.dto.Request.BossAssociationRequest;
import com.example.centralhackathon.dto.Response.BossAssociationResponse;
import com.example.centralhackathon.service.BossAssociationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boss")
public class BossAssociationController {

    private final BossAssociationService bossAssociationService;

    @PostMapping(value = "/association/register", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse> register(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") BossAssociationRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        BossAssociationResponse res =
                bossAssociationService.register(userDetails.getUsername(), request, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "사장님 제휴 등록 완료", res));
    }
}
