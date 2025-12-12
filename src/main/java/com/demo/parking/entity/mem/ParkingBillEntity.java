package com.demo.parking.entity.mem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingBillEntity {

    private Long billId;
    private Integer parkingSpaceNumber;
    private String vehicleRegistrationNumber;
    private Double vehicleCharge;
    private LocalDateTime parkingStartTime;
    private LocalDateTime parkingEndTime;
}
