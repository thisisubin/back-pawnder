package com.pawnder.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@AllArgsConstructor
public class AbandonedStatusResponse {
    private long lost;
    private long protecting;
    private long waiting;
    private long adopt;
}
