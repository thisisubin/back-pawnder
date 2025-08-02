package com.pawnder.entity;

import com.pawnder.constant.AdoptStatus;
import com.pawnder.constant.PetStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AdoptPet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private AbandonedPet abandonedPet;

    @Enumerated(EnumType.STRING)
    private AdoptStatus adoptStatus;


    private LocalDateTime appliedAt;  // 신청 시간
    private LocalDateTime approvedAt; // 승인 시간
}
