package com.pawnder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawnder.dto.PredictionResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Comparator;

@Service
public class CustomVisionService {


    @Value("${azure.customvision.prediction-key}")
    private String predictionKey;

    @Value("${azure.customvision.endpoint}")
    private String endpoint;

    @Value("${azure.customvision.project-id}")
    private String projectId;

    @Value("${azure.customvision.publish-name}")
    private String publishName;

    private final RestTemplate restTemplate = new RestTemplate();

    public String predictImage(byte[] imageBytes) {
        String url = String.format("%s/customvision/v3.0/Prediction/%s/classify/iterations/%s/image",
                endpoint, projectId, publishName);

        System.out.println("CustomVision API 호출 시작");
        System.out.println("URL: " + url);
        System.out.println("Project ID: " + projectId);
        System.out.println("Publish Name: " + publishName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Prediction-Key", predictionKey);

        HttpEntity<byte[]> entity = new HttpEntity<>(imageBytes, headers);

        try {
            System.out.println("API 요청 전송 중...");
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            
            System.out.println("API 응답 상태: " + response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("API 호출 성공");
                return response.getBody();
            } else {
                System.out.println("API 호출 실패 - 상태 코드: " + response.getStatusCode());
                throw new RuntimeException("Prediction API 호출 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("API 호출 중 에러 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("CustomVision API 호출 중 에러: " + e.getMessage(), e);
        }
    }

    public PredictionResultDto.Prediction getTopPrediction(byte[] imageBytes) throws IOException {
        try {
            String jsonResponse = predictImage(imageBytes);
            System.out.println("API 응답 JSON: " + jsonResponse);

            ObjectMapper mapper = new ObjectMapper();
            PredictionResultDto result = mapper.readValue(jsonResponse, PredictionResultDto.class);

            if (result.predictions == null || result.predictions.isEmpty()) {
                System.out.println("예측 결과가 비어있습니다");
                throw new RuntimeException("예측 결과가 없습니다.");
            }

            // 직접 필드에 접근 (getTagName() 대신 tagName 사용)
            PredictionResultDto.Prediction topPrediction = result.predictions.stream()
                    .max(Comparator.comparingDouble(p -> p.getProbability()))
                    .orElseThrow(() -> new RuntimeException("예측 결과가 없습니다."));

            System.out.println("최고 확률 예측: " + topPrediction.getTagName() + " (확률: " + topPrediction.getProbability() + ")");
            return topPrediction;
        } catch (IOException e) {
            System.out.println("JSON 파싱 중 에러 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
