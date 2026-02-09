package com.example.api.kakao.client;

import com.example.api.exception.KakaoApiException;
import com.example.api.kakao.dto.AlimtalkRequest;
import com.example.api.kakao.dto.FriendtalkRequest;
import com.example.api.kakao.dto.KakaoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoApiClient {

    private final WebClient kakaoWebClient;

    @Value("${kakao.api.admin-key}")
    private String adminKey;

    @Value("${kakao.api.sender-key}")
    private String senderKey;

    @Value("${kakao.bizmessage.api-key}")
    private String bizApiKey;

    @Value("${kakao.bizmessage.api-secret}")
    private String bizApiSecret;

    /**
     * 카카오 친구톡 발송 (카카오톡 채널 친구에게만 발송 가능)
     */
    public Mono<KakaoResponse> sendFriendtalk(FriendtalkRequest request) {
        return kakaoWebClient.post()
                .uri("/v1/api/talk/friends/message/default/send")
                .header("Authorization", "KakaoAK " + adminKey)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Friendtalk API Error: {}", body);
                                    return Mono.error(new KakaoApiException(
                                            "Failed to send friendtalk: " + body));
                                })
                )
                .bodyToMono(KakaoResponse.class)
                .doOnSuccess(response ->
                        log.info("Friendtalk sent successfully: {}", response))
                .doOnError(error ->
                        log.error("Failed to send friendtalk", error));
    }

    /**
     * 카카오 알림톡 발송 (사전 승인된 템플릿으로만 발송 가능)
     * Solapi(구 Coolsms) 등의 대행사 API 사용
     */
    public Mono<KakaoResponse> sendAlimtalk(AlimtalkRequest request) {
        // Solapi 기준 예시
        String authHeader = generateSolapiAuthHeader();

        return WebClient.create("https://api.solapi.com")
                .post()
                .uri("/kakao/v1/alimtalk/send")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Alimtalk API Error: {}", body);
                                    return Mono.error(new KakaoApiException(
                                            "Failed to send alimtalk: " + body));
                                })
                )
                .bodyToMono(KakaoResponse.class)
                .doOnSuccess(response ->
                        log.info("Alimtalk sent successfully: {}", response))
                .doOnError(error ->
                        log.error("Failed to send alimtalk", error));
    }

    /**
     * Solapi 인증 헤더 생성
     */
    private String generateSolapiAuthHeader() {
        try {
            String salt = UUID.randomUUID().toString().replaceAll("-", "");
            String date = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                    .format(DateTimeFormatter.ISO_INSTANT);

            String signature = getSignature(date, salt);

            return String.format("HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
                    bizApiKey, date, salt, signature);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate auth header", e);
        }
    }

    private String getSignature(String date, String salt) throws Exception {
        String message = date + salt;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
                bizApiSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
        mac.init(secretKey);

        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(rawHmac);
    }

}
