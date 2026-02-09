package com.example.api.controller;

import com.example.api.dto.request.UserCreateRequest;
import com.example.api.dto.response.UserResponse;
import com.example.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreateRequest request) {
        UserResponse user = userService.create(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username,
                                                      @RequestParam String password) {
        // JWT 토큰 생성 로직 구현 필요
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login endpoint - JWT implementation needed");
        response.put("username", username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        // 현재 로그인한 사용자 정보 반환
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Current user endpoint - Authentication implementation needed");
        return ResponseEntity.ok(response);
    }

}
