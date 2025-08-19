package com.example.centralhackathon.service;

import com.example.centralhackathon.dto.Request.CouncilAssociationRequest;
import com.example.centralhackathon.entity.CouncilAssociation;
import com.example.centralhackathon.entity.Users;
import com.example.centralhackathon.repository.CouncilAssociationRepository;
import com.example.centralhackathon.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouncilService {
    private final CouncilAssociationRepository councilAssociationRepository;
    private final UserRepository userRepository;

    public void registerAssociation(CouncilAssociationRequest req, String username) {
        CouncilAssociation entity = new CouncilAssociation();
        Users user = userRepository.findByUsername(username).orElseThrow(() ->
                new EntityNotFoundException("User not found. id=" + username));
        entity.setUser(user);
        entity.setBoon(req.getBoon());
        entity.setIndustry(req.getIndustry());
        entity.setPeriod(req.getPeriod());
        entity.setTargetSchool(req.getTargetSchool());
        entity.setTargetCollege(req.getTargetCollege());
        entity.setTargetDepartment(req.getTargetDepartment());
        entity.setSignificant(req.getSignificant());
        councilAssociationRepository.save(entity);
    }
}
