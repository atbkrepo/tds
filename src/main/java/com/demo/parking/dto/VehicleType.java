package com.demo.parking.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum VehicleType {
    CAR_SMALL(1, "Small Car", 0.1),
    CAR_MEDIUM(2, "Medium Car", 0.2),
    CAR_LARGE(3, "Large Car", 0.4);

    @JsonValue
    private final int typeId;
    private final String description;
    private final Double pricePerHour;

    VehicleType(int typeId, String description, Double pricePerHour) {
        this.typeId = typeId;
        this.description = description;
        this.pricePerHour = pricePerHour;
    }
}
