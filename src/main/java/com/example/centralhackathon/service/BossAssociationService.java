package com.example.centralhackathon.service;
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

        bossAssociationRepository.save(assoc);
        return toResponse(assoc);
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
        Page<CouncilAssociation>  page = associationRepository
                .findCouncilAssociationsByBossUsernameAndStatusAndResponder(
                        username, AssociationCondition.WAITING, Role.BOSS, pageable);

        return page.map(BossAssociationService::toCouncilResponse);
    }

    @Transactional(readOnly = true)
    public Page<BossRequestManageResponse> getWaitingBossRequestsForCouncil(
            String username, Pageable pageable
    ) {
        Page<CouncilAssociation> page = associationRepository
                .findCouncilAssociationsByBossUsernameAndStatusAndResponder(
                        username, AssociationCondition.WAITING, Role.COUNCIL,pageable);

        return page.map(BossAssociationService::toCouncilResponse);
    }

    private static BossRequestManageResponse toCouncilResponse(CouncilAssociation c) {
        Users user = c.getUser();
        // 2) 보스 전용 필드 필요하면 언프로시 후 캐스팅
        StudentCouncil sc = unproxy(user, StudentCouncil.class);
        BossRequestManageResponse dto = new BossRequestManageResponse();
        dto.setSchoolName(sc.getSchoolName());
        dto.setCollege(sc.getCollege());
        dto.setDepartment(sc.getDepartment());
        dto.setId(c.getId());
        dto.setIndustry(c.getIndustry());
        dto.setBoon(c.getBoon());
        dto.setPeriod(c.getPeriod());
        dto.setNum(c.getNum());
        dto.setSignificant(c.getSignificant());
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
