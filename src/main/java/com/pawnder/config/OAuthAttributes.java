package com.pawnder.config;

import com.pawnder.constant.Role;
import com.pawnder.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private Role role;
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name,
                           String email, Role role) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.role = role;
    }
    public OAuthAttributes() {
    }
    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if(registrationId.equals("kakao")){
            return ofKakao(userNameAttributeName, attributes);
        }
        if(registrationId.equals("naver")){
            return ofNaver(userNameAttributeName,attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");    // 네이버에서 받은 데이터에서 프로필 정보다 담긴 response 값을 꺼낸다.

        String email = (String) response.get("email");

        Role role = email.equals("bm1418109@naver.com") ? Role.ADMIN : Role.USER;

        return new OAuthAttributes(attributes,
                userNameAttributeName,
                (String) response.get("name"),
                (String) response.get("email"),
                role);
    }
    private static OAuthAttributes ofKakao(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");

        String email = (String) kakao_account.get("email");
        String name = (String) profile.get("nickname");

        return new OAuthAttributes(
                attributes,
                userNameAttributeName,
                name,
                email,
                Role.USER // 카카오는 무조건 ROLE_USER
        );
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                            Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        return new OAuthAttributes(
                attributes,
                userNameAttributeName,
                name,
                email,
                Role.USER // 구글도 무조건 ROLE_USER
        );
    }

    public User toEntity() {
        return new User(name, email, role);
    }
}