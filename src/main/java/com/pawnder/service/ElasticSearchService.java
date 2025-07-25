package com.pawnder.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.pawnder.entity.AbandonedPet;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final ElasticsearchClient elasticsearchClient;

    public void indexAbandonedPet(AbandonedPet pet) {
        try {
            elasticsearchClient.index(i -> i
                    .index("abandoned_pets") // 인덱스 이름
                    .id(pet.getId().toString())
                    .document(pet)
            );
            log.info("Elasticsearch에 인덱싱 완료: {}", pet.getId());
        } catch (IOException e) {
            log.error("Elasticsearch 인덱싱 실패: {}", e.getMessage());
        }
    }

    //키워드별 유기견 조회
    public List<AbandonedPet> searchAbandonedPets(String keyword) {
        try {
            SearchResponse<AbandonedPet> response = elasticsearchClient.search(s -> s
                            .index("abandoned_pets")
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .query(keyword)
                                            .fields("foundDate","foundTime", "description", "type", "location", "gender")
                                    )
                            ),
                    AbandonedPet.class // 검색 결과를 매핑할 클래스
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
