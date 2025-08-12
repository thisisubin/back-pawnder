package com.pawnder.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplyStatsResponse {
    private long reportCount;
    private long adoptionCount;
    private long pendingReports;
    private long pendingAdoptions;
}
