package com.pawnder.repository;

import com.pawnder.constant.AdoptStatus;
import com.pawnder.dto.AdoptPetDto;
import com.pawnder.entity.AbandonedPet;
import com.pawnder.entity.AdoptPet;
import com.pawnder.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface AdoptPetRepository extends JpaRepository<AdoptPet, Long> {
    Optional<AdoptPet> findByAbandonedPet(AbandonedPet abandonedPet);

    // 관리자용 - 기본 메서드 사용
    List<AdoptPet> findAll();

    // 유저가 특정 유기견에 신청했는지 확인
    Optional<AdoptPet> findByUserAndAbandonedPet(User user, AbandonedPet abandonedPet);

    Optional<AdoptPet> findByUserUserIdAndAdoptStatus(String userId, AdoptStatus adoptStatus);

}
