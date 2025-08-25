// AssociationPaperController.java
package com.example.centralhackathon.controller;

import com.example.centralhackathon.config.ApiResponse;
import com.example.centralhackathon.dto.Request.AssociationPaperRequest;
import com.example.centralhackathon.dto.Response.AssociationPaperResponse;
import com.example.centralhackathon.entity.AssociationCondition;
import com.example.centralhackathon.entity.Role;
import com.example.centralhackathon.service.AssociationPaperService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "제휴 협약서 작성",
            description = "id에는 제휴 협약서를 작성할 제휴 객체의 id넣으심 됩니다(assocId)" +
                    "requester에는 신청하는 측 COUNCIL/BOSS를 넣으면 됩니다")
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
    @Operation(
            summary = "제휴 협약서 하나 조회",
            description = "id에는 제휴 협약서를 조회할 제휴 객체의 id넣으심 됩니다(assocId)")
    @GetMapping("/by-association/{associationId}")
    public ResponseEntity<AssociationPaperResponse> getByAssociationId(
            @PathVariable Long associationId
    ) {
        return ResponseEntity.ok(associationPaperService.getByAssociationId(associationId));
    }

    @Operation(
            summary = "보낸/받은 제휴 협약서 정보들",
            description = " 내가 학생회인데 받은거 보고싶다 -> requester는 BOSS / 보낸거 보고싶다 -> requester는 COUNCIL" +
                    "{\"page\": 0} 이렇게 그냥 몇페이지 볼건지만 보내면 됩니다")
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
    @Operation(
            summary = "제휴 협약서 작성 후 메일보내기",
            description = "requester에는 쓰는쪽이 학생회면 COUNCIL, 사장이면 BOSS")
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
    @Operation(
            summary = "사장님이 보낸/받은 제휴 협약서 정보들",
            description = " 내가 사장님인데 받은거 보고싶다 -> requester는 COUNCIL / 보낸거 보고싶다 -> requester는 BOSS" +
                    " {\"page\": 0} 이렇게 몇 페이지 볼건지만 보내면 됩니다"
    )
    @GetMapping("/boss/confirm-waiting")
    public ResponseEntity<Page<AssociationPaperResponse>> getBossConfirmWaitingByRequester(
            @AuthenticationPrincipal(expression = "username") String username,
            @RequestParam(name = "requester") Role requester, // COUNCIL / BOSS
            @PageableDefault(size = 2, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                associationPaperService.getBossPapersConfirmWaitingByRequester(username, requester, pageable)
        );
    }


}
