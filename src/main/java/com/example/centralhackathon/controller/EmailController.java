package com.example.centralhackathon.controller;


import com.example.centralhackathon.config.ApiResponse;
import com.example.centralhackathon.dto.Request.EmailRequestDto;
import com.example.centralhackathon.dto.Request.EmailVerifyDto;
import com.example.centralhackathon.service.MailSendService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final MailSendService mailSendService;

    @Operation(
            summary = "이메일 인증 메일 보내기",
            description = "-")
    @PostMapping("/send")
    public ResponseEntity<?> SendEmail(@RequestBody @Valid EmailRequestDto emailRequestDto){
        try{
            mailSendService.sendEmail(emailRequestDto);
            return ResponseEntity.ok(new ApiResponse(true, "인증 이메일 전송 완료", null));
        } catch (MessagingException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "인증 메일 전송 중 오류가 발생했습니다. 다시 시도해주세요.", null));
        }
    }

    @Operation(
            summary = "메일 인증번호",
            description = "-")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid EmailVerifyDto emailVerifyDto){
        boolean verify = mailSendService.verifyCode(emailVerifyDto);
        if (verify) {
            return ResponseEntity.ok(new ApiResponse(true, "인증에 성공했습니다.", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "인증에 실패했습니다.", null));
        }
    }



}
