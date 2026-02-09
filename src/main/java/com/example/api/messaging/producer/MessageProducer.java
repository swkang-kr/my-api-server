package com.example.api.messaging.producer;

import com.example.api.messaging.dto.EmailMessage;
import com.example.api.messaging.dto.KakaoMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.email}")
    private String emailRoutingKey;

    @Value("${rabbitmq.routing-key.notification}")
    private String notificationRoutingKey;

    @Value("${rabbitmq.routing-key.kakao}")
    private String kakaoRoutingKey;

    public void sendEmailMessage(EmailMessage message) {
        log.info("Sending email message to queue: {}", message);
        rabbitTemplate.convertAndSend(exchange, emailRoutingKey, message);
    }

    public void sendNotificationMessage(String message) {
        log.info("Sending notification message to queue: {}", message);
        rabbitTemplate.convertAndSend(exchange, notificationRoutingKey, message);
    }

    public void sendKakaoMessage(KakaoMessage message) {
        log.info("Sending kakao message to queue: {}", message);
        rabbitTemplate.convertAndSend(exchange, kakaoRoutingKey, message);
    }

}
