package com.example.api.kakao.template;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KakaoTemplateCode {

    WELCOME_TEMPLATE("WELCOME_001", "회원가입 환영 메시지"),
    ORDER_CONFIRMATION_TEMPLATE("ORDER_001", "주문 확인 메시지"),
    PAYMENT_CONFIRMATION_TEMPLATE("PAYMENT_001", "결제 완료 메시지"),
    SHIPPING_NOTIFICATION_TEMPLATE("SHIPPING_001", "배송 시작 알림"),
    DELIVERY_COMPLETE_TEMPLATE("DELIVERY_001", "배송 완료 알림"),
    PASSWORD_RESET_TEMPLATE("PASSWORD_001", "비밀번호 재설정"),
    NOTIFICATION_TEMPLATE("NOTIFICATION_001", "일반 알림");

    private final String code;
    private final String description;

}
