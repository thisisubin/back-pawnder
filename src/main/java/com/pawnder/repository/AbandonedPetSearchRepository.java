package com.pawnder.repository;

import com.pawnder.entity.AbandonedPetDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDate;
import java.util.List;

public interface AbandonedPetSearchRepository extends ElasticsearchRepository<AbandonedPetDocument, Long> {
    // type으로 검색
    List<AbandonedPetDocument> findByTypeContaining(String type);

    //location으로 검색
    List<AbandonedPetDocument> findByLocationContaining(String location);

    //foundDate으로 검색
    List<AbandonedPetDocument> findByFoundDate(LocalDate foundDate);
    
}
