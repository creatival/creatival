package com.creatival;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailTestController {

    private final MailService mailService;

    @GetMapping("/mail-test")
    public String mailTest() throws MessagingException {

        mailService.sendTestMail("arist6034@gmail.com");

        return "메일 발송 완료";
    }
}
