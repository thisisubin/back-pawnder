package com.pawnder.repository;

import com.pawnder.entity.AbandonedPet;
import com.pawnder.entity.AdoptPet;
import com.pawnder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdoptPetRepository extends JpaRepository<AdoptPet, Long> {
    Optional<AdoptPet> findByAbandonedPet(AbandonedPet abandonedPet);

    // 또는 여러 유저가 신청할 수 있는 구조라면
    List<AdoptPet> findAllByAbandonedPet(AbandonedPet abandonedPet);

    // 유저가 특정 유기견에 신청했는지 확인
    Optional<AdoptPet> findByUserAndAbandonedPet(User user, AbandonedPet abandonedPet);
}
