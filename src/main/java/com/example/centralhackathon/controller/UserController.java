package com.example.centralhackathon.controller;

import com.amazonaws.services.ec2.model.LocalGatewayVirtualInterface;
import com.example.centralhackathon.config.ApiResponse;
import com.example.centralhackathon.dto.Request.BossSignUp;
import com.example.centralhackathon.dto.Request.CouncilSignUp;
import com.example.centralhackathon.dto.Request.LoginRequest;
import com.example.centralhackathon.dto.Request.NormalSignUp;
import com.example.centralhackathon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/duplicate")
    public ResponseEntity<ApiResponse> checkDuplicate(@RequestParam("username") String username) {
        boolean dup = userService.isDuplicate(username);
        return ResponseEntity.ok(new ApiResponse(true, dup ? "중복 아이디" : "사용 가능", dup));
    }

/*
    @PostMapping("/signup/normal")
    public ResponseEntity<ApiResponse> signUpNormal(@RequestBody NormalSignUp req) {
        userService.NormalSignUp(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "회원가입(학생) 완료", null));
    }*/


    @PostMapping("/signup/council")
    public ResponseEntity<ApiResponse> signUpCouncil(@RequestBody CouncilSignUp req) {
        userService.CouncilSignUp(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "회원가입(학생회) 완료", null));
    }


    @PostMapping("/signup/boss")
    public ResponseEntity<ApiResponse> signUpBoss(@RequestBody BossSignUp req) {
        userService.BossSignUp(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "회원가입(사장님) 완료", null));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest req) {
        try {
            String token = userService.login(req);
            return ResponseEntity.ok(new ApiResponse(true, "로그인 성공", token));
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, ex.getMessage(), null));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "서버 오류가 발생했습니다.", null));
        }
    }
}