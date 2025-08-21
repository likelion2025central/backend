package com.example.centralhackathon.controller;
import com.example.centralhackathon.dto.Request.MakeAssociationRequest;
import com.example.centralhackathon.dto.Request.UpdateAssociationStatusRequest;
import com.example.centralhackathon.dto.Response.MakeAssociationResponse;
import com.example.centralhackathon.dto.Response.UpdateAssociationStatusResponse;
import com.example.centralhackathon.entity.AssociationCondition;
import com.example.centralhackathon.service.AssociationService;
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

    @PostMapping
    public ResponseEntity<MakeAssociationResponse> createAssociations(
            @Valid @RequestBody MakeAssociationRequest req,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws MessagingException {
        int created = associationService.makeAssociation(req.getTargetIds(), req.getRequesterType(), req.getRequesterId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new MakeAssociationResponse(created));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UpdateAssociationStatusResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssociationStatusRequest req
    ) {
        associationService.updateStatus(id, req.getStatus());
        return ResponseEntity.ok(new UpdateAssociationStatusResponse(id, req.getStatus()));
    }

    // (선택) 단축 엔드포인트들
    @PostMapping("/{id}/negotiate")
    public ResponseEntity<UpdateAssociationStatusResponse> markNegotiating(@PathVariable Long id) {
        associationService.updateStatus(id, AssociationCondition.NEGOTIATING);
        return ResponseEntity.ok(new UpdateAssociationStatusResponse(id, AssociationCondition.NEGOTIATING));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<UpdateAssociationStatusResponse> markConfirmed(@PathVariable Long id) {
        associationService.updateStatus(id, AssociationCondition.CONFIRMED);
        return ResponseEntity.ok(new UpdateAssociationStatusResponse(id, AssociationCondition.CONFIRMED));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<UpdateAssociationStatusResponse> markRejected(@PathVariable Long id) {
        associationService.updateStatus(id, AssociationCondition.REJECTED);
        return ResponseEntity.ok(new UpdateAssociationStatusResponse(id, AssociationCondition.REJECTED));
    }
}
