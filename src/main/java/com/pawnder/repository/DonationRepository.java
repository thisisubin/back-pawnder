package com.pawnder.repository;

import com.pawnder.dto.DonationVerifyRequest;
import com.pawnder.dto.MyDonationDto;
import com.pawnder.entity.Donation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByUserName(String userId);


    @Query("SELECT new com.pawnder.dto.MyDonationDto(d.id, d.amount, d.donatedAt, p.status, p.id, p.type, p.imageUrl) " +
            "FROM Donation d JOIN AbandonedPet p ON d.abandonedPetId = p.id " +
            "WHERE d.userName = :userName")
    List<MyDonationDto> findMyDonationsWithPet(@Param("userName") String userName);


    //전체 후원 총금액
    @Query("SELECT COALESCE(SUM(d.amount),0) FROM Donation d")
    long getTotalAmount();

    //월별 후원 총금액
    @Query("SELECT COUNT(d) FROM Donation d WHERE MONTH(d.donatedAt) = MONTH(CURRENT_DATE) AND YEAR(d.donatedAt) = YEAR(CURRENT_DATE)")
    long getMonthlyCount();


}
