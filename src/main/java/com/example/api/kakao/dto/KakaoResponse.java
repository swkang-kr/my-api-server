package com.example.api.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoResponse {

    @JsonProperty("successful_receiver_uuids")
    private List<String> successfulReceiverUuids;

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("statusMessage")
    private String statusMessage;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("accountId")
    private String accountId;

}
