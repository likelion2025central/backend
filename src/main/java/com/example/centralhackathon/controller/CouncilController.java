package com.example.centralhackathon.controller;

import com.example.centralhackathon.config.ApiResponse;
import com.example.centralhackathon.dto.Request.BossSignUp;
import com.example.centralhackathon.dto.Request.CouncilAssociationRequest;
import com.example.centralhackathon.dto.Request.CouncilAssociationUpdateRequest;
import com.example.centralhackathon.dto.Request.PagePayload;
import com.example.centralhackathon.dto.Response.BossAssociationResponse;
import com.example.centralhackathon.dto.Response.CouncilAssociationResponse;
import com.example.centralhackathon.dto.Response.CouncilRequestManageResponse;
import com.example.centralhackathon.service.CouncilService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/council")
public class CouncilController {
    private final CouncilService councilService;
    @Operation(
            summary = "작성한 희망 제휴 작성"
    )
    @PostMapping("/association/register")
    public ResponseEntity<ApiResponse> register(@RequestBody CouncilAssociationRequest req, @AuthenticationPrincipal UserDetails userDetails) {
        councilService.registerAssociation(req, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "희망 제휴 등록 완료", null));
    }
    @Operation(
            summary = "작성한 희망 제휴들 조회",
            description = "{\"page\": 0} 이렇게 그냥 몇페이지 볼건지만 보내면 됩니다"
    )
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
    @Operation(
            summary = "작성한 희망 제휴 수정",
            description = "필드 값 다 채워서 보내주셔야해요"
    )
    @PutMapping("/association/{id}")
    public ResponseEntity<ApiResponse> updateAssociation(
            @PathVariable("id") Long associationId,
            @RequestBody CouncilAssociationUpdateRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        CouncilAssociationResponse updated =
                councilService.updateAssociation(associationId, req, userDetails.getUsername());

        return ResponseEntity.ok(new ApiResponse(true, "희망 제휴 수정 완료", updated));
    }

    @GetMapping("/received/waiting")
    public ResponseEntity<Page<CouncilRequestManageResponse>> getReceivedWaiting(
            @AuthenticationPrincipal(expression = "username") String username,
            @PageableDefault(size = 2, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CouncilRequestManageResponse> result =
                councilService.getWaitingBossRequestsForCouncil(username, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sent/waiting")
    public ResponseEntity<Page<CouncilRequestManageResponse>> getSendWaiting(
            @AuthenticationPrincipal(expression = "username") String username,
            @PageableDefault(size = 2, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CouncilRequestManageResponse> result =
                councilService.getWaitingCouncilRequestsForBoss(username, pageable);
        return ResponseEntity.ok(result);
    }

}
