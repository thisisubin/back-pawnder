package com.pawnder.dto;

import com.pawnder.constant.AdoptStatus;
import com.pawnder.entity.AdoptPet;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdoptPetDto {
    private Long id;
    private String userName;
    private String petImageUrl;
    private LocalDateTime appliedAt;
    private AdoptStatus adoptStatus;
    private String location;
    private String type;

    public AdoptPetDto(AdoptPet adoptPet) {
        this.id = adoptPet.getId();
        this.userName = adoptPet.getUser().getUserId();
        this.petImageUrl = adoptPet.getAbandonedPet().getImageUrl();
        this.location = adoptPet.getAbandonedPet().getLocation();
        this.type = adoptPet.getAbandonedPet().getType();
        this.appliedAt = adoptPet.getAppliedAt();
        this.adoptStatus = adoptPet.getAdoptStatus();
    }
}

