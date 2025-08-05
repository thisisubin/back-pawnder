package com.pawnder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String impUid; // 아임포트 고유 결제 번호
    private String merchantUid; // 우리가 생성한 고유 주문 번호

    private String userName; // 후원자 이름

    private int amount; // 결제 금액

    private String paymentMethod; // 카드, 계좌이체 등

    private Long abandonedPetId; // 유기견 id

    private LocalDateTime donatedAt; // 후원일자

    @PrePersist
    public void prePersit() {
        this.donatedAt = LocalDateTime.now();
    }
}
