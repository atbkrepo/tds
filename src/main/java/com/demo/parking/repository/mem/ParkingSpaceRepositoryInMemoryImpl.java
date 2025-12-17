package com.demo.parking.repository.mem;

import com.demo.parking.dto.StrategyEnum;
import com.demo.parking.entity.mem.VehicleEntity;
import com.demo.parking.repository.ParkingSpaceRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.demo.parking.constants.Constants.TOTAL_SPACES;

@Repository
@Slf4j
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
    public synchronized VehicleEntity registerVehicle(VehicleEntity vehicle, StrategyEnum strategy) {
        if (this.parkingSpace.containsKey(vehicle.getVehicleRegistrationNumber())) {
            throw new RuntimeException("Vehicle is already registered");
        }

        Integer spaceNum = this.getSpaceNumByStrategy(strategy);

        vehicle.setParkingSpaceNumber(spaceNum);
        this.parkingSpace.put(vehicle.getVehicleRegistrationNumber(), vehicle);
        this.freeParkingSpace.remove(spaceNum);
        return vehicle;
    }

    private Integer getSpaceNumByStrategy(StrategyEnum strategy) {
        Stream<Integer> stream = this.freeParkingSpace.stream();

        Optional<Integer> spaceNumOptional = Optional.empty();
        switch (strategy) {
            case ANY -> spaceNumOptional = stream.findAny();
            case MIN -> spaceNumOptional = stream.min(Integer::compareTo);
            case MAX -> spaceNumOptional = stream.max(Integer::compareTo);
        }
        Integer num = spaceNumOptional.orElseThrow(() -> new RuntimeException("no space available"));
        log.info("Strategy: {}, space number: {}", strategy.name(), num);
        return num;
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
