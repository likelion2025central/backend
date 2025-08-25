// AssociationPaperService.java
package com.example.centralhackathon.service;

import com.example.centralhackathon.config.EmailCertificationUtil;
import com.example.centralhackathon.dto.Request.AssociationPaperRequest;
import com.example.centralhackathon.dto.Response.AssocForStudentResponse;
import com.example.centralhackathon.dto.Response.AssociationPaperResponse;
import com.example.centralhackathon.entity.*;
import com.example.centralhackathon.repository.AssociationPaperRepository;
import com.example.centralhackathon.repository.AssociationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.sql.Date;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class AssociationPaperService {

    private final AssociationPaperRepository associationPaperRepository;
    private final AssociationRepository associationRepository;
    private final EmailCertificationUtil emailUtil;

    @Transactional
    public AssociationPaperResponse createPaper(Long associationId, AssociationPaperRequest req) {
        // 1. Association 찾기
        Association assoc = associationRepository.findById(associationId)
                .orElseThrow(() -> new EntityNotFoundException("Association not found. id=" + associationId));

        // 2. Paper 생성
        AssociationPaper paper = new AssociationPaper();
        paper.setAssociation(assoc);
        paper.setCouncilInfo(req.getCouncilInfo());
        paper.setStoreName(req.getStoreName());
        paper.setBoon(req.getBoon());
        paper.setStartDate(req.getStartDate());
        paper.setEndDate(req.getEndDate());
        paper.setTargetSchool(req.getTargetSchool());
        paper.setTargetCollege(req.getTargetCollege());
        paper.setTargetDepartment(req.getTargetDepartment());
        paper.setRequester(req.getRequester());

        // 3. 저장
        AssociationPaper saved = associationPaperRepository.save(paper);

        // 4. Association 상태 변경 -> CONFIRM_WAITING
        assoc.setStatus(AssociationCondition.CONFIRM_WAITING);

        return toResponse(saved);
    }
    // 1) 단건: associationId로 조회
    @Transactional(readOnly = true)
    public AssociationPaperResponse getByAssociationId(Long associationId) {
        AssociationPaper paper = associationPaperRepository.findByAssociation_Id(associationId);
        if (paper == null) {
            throw new EntityNotFoundException("AssociationPaper not found. associationId=" + associationId);
        }
        return toResponse(paper);
    }

    // 학생회 측: CONFIRM_WAITING + requester 필터로 페이징 조회
    @Transactional(readOnly = true)
    public Page<AssociationPaperResponse> getCouncilPapersConfirmWaitingByRequester(
            String username,
            Role requester,                // COUNCIL or BOSS (누가 작성했는지)
            Pageable pageable
    ) {
        Page<AssociationPaper> page = associationPaperRepository
                .findByAssociation_Council_User_UsernameAndAssociation_StatusAndRequester(
                        username,
                        AssociationCondition.CONFIRM_WAITING,
                        requester,
                        pageable
                );
        return page.map(this::toResponse);
    }

    public void sendMail(AssociationPaperResponse req) throws MessagingException {
        String info = "", to = "";
        if(req.getRequester() == Role.COUNCIL){
            Association association = associationRepository.findById(req.getAssociationId()).orElseThrow();
            info = req.getCouncilInfo();
            BossAssociation bossAssoc = association.getBoss();
            Users userBase = bossAssoc.getUser();
            // 1) 역할로 1차 검증 (프록시여도 OK)
            if (userBase.getRole() != Role.BOSS) {
                throw new IllegalStateException("연결된 유저가 사장이 아닙니다. id=" + userBase.getId());
            }
            // 2) 보스 전용 필드 필요하면 언프로시 후 캐스팅
            Boss bo = unproxy(userBase, Boss.class);
            to = bo.getEmail();
        } else if(req.getRequester() == Role.BOSS){
            Association association = associationRepository.findById(req.getAssociationId()).orElseThrow();
            info = req.getStoreName();
            CouncilAssociation councilAssoc = association.getCouncil();
            Users userBase = councilAssoc.getUser();
            if (userBase.getRole() != Role.BOSS) {
                throw new IllegalStateException("연결된 유저가 학생회가 아닙니다. id=" + userBase.getId());
            }
            StudentCouncil sc = unproxy(userBase, StudentCouncil.class);
            to = sc.getEmail();
        }

        String title ="[제휴고리 알림] 최종 제휴 협약서를 확인해주세요";
        String content=
                "[제휴고리 알림] 최종 제휴 협약서를 확인해주세요\n" +
                        "<br><br>"+
                        "["+info+"]" +
                        "<br><br>"+
                        "혜택 | ["+req.getBoon()+"]" +
                        "<br>"+
                        "기간 | ["+req.getStartDate()+ "-" +req.getEndDate()+"]" +
                        "<br>"+
                        "대상 | [" + req.getTargetSchool()+" "+req.getTargetCollege()+" "+req.getTargetDepartment()+"]";

        emailUtil.sendAssocEmail(to, title, content);
    }

    public Page<AssocForStudentResponse> getConfirmedActivePapers(
            String school,
            String college,
            String department,
            String category,
            int page,
            int size
    ) {
        return associationPaperRepository.findConfirmedActiveStudentDtos(
                LocalDate.now(),
                emptyToNull(school), emptyToNull(college), emptyToNull(department), emptyToNull(category),
                PageRequest.of(page, size)
        );
    }

    public Page<AssocForStudentResponse> getConfirmedActivePapersByStoreName(
            String school,
            String college,
            String department,
            String keyWord,
            int page,
            int size
    ) {
        return associationPaperRepository.findConfirmedActiveStudentDtosByStoreName(
                LocalDate.now(),
                emptyToNull(school), emptyToNull(college), emptyToNull(department), emptyToNull(keyWord),
                PageRequest.of(page, size)
        );
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }


    /* ---------- 매퍼 ---------- */
    private AssociationPaperResponse toResponse(AssociationPaper e) {
        AssociationPaperResponse dto = new AssociationPaperResponse();
        dto.setId(e.getId());
        dto.setAssociationId(e.getAssociation().getId());
        dto.setCouncilInfo(e.getCouncilInfo());
        dto.setStoreName(e.getStoreName());
        dto.setBoon(e.getBoon());
        dto.setStartDate(e.getStartDate());
        dto.setEndDate(e.getEndDate());
        dto.setTargetSchool(e.getTargetSchool());
        dto.setTargetCollege(e.getTargetCollege());
        dto.setTargetDepartment(e.getTargetDepartment());
        return dto;
    }
    @SuppressWarnings("unchecked")
    private <T> T unproxy(Object entity, Class<T> targetType) {
        Object impl = (entity instanceof HibernateProxy)
                ? ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation()
                : entity;
        return (T) impl; // 실제 구현체로 반환
    }
    @Transactional(readOnly = true)
    public Page<AssociationPaperResponse> getBossPapersConfirmWaitingByRequester(
            String username,
            Role requester,                // COUNCIL or BOSS (누가 작성했는지)
            Pageable pageable
    ) {
        Page<AssociationPaper> page = associationPaperRepository
                .findByAssociation_Boss_User_UsernameAndAssociation_StatusAndRequester(
                        username,
                        AssociationCondition.CONFIRM_WAITING,
                        requester,
                        pageable
                );
        return page.map(this::toResponse);
    }

}
