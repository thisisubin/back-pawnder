package com.pawnder.entity;

import com.pawnder.constant.PetStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/* 유기동물 등록 엔티티 */

@Entity
@Getter
@Setter
public class AbandonedPet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제보 기반 정보
    private String type;                  // 품종
    private String gender;                // 성별 (M/F)
    private LocalDate foundDate;          // 발견 날짜
    private LocalTime foundTime;          // 발견 시간
    private String description; // 특이사항

    @Enumerated(EnumType.STRING)
    private PetStatus status;// 상태 (ex: 보호중- PROTECTING, 입양완료 - ADOPTED 등)

    // 이미지 경로
    private String imageUrl;              // 사진 업로드된 경우 저장

    // 제보 연결 (Optional: 제보 추적용)
    @OneToOne
    private AbandonedPetForm abandonedPetForm;

    //관리자가 등록시켜야 함..

    // 등록 일시
    private LocalDateTime createdAt = LocalDateTime.now();
}
