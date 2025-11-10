package com.deliverit.restaurant.infrastructure.api.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddressResponseDto {
    private List<Document> documents;

    @Data
    public static class Document {
        @JsonProperty("x")
        private Double longitude;

        @JsonProperty("y")
        private Double latitude;
    }
}
