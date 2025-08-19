package com.example.centralhackathon.service;

import com.example.centralhackathon.dto.Request.*;
import com.example.centralhackathon.entity.Boss;
import com.example.centralhackathon.entity.NormalUser;
import com.example.centralhackathon.entity.StudentCouncil;
import com.example.centralhackathon.entity.Users;
import com.example.centralhackathon.jwt.JwtUtil;
import com.example.centralhackathon.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    /*
    @Transactional
    public void NormalSignUp(NormalSignUp normal) {
        if (normal == null) {
            throw new IllegalArgumentException("요청이 올바르지 않습니다.");
        }
        if (normal.getUsername() == null || normal.getUsername().isBlank()) {
            throw new IllegalArgumentException("아이디는 필수입니다.");
        }
        if (normal.getPassword() == null || normal.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if(userRepository.existsByUsername(normal.getUsername())){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        NormalUser entity = new NormalUser();
        entity.setUsername(normal.getUsername());
        entity.setPassword(encoder.encode(normal.getPassword()));
        entity.setSchoolName(normal.getSchoolName());
        entity.setMajor(normal.getMajor());
        userRepository.save(entity);
    }*/

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
        userRepository.save(entity);
    }

    @Transactional
    public String login(LoginRequest loginRequest) {
        Users user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(()->new UsernameNotFoundException("존재하지 않는 유저입니다."));
        if(!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        UserInfo userInfo = UserInfo.toDto(user);
        return jwtUtil.createAccessToken(userInfo);
    }
}
