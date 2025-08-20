package com.example.centralhackathon.controller;

import com.example.centralhackathon.config.ApiResponse;
import com.example.centralhackathon.dto.Request.BossAssociationRequest;
import com.example.centralhackathon.dto.Response.BossAssociationResponse;
import com.example.centralhackathon.service.BossAssociationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/association/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> register(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute BossAssociationRequest req
    ) throws IOException {

        bossAssociationService.register(userDetails.getUsername(), req, req.getImage());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "사장님 제휴 등록 완료", null));
    }
}
