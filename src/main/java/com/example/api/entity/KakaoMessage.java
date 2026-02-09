package com.example.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoMessage {

    private Long id;
    private Long userId;
    private String messageType;
    private String recipientPhone;
    private String templateCode;
    private String subject;
    private String content;
    private String buttonData;
    private String status;
    private String errorCode;
    private String errorMessage;
    private String requestId;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;

}
