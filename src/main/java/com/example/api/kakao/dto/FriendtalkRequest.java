package com.example.api.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendtalkRequest {

    @JsonProperty("receiver_uuids")
    private List<String> receiverUuids;  // 수신자 UUID 목록

    @JsonProperty("template_object")
    private TemplateObject templateObject;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateObject {

        @JsonProperty("object_type")
        private String objectType;  // "text", "list", "commerce" 등

        @JsonProperty("text")
        private String text;  // 메시지 내용

        @JsonProperty("link")
        private Link link;

        @JsonProperty("button_title")
        private String buttonTitle;  // 버튼 제목

        @JsonProperty("buttons")
        private List<Button> buttons;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Link {

        @JsonProperty("web_url")
        private String webUrl;

        @JsonProperty("mobile_web_url")
        private String mobileWebUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Button {

        @JsonProperty("title")
        private String title;

        @JsonProperty("link")
        private Link link;
    }

}
