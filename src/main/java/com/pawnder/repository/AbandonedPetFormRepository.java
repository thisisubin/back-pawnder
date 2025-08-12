package com.pawnder.repository;

import com.pawnder.constant.PetStatus;
import com.pawnder.entity.AbandonedPet;
import com.pawnder.entity.AbandonedPetForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AbandonedPetFormRepository extends JpaRepository<AbandonedPetForm, Long> {
    List<AbandonedPetForm> findByUserUserId(String userId);

    List<AbandonedPetForm> findByStatus(PetStatus status);
    List<AbandonedPetForm> findByStatusIn(List<PetStatus> statuses);

    Optional<AbandonedPetForm> findByUserUserIdAndStatus(String userId, PetStatus petStatus);

    long countByStatus(PetStatus status);

}
