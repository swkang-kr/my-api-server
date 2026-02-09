package com.example.api.controller;

import com.example.api.dto.request.KakaoMessageRequest;
import com.example.api.kakao.dto.AlimtalkRequest;
import com.example.api.kakao.dto.KakaoResponse;
import com.example.api.service.EmailService;
import com.example.api.service.KakaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final KakaoService kakaoService;
    private final EmailService emailService;

    /**
     * 알림톡 발송
     */
    @PostMapping("/kakao/alimtalk")
    public ResponseEntity<?> sendAlimtalk(
            @Valid @RequestBody KakaoMessageRequest request) {

        try {
            List<AlimtalkRequest.Button> buttons = null;
            if (request.getButtons() != null) {
                buttons = request.getButtons().stream()
                        .map(btn -> AlimtalkRequest.Button.builder()
                                .type(btn.getType())
                                .name(btn.getName())
                                .linkMobile(btn.getLinkMobile())
                                .linkPc(btn.getLinkPc())
                                .build())
                        .collect(Collectors.toList());
            }

            KakaoResponse response = kakaoService.sendAlimtalk(
                    request.getRecipient(),
                    request.getTemplateCode(),
                    request.getVariables(),
                    buttons
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "requestId", response.getRequestId()
            ));
        } catch (Exception e) {
            log.error("Failed to send alimtalk", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 알림톡 비동기 발송
     */
    @PostMapping("/kakao/alimtalk/async")
    public ResponseEntity<?> sendAlimtalkAsync(
            @Valid @RequestBody KakaoMessageRequest request) {

        try {
            List<AlimtalkRequest.Button> buttons = null;
            if (request.getButtons() != null) {
                buttons = request.getButtons().stream()
                        .map(btn -> AlimtalkRequest.Button.builder()
                                .type(btn.getType())
                                .name(btn.getName())
                                .linkMobile(btn.getLinkMobile())
                                .linkPc(btn.getLinkPc())
                                .build())
                        .collect(Collectors.toList());
            }

            kakaoService.sendAlimtalkAsync(
                    request.getRecipient(),
                    request.getTemplateCode(),
                    request.getVariables(),
                    buttons
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Alimtalk queued for sending"
            ));
        } catch (Exception e) {
            log.error("Failed to queue alimtalk", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 친구톡 발송
     */
    @PostMapping("/kakao/friendtalk")
    public ResponseEntity<?> sendFriendtalk(
            @RequestParam List<String> receiverUuids,
            @RequestParam String text,
            @RequestParam(required = false) String buttonTitle,
            @RequestParam(required = false) String webUrl) {

        try {
            KakaoResponse response = kakaoService.sendFriendtalk(
                    receiverUuids, text, buttonTitle, webUrl);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "requestId", response.getRequestId()
            ));
        } catch (Exception e) {
            log.error("Failed to send friendtalk", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 멀티채널 알림 발송 (이메일 + 카카오톡)
     */
    @PostMapping("/multi-channel")
    public ResponseEntity<?> sendMultiChannelNotification(
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String name,
            @RequestParam String subject,
            @RequestParam String content) {

        try {
            // 이메일 발송
            emailService.sendEmailAsync(email, subject, content);

            // 카카오톡 알림톡 발송
            Map<String, String> variables = new HashMap<>();
            variables.put("name", name);
            variables.put("content", content);

            kakaoService.sendAlimtalkAsync(
                    phone,
                    "NOTIFICATION_TEMPLATE",
                    variables,
                    null
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Multi-channel notification queued"
            ));
        } catch (Exception e) {
            log.error("Failed to send multi-channel notification", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
