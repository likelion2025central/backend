package com.example.centralhackathon.config.matching;

import com.example.centralhackathon.entity.BossAssociation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchBossResult {
    private BossAssociation boss;
    private double score;
}