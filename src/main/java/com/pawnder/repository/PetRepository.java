package com.pawnder.repository;

import com.pawnder.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, String> {
    List<Pet> findByUserUserId(String userId);
    Optional<Pet> findByPetId(String petId);


}
