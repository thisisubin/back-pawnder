package com.pawnder.service;

import com.pawnder.constant.PetStatus;
import com.pawnder.dto.AbandonPetFormDto;
import com.pawnder.dto.PredictionResultDto;
import com.pawnder.entity.AbandonedPet;
import com.pawnder.entity.AbandonedPetForm;
import com.pawnder.entity.User;
import com.pawnder.repository.AbandonedPetRepository;
import com.pawnder.repository.AbandonedPetFormRepository;
import com.pawnder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

//유기동물 관련 서비스 - 제보
@Slf4j
@Service
@RequiredArgsConstructor
public class AbandonPetService {
    private final AbandonedPetFormRepository abandonedPetFormRepository;
    private final UserRepository userRepository;
    private final AbandonedPetRepository abandonedPetRepository;
    private final AbandonedPetElasticService abandonedPetElasticService;
    private final FileService fileService;
    private final CustomVisionService customVisionService;
    private final NotificationService notificationService;
    private final SmsService smsService;


    //유기동물 제보 form을 제보 DB에 저장하는 메서드 (유저)
    public void save(AbandonPetFormDto dto, String userId, MultipartFile imagurl) throws IOException {
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
        pet.setLocation(dto.getLocation());
        pet.setStatus(PetStatus.LOST);
        pet.setUser(user);

        // 1. 이미지로 품종 예측
        String predictedType = "믹스견";
        if (imagurl != null && !imagurl.isEmpty()) {
            byte[] imageBytes = imagurl.getBytes();
            PredictionResultDto.Prediction topPrediction = customVisionService.getTopPrediction(imageBytes);
            predictedType = topPrediction.getTagName(); // 예: "Bichon", "Poodle"
        }

        log.info("예측된 품종 타입 : " + predictedType);

        // 2. 예측된 품종으로 type 설정
        pet.setType(predictedType);


        // 프로필 이미지 처리
        if (imagurl != null && !imagurl.isEmpty()) {
            String savedFileName = fileService.uploadFile(imagurl); // 파일 저장 서비스
            pet.setImageUrl(savedFileName);
        } else {
            pet.setImageUrl("/images/default-profile.png");
        }

        // 유기견 제보 알림
        notificationService.sendNotification("admin", userId, pet.getFoundDate()+"에 발견된 유기견 제보가 접수되었습니다.", "ABANDONED_PET");

        abandonedPetFormRepository.save(pet);
    }

    //유기동물 확정 (관리자 등록) - 제보된 폼에 있는 필드 정보를 관리자가 AbandonedPet 엔티티에 (PROTECTING) 저장
    @Transactional
    public void registerAsAbandonedPet(Long formId) {
        AbandonedPetForm form = abandonedPetFormRepository.findById(formId)
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
        abandonedPet.setAbandonedPetForm(form);

        //AbandonedPetFormRepository에서 id를 꺼내와서
        //그 id의 Status를 PROTECTING으로 Update
        form.setStatus(PetStatus.PROTECTING);

        abandonedPetRepository.save(abandonedPet);

        //유기견 제보를 등록했다고 해당 유저에게 알림 전송
        notificationService.sendNotification(form.getUser().getUserId(), "admin", "관리자가 유기견 제보를 접수했습니다.", "ADMIN_ALERT");

        smsService.sendSMS(form.getUser().getPhoneNm(), "[Pawnder] 관리자가 유기견 제보를 접수했습니다.");

        log.info("유기동물 등록 완료: formId={}, petId={}", formId, abandonedPet.getId());

        try {
            abandonedPetElasticService.saveToElasticsearch(abandonedPet);
            log.info("Elasticsearch 저장 성공: petId={}", abandonedPet.getId());
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패: petId={}", abandonedPet.getId(), e);
        }
    }

}

