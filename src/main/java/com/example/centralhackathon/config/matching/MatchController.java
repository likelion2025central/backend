package com.example.centralhackathon.config.matching;

import com.example.centralhackathon.repository.BossAssociationRepository;
import com.example.centralhackathon.repository.CouncilAssociationRepository;
import com.example.centralhackathon.service.BossAssociationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchingService matching;
    private final MatchingIndexingService indexing;
    private final BossAssociationRepository bossRepo;
    private final CouncilAssociationRepository councilRepo;
    private final BossAssociationService bossService;

    // 학생회 1건 → 사장님 다수
    @Operation(
            summary = "학생회가 AI추천 사장님글들 보기",
            description = "학생회쪽 제휴 아이디를 넣으시면 AI가 추천한 사장님작성 제휴 리스트가 뜹니다. topK는 상위 몇개 가져올건지 넣으심 돼요" )
    @GetMapping("/council/{id}")
    public List<Map<String,Object>> matchFromCouncil(@PathVariable Long id,
                                                     @RequestParam(defaultValue="10") int topK) {
        return matching.matchFromCouncil(id, topK).stream()
                .map(r -> {
                    Map<String,Object> m = new java.util.LinkedHashMap<>();
                    m.put("bossAssocId",        r.getBoss().getId());
                    m.put("storeImg", r.getBoss().getImgUrl());
                    m.put("storeName", bossService.getStoreName(r.getBoss().getUser().getId()));
                    m.put("industry",      r.getBoss().getIndustry());
                    m.put("boon",          r.getBoss().getBoon());
                    m.put("targetSchool",  r.getBoss().getTargetSchool());
                    m.put("suitability",  toSuitability(r.getScore()));
                    return m;
                })
                .toList();
    }

    // 사장님 1건 → 학생회 다수
    @Operation(
            summary = "사장님이 AI추천 학생회글들 보기",
            description = "사장님쪽 제휴 아이디를 넣으시면 AI가 추천한 학생회작성 제휴 리스트가 뜹니다. topK는 상위 몇개 가져올건지 넣으심 돼요" )
    @GetMapping("/boss/{id}")
    public List<Map<String,Object>> matchFromBoss(@PathVariable Long id,
                                                  @RequestParam(defaultValue="10") int topK) {
        return matching.matchFromBoss(id, topK).stream()
                .map(r -> {
                    Map<String,Object> m = new java.util.LinkedHashMap<>();
                    m.put("councilAssocId",       r.getCouncil().getId());
                    m.put("industry",        r.getCouncil().getIndustry());
                    m.put("boon",            r.getCouncil().getBoon());
                    m.put("targetSchool",    r.getCouncil().getTargetSchool());
                    m.put("targetCollege",   r.getCouncil().getTargetCollege());
                    m.put("targetDepartment",r.getCouncil().getTargetDepartment());
                    m.put("num",             r.getCouncil().getNum());
                    m.put("suitability",  toSuitability(r.getScore()));
                    return m;
                })
                .toList();
    }
    private static double toSuitability(Object rawScore) {
        double s = ((Number) rawScore).doubleValue();
        // [-1,1] → [0,1] 변환 + 안전 클램프
        double sim = (s + 1.0) / 2.0;
        return Math.max(0.0, Math.min(1.0, sim));
    }


    // ===== 재인덱싱 유틸 =====
    @Operation(
            summary = "신경 X",
            description = "사장님 작성 제휴아이디 넣으면 벡터 DB에 임베딩. 쓸일 없음 백엔드 테스트용" )
    @PostMapping("/reindex/boss/{id}")
    public String reindexBoss(@PathVariable Long id) {
        var b = bossRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Boss not found"));
        indexing.indexBoss(b);
        return "ok";
    }
    @Operation(
            summary = "신경 X",
            description = "학생회 작성 제휴아이디 넣으면 벡터 DB에 임베딩. 쓸일 없음 백엔드 테스트용" )
    @PostMapping("/reindex/council/{id}")
    public String reindexCouncil(@PathVariable Long id) {
        var c = councilRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Council not found"));
        indexing.indexCouncil(c);
        return "ok";
    }
}
