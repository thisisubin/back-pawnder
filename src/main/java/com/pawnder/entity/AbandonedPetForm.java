package com.pawnder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/* 유기 제보 (입양해라) 펫 엔티티 */

@Entity
@Getter
@Setter
public class AbandonedPetForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; //품종
    private String gender; //성별
    private String description; //상태설명
    private double latitude; //위도
    private double longitude; //경도
    private String location; //지역
    private String imageUrl; //유기동물 사진
    private LocalDate foundDate; //발견일
    private LocalTime foundTime; //발견시각

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}

