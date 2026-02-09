package com.example.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoMessageRequest {

    @NotBlank
    private String recipient;  // 수신자 전화번호

    @NotBlank
    private String messageType;  // ALIMTALK, FRIENDTALK

    private String templateCode;  // 알림톡 템플릿 코드

    @NotBlank
    private String content;  // 메시지 내용

    private String subject;  // 알림톡 제목

    private Map<String, String> variables;  // 템플릿 치환 변수

    private List<ButtonDto> buttons;  // 버튼 정보

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ButtonDto {
        private String type;
        private String name;
        private String linkMobile;
        private String linkPc;
    }

}
