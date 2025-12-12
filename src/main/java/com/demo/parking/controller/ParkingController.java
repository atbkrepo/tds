package com.demo.parking.controller;

import com.demo.parking.dto.*;
import com.demo.parking.service.ParkingVehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Parking Controller", description = "Endpoints for parking operations")
public class ParkingController {
    private final ParkingVehicleService parkingVehicleService;

    @Operation(description = "Gets available and occupied number of spaces")
    @GetMapping(path = "/parking", produces = "application/json")
    public ResponseEntity<ParkingSlotsDto> getParkinStatus() {
        ParkingSlotsDto result = this.parkingVehicleService.getParkingStatus();
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        log.debug("Parking status retrieved: {}", result);
        return ResponseEntity.ok(result);
    }

    @Operation(description = "Parks a given vehicle in the first available space and returns the vehicle and its space number")
    @PostMapping(path = "/parking", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ParkingResponseDto> parkVehicle(@RequestBody ParkingRequestDto request) {
        ParkingResponseDto result = this.parkingVehicleService.parkVehicle(request);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        log.debug("Car parked successfully: {}", result);
        return ResponseEntity.ok(result);
    }

    @Operation(description = "Frees up this vehicles space and return its final charge from its parking time until now")
    @PostMapping(path = "/parking/bill", produces = "application/json", consumes = "application/json")
    public ResponseEntity<BillResponseDto> billVehicle(@RequestBody BillRequestDto request) {
        BillResponseDto result = this.parkingVehicleService.unParkVehicle(request);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        log.debug("Car billed successfully: {}", result);
        return ResponseEntity.ok(result);
    }
}
