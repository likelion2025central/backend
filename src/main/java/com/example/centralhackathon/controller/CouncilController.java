package com.example.centralhackathon.controller;

import com.example.centralhackathon.config.ApiResponse;
import com.example.centralhackathon.dto.Request.BossSignUp;
import com.example.centralhackathon.dto.Request.CouncilAssociationRequest;
import com.example.centralhackathon.dto.Request.PagePayload;
import com.example.centralhackathon.dto.Response.CouncilAssociationResponse;
import com.example.centralhackathon.service.CouncilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/council")
public class CouncilController {
    private final CouncilService councilService;
    @PostMapping("/association/register")
    public ResponseEntity<ApiResponse> register(@RequestBody CouncilAssociationRequest req, @AuthenticationPrincipal UserDetails userDetails) {
        councilService.registerAssociation(req, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "희망 제휴 등록 완료", null));
    }

    @GetMapping("/association")
    public ResponseEntity<ApiResponse> getAssociations(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 3, sort = "createdDate", direction = Sort.Direction.DESC)
            Pageable pageable) {

        var page = councilService.getCouncilAssociations(userDetails.getUsername(), pageable);

        var body = new PagePayload<>(page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious());

        return ResponseEntity.ok(new ApiResponse(true, "희망 제휴 조회 완료", body));
    }
}
