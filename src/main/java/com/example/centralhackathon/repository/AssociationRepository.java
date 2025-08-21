package com.example.centralhackathon.repository;

import com.example.centralhackathon.entity.*;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssociationRepository extends JpaRepository<Association, Long> {
    @Query("""
    select a.boss
    from Association a
    where a.council.user.username = :username
      and a.status = :status
      and a.responder = :responder
""")
    Page<BossAssociation> findBossAssociationsByCouncilUsernameAndStatusAndResponder(
            @Param("username") String username,
            @Param("status") AssociationCondition status,
            @Param("responder") Role responder,
            Pageable pageable
    );
    @Query("""
    select a.council
    from Association a
    where a.boss.user.username = :username
      and a.status = :status
      and a.responder = :responder
""")
    Page<CouncilAssociation> findCouncilAssociationsByBossUsernameAndStatusAndResponder(
            @Param("username") String username,
            @Param("status") AssociationCondition status,
            @Param("responder") Role responder,
            Pageable pageable
    );

    // 학생회가 받은 요청 (responder = COUNCIL) 중 상태 필터
    @EntityGraph(attributePaths = {"boss", "boss.user"})
    Page<Association> findByCouncil_User_UsernameAndStatusAndResponder(
            String username,
            AssociationCondition status,
            Role responder,
            Pageable pageable
    );

    // (참고) 사장이 받은 요청 (responder = BOSS) — 필요 시 사용
    @EntityGraph(attributePaths = {"council", "council.user"})
    Page<Association> findByBoss_User_UsernameAndStatusAndResponder(
            String username,
            AssociationCondition status,
            Role responder,
            Pageable pageable
    );
}
