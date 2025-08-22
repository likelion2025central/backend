package com.example.centralhackathon.config.matching;

import com.example.centralhackathon.entity.BossAssociation;
import com.example.centralhackathon.entity.CouncilAssociation;
import com.example.centralhackathon.repository.BossAssociationRepository;
import com.example.centralhackathon.repository.CouncilAssociationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final EmbeddingClient emb;
    private final QdrantClient qd;
    private final ProfileText text;
    private final BossAssociationRepository bossRepo;
    private final CouncilAssociationRepository councilRepo;
    @Value("${app.qdrant.bosses}")   private String bossCol;
    @Value("${app.qdrant.councils}") private String councilCol;

    /** 학생회 1건 -> 사장님 다수 */
    public List<MatchBossResult> matchFromCouncil(Long councilId, int topN) {
        CouncilAssociation c = councilRepo.findById(councilId)
            .orElseThrow(() -> new EntityNotFoundException("Council not found: "+councilId));
        String qText = text.councilText(c);
        float[] qVec = emb.embedOne(qText);

        Map<String,Object> filter = null;
        if (notBlank(c.getIndustry())) {
            filter = Map.of("must", List.of(
                Map.of("key","industry", "match", Map.of("text", c.getIndustry()))
            ));
        }

        // 1차: Boss 컬렉션에서 검색
        List<Map<String,Object>> hits = qd.search(bossCol, qVec, 50, filter);
        List<Long> bossIds = extractIds(hits);
        if (bossIds.isEmpty()) return List.of();

        Map<Long, BossAssociation> byId = bossRepo.findAllById(bossIds).stream()
            .collect(Collectors.toMap(BossAssociation::getId, b->b));
        List<String> candTexts = bossIds.stream().map(id -> text.bossText(byId.get(id))).toList();

        List<Double> scores = emb.rerank(qText, candTexts);
        applySimpleBonusForCouncilQuery(scores, bossIds, byId, c);

        return rankTopBosses(bossIds, byId, scores, topN);
    }

    /** 사장님 1건 -> 학생회 다수 */
    public List<MatchCouncilResult> matchFromBoss(Long bossId, int topN) {
        BossAssociation b = bossRepo.findById(bossId)
            .orElseThrow(() -> new EntityNotFoundException("Boss not found: "+bossId));
        String qText = text.bossText(b);
        float[] qVec = emb.embedOne(qText);

        Map<String,Object> filter = null;
        if (notBlank(b.getIndustry())) {
            filter = Map.of("must", List.of(
                Map.of("key","industry", "match", Map.of("text", b.getIndustry()))
            ));
        }

        // 1차: Council 컬렉션에서 검색
        List<Map<String,Object>> hits = qd.search(councilCol, qVec, 50, filter);
        List<Long> councilIds = extractIds(hits);
        if (councilIds.isEmpty()) return List.of();

        Map<Long,CouncilAssociation> byId = councilRepo.findAllById(councilIds).stream()
            .collect(Collectors.toMap(CouncilAssociation::getId, c->c));
        List<String> candTexts = councilIds.stream().map(id -> text.councilText(byId.get(id))).toList();

        List<Double> scores = emb.rerank(qText, candTexts);
        applySimpleBonusForBossQuery(scores, councilIds, byId, b);

        return rankTopCouncils(councilIds, byId, scores, topN);
    }

    // ===== 공통 유틸 =====
    private List<Long> extractIds(List<Map<String,Object>> hits) {
        return hits.stream().map(h -> {
            Map payload = (Map) h.get("payload");
            Number nid = (Number) payload.get("id");
            return nid.longValue();
        }).toList();
    }
    private boolean notBlank(String s){ return s!=null && !s.isBlank(); }

    /** 학생회 쿼리 관점 보정치 */
    private void applySimpleBonusForCouncilQuery(List<Double> scores, List<Long> ids,
                                                 Map<Long,BossAssociation> byId, CouncilAssociation c) {
        for (int i=0;i<ids.size();i++) {
            BossAssociation b = byId.get(ids.get(i));
            double bonus = 0.0;
            if (notBlank(c.getTargetSchool()) && notBlank(b.getTargetSchool())) {
                if (!b.getTargetSchool().contains(c.getTargetSchool())) bonus -= 0.15;
            }
            if (notBlank(c.getIndustry()) && notBlank(b.getIndustry())
                && b.getIndustry().equalsIgnoreCase(c.getIndustry())) bonus += 0.05;
            scores.set(i, scores.get(i) + bonus);
        }
    }

    /** 사장님 쿼리 관점 보정치 */
    private void applySimpleBonusForBossQuery(List<Double> scores, List<Long> ids,
                                              Map<Long,CouncilAssociation> byId, BossAssociation b) {
        for (int i=0;i<ids.size();i++) {
            CouncilAssociation c = byId.get(ids.get(i));
            double bonus = 0.0;
            // 업종 정확 일치 가점
            if (notBlank(b.getIndustry()) && notBlank(c.getIndustry())
                && b.getIndustry().equalsIgnoreCase(c.getIndustry())) bonus += 0.05;
            // 학교 타겟 맞추기 (사장님 글에 명시된 학교가 있으면 그쪽을 우선)
            if (notBlank(b.getTargetSchool()) && notBlank(c.getTargetSchool())) {
                if (!c.getTargetSchool().contains(b.getTargetSchool())) bonus -= 0.10;
            }
            // 예상 인원(num) 기준 간단 가점 (사장님이 인원 제한 없다면 생략)
            // 예: 학생회 num이 클수록(=잠재 고객 많음) 소폭 가점
            if (c.getNum()!=null && c.getNum()>0) {
                bonus += Math.min(0.05, c.getNum()/1000.0 * 0.05); // 최대 +0.05
            }
            scores.set(i, scores.get(i) + bonus);
        }
    }

    private List<MatchBossResult> rankTopBosses(List<Long> ids, Map<Long,BossAssociation> byId,
                                                List<Double> scores, int topN){
        return IntStream.range(0, ids.size()).boxed()
            .sorted((i,j) -> Double.compare(scores.get(j), scores.get(i)))
            .limit(topN)
            .map(i -> new MatchBossResult(byId.get(ids.get(i)), scores.get(i)))
            .toList();
    }

    private List<MatchCouncilResult> rankTopCouncils(List<Long> ids, Map<Long,CouncilAssociation> byId,
                                                     List<Double> scores, int topN){
        return IntStream.range(0, ids.size()).boxed()
            .sorted((i,j) -> Double.compare(scores.get(j), scores.get(i)))
            .limit(topN)
            .map(i -> new MatchCouncilResult(byId.get(ids.get(i)), scores.get(i)))
            .toList();
    }
}




