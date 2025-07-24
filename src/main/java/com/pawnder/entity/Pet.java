package com.pawnder.entity;

import com.pawnder.constant.Gender;
import com.pawnder.constant.Size;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id

    private boolean adopt; //입양 여부

    private String name; //반려동물 이름

    private String type; //반려동물 종류

    private double weight; //반려동물 몸무게

    @Enumerated(EnumType.STRING)
    private Size size; //반려동물 사이즈

    private int age; //반려동물 나이
    private String profile = "default-profile.png"; // 반려동물 사진 - 기본값으로 초기화

    @Enumerated(EnumType.STRING)
    private Gender gender; //반려동물 성별

    private String petId; //등록 번호

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
