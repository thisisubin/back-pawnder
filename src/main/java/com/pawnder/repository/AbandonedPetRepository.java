package com.pawnder.repository;

import com.pawnder.constant.PetStatus;
import com.pawnder.entity.AbandonedPet;
import com.pawnder.entity.AbandonedPetForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AbandonedPetRepository extends JpaRepository<AbandonedPet, Long> {
    List<AbandonedPet> findByStatus(PetStatus status);


    Optional<AbandonedPet> findByAbandonedPetForm_Id(Long abandonedPetFormId);

}
