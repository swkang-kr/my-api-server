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
public class User {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String name;
    private String provider;
    private String providerId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
