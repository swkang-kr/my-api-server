package com.example.api.service;

import com.example.api.messaging.dto.EmailMessage;
import com.example.api.messaging.producer.MessageProducer;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final MessageProducer messageProducer;

    @Value("${mail.from.address}")
    private String fromAddress;

    @Value("${mail.from.name}")
    private String fromName;

    /**
     * 이메일 발송 (동기)
     */
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * 이메일 발송 (비동기 - RabbitMQ)
     */
    public void sendEmailAsync(String to, String subject, String content) {
        EmailMessage emailMessage = EmailMessage.builder()
                .recipient(to)
                .subject(subject)
                .content(content)
                .build();

        messageProducer.sendEmailMessage(emailMessage);
        log.info("Email message queued for: {}", to);
    }

    /**
     * HTML 템플릿 이메일 발송
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        sendEmail(to, subject, htmlContent);
    }

    /**
     * 회원가입 환영 이메일
     */
    public void sendWelcomeEmail(String to, String name) {
        String subject = "환영합니다! " + name + "님";
        String content = buildWelcomeEmailContent(name);
        sendEmailAsync(to, subject, content);
    }

    /**
     * 비밀번호 재설정 이메일
     */
    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "비밀번호 재설정 요청";
        String content = buildPasswordResetEmailContent(resetToken);
        sendEmailAsync(to, subject, content);
    }

    private String buildWelcomeEmailContent(String name) {
        return String.format("""
                <html>
                <body>
                    <h1>환영합니다, %s님!</h1>
                    <p>My API Server에 가입해주셔서 감사합니다.</p>
                    <p>지금 바로 서비스를 시작해보세요!</p>
                    <a href="https://example.com/app" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">서비스 시작하기</a>
                </body>
                </html>
                """, name);
    }

    private String buildPasswordResetEmailContent(String resetToken) {
        return String.format("""
                <html>
                <body>
                    <h1>비밀번호 재설정</h1>
                    <p>비밀번호 재설정을 요청하셨습니다.</p>
                    <p>아래 링크를 클릭하여 비밀번호를 재설정하세요:</p>
                    <a href="https://example.com/reset-password?token=%s" style="background-color: #008CBA; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">비밀번호 재설정</a>
                    <p>이 링크는 24시간 동안 유효합니다.</p>
                </body>
                </html>
                """, resetToken);
    }

}
