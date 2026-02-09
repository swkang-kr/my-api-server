package com.example.api.service;

import com.example.api.kakao.client.KakaoApiClient;
import com.example.api.kakao.dto.AlimtalkRequest;
import com.example.api.kakao.dto.FriendtalkRequest;
import com.example.api.kakao.dto.KakaoResponse;
import com.example.api.messaging.dto.KakaoMessage;
import com.example.api.messaging.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    private final KakaoApiClient kakaoApiClient;
    private final MessageProducer messageProducer;

    @Value("${kakao.api.sender-key}")
    private String senderKey;

    /**
     * 알림톡 발송 (동기)
     */
    public KakaoResponse sendAlimtalk(String recipient, String templateCode,
                                      Map<String, String> variables,
                                      List<AlimtalkRequest.Button> buttons) {

        AlimtalkRequest.KakaoOptions kakaoOptions = AlimtalkRequest.KakaoOptions.builder()
                .senderKey(senderKey)
                .templateCode(templateCode)
                .variables(variables)
                .buttons(buttons)
                .build();

        AlimtalkRequest request = AlimtalkRequest.builder()
                .recipient(recipient)
                .kakaoOptions(kakaoOptions)
                .build();

        try {
            KakaoResponse response = kakaoApiClient.sendAlimtalk(request).block();

            // 발송 로그 저장
            saveKakaoLog(recipient, "ALIMTALK", templateCode,
                    "SUCCESS", response.getRequestId());

            return response;
        } catch (Exception e) {
            log.error("Failed to send alimtalk to {}", recipient, e);
            saveKakaoLog(recipient, "ALIMTALK", templateCode,
                    "FAILED", null);
            throw new RuntimeException("Failed to send alimtalk", e);
        }
    }

    /**
     * 알림톡 발송 (비동기 - RabbitMQ)
     */
    public void sendAlimtalkAsync(String recipient, String templateCode,
                                   Map<String, String> variables,
                                   List<AlimtalkRequest.Button> buttons) {

        KakaoMessage message = KakaoMessage.builder()
                .recipient(recipient)
                .messageType("ALIMTALK")
                .templateCode(templateCode)
                .variables(variables)
                .buttons(convertToButtonDto(buttons))
                .build();

        messageProducer.sendKakaoMessage(message);
    }

    /**
     * 친구톡 발송 (동기)
     */
    public KakaoResponse sendFriendtalk(List<String> receiverUuids, String text,
                                        String buttonTitle, String webUrl) {

        FriendtalkRequest.Link link = FriendtalkRequest.Link.builder()
                .webUrl(webUrl)
                .mobileWebUrl(webUrl)
                .build();

        FriendtalkRequest.TemplateObject templateObject =
                FriendtalkRequest.TemplateObject.builder()
                        .objectType("text")
                        .text(text)
                        .link(link)
                        .buttonTitle(buttonTitle)
                        .build();

        FriendtalkRequest request = FriendtalkRequest.builder()
                .receiverUuids(receiverUuids)
                .templateObject(templateObject)
                .build();

        try {
            KakaoResponse response = kakaoApiClient.sendFriendtalk(request).block();

            // 발송 로그 저장
            receiverUuids.forEach(uuid ->
                    saveKakaoLog(uuid, "FRIENDTALK", null,
                            "SUCCESS", response.getRequestId()));

            return response;
        } catch (Exception e) {
            log.error("Failed to send friendtalk", e);
            receiverUuids.forEach(uuid ->
                    saveKakaoLog(uuid, "FRIENDTALK", null, "FAILED", null));
            throw new RuntimeException("Failed to send friendtalk", e);
        }
    }

    /**
     * 친구톡 발송 (비동기)
     */
    public void sendFriendtalkAsync(List<String> receiverUuids, String text,
                                     String buttonTitle, String webUrl) {

        KakaoMessage message = KakaoMessage.builder()
                .receiverUuids(receiverUuids)
                .messageType("FRIENDTALK")
                .content(text)
                .buttonTitle(buttonTitle)
                .webUrl(webUrl)
                .build();

        messageProducer.sendKakaoMessage(message);
    }

    /**
     * 회원가입 환영 알림톡 발송
     */
    public void sendWelcomeAlimtalk(String phone, String name) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("date", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));

        List<AlimtalkRequest.Button> buttons = Arrays.asList(
                AlimtalkRequest.Button.builder()
                        .type("WL")
                        .name("서비스 시작하기")
                        .linkMobile("https://example.com/app")
                        .linkPc("https://example.com/web")
                        .build()
        );

        sendAlimtalkAsync(phone, "WELCOME_TEMPLATE", variables, buttons);
    }

    /**
     * 주문 완료 알림톡 발송
     */
    public void sendOrderConfirmationAlimtalk(String phone, String orderNumber,
                                              String productName, int amount) {
        Map<String, String> variables = new HashMap<>();
        variables.put("orderNumber", orderNumber);
        variables.put("productName", productName);
        variables.put("amount", String.format("%,d", amount));

        List<AlimtalkRequest.Button> buttons = Arrays.asList(
                AlimtalkRequest.Button.builder()
                        .type("WL")
                        .name("주문 상세보기")
                        .linkMobile("https://example.com/order/" + orderNumber)
                        .linkPc("https://example.com/order/" + orderNumber)
                        .build(),
                AlimtalkRequest.Button.builder()
                        .type("DS")
                        .name("배송 조회")
                        .build()
        );

        sendAlimtalkAsync(phone, "ORDER_CONFIRMATION_TEMPLATE", variables, buttons);
    }

    private void saveKakaoLog(String recipient, String messageType,
                              String templateCode, String status, String requestId) {
        // DB에 발송 로그 저장 (MyBatis 사용)
        log.info("Saved kakao log - recipient: {}, type: {}, status: {}",
                recipient, messageType, status);
    }

    private List<KakaoMessage.ButtonDto> convertToButtonDto(
            List<AlimtalkRequest.Button> buttons) {
        if (buttons == null) return null;

        return buttons.stream()
                .map(btn -> KakaoMessage.ButtonDto.builder()
                        .type(btn.getType())
                        .name(btn.getName())
                        .linkMobile(btn.getLinkMobile())
                        .linkPc(btn.getLinkPc())
                        .build())
                .collect(Collectors.toList());
    }

}
