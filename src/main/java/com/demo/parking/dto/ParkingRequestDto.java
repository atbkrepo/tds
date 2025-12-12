package com.demo.parking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingRequestDto {

    @JsonProperty("vehicleReg")
    private String vehicleRegistrationNumber;

    @JsonProperty("vehicleType")
    private VehicleType vehicleType;
}
