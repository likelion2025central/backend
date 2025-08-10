package com.example.centralhackathon.jwt;

import com.example.centralhackathon.dto.Request.UserInfo;
import com.example.centralhackathon.entity.Users;
import com.example.centralhackathon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("해당 유저 없음"));

        UserInfo userInfo = UserInfo.toDto(user);
        return new CustomUserDetails(userInfo);
    }
}

