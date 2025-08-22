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
    @Query("SELECT b.storeName FROM Boss b WHERE b.id = :userId")
    String findBossStoreNameByUserId(@Param("userId") Long userId);

}
