package com.pawnder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class IamportService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${imp.api-key}")
    private String apiKey;
    @Value("${imp.api-secret}")
    private String apiSecret;

    private String accessToken;
    private long tokenExpiryTime;

    private synchronized void refreshTokenIfNeeded() {
        if (accessToken == null || System.currentTimeMillis() > tokenExpiryTime) {
            try {
                System.out.println("apiKey = " + apiKey);
                System.out.println("apiSecret = " + apiSecret);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");

                Map<String, String> body = Map.of(
                        "imp_key", apiKey,
                        "imp_secret", apiSecret
                );

                // JSON 문자열로 명시적으로 변환
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonBody = objectMapper.writeValueAsString(body);

                HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(
                        "https://api.iamport.kr/users/getToken",
                        request,
                        Map.class
                );

                System.out.println("아임포트 응답 전체: " + response.getBody());

                Map<String, Object> res = (Map<String, Object>) response.getBody().get("response");
                if (res == null) {
                    throw new RuntimeException("아임포트 응답에서 'response'가 null입니다. 요청 실패로 보입니다.");
                }

                accessToken = (String) res.get("access_token");
                int expiredIn = (Integer) res.get("expired_at");
                tokenExpiryTime = System.currentTimeMillis() + expiredIn * 1000L;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("아임포트 토큰 발급 실패: " + e.getMessage());
            }
        }
    }



    public Map<String, Object> getPaymentByImpUid(String impUid) {
        refreshTokenIfNeeded();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.iamport.kr/payments/" + impUid,
                HttpMethod.GET,
                entity,
                Map.class
        );

        return (Map<String, Object>) response.getBody().get("response");
    }
}

