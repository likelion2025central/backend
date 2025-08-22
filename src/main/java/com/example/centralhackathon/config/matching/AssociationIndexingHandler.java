package com.example.centralhackathon.config.matching;

import com.example.centralhackathon.repository.BossAssociationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AssociationIndexingHandler {

    private final MatchingIndexingService indexing;
    private final BossAssociationRepository bossRepo;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBossChanged(BossAssociationChangedEvent e) {
        bossRepo.findById(e.id()).ifPresent(indexing::indexBoss);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCouncilChanged(CouncilAssociationChangedEvent e) {
        bossRepo.findById(e.id()).ifPresent(indexing::indexBoss);
    }
}
