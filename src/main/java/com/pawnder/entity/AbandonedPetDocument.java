package com.pawnder.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(indexName = "abandoned-pets")
@Getter
@Setter
public class AbandonedPetDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String type;            // 품종

    @Field(type = FieldType.Keyword)
    private String gender;          // 성별

    @Field(type = FieldType.Text)
    private String foundDate;

    @Field(type = FieldType.Text)
    private String foundTime; // "13:25:00" 형태로 저장

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String location;


}
