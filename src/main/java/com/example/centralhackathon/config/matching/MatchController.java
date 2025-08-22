package com.example.centralhackathon.config.matching;

import com.example.centralhackathon.repository.BossAssociationRepository;
import com.example.centralhackathon.repository.CouncilAssociationRepository;
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

    // 학생회 1건 → 사장님 다수
    @GetMapping("/council/{id}")
    public List<Map<String,Object>> matchFromCouncil(@PathVariable Long id,
                                                     @RequestParam(defaultValue="10") int topK) {
        return matching.matchFromCouncil(id, topK).stream()
                .map(r -> {
                    Map<String,Object> m = new java.util.LinkedHashMap<>();
                    m.put("bossId",        r.getBoss().getId());
                    m.put("industry",      r.getBoss().getIndustry());
                    m.put("boon",          r.getBoss().getBoon());
                    m.put("targetSchool",  r.getBoss().getTargetSchool());
                    m.put("score",         r.getScore());
                    return m;
                })
                .toList();
    }

    // 사장님 1건 → 학생회 다수
    @GetMapping("/boss/{id}")
    public List<Map<String,Object>> matchFromBoss(@PathVariable Long id,
                                                  @RequestParam(defaultValue="10") int topK) {
        return matching.matchFromBoss(id, topK).stream()
                .map(r -> {
                    Map<String,Object> m = new java.util.LinkedHashMap<>();
                    m.put("councilId",       r.getCouncil().getId());
                    m.put("industry",        r.getCouncil().getIndustry());
                    m.put("boon",            r.getCouncil().getBoon());
                    m.put("targetSchool",    r.getCouncil().getTargetSchool());
                    m.put("targetCollege",   r.getCouncil().getTargetCollege());
                    m.put("targetDepartment",r.getCouncil().getTargetDepartment());
                    m.put("num",             r.getCouncil().getNum());
                    m.put("score",           r.getScore());
                    return m;
                })
                .toList();
    }


    // ===== 재인덱싱 유틸 =====
    @PostMapping("/reindex/boss/{id}")
    public String reindexBoss(@PathVariable Long id) {
        var b = bossRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Boss not found"));
        indexing.indexBoss(b);
        return "ok";
    }

    @PostMapping("/reindex/council/{id}")
    public String reindexCouncil(@PathVariable Long id) {
        var c = councilRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Council not found"));
        indexing.indexCouncil(c);
        return "ok";
    }
}
