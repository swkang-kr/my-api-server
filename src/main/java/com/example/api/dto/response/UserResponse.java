package com.example.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String name;
    private String provider;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
