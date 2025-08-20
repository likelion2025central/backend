package com.example.centralhackathon.repository;

import com.example.centralhackathon.entity.BossAssociation;
import com.example.centralhackathon.entity.CouncilAssociation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BossAssociationRepository extends JpaRepository<BossAssociation, Long> {
    Page<BossAssociation> findByUserId(Long userId, Pageable pageable);
}
