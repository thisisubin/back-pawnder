package com.pawnder.repository;

import com.pawnder.entity.AbandonedPet;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbandonedPetRepository extends JpaRepository<AbandonedPet, Long> {
}
