package com.pawnder.service;

import com.pawnder.constant.PetStatus;
import com.pawnder.dto.AbandonPetFormDto;
import com.pawnder.dto.dashboard.AbandonedStatusResponse;
import com.pawnder.dto.dashboard.ApplyStatsResponse;
import com.pawnder.dto.dashboard.DonationStatsResponse;
import com.pawnder.repository.AbandonedPetFormRepository;
import com.pawnder.repository.AbandonedPetRepository;
import com.pawnder.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final AbandonedPetRepository abandonedPetRepository;
    private final DonationRepository donationRepository;
    private final AbandonedPetFormRepository abandonedPetFormRepository;

    public AbandonedStatusResponse getAbandonedPetStatus() {
        long lost = abandonedPetRepository.countByStatus(PetStatus.LOST);
        long protecting = abandonedPetRepository.countByStatus(PetStatus.PROTECTING);
        long waiting = abandonedPetRepository.countByStatus(PetStatus.WAITING);
        long adopt = abandonedPetRepository.countByStatus(PetStatus.ADOPT);

        return new AbandonedStatusResponse(lost, protecting, waiting, adopt);
    }

    public DonationStatsResponse getDonationStats() {
        long totalAmount = donationRepository.getTotalAmount();
        long monthlyCount = donationRepository.getMonthlyCount();

        return new DonationStatsResponse(totalAmount, monthlyCount);
    }

    public ApplyStatsResponse getApplicationStats() {
        long reportCount = abandonedPetFormRepository.countByStatus(PetStatus.PROTECTING); //제보등록수
        long adoptionCount = abandonedPetRepository.count(); //입양된 견수
        long pendingReports = abandonedPetFormRepository.countByStatus(PetStatus.LOST); //제보수
        long pendingAdoptions = abandonedPetRepository.countByStatus(PetStatus.WAITING); //입양대기 수

        return new ApplyStatsResponse(reportCount, adoptionCount, pendingReports, pendingAdoptions);
    }
}

