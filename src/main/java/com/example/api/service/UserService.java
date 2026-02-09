package com.example.api.service;

import com.example.api.dto.request.UserCreateRequest;
import com.example.api.dto.request.UserUpdateRequest;
import com.example.api.dto.response.UserResponse;
import com.example.api.entity.User;
import com.example.api.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        return convertToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }
        return convertToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userMapper.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        // Check if username or email already exists
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .status("ACTIVE")
                .build();

        userMapper.insert(user);
        log.info("User created: {}", user.getUsername());

        return convertToResponse(user);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        userMapper.update(user);
        log.info("User updated: {}", user.getUsername());

        return convertToResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        userMapper.delete(id);
        log.info("User deleted: {}", user.getUsername());
    }

    @Transactional
    public User findOrCreateOAuth2User(String provider, String providerId, String email, String name) {
        User user = userMapper.findByProviderAndProviderId(provider, providerId);

        if (user == null) {
            user = User.builder()
                    .username(provider + "_" + providerId)
                    .email(email)
                    .name(name)
                    .provider(provider)
                    .providerId(providerId)
                    .status("ACTIVE")
                    .build();

            userMapper.insert(user);
            log.info("OAuth2 user created: {}", user.getUsername());
        }

        return user;
    }

    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .provider(user.getProvider())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
