package com.example.centralhackathon.config.matching;

import com.example.centralhackathon.entity.BossAssociation;
import com.example.centralhackathon.entity.CouncilAssociation;
import org.springframework.stereotype.Component;

@Component
public class ProfileText {
    public String councilText(CouncilAssociation c) {
        return String.join(" ",
            "[요청]",
            "업종:", nz(c.getIndustry()),
            "요구 혜택:", nz(c.getBoon()),
            "기간:", nz(c.getPeriod()),
            "예상 인원:", c.getNum()==null? "미기재" : c.getNum()+"명",
            "대상:", String.join("/", nz(c.getTargetSchool()), nz(c.getTargetCollege()), nz(c.getTargetDepartment())),
            "기타:", nz(c.getSignificant())
        ).replaceAll("\\s+", " ").trim();
    }
    public String bossText(BossAssociation b) {
        return String.join(" ",
            "[제공]",
            "업종:", nz(b.getIndustry()),
            "제공 혜택:", nz(b.getBoon()),
            "제휴 기간:", nz(b.getPeriod()),
            "대상 학교:", nz(b.getTargetSchool()),
            "기타:", nz(b.getSignificant())
        ).replaceAll("\\s+", " ").trim();
    }
    private String nz(String s){ return (s==null||s.isBlank())? "미기재" : s.trim(); }
}
