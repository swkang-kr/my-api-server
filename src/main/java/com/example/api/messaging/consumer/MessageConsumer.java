package com.example.api.messaging.consumer;

import com.example.api.kakao.client.KakaoApiClient;
import com.example.api.kakao.dto.AlimtalkRequest;
import com.example.api.kakao.dto.FriendtalkRequest;
import com.example.api.messaging.dto.EmailMessage;
import com.example.api.messaging.dto.KakaoMessage;
import com.example.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final EmailService emailService;
    private final KakaoApiClient kakaoApiClient;

    @Value("${kakao.api.sender-key}")
    private String senderKey;

    @RabbitListener(queues = "${rabbitmq.queue.email}")
    public void receiveEmailMessage(EmailMessage message) {
        log.info("Received email message from queue: {}", message);
        try {
            emailService.sendEmail(
                    message.getRecipient(),
                    message.getSubject(),
                    message.getContent()
            );
        } catch (Exception e) {
            log.error("Failed to send email", e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void receiveNotificationMessage(String message) {
        log.info("Received notification message from queue: {}", message);
    }

    @RabbitListener(queues = "${rabbitmq.queue.kakao}")
    public void receiveKakaoMessage(KakaoMessage message) {
        log.info("Received kakao message from queue: {}", message);
        try {
            if ("ALIMTALK".equals(message.getMessageType())) {
                sendAlimtalkFromQueue(message);
            } else if ("FRIENDTALK".equals(message.getMessageType())) {
                sendFriendtalkFromQueue(message);
            }
        } catch (Exception e) {
            log.error("Failed to send kakao message", e);
        }
    }

    private void sendAlimtalkFromQueue(KakaoMessage message) {
        List<AlimtalkRequest.Button> buttons = null;
        if (message.getButtons() != null) {
            buttons = message.getButtons().stream()
                    .map(btn -> AlimtalkRequest.Button.builder()
                            .type(btn.getType())
                            .name(btn.getName())
                            .linkMobile(btn.getLinkMobile())
                            .linkPc(btn.getLinkPc())
                            .build())
                    .collect(Collectors.toList());
        }

        AlimtalkRequest.KakaoOptions kakaoOptions =
                AlimtalkRequest.KakaoOptions.builder()
                        .senderKey(senderKey)
                        .templateCode(message.getTemplateCode())
                        .variables(message.getVariables())
                        .buttons(buttons)
                        .build();

        AlimtalkRequest request = AlimtalkRequest.builder()
                .recipient(message.getRecipient())
                .kakaoOptions(kakaoOptions)
                .build();

        kakaoApiClient.sendAlimtalk(request)
                .doOnSuccess(response ->
                        log.info("Alimtalk sent from queue: {}", response))
                .doOnError(error ->
                        log.error("Failed to send alimtalk from queue", error))
                .subscribe();
    }

    private void sendFriendtalkFromQueue(KakaoMessage message) {
        FriendtalkRequest.Link link = FriendtalkRequest.Link.builder()
                .webUrl(message.getWebUrl())
                .mobileWebUrl(message.getWebUrl())
                .build();

        FriendtalkRequest.TemplateObject templateObject =
                FriendtalkRequest.TemplateObject.builder()
                        .objectType("text")
                        .text(message.getContent())
                        .link(link)
                        .buttonTitle(message.getButtonTitle())
                        .build();

        FriendtalkRequest request = FriendtalkRequest.builder()
                .receiverUuids(message.getReceiverUuids())
                .templateObject(templateObject)
                .build();

        kakaoApiClient.sendFriendtalk(request)
                .doOnSuccess(response ->
                        log.info("Friendtalk sent from queue: {}", response))
                .doOnError(error ->
                        log.error("Failed to send friendtalk from queue", error))
                .subscribe();
    }

}
