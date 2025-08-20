package com.example.centralhackathon.service;
import com.example.centralhackathon.dto.Request.BossAssociationRequest;
import com.example.centralhackathon.dto.Response.BossAssociationResponse;
import com.example.centralhackathon.entity.BossAssociation;
import com.example.centralhackathon.entity.Users;
import com.example.centralhackathon.repository.BossAssociationRepository;
import com.example.centralhackathon.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
}
