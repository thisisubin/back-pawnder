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

    private String gender;
    private String description;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private LocalDate foundDate;
    private LocalTime foundTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}

