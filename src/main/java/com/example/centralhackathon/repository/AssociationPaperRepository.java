package com.example.centralhackathon.repository;

import com.example.centralhackathon.entity.AssociationCondition;
import com.example.centralhackathon.entity.AssociationPaper;
import com.example.centralhackathon.entity.Role;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;


public interface AssociationPaperRepository extends JpaRepository<AssociationPaper, Long> {
    // 단건: associationId로 가져오기
    AssociationPaper findByAssociation_Id(Long associationId);

    // 학생회 측: 내가 관련된 협약서들 중 status + 누가 작성했는지로 필터
    @EntityGraph(attributePaths = {"association", "association.council", "association.boss"})
    Page<AssociationPaper> findByAssociation_Council_User_UsernameAndAssociation_StatusAndRequester(
            String username,
            AssociationCondition status,
            Role requester,
            Pageable pageable
    );

    @Query("""
    select ap
    from AssociationPaper ap
    join ap.association a
    join a.boss b
    where ap.endDate > :today
      and a.status = com.example.centralhackathon.entity.AssociationCondition.CONFIRMED
      and (:school  is null or ap.targetSchool = :school)
      and (:college is null or ap.targetCollege = :college)
      and (:department is null or ap.targetDepartment = :department)
      and (:category is null or b.industry = :category)
    order by ap.endDate asc
""")
    Page<AssociationPaper> findConfirmedActivePapers(
            @Param("today") LocalDate today,
            @Param("school") String school,
            @Param("college") String college,
            @Param("department") String department, // <- 여기와 쿼리의 :department 일치
            @Param("category") String category,
            Pageable pageable
    );

    @Query("""
        select ap
        from AssociationPaper ap
        join ap.association a
        join a.boss b
        where ap.endDate > :today
          and a.status = com.example.centralhackathon.entity.AssociationCondition.CONFIRMED
          and (:school   is null or ap.targetSchool = :school)
          and (:college  is null or ap.targetCollege = :college)
          and (:department is null or ap.targetDepartment = :department)
          and (:keyWord  is null or lower(ap.storeName) = lower(:keyWord))
        order by ap.endDate asc
    """)
    Page<AssociationPaper> findConfirmedActivePapersByStoreName(
            @Param("today") LocalDate today,
            @Param("school") String school,
            @Param("college") String college,
            @Param("department") String department,
            @Param("keyWord") String keyWord,
            Pageable pageable
    );
}