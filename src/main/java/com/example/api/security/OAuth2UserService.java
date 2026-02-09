package com.example.api.security;

import com.example.api.entity.User;
import com.example.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Extract user info based on provider
        String providerId = extractProviderId(registrationId, attributes, userNameAttributeName);
        String email = extractEmail(registrationId, attributes);
        String name = extractName(registrationId, attributes);

        log.info("OAuth2 login - Provider: {}, ProviderId: {}, Email: {}", registrationId, providerId, email);

        // Find or create user
        User user = userService.findOrCreateOAuth2User(registrationId, providerId, email, name);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                userNameAttributeName
        );
    }

    private String extractProviderId(String registrationId, Map<String, Object> attributes, String userNameAttributeName) {
        return String.valueOf(attributes.get(userNameAttributeName));
    }

    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return (String) attributes.get("email");
            case "github":
                return (String) attributes.get("email");
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            default:
                return null;
        }
    }

    private String extractName(String registrationId, Map<String, Object> attributes) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return (String) attributes.get("name");
            case "github":
                return (String) attributes.get("name");
            case "kakao":
                Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                return properties != null ? (String) properties.get("nickname") : null;
            default:
                return null;
        }
    }

}
