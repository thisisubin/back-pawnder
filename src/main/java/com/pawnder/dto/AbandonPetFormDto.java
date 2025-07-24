package com.pawnder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/* 유기된 동물 제보 폼 DTO */

@Getter
@Setter
@Schema(description = "유기동물 제보 DTO")
public class AbandonPetFormDto {
    // 발견한 장소 위도
    private double latitude;

    // 발견한 장소 경도
    private double longitude;

    // 동물 이미지 (파일명 or URL or Multipart 처리용 필드)
    private String imageUrl; // 또는 MultipartFile image로 처리할 수도 있음

    // 성별 (예: "수컷", "암컷", "중성화 여부 포함 등")
    private String gender;

    // 특이사항 (ex. "다리를 절음", "사람을 잘 따름" 등)
    private String description;

    @Schema(type = "string", format = "date", example = "2025-07-09")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate foundDate;

    @Schema(type = "string", format = "time", example = "14:30")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime foundTime;

}
