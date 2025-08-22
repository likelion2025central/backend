package com.example.centralhackathon.config.matching;

import com.example.centralhackathon.entity.CouncilAssociation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchCouncilResult {
    private CouncilAssociation council;
    private double score;
}