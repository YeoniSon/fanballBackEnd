package com.example.fanball.user.mail;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Component
public class MailComponent {
    private static final Logger log = LoggerFactory.getLogger(MailComponent.class);
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String defaultFrom;

    public boolean sendMail(String email, String subject, String content) {
        boolean result = false;
        MimeMessagePreparator msg = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper =
                        new MimeMessageHelper(mimeMessage, true, "UTF-8");

                mimeMessageHelper.setFrom(defaultFrom);
                mimeMessageHelper.setTo(email);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(content, true);
            }
        };

        try{
            mailSender.send(msg);
            result = true;
        } catch (Exception e) {
            log.error("Failed to send mail to {}: {}", email, e.getMessage(), e);
        }
        return result;
    }

}
