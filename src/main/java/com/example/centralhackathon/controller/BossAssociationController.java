package com.example.centralhackathon.controller;

import com.example.centralhackathon.config.ApiResponse;
import com.example.centralhackathon.dto.Request.BossAssociationRequest;
import com.example.centralhackathon.dto.Request.BossAssociationUpdateRequest;
import com.example.centralhackathon.dto.Request.CouncilAssociationUpdateRequest;
import com.example.centralhackathon.dto.Request.PagePayload;
import com.example.centralhackathon.dto.Response.BossAssociationResponse;
import com.example.centralhackathon.dto.Response.CouncilAssociationResponse;
import com.example.centralhackathon.service.BossAssociationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @Operation(
            summary = "작성한 희망 제휴들 조회",
            description = "{\"page\": 0} 이렇게 그냥 몇페이지 볼건지만 보내면 됩니다"
    )
    @GetMapping("/association")
    public ResponseEntity<ApiResponse> getAssociations(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 3, sort = "createdDate", direction = Sort.Direction.DESC)
            Pageable pageable) {

        var page = bossAssociationService.getBossAssociations(userDetails.getUsername(), pageable);

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
            description = "필드 값 다 채워서 보내주셔야하는데 이미지는 수정 안할경우 그냥 null보내주세요"
    )
    @PutMapping("/association/{id}")
    public ResponseEntity<ApiResponse> updateAssociation(
            @PathVariable("id") Long associationId,
            @ModelAttribute BossAssociationUpdateRequest req,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        BossAssociationResponse updated =
                bossAssociationService.updateAssociation(associationId, req, userDetails.getUsername());

        return ResponseEntity.ok(new ApiResponse(true, "희망 제휴 수정 완료", updated));
    }
}
