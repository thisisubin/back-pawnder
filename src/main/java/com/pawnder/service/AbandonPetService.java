package com.pawnder.service;

import com.pawnder.constant.PetStatus;
import com.pawnder.dto.AbandonPetFormDto;
import com.pawnder.entity.AbandonedPet;
import com.pawnder.entity.AbandonedPetForm;
import com.pawnder.entity.User;
import com.pawnder.repository.AbandonedPetRepository;
import com.pawnder.repository.AdoptPetFormRepository;
import com.pawnder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

//유기동물 관련 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class AbandonPetService {
    private final AdoptPetFormRepository adoptPetFormRepository;
    private final UserRepository userRepository;
    private final AbandonedPetRepository abandonedPetRepository;
    private final AbandonedPetElasticService abandonedPetElasticService;

    //유기동물 제보 form을 제보 DB에 저장하는 메서드 (유저)
    public void save(AbandonPetFormDto dto, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        AbandonedPetForm pet = new AbandonedPetForm();
        pet.setGender(dto.getGender());
        pet.setDescription(dto.getDescription());
        pet.setLatitude(dto.getLatitude());
        pet.setLongitude(dto.getLongitude());
        pet.setImageUrl(dto.getImageUrl());
        pet.setFoundDate(dto.getFoundDate());
        pet.setFoundTime(dto.getFoundTime());
        pet.setType(dto.getType());
        pet.setLocation(dto.getLocation());
        pet.setUser(user);

        adoptPetFormRepository.save(pet);
    }

    //유기동물 확정 (관리자 등록) - 제보된 폼에 있는 필드 정보를 관리자가 AbandonedPet 엔티티에 (PROTECTING) 저장
    @Transactional
    public void registerAsAbandonedPet(Long formId) {
        AbandonedPetForm form = adoptPetFormRepository.findById(formId)
                .orElseThrow(() -> new IllegalArgumentException("제보서 없음"));
        AbandonedPet abandonedPet = new AbandonedPet();
        abandonedPet.setGender(form.getGender());
        abandonedPet.setFoundDate(form.getFoundDate());
        abandonedPet.setFoundTime(form.getFoundTime());
        abandonedPet.setDescription(form.getDescription());
        abandonedPet.setCreatedAt(LocalDateTime.now());
        abandonedPet.setImageUrl(form.getImageUrl());
        abandonedPet.setStatus(PetStatus.PROTECTING);
        abandonedPet.setLocation(form.getLocation());
        abandonedPet.setType(form.getType());

        abandonedPetRepository.save(abandonedPet);

        log.info("유기동물 등록 완료: formId={}, petId={}", formId, abandonedPet.getId());

        try {
            abandonedPetElasticService.saveToElasticsearch(abandonedPet);
            log.info("Elasticsearch 저장 성공: petId={}", abandonedPet.getId());
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패: petId={}", abandonedPet.getId(), e);
        }
    }



    //유기동물 목록 조회 (유저 / 관리자 모두 조회 가능)
    public List<AbandonedPet> getAllAbandonedPets() {
        return abandonedPetRepository.findAll();
    }

}

