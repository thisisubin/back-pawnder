package com.pawnder.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.pawnder.entity.AbandonedPet;
import com.pawnder.entity.AbandonedPetDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AbandonedPetElasticService {
    private final ElasticsearchClient elasticsearchClient;

    public void saveToElasticsearch(AbandonedPet pet) {
        try {
            AbandonedPetDocument doc = new AbandonedPetDocument();
            doc.setId(pet.getId());
            doc.setLocation(pet.getLocation()); // 직접 필드에 맞게 매핑
            doc.setGender(pet.getGender());
            doc.setType(pet.getType());
            doc.setFoundDate(pet.getFoundDate().toString());
            doc.setFoundTime(pet.getFoundTime().toString());

            elasticsearchClient.index(i -> i
                    .index("abandoned-pets")
                    .id(String.valueOf(doc.getId()))
                    .document(doc)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
