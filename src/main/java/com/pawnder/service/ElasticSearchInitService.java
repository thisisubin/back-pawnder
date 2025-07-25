package com.pawnder.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElasticSearchInitService {
    private final ElasticsearchClient elasticsearchClient;

    public ElasticSearchInitService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @PostConstruct
    public void init() throws IOException {
        try {
            System.out.println("Elasticsearch 연결 시도 중...");
            createAbandonedPetIndex();
        } catch (Exception e) {
            System.err.println("Elasticsearch 초기화 중 오류 발생:");
            e.printStackTrace();
        }
    }

    // 위의 createAbandonedPetIndex() 메서드 여기에 위치


    public void createAbandonedPetIndex() throws IOException {
        String indexName = "abandoned_pets";

        BooleanResponse exists = elasticsearchClient.indices().exists(e -> e.index(indexName));
        if (!exists.value()) {
            elasticsearchClient.indices().create(c -> c
                    .index(indexName)
                    .settings(s -> s
                            .numberOfShards("1")
                            .numberOfReplicas("1")
                    )
                    .mappings(m -> m
                            .properties("foundDate", p -> p.date(d -> d.format("yyyy-MM-dd")))
                            .properties("foundTime", p -> p.date(d -> d.format("HH:mm:ss")))
                            .properties("description", p -> p.text(t -> t))
                            .properties("type", p -> p.keyword(t -> t))
                            .properties("location", p -> p.keyword(t -> t))
                            .properties("gender", p -> p.keyword(t -> t))
                    )
            );
            System.out.println("'abandoned_pets' 인덱스 생성 완료!");
        } else {
            System.out.println("이미 인덱스가 존재합니다.");
        }
    }

}
