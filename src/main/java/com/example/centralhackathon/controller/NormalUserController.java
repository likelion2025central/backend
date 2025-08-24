package com.example.centralhackathon.controller;

import com.example.centralhackathon.dto.Response.AssocForStudentResponse;
import com.example.centralhackathon.dto.Response.AssociationPaperResponse;
import com.example.centralhackathon.service.AssociationPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/normal")
public class NormalUserController {
    private final AssociationPaperService associationPaperService;

    @GetMapping("/confirmed-active/category")
    public Page<AssocForStudentResponse> getConfirmedActivePapers(
            @RequestParam(required = false) String school,
            @RequestParam(required = false) String college,
            @RequestParam(required = false, name = "department") String dept,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        return associationPaperService.getConfirmedActivePapers(
                school, college, dept, category, page, size
        );
    }

    @GetMapping("/confirmed-active/search")
    public Page<AssocForStudentResponse> getConfirmedActivePapersByStoreName(
            @RequestParam(required = false) String school,
            @RequestParam(required = false) String college,
            @RequestParam(required = false, name = "department") String dept,
            @RequestParam(required = false, name = "keyWord") String keyWord,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        return associationPaperService.getConfirmedActivePapersByStoreName(
                school, college, dept, keyWord, page, size
        );
    }
}
