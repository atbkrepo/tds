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
public class BillResponseDto {

    @JsonProperty("billId")
    private Long billId;

    @JsonProperty("vehicleReg")
    private String vehicleRegistrationNumber;

    @JsonProperty("vehicleCharge")
    private Double vehicleCharge;

    @JsonProperty("timeIn")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private LocalDateTime parkingStartTime;

    @JsonProperty("timeOut")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private LocalDateTime parkingEndTime;
}
