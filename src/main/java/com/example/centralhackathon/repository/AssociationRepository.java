package com.example.centralhackathon.repository;

import com.example.centralhackathon.entity.*;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface AssociationRepository extends JpaRepository<Association, Long> {
    // Boss 입장 — 받은(inbox): council -> boss
    @EntityGraph(attributePaths = {"council", "council.user", "boss", "boss.user"})
    Page<Association> findByBoss_User_UsernameAndStatusAndResponder(
            String username,
            AssociationCondition status,
            Role responder,
            Pageable pageable
    );

    // Boss 입장 — 보낸(outbox): boss -> council
    @EntityGraph(attributePaths = {"council", "council.user", "boss", "boss.user"})
    Page<Association> findByBoss_User_UsernameAndStatusAndRequester(
            String username,
            AssociationCondition status,
            Role requester,
            Pageable pageable
    );

    // Council 입장 — 받은(inbox): boss -> council
    @EntityGraph(attributePaths = {"council", "council.user", "boss", "boss.user"})
    Page<Association> findByCouncil_User_UsernameAndStatusAndResponder(
            String username,
            AssociationCondition status,
            Role responder,
            Pageable pageable
    );

    // Council 입장 — 보낸(outbox): council -> boss
    @EntityGraph(attributePaths = {"council", "council.user", "boss", "boss.user"})
    Page<Association> findByCouncil_User_UsernameAndStatusAndRequester(
            String username,
            AssociationCondition status,
            Role requester,
            Pageable pageable
    );

/*
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
    );*/

    // 학생회가 관여한(보낸/받은 무관) + 상태 다중 필터
    @EntityGraph(attributePaths = {"boss", "boss.user"})
    Page<Association> findByCouncil_User_UsernameAndStatus(
            String username,
           AssociationCondition status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"council", "council.user"})
    Page<Association> findByBoss_User_UsernameAndStatus(
            String username,
            AssociationCondition status,
            Pageable pageable
    );
}
