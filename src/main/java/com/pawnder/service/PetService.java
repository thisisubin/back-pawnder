package com.pawnder.service;

import com.pawnder.dto.PetProfileDto;
import com.pawnder.dto.UserLoginDto;
import com.pawnder.entity.Pet;
import com.pawnder.entity.User;
import com.pawnder.repository.PetRepository;
import com.pawnder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    //반려동물 등록 함수
    public void enrollPet(String userId, PetProfileDto petProfileDto, MultipartFile profileImage) throws IOException {
        //1. 유저의 아이디를 가져와서 등록
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        //2. userId로 petProfile 등록 DTO->Entity 변환
        Pet pet = new Pet();
        pet.setName(petProfileDto.getName());
        pet.setAdopt(petProfileDto.isAdopt());
        pet.setType(petProfileDto.getType());
        pet.setWeight(petProfileDto.getWeight());
        pet.setSize(petProfileDto.getSize());
        pet.setAge(petProfileDto.getAge());
        pet.setGender(petProfileDto.getGender());
        pet.setPetId(petProfileDto.getPetId());

        // 프로필 이미지 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            String savedFileName = fileService.uploadFile(profileImage); // 파일 저장 서비스
            pet.setProfile(savedFileName);
        } else {
            pet.setProfile("/images/default-profile.png");
        }


        //3. 연관관계 설정
        pet.setUser(user);

        //4. 저장
        petRepository.save(pet);
    }

    public List<PetProfileDto> getPetsByUserId(String userId) {
        List<Pet> pets = petRepository.findByUserUserId(userId);
        return pets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PetProfileDto convertToDto(Pet pet) {
        PetProfileDto dto = new PetProfileDto();
        dto.setName(pet.getName());
        dto.setAdopt(pet.isAdopt());
        dto.setType(pet.getType());
        dto.setWeight(pet.getWeight());
        dto.setSize(pet.getSize());
        dto.setAge(pet.getAge());
        dto.setGender(pet.getGender());
        dto.setPetId(pet.getPetId());

        // ⭐ 중요: 이미지 URL 설정 (누락되었던 부분)
        String imageUrl = pet.getProfile();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // uploadFile에서 이미 "/uploads/"로 시작하는 경로를 반환하므로
            // 추가적인 경로 조작 없이 그대로 사용
            dto.setProfile(imageUrl);
        } else {
            dto.setProfile(null);
        }


        return dto;
    }

    @Transactional
    public boolean updatePet(String petId, String userId, PetProfileDto petProfileDto, MultipartFile profileImage) throws IOException {
        // 1. 유저 확인
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 2. 기존 pet 조회 (권한 체크 포함)
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려견 정보를 찾을 수 없습니다."));

        if (!pet.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 반려견에 대한 수정 권한이 없습니다.");
        }

        // 3. 필드 수정
        pet.setName(petProfileDto.getName());
        pet.setAdopt(petProfileDto.isAdopt());
        pet.setType(petProfileDto.getType());
        pet.setWeight(petProfileDto.getWeight());
        pet.setSize(petProfileDto.getSize());
        pet.setAge(petProfileDto.getAge());
        pet.setGender(petProfileDto.getGender());

        // 4. 프로필 이미지 변경 처리 (기존 파일 삭제는 선택)
        if (profileImage != null && !profileImage.isEmpty()) {
            // 기존 이미지가 있다면 삭제 (선택 사항)
            if (pet.getProfile() != null && pet.getProfile().startsWith("/uploads/")) {
                fileService.deleteFile(pet.getProfile());
            }

            String savedFileName = fileService.uploadFile(profileImage);
            pet.setProfile(savedFileName);
        }

        // 5. 저장
        petRepository.save(pet);  // 사실상 JPA 영속성 컨텍스트 덕분에 생략해도 자동 반영됨

        return true;
    }

    @Transactional
    public boolean deletePet(String petId, String userId) {
        // 1. 유저 확인
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 2. 반려견 찾기
        Pet pet = petRepository.findByPetId(petId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려견을 찾을 수 없습니다."));

        // 3. 권한 확인
        if (!pet.getUser().getUserId().equals(user.getUserId())) {
            return false;
        }

        // 4. 프로필 이미지 파일 삭제 (선택)
        if (pet.getProfile() != null && pet.getProfile().startsWith("/uploads/")) {
            fileService.deleteFile(pet.getProfile());
        }

        // 5. 삭제
        petRepository.delete(pet);
        return true;
    }


}
