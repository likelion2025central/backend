package com.example.centralhackathon.repository;

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
}
