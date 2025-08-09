package com.pawnder.config;

import com.pawnder.constant.Role;
import com.pawnder.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@Builder
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String socialId; // socialId 필드 추가
    private String provider; // provider 필드 추가
    private Role role;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if (registrationId.equals("kakao")) {
            return ofKakao(userNameAttributeName, attributes);
        }
        if (registrationId.equals("naver")) {
            return ofNaver(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        String socialId = (String) response.get("id"); // Naver에서 socialId 추출
        String email = (String) response.get("email");
        String name = (String) response.get("name");
        Role role = email.equals("bm1418109@naver.com") ? Role.ADMIN : Role.USER;

        return OAuthAttributes.builder()
                .name(name)
                .email(email)
                .socialId(socialId) // 추출한 socialId 설정
                .provider("naver") // provider 설정
                .role(role)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");
        String socialId = String.valueOf(attributes.get("id")); // Kakao에서 socialId 추출
        String email = (String) kakao_account.get("email");
        String name = (String) profile.get("nickname");

        return OAuthAttributes.builder()
                .name(name)
                .email(email)
                .socialId(socialId) // 추출한 socialId 설정
                .provider("kakao") // provider 설정
                .role(Role.USER)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                            Map<String, Object> attributes) {
        String socialId = (String) attributes.get("sub"); // Google에서 socialId 추출
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        return OAuthAttributes.builder()
                .name(name)
                .email(email)
                .socialId(socialId) // 추출한 socialId 설정
                .provider("google") // provider 설정
                .role(Role.USER)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .userId(this.socialId)       // userId 필드에 socialId 값을 할당
                .socialId(this.socialId)     // socialId 필드에도 동일하게 할당
                .email(this.email)
                .name(this.name)
                .role(this.role)
                .provider(this.provider)
                .build();
    }
}
