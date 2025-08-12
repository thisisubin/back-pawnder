package com.pawnder.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DonationStatsResponse {
    private long totalAmount;
    private long monthlyCount;
}
