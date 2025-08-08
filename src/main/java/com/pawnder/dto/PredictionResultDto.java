package com.pawnder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
public class PredictionResultDto {

    private String id; //아이디
    private String project; //프로젝트 이름
    private String iteration; //Azure 모델 이름
    private String created;
    public List<Prediction> predictions;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class Prediction {
        private double probability;
        private String tagId;
        private String tagName;
    }
}
