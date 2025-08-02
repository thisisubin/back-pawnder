package com.pawnder.service;

/* 입양 관련 서비스 로직 */

import com.pawnder.constant.AdoptStatus;
import com.pawnder.constant.PetStatus;
import com.pawnder.entity.AbandonedPet;
import com.pawnder.entity.AbandonedPetForm;
import com.pawnder.entity.AdoptPet;
import com.pawnder.entity.User;
import com.pawnder.repository.AbandonedPetFormRepository;
import com.pawnder.repository.AbandonedPetRepository;
import com.pawnder.repository.AdoptPetRepository;
import com.pawnder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AdoptPetService {
    private final UserRepository userRepository;
    private final AbandonedPetRepository abandonedPetRepository;
    private final AbandonedPetFormRepository abandonedPetFormRepository;
    private final AdoptPetRepository adoptPetRepository;

    // AdoptPetService
    //유저 -> 입양 신청
    public void applyAdoption(Long petId, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        AbandonedPet pet = abandonedPetRepository.findByAbandonedPetForm_Id(petId)
                .orElseThrow(() -> new IllegalArgumentException("유기견 없음"));
        AbandonedPetForm abandonedPetForm = abandonedPetFormRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("유기견 폼 없음"));

        // 상태 변경
        pet.setStatus(PetStatus.WAITING);
        abandonedPetForm.setStatus(PetStatus.WAITING);

        // AdoptPet 기록
        AdoptPet adoptPet = new AdoptPet();
        adoptPet.setUser(user);
        adoptPet.setAbandonedPet(pet);
        adoptPet.setAdoptStatus(AdoptStatus.REQUESTED);
        adoptPet.setAppliedAt(LocalDateTime.now());
        adoptPetRepository.save(adoptPet);
    }

    //관리자 -> 입양 승인
    public void approveAdoption(Long adoptPetId) {
        // 1. AdoptPet 조회
        AdoptPet adoptPet = adoptPetRepository.findById(adoptPetId)
                .orElseThrow(() -> new IllegalArgumentException("입양신청 없음"));

        // 2. 해당 입양 신청에 연결된 유기견 조회
        AbandonedPet pet = adoptPet.getAbandonedPet();
        if (pet == null) throw new IllegalArgumentException("입양신청에 연결된 유기견 없음");

        // 3. 유기견 폼 조회
        AbandonedPetForm form = abandonedPetFormRepository.findById(pet.getAbandonedPetForm().getId())
                .orElseThrow(() -> new IllegalArgumentException("유기견 폼 없음"));

        // 4. 상태 변경
        pet.setStatus(PetStatus.ADOPT);
        form.setStatus(PetStatus.ADOPT);
        adoptPet.setAdoptStatus(AdoptStatus.APPROVED);
        adoptPet.setApprovedAt(LocalDateTime.now());
    }


}
