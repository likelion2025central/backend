package com.example.centralhackathon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class SmsRepository {
    private final String PREFIX = "sms:"; // Redis 키에 사용할 접두사
    private final StringRedisTemplate stringRedisTemplate; // Redis 작업을 위한 StringRedisTemplate 객체

    // SMS 인증 정보를 생성하는 메서드
    public void createSmsCertification(String phone, String code){
        int LIMIT_TIME = 3 * 60; // 인증 코드의 유효 시간(초), 3분 설정
        stringRedisTemplate.opsForValue()
                .set(PREFIX + phone, code, Duration.ofSeconds(LIMIT_TIME)); // Redis에 키와 값을 설정, 유효 시간도 함께 설정
    }

    // SMS 인증 정보를 가져오는 메서드
    public String getSmsCertification(String phone){
        return stringRedisTemplate.opsForValue().get(PREFIX + phone); // Redis에서 키에 해당하는 값을 가져옴
    }

    // SMS 인증 정보를 삭제하는 메서드
    public void deleteSmsCertification(String phone){
        stringRedisTemplate.delete(PREFIX + phone); // Redis에서 해당 키를 삭제
    }

    // 해당 키가 존재하는지 확인하는 메서드
    public boolean hasKey(String phone){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX + phone)); // Redis에서 키의 존재 여부를 확인
    }
}
