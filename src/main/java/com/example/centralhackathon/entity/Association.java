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
    private CouncilAssociation boss;
    private Integer stage;
    private Role requester;
    private Role responder;
    private Integer condition; // 0->요청대기 1->협의중 2-> 제휴 확정 3->반려
}
