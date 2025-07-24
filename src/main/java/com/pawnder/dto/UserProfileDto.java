package com.pawnder.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class UserProfileDto {
    //사용자 이름
    private String userId;
    //사용자의 반려동물 리스트
    List<PetProfileDto> petProfileDtoList;

}
