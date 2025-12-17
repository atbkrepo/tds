package com.demo.parking.repository;

import com.demo.parking.dto.StrategyEnum;
import com.demo.parking.entity.mem.VehicleEntity;

public interface ParkingSpaceRepository {

    int getAvailableSlots();
    VehicleEntity registerVehicle(VehicleEntity vehicle, StrategyEnum strategy);
    VehicleEntity getVehicleByPlate(String vehicleRegistrationNumber);
    void unRegisterVehicle(VehicleEntity vehicle);
}
