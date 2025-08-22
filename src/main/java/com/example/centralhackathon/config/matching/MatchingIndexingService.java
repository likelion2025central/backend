package com.example.centralhackathon.config.matching;

import com.example.centralhackathon.entity.BossAssociation;
import com.example.centralhackathon.entity.CouncilAssociation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatchingIndexingService {
    private final EmbeddingClient emb;
    private final QdrantClient qd;
    private final ProfileText text;
    @Value("${app.qdrant.bosses}")   private String bossCol;
    @Value("${app.qdrant.councils}") private String councilCol;

    public void indexBoss(BossAssociation b) {
        String doc = text.bossText(b);
        float[] vec = emb.embedOne(doc);
        Map<String,Object> payload = Map.of(
            "industry", n(b.getIndustry()),
            "targetSchool", n(b.getTargetSchool()),
            "boon", n(b.getBoon()),
            "period", n(b.getPeriod()),
            "imgUrl", n(b.getImgUrl())
        );
        qd.upsert(bossCol, b.getId(), vec, payload);
    }

    public void indexCouncil(CouncilAssociation c) {
        String doc = text.councilText(c);
        float[] vec = emb.embedOne(doc);
        Map<String,Object> payload = Map.of(
            "industry", n(c.getIndustry()),
            "targetSchool", n(c.getTargetSchool()),
            "targetCollege", n(c.getTargetCollege()),
            "targetDepartment", n(c.getTargetDepartment()),
            "boon", n(c.getBoon()),
            "period", n(c.getPeriod()),
            "num", c.getNum()==null ? 0 : c.getNum()
        );
        qd.upsert(councilCol, c.getId(), vec, payload);
    }

    private String n(String s){ return s==null? "" : s; }
}
