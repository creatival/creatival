package com.creatival;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendTestMail(String to) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Creatival 메일 테스트");

        String content = """
                <h2>메일 발송 테스트 성공 🎉</h2>
                <p>Spring Boot에서 Gmail SMTP 연결이 정상 동작합니다.</p>
                """;

        helper.setText(content, true);

        mailSender.send(message);
    }
}
