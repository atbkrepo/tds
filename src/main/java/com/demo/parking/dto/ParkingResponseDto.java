package com.demo.parking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingResponseDto {

    @JsonProperty("vehicleReg")
    private String vehicleRegistrationNumber;

    @JsonProperty("spaceNumber")
    private Integer parkingSpaceNumber;

    @JsonProperty("timeIn")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private LocalDateTime parkingStartTime;
}
