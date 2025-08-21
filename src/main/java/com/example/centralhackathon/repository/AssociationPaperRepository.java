package com.example.centralhackathon.repository;

import com.example.centralhackathon.entity.AssociationCondition;
import com.example.centralhackathon.entity.AssociationPaper;
import com.example.centralhackathon.entity.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;




public interface AssociationPaperRepository extends JpaRepository<AssociationPaper, Long> {
    // 단건: associationId로 가져오기
    AssociationPaper findByAssociation_Id(Long associationId);

    // 학생회 측: 내가 관련된 협약서들 중 status + 누가 작성했는지로 필터
    @EntityGraph(attributePaths = {"association", "association.council", "association.boss"})
    Page<AssociationPaper> findByAssociation_Council_User_UsernameAndAssociation_StatusAndRequester(
            String username,
            AssociationCondition status,
            Role requester,
            Pageable pageable
    );
}