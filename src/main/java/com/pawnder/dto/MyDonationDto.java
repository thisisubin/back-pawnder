package com.pawnder.dto;

import lombok.*;

import java.time.LocalDateTime;

import com.pawnder.constant.PetStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor // 생성자 자동 생성
public class MyDonationDto {
    private Long donationId;
    private int amount;
    private LocalDateTime donatedAt;

    private PetStatus status;
    private Long petId;
    private String type;
    private String imageUrl;
}


