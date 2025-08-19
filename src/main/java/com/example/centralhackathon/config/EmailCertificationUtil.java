package com.example.centralhackathon.config;


import com.example.centralhackathon.service.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class EmailCertificationUtil {
    @Value("${spring.mail.username}") // coolsms의 API 키 주입
    private String sender;
    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;


    public void sendEmail(String email, String code) throws MessagingException {
        String title = "[ 제휴고리(JEHUGORI) ]인증 이메일 입니다."; // 이메일 제목
        String content =
                "이메일 인증번호 이메일 입니다." + 	//html 형식으로 작성
                        "<br><br>" +
                        "인증 번호는 " + code + "입니다." +
                        "<br>" +
                        "서비스로 돌아가 인증번호를 입력해주세요.";
        send(email, title, content, code);

    }

    //이메일을 전송
    private void send(String sendTo, String title, String content, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
        helper.setFrom(sender);
        helper.setTo(sendTo);
        helper.setSubject(title);
        helper.setText(content,true);
        mailSender.send(message);

        emailRepository.createEmailCertification(sendTo, code);

    }
}

