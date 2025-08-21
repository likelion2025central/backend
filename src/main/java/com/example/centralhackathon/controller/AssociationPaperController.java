// AssociationPaperController.java
package com.example.centralhackathon.controller;

import com.example.centralhackathon.config.ApiResponse;
import com.example.centralhackathon.dto.Request.AssociationPaperRequest;
import com.example.centralhackathon.dto.Response.AssociationPaperResponse;
import com.example.centralhackathon.entity.AssociationCondition;
import com.example.centralhackathon.entity.Role;
import com.example.centralhackathon.service.AssociationPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/associations/papers")
@RequiredArgsConstructor
public class AssociationPaperController {

    private final AssociationPaperService associationPaperService;

    @PostMapping("/{associationId}")
    public ResponseEntity<AssociationPaperResponse> createPaper(
            @PathVariable Long associationId,
            @RequestParam Role requester,
            @RequestBody AssociationPaperRequest req
    ) {
        req.setRequester(requester);
        AssociationPaperResponse response = associationPaperService.createPaper(associationId, req);
        return ResponseEntity.ok(response);
    }
    // 1) 단건 조회: associationId로
    @GetMapping("/by-association/{associationId}")
    public ResponseEntity<AssociationPaperResponse> getByAssociationId(
            @PathVariable Long associationId
    ) {
        return ResponseEntity.ok(associationPaperService.getByAssociationId(associationId));
    }

    @GetMapping("/council/confirm-waiting")
    public ResponseEntity<Page<AssociationPaperResponse>> getCouncilConfirmWaitingByRequester(
            @AuthenticationPrincipal(expression = "username") String username,
            @RequestParam(name = "requester") Role requester, // COUNCIL / BOSS
            @PageableDefault(size = 2, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                associationPaperService.getCouncilPapersConfirmWaitingByRequester(username, requester, pageable)
        );
    }
    @PostMapping("/send-confirm-mail")
    public ResponseEntity<?> sendConfirmMail(
            @RequestBody AssociationPaperResponse req
    ){
    try{
        associationPaperService.sendMail(req);
        return ResponseEntity.ok(new ApiResponse(true, "제휴 확정 이메일 전송 완료", null));
    } catch (MessagingException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "인증 메일 전송 중 오류가 발생했습니다. 다시 시도해주세요.", null));
    }
    }


}
