package com.example.centralhackathon.controller;
import com.example.centralhackathon.dto.Request.MakeAssociationRequest;
import com.example.centralhackathon.dto.Request.UpdateAssociationStatusRequest;
import com.example.centralhackathon.dto.Response.MakeAssociationResponse;
import com.example.centralhackathon.dto.Response.UpdateAssociationStatusResponse;
import com.example.centralhackathon.entity.AssociationCondition;
import com.example.centralhackathon.service.AssociationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/associations")
public class AssociationController {

    private final AssociationService associationService;

    @Operation(
            summary = "제휴 요청",
            description = "requesterType에는 신청하는 측 COUNCIL/BOSS를 넣으면 되고" +
                    "requesterId에는 신청하는 쪽이 작성한 제휴글의 아이디를" +
                    "targetIds에는 제휴 요청할 글들의 아이디 리스트들을 보내시면 됩니다")
    @PostMapping
    public ResponseEntity<MakeAssociationResponse> createAssociations(
            @Valid @RequestBody MakeAssociationRequest req
    ) throws MessagingException {
        int created = associationService.makeAssociation(req.getTargetIds(), req.getRequesterType(), req.getRequesterId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new MakeAssociationResponse(created));
    }
/*
    @PatchMapping("/{id}/status")
    public ResponseEntity<UpdateAssociationStatusResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssociationStatusRequest req
    ) {
        associationService.updateStatus(id, req.getStatus());
        return ResponseEntity.ok(new UpdateAssociationStatusResponse(id, req.getStatus()));
    }*/

    @Operation(
            summary = "제휴 협의중으로 변경",
            description = "id에는 변경할 협의 객체의 id넣으심 됩니다(assocId)")
    @PostMapping("/{id}/negotiate")
    public ResponseEntity<UpdateAssociationStatusResponse> markNegotiating(@PathVariable Long id) {
        associationService.updateStatus(id, AssociationCondition.NEGOTIATING);
        return ResponseEntity.ok(new UpdateAssociationStatusResponse(id, AssociationCondition.NEGOTIATING));
    }
    @Operation(
            summary = "제휴 완료로 변경",
            description = "id에는 변경할 협의 객체의 id넣으심 됩니다(assocId)")
    @PostMapping("/{id}/confirm")
    public ResponseEntity<UpdateAssociationStatusResponse> markConfirmed(@PathVariable Long id) {
        associationService.updateStatus(id, AssociationCondition.CONFIRMED);
        return ResponseEntity.ok(new UpdateAssociationStatusResponse(id, AssociationCondition.CONFIRMED));
    }
    @Operation(
            summary = "제휴 거절로 변경",
            description = "id에는 변경할 협의 객체의 id넣으심 됩니다(assocId)")
    @PostMapping("/{id}/reject")
    public ResponseEntity<UpdateAssociationStatusResponse> markRejected(@PathVariable Long id) {
        associationService.updateStatus(id, AssociationCondition.REJECTED);
        return ResponseEntity.ok(new UpdateAssociationStatusResponse(id, AssociationCondition.REJECTED));
    }
}
