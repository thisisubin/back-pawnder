package com.pawnder.dto;

import com.pawnder.entity.AbandonedPetForm;
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

    private Long id;

    private double latitude;
    private double longitude;
    private String imageUrl;
    private String gender;
    private String description;
    private String location;
    private String type;

    @Schema(type = "string", format = "date", example = "2025-07-09")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String foundDate;

    @Schema(type = "string", format = "time", example = "14:30")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private String foundTime;

    // 등록한 사용자 정보 (ID 또는 닉네임)
    private String userId;

    // 생성자 추가
    public AbandonPetFormDto(AbandonedPetForm form) {
        this.id = form.getId();
        this.latitude = form.getLatitude();
        this.longitude = form.getLongitude();
        this.imageUrl = form.getImageUrl();
        this.gender = form.getGender();
        this.description = form.getDescription();
        this.location = form.getLocation();
        this.type = form.getType();
        this.foundDate = form.getFoundDate().toString();
        this.foundTime = form.getFoundTime().toString();

        // 유저 ID 추가
        this.userId = form.getUser().getUserId(); // 또는 .getEmail() 혹은 .getNickname()
    }
}
