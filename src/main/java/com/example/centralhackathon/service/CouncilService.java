package com.example.centralhackathon.service;

import com.example.centralhackathon.dto.Request.CouncilAssociationRequest;
import com.example.centralhackathon.dto.Request.CouncilAssociationUpdateRequest;
import com.example.centralhackathon.dto.Response.BossAssociationResponse;
import com.example.centralhackathon.dto.Response.CouncilAssociationResponse;
import com.example.centralhackathon.dto.Response.CouncilRequestManageResponse;
import com.example.centralhackathon.entity.*;
import com.example.centralhackathon.repository.AssociationRepository;
import com.example.centralhackathon.repository.CouncilAssociationRepository;
import com.example.centralhackathon.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouncilService {
    private final CouncilAssociationRepository councilAssociationRepository;
    private final UserRepository userRepository;
    private final AssociationRepository associationRepository;

    public void registerAssociation(CouncilAssociationRequest req, String username) {
        CouncilAssociation entity = new CouncilAssociation();
        Users user = userRepository.findByUsername(username).orElseThrow(() ->
                new EntityNotFoundException("User not found. id=" + username));
        entity.setUser(user);
        entity.setBoon(req.getBoon());
        entity.setNum(req.getNum());
        entity.setIndustry(req.getIndustry());
        entity.setPeriod(req.getPeriod());
        entity.setTargetSchool(req.getTargetSchool());
        entity.setTargetCollege(req.getTargetCollege());
        entity.setTargetDepartment(req.getTargetDepartment());
        entity.setSignificant(req.getSignificant());
        councilAssociationRepository.save(entity);
    }
    @Transactional
    public Page<CouncilAssociationResponse> getCouncilAssociations(String username, Pageable pageable) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found. username=" + username));

        Page<CouncilAssociation> page = councilAssociationRepository.findByUserId(user.getId(), pageable);

        return page.map(entity -> {
            CouncilAssociationResponse dto = new CouncilAssociationResponse();
            dto.setId(entity.getId());
            dto.setIndustry(entity.getIndustry());
            dto.setBoon(entity.getBoon());
            dto.setNum(entity.getNum());
            dto.setPeriod(entity.getPeriod());
            dto.setTargetSchool(entity.getTargetSchool());
            dto.setTargetCollege(entity.getTargetCollege());
            dto.setTargetDepartment(entity.getTargetDepartment());
            dto.setSignificant(entity.getSignificant());
            return dto;
        });
    }
    @Transactional
    public CouncilAssociationResponse updateAssociation(Long associationId,
                                                        CouncilAssociationUpdateRequest req,
                                                        String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found. username=" + username));

        CouncilAssociation entity = councilAssociationRepository.findById(associationId)
                .orElseThrow(() -> new EntityNotFoundException("Association not found. id=" + associationId));

        if (!entity.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("본인이 등록한 제휴만 수정할 수 있습니다.");
        }

        // 필드 업데이트
        entity.setIndustry(req.getIndustry());
        entity.setBoon(req.getBoon());
        entity.setNum(req.getNum());
        entity.setPeriod(req.getPeriod());
        entity.setTargetSchool(req.getTargetSchool());
        entity.setTargetCollege(req.getTargetCollege());
        entity.setTargetDepartment(req.getTargetDepartment());
        entity.setSignificant(req.getSignificant());

        return toResponse(entity);
    }

    private CouncilAssociationResponse toResponse(CouncilAssociation e) {
        CouncilAssociationResponse dto = new CouncilAssociationResponse();
        dto.setId(e.getId());
        dto.setIndustry(e.getIndustry());
        dto.setBoon(e.getBoon());
        dto.setNum(e.getNum());
        dto.setPeriod(e.getPeriod());
        dto.setTargetSchool(e.getTargetSchool());
        dto.setTargetCollege(e.getTargetCollege());
        dto.setTargetDepartment(e.getTargetDepartment());
        dto.setSignificant(e.getSignificant());
        return dto;
    }

    @Transactional(readOnly = true)
    public Page<CouncilRequestManageResponse> getWaitingBossRequestsForCouncil(
            String username, Pageable pageable
    ) {
        Page<BossAssociation> page = associationRepository
                .findBossAssociationsByCouncilUsernameAndStatusAndResponder(
                        username, AssociationCondition.WAITING, Role.COUNCIL, pageable);

        return page.map(CouncilService::toBossResponse);
    }

    @Transactional(readOnly = true)
    public Page<CouncilRequestManageResponse> getWaitingCouncilRequestsForBoss(
            String username, Pageable pageable
    ) {
        Page<BossAssociation> page = associationRepository
                .findBossAssociationsByCouncilUsernameAndStatusAndResponder(
                        username, AssociationCondition.WAITING, Role.BOSS,pageable);

        return page.map(CouncilService::toBossResponse);
    }

    private static CouncilRequestManageResponse toBossResponse(BossAssociation b) {
        Users user = b.getUser();
        // 2) 보스 전용 필드 필요하면 언프로시 후 캐스팅
        Boss bo = unproxy(user, Boss.class);
        CouncilRequestManageResponse dto = new CouncilRequestManageResponse();
        dto.setStoreName(bo.getStoreName());
        dto.setId(b.getId());
        dto.setIndustry(b.getIndustry());
        dto.setBoon(b.getBoon());
        dto.setPeriod(b.getPeriod());
        dto.setNum(b.getNum());
        dto.setTargetSchool(b.getTargetSchool());
        dto.setSignificant(b.getSignificant());
        dto.setImgUrl(b.getImgUrl());
        return dto;
    }
    @SuppressWarnings("unchecked")
    private static <T> T unproxy(Object entity, Class<T> targetType) {
        Object impl = (entity instanceof HibernateProxy)
                ? ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation()
                : entity;
        return (T) impl; // 실제 구현체로 반환
    }
}
