package com.example.api.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlimtalkRequest {

    @JsonProperty("to")
    private String recipient;  // 수신자 전화번호 (01012345678 형식)

    @JsonProperty("from")
    private String sender;  // 발신자 전화번호

    @JsonProperty("kakaoOptions")
    private KakaoOptions kakaoOptions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoOptions {

        @JsonProperty("pfId")
        private String senderKey;  // 발신 프로필 키

        @JsonProperty("templateId")
        private String templateCode;  // 템플릿 코드

        @JsonProperty("variables")
        private Map<String, String> variables;  // 템플릿 치환 변수

        @JsonProperty("buttons")
        private List<Button> buttons;  // 버튼 정보
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Button {

        @JsonProperty("buttonType")
        private String type;  // WL(웹링크), AL(앱링크), DS(배송조회), BK(봇키워드), MD(메시지전달)

        @JsonProperty("buttonName")
        private String name;  // 버튼명

        @JsonProperty("linkMo")
        private String linkMobile;  // 모바일 웹 링크

        @JsonProperty("linkPc")
        private String linkPc;  // PC 웹 링크

        @JsonProperty("linkAnd")
        private String linkAndroid;  // 안드로이드 앱 링크

        @JsonProperty("linkIos")
        private String linkIos;  // iOS 앱 링크
    }

}
