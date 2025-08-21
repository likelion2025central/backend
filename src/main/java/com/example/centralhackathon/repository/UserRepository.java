package com.example.centralhackathon.repository;

import com.example.centralhackathon.entity.BossAssociation;
import com.example.centralhackathon.entity.CouncilAssociation;
import com.example.centralhackathon.entity.StudentCouncil;
import com.example.centralhackathon.entity.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Boolean existsByUsername(String username);
    // StudentCouncil만 조회하는 메서드
    @Query("SELECT sc FROM StudentCouncil sc WHERE sc.id = :id")
    Optional<StudentCouncil> findStudentCouncilById(@Param("id") Long id);

    // BossAssociationRepository
    @Query("""
select ba
from BossAssociation ba
join fetch TREAT(ba.user as Boss) b
where ba.id = :id
""")
    Optional<BossAssociation> findByIdWithBossUser(@Param("id") Long id);

    // CouncilAssociationRepository
    @Query("""
select ca
from CouncilAssociation ca
join fetch TREAT(ca.user as StudentCouncil) sc
where ca.id = :id
""")
    Optional<CouncilAssociation> findByIdWithCouncilUser(@Param("id") Long id);

}
