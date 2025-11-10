package com.deliverit.restaurant.infrastructure.api.map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Coordinates {
    private Double longitude;
    private Double latitude;
}
