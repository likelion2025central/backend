package com.example.centralhackathon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Association extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "council_assoc_id")
    private CouncilAssociation council;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boss_assoc_id")
    private BossAssociation boss;

    private Role requester;
    private Role responder;
    /**
     * WAITING(요청대기), NEGOTIATING(협의중), CONFIRMED(제휴 확정), REJECTED(반려)
     */
    @Enumerated(EnumType.STRING)
    private AssociationCondition status;
}
