package com.example.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoMessageResponse {

    private boolean success;
    private String requestId;
    private String message;
    private String errorCode;
    private String errorMessage;

}
