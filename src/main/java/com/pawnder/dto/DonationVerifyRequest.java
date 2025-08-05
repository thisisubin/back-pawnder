package com.pawnder.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class DonationVerifyRequest {
    private String impUid;
    private String merchantUid;
    private Integer amount;
    private String userName;
    private String paymentMethod;
    private Long abandonedPetId;
}
