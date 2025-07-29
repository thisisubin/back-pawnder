package com.pawnder.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.pawnder.entity.AbandonedPetDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AbandonedPetSearchService {

    private final ElasticsearchClient elasticsearchClient;

    public List<AbandonedPetDocument> search(LocalDate foundDate, LocalTime foundTime, String location) {
        try {
            // 쿼리 리스트 생성
            List<Query> mustQueries = new ArrayList<>();

            if (foundDate != null) {
                mustQueries.add(Query.of(q -> q
                        .term(t -> t
                                .field("foundDate")
                                .value(foundDate.toString())
                        )
                ));
            }

            if (foundTime != null) {
                mustQueries.add(Query.of(q -> q
                        .term(m -> m
                                .field("foundTime")
                                .value(foundTime.toString())
                        )
                ));
            }

            if (location != null && !location.isEmpty()) {
                mustQueries.add(Query.of(q -> q
                        .term(t -> t
                                .field("location")
                                .value(location)
                        )
                ));
            }

            // bool 쿼리 조립
            BoolQuery boolQuery = BoolQuery.of(b -> b.must(mustQueries));

            // 검색 요청
            SearchResponse<AbandonedPetDocument> response = elasticsearchClient.search(s -> s
                            .index("abandoned-pets")
                            .query(q -> q
                                    .bool(boolQuery)
                            ),
                    AbandonedPetDocument.class
            );

            // 결과 반환
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // 예외 발생 시 빈 리스트 반환
        }
    }
}

