package com.example.centralhackathon.service;
import com.example.centralhackathon.config.matching.BossAssociationChangedEvent;
import com.example.centralhackathon.dto.Request.BossAssociationRequest;
import com.example.centralhackathon.dto.Request.BossAssociationUpdateRequest;
import com.example.centralhackathon.dto.Request.CouncilAssociationUpdateRequest;
import com.example.centralhackathon.dto.Response.BossAssociationResponse;
import com.example.centralhackathon.dto.Response.BossRequestManageResponse;
import com.example.centralhackathon.dto.Response.CouncilAssociationResponse;
import com.example.centralhackathon.dto.Response.CouncilRequestManageResponse;
import com.example.centralhackathon.entity.*;
import com.example.centralhackathon.repository.AssociationRepository;
import com.example.centralhackathon.repository.BossAssociationRepository;
import com.example.centralhackathon.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BossAssociationService {

    private final BossAssociationRepository bossAssociationRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final AssociationRepository associationRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public BossAssociationResponse register(String username,
                                            BossAssociationRequest req,
                                            MultipartFile image) throws IOException {

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found. username=" + username));

        BossAssociation assoc = new BossAssociation();
        assoc.setUser(user);
        assoc.setIndustry(req.getIndustry());
        assoc.setBoon(req.getBoon());
        assoc.setPeriod(req.getPeriod());
        assoc.setNum(req.getNum());
        assoc.setTargetSchool(req.getTargetSchool());
        assoc.setSignificant(req.getSignificant());

        // 이미지(선택) 업로드
        if (image != null && !image.isEmpty()) {
            System.out.println(image.getOriginalFilename());
            String url = s3Service.upload(image, "boss-associations");
            assoc.setImgUrl(url);
        }

        BossAssociation saved = bossAssociationRepository.save(assoc);

        publisher.publishEvent(new BossAssociationChangedEvent(saved.getId()));

        return toResponse(saved);
    }

    private BossAssociationResponse toResponse(BossAssociation e) {
        BossAssociationResponse dto = new BossAssociationResponse();
        dto.setId(e.getId());
        dto.setIndustry(e.getIndustry());
        dto.setBoon(e.getBoon());
        dto.setPeriod(e.getPeriod());
        dto.setNum(e.getNum());
        dto.setTargetSchool(e.getTargetSchool());
        dto.setSignificant(e.getSignificant());
        dto.setImgUrl(e.getImgUrl());
        return dto;
    }

    public Page<BossAssociationResponse> getBossAssociations(String username, Pageable pageable) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found. username=" + username));

        Page<BossAssociation> page = bossAssociationRepository.findByUserId(user.getId(), pageable);

        return page.map(entity -> {
            BossAssociationResponse dto = new BossAssociationResponse();
            dto.setId(entity.getId());
            dto.setIndustry(entity.getIndustry());
            dto.setBoon(entity.getBoon());
            dto.setNum(entity.getNum());
            dto.setPeriod(entity.getPeriod());
            dto.setTargetSchool(entity.getTargetSchool());
            dto.setSignificant(entity.getSignificant());
            dto.setImgUrl(entity.getImgUrl());
            return dto;
        });
    }
    @Transactional
    public BossAssociationResponse updateAssociation(Long associationId,
                                                     BossAssociationUpdateRequest req,
                                                     String username) throws IOException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found. username=" + username));

        BossAssociation entity = bossAssociationRepository.findById(associationId)
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
        entity.setSignificant(req.getSignificant());

        if (req.getImage() != null && !req.getImage().isEmpty()) {
            String newUrl = s3Service.upload(req.getImage(), "boss-association");
            entity.setImgUrl(newUrl);
        }

        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<BossRequestManageResponse> getWaitingCouncilRequestsForBoss(
            String username, Pageable pageable
    ) {
        // 사장이 받은 '대기중' 요청 → responder = BOSS
        Page<Association> page = associationRepository
                .findByBoss_User_UsernameAndStatusAndResponder(
                        username, AssociationCondition.WAITING, Role.BOSS, pageable);

        return page.map(BossAssociationService::toBossReceivedFromCouncilDto);
    }

    @Transactional(readOnly = true)
    public Page<BossRequestManageResponse> getWaitingBossRequestsForCouncil(
            String username, Pageable pageable
    ) {
        // 사장이 보낸 '대기중' 요청 → responder = COUNCIL
        Page<Association> page = associationRepository
                .findByBoss_User_UsernameAndStatusAndResponder(
                        username, AssociationCondition.WAITING, Role.COUNCIL, pageable);

        return page.map(BossAssociationService::toBossSentToCouncilDto);
    }

    @Transactional(readOnly = true)
    public Page<BossRequestManageResponse> getCouncilAssociationsByStatusForBoss(
            String username,
            AssociationCondition status,
            Pageable pageable
    ) {
        Page<Association> page = associationRepository
                .findByBoss_User_UsernameAndStatus(username, status, pageable);

        return page.map(BossAssociationService::toBossSentToCouncilDto);
    }
    public String getStoreName(Long userId){
        return userRepository.findBossStoreNameByUserId(userId);
    }

    // 공용 매퍼: Association → CouncilAssociation 중심으로 DTO 생성
    private static BossRequestManageResponse toBossReceivedFromCouncilDto(Association a) {
        CouncilAssociation c = a.getCouncil();
        Users u = c.getUser();
        // StudentCouncil 전용 필드 접근 (필요 시만 언프로시)
        StudentCouncil sc = unproxy(u, StudentCouncil.class);

        BossRequestManageResponse dto = new BossRequestManageResponse();
        dto.setAssociationId(a.getId());    // ★ Association PK
        dto.setCouncilAssocId(c.getId());               // CouncilAssociation ID
        dto.setSchoolName(sc.getSchoolName());
        dto.setCollege(sc.getCollege());
        dto.setDepartment(sc.getDepartment());
        dto.setIndustry(c.getIndustry());
        dto.setBoon(c.getBoon());
        dto.setPeriod(c.getPeriod());
        dto.setNum(c.getNum());
        dto.setSignificant(c.getSignificant());
        return dto;
    }

    // 보낸/받은이 동일 포맷이면 위 매퍼 재사용 가능. 분리해두면 커스터마이징이 쉬움.
    private static BossRequestManageResponse toBossSentToCouncilDto(Association a) {
        return toBossReceivedFromCouncilDto(a);
    }


    @SuppressWarnings("unchecked")
    private static <T> T unproxy(Object entity, Class<T> targetType) {
        Object impl = (entity instanceof HibernateProxy)
                ? ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation()
                : entity;
        return (T) impl;
    }}
