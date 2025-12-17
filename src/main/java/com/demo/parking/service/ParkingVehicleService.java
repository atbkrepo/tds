package com.demo.parking.service;

import com.demo.parking.dto.*;
import com.demo.parking.entity.mem.ParkingBillEntity;
import com.demo.parking.entity.mem.VehicleEntity;
import com.demo.parking.repository.mem.ParkingSpaceRepositoryInMemoryImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

import static com.demo.parking.constants.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingVehicleService {
    private final ParkingSpaceRepositoryInMemoryImpl repository;
    private final BillingVehicleService billingVehicleService;
    private final ModelMapper modelMapper;

    @Value("${parking.strategy: ANY}")
    private StrategyEnum strategy;

    public ParkingSlotsDto getParkingStatus() {
        int available = this.repository.getAvailableSlots();
        return ParkingSlotsDto.builder()
                .available(available)
                .occupied(TOTAL_SPACES - available)
                .build();
    }

    public ParkingResponseDto parkVehicle(ParkingRequestDto request) {
        VehicleEntity vehicle = this.modelMapper.map(request, VehicleEntity.class);
        vehicle.setParkingStartTime(LocalDateTime.now(Clock.systemUTC()));
        return this.modelMapper.map(this.repository.registerVehicle(vehicle, strategy), ParkingResponseDto.class);
    }

    public synchronized BillResponseDto unParkVehicle(BillRequestDto request) {
        VehicleEntity vehicle = this.repository.getVehicleByPlate(request.getVehicleRegistrationNumber());
        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found in the parking lot");
        }
        ParkingBillEntity parkingBill = this.billingVehicleService.billVehicle(vehicle);
        try {
            this.repository.unRegisterVehicle(vehicle);
        } catch (Exception e) {
            this.billingVehicleService.removeBillVehicle(parkingBill);
            log.error("Error unregistering vehicle: {}", e.getMessage());
            throw e;
        }
        return this.modelMapper.map(parkingBill, BillResponseDto.class);
    }

}
