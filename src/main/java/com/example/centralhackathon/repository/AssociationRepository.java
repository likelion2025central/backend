package com.example.centralhackathon.repository;

import com.example.centralhackathon.entity.*;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
}
