package com.demo.parking.repository.mem;

import com.demo.parking.entity.mem.VehicleEntity;
import com.demo.parking.repository.ParkingSpaceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.demo.parking.constants.Constants.TOTAL_SPACES;

@Repository
public class ParkingSpaceRepositoryInMemoryImpl implements ParkingSpaceRepository {

    // vehicleNum/vehicle
    final Map<String, VehicleEntity> parkingSpace = new ConcurrentHashMap<>(TOTAL_SPACES);
    final Set<Integer> freeParkingSpace = ConcurrentHashMap.newKeySet(TOTAL_SPACES);

    @PostConstruct
    void init() {
        for (int i = 1; i <= TOTAL_SPACES; i++) {
            this.freeParkingSpace.add(i);
        }
    }

    @Override
    public int getAvailableSlots() {
        return this.freeParkingSpace.size();
    }

    @Override
    public synchronized VehicleEntity registerVehicle(VehicleEntity vehicle) {
        if (this.parkingSpace.containsKey(vehicle.getVehicleRegistrationNumber())) {
            throw new RuntimeException("Vehicle is already registered");
        }
        Integer spaceNum = this.freeParkingSpace.stream().findAny().orElseThrow(() -> new RuntimeException("no space available"));
        vehicle.setParkingSpaceNumber(spaceNum);
        this.parkingSpace.put(vehicle.getVehicleRegistrationNumber(), vehicle);
        this.freeParkingSpace.remove(spaceNum);
        return vehicle;
    }

    @Override
    public VehicleEntity getVehicleByPlate(String vehicleRegistrationNumber) {
        return this.parkingSpace.get(vehicleRegistrationNumber);
    }

    @Override
    public synchronized void unRegisterVehicle(VehicleEntity vehicle) {
        if (!this.parkingSpace.containsKey(vehicle.getVehicleRegistrationNumber())) {
            throw new RuntimeException("Vehicle is not registered");
        }
        this.parkingSpace.remove(vehicle.getVehicleRegistrationNumber());
        this.freeParkingSpace.add(vehicle.getParkingSpaceNumber());
    }
}
