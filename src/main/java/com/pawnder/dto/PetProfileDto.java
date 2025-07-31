package com.pawnder.dto;

import com.pawnder.constant.Gender;
import com.pawnder.constant.Size;
import com.pawnder.entity.Pet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/* 나의 반려동물 프로필 셋팅 DTO */

@Getter
@Setter
@Data
@Schema(name = "반려동물 프로필 DTO")
public class PetProfileDto {
    //이름
    private String name;

    //반려동물 사진
    private String profile; // 프로필 UUID

    //입양여부
    private boolean adopt;

    //종류 (치와와/푸들/등등)
    private String type;

    //몸무게
    private double weight;

    //사이즈 (대형/중형/소형)
    private Size size;

    //나이
    private int age;

    //성별
    private Gender gender;

    //동물 등록 번호
    private String petId;

    // 👉 Entity -> DTO 변환
    public static PetProfileDto fromEntity(Pet pet) {
        PetProfileDto dto = new PetProfileDto();
        dto.setPetId(pet.getPetId());
        dto.setName(pet.getName());
        dto.setType(pet.getType());
        dto.setAdopt(pet.isAdopt());
        dto.setGender(pet.getGender());
        dto.setWeight(pet.getWeight());
        dto.setSize(pet.getSize());
        dto.setAge(pet.getAge());
        dto.setProfile(pet.getProfile());
        return dto;
    }
}
