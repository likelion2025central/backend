package com.example.centralhackathon.repository;

import com.example.centralhackathon.dto.Response.CouncilAssociationResponse;
import com.example.centralhackathon.entity.CouncilAssociation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouncilAssociationRepository extends JpaRepository<CouncilAssociation, Long> {
    List<CouncilAssociation> findAllByUserId(Long userId);
    Page<CouncilAssociation> findByUserId(Long userId, Pageable pageable);
}
