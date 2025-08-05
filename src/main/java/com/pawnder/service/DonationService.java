package com.pawnder.service;

import com.pawnder.dto.DonationVerifyRequest;
import com.pawnder.entity.Donation;
import com.pawnder.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    @Transactional
    public void saveDonation(DonationVerifyRequest donationVerifyRequest) {
        Donation donation = new Donation();
        donation.setImpUid(donationVerifyRequest.getImpUid());
        donation.setMerchantUid(donationVerifyRequest.getMerchantUid());
        donation.setAmount(donationVerifyRequest.getAmount());
        donation.setUserName(donationVerifyRequest.getUserName());
        donation.setPaymentMethod(donationVerifyRequest.getPaymentMethod());
        donation.setAbandonedPetId(donationVerifyRequest.getAbandonedPetId());
        donation.setDonatedAt(LocalDateTime.now());

        donationRepository.save(donation);
    }
}
