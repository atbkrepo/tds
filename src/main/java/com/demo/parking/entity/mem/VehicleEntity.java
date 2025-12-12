package com.demo.parking.entity.mem;

import com.demo.parking.dto.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleEntity {
    private String vehicleRegistrationNumber;
    private LocalDateTime parkingStartTime;
    private VehicleType vehicleType;
    private Integer parkingSpaceNumber;
}
