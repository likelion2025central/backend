package com.example.centralhackathon.service;

import com.example.centralhackathon.dto.Request.*;
import com.example.centralhackathon.dto.Response.LoginResponse;
import com.example.centralhackathon.dto.Response.UserProfileResponse;
import com.example.centralhackathon.entity.Boss;
import com.example.centralhackathon.entity.Role;
import com.example.centralhackathon.entity.StudentCouncil;
import com.example.centralhackathon.entity.Users;
import com.example.centralhackathon.jwt.JwtUtil;
import com.example.centralhackathon.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public Boolean isDuplicate(String userName) {
        return userRepository.existsByUsername(userName);
    }


    @Transactional
    public void CouncilSignUp(CouncilSignUp council) {
        if (council == null) {
            throw new IllegalArgumentException("요청이 올바르지 않습니다.");
        }
        if (council.getUsername() == null || council.getUsername().isBlank()) {
            throw new IllegalArgumentException("아이디는 필수입니다.");
        }
        if (council.getPassword() == null || council.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if(userRepository.existsByUsername(council.getUsername())){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        StudentCouncil entity = new StudentCouncil();
        entity.setUsername(council.getUsername());
        entity.setPassword(encoder.encode(council.getPassword()));
        entity.setPhone(council.getPhone());
        entity.setCollege(council.getCollege());
        entity.setSchoolName(council.getSchoolName());
        entity.setDepartment(council.getDepartment());
        entity.setEmail(council.getEmail());
        userRepository.save(entity);
    }

    @Transactional
    public void BossSignUp(BossSignUp boss) {
        if (boss == null) {
            throw new IllegalArgumentException("요청이 올바르지 않습니다.");
        }
        if (boss.getUsername() == null || boss.getUsername().isBlank()) {
            throw new IllegalArgumentException("아이디는 필수입니다.");
        }
        if (boss.getPassword() == null || boss.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if(userRepository.existsByUsername(boss.getUsername())){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        Boss entity = new Boss();
        entity.setUsername(boss.getUsername());
        entity.setPassword(encoder.encode(boss.getPassword()));
        entity.setPhone(boss.getPhone());
        entity.setBizRegNo(boss.getBizRegNo());
        entity.setStoreName(boss.getStoreName());
        entity.setEmail(boss.getEmail());
        userRepository.save(entity);
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Users user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(()->new UsernameNotFoundException("존재하지 않는 유저입니다."));
        if(!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        UserInfo userInfo = UserInfo.toDto(user);
        String token = jwtUtil.createAccessToken(userInfo); // 토큰 문자열

        LoginResponse res = new LoginResponse();
        res.setToken(token);
        res.setRole(user.getRole()); // ★ 여기서 하위 클래스가 돌려준 역할

        return res;
    }

    public UserProfileResponse me(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        Role role = user.getRole();
        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(role);

        switch (role) {
            case COUNCIL -> {
                StudentCouncil c = (StudentCouncil) user;
                builder.council(UserProfileResponse.CouncilInfo.builder()
                        .schoolName(c.getSchoolName())
                        .college(c.getCollege())
                        .department(c.getDepartment())
                        .email(c.getEmail())
                        .phone(c.getPhone())
                        .build());
            }
            case BOSS -> {
                Boss b = (Boss) user;
                builder.boss(UserProfileResponse.BossInfo.builder()
                        .storeName(b.getStoreName())
                        .bizRegNo(b.getBizRegNo())
                        .phone(b.getPhone())
                        .email(b.getEmail())
                        .build());
            }
        }

        return builder.build();
    }

}
