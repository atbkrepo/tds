package com.demo.parking.repository.mem;

import com.demo.parking.dto.StrategyEnum;
import com.demo.parking.dto.VehicleType;
import com.demo.parking.entity.mem.VehicleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.demo.parking.constants.Constants.TOTAL_SPACES;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ParkingSpaceRepository Concurrency Tests")
class ParkingSpaceRepositoryConcurrencyTest {

    private ParkingSpaceRepositoryInMemoryImpl repository;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        repository = new ParkingSpaceRepositoryInMemoryImpl();
        repository.init();
        executorService = Executors.newFixedThreadPool(50);
    }

    @RepeatedTest(5)
    @DisplayName("Concurrent vehicle registration should not exceed parking capacity")
    void testConcurrentRegistration_ShouldNotExceedCapacity() throws InterruptedException {
        int numberOfVehicles = TOTAL_SPACES * 5; // 50 vehicles for 10 spaces
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numberOfVehicles);

        AtomicInteger failedRegistrations = new AtomicInteger(0);

        //multiple threads try to register vehicles in parallel
        for (int i = 0; i < numberOfVehicles; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    VehicleEntity vehicle = createVehicle("plate-" + index);
                    VehicleEntity registered = repository.registerVehicle(vehicle, StrategyEnum.ANY);
                } catch (Exception e) {
                    failedRegistrations.incrementAndGet();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(completionLatch.await(10, TimeUnit.SECONDS), "All registration attempts should complete in time of 10 seconds");

        //verify exactly TOTAL_SPACES vehicles are registered
        assertEquals(TOTAL_SPACES, repository.parkingSpace.size(), TOTAL_SPACES + " vehicles should be registered");
        assertEquals(numberOfVehicles - TOTAL_SPACES, failedRegistrations.get(), "Remaining vehicles should fail with registration");
        assertEquals(0, repository.getAvailableSlots(), "No parking slots should be available");

        // check that all parking space numbers are unique
        Set<Integer> assignedSpaces = repository.parkingSpace.values().stream()
                .map(VehicleEntity::getParkingSpaceNumber)
                .collect(Collectors.toSet());
        assertEquals(TOTAL_SPACES, assignedSpaces.size(), "All parking space numbers should be unique");
    }

    @RepeatedTest(5)
    @DisplayName("Concurrent registration and unregistration should be consistent")
    void testConcurrentRegistrationAndUnregistration_isConsist() throws InterruptedException {
        int numberOfOperations = 500;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numberOfOperations);

        List<VehicleEntity> registeredVehicles = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger successfulRegistrations = new AtomicInteger(0);
        AtomicInteger successfulUnregistrations = new AtomicInteger(0);

        //intensive registration and unregistration operations
        for (int i = 0; i < numberOfOperations; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    if (index % 2 == 0) {//evry second task is register
                        VehicleEntity vehicle = createVehicle("plate-" + index);
                        try {
                            VehicleEntity registered = repository.registerVehicle(vehicle, StrategyEnum.ANY);
                            registeredVehicles.add(registered);
                            successfulRegistrations.incrementAndGet();
                        } catch (RuntimeException ignored) {
                            //parking full or already registered
                        }
                    } else {
                        // for unregistration - pick a random registered vehicle
                        if (!registeredVehicles.isEmpty()) {
                            try {
                                VehicleEntity forDeletion = registeredVehicles.get(ThreadLocalRandom.current().nextInt(registeredVehicles.size()));
                                repository.unRegisterVehicle(forDeletion);
                                registeredVehicles.remove(forDeletion);
                                successfulUnregistrations.incrementAndGet();
                            } catch (RuntimeException ignored) {
                                // already unregistered
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(completionLatch.await(15, TimeUnit.SECONDS), "All operations should complete");

        //check consistency
        int sr = successfulRegistrations.get();
        int su = successfulUnregistrations.get();
        int expectedOccupied = sr - su;
        int actualAvailable = repository.getAvailableSlots();

        assertEquals(actualAvailable, TOTAL_SPACES - expectedOccupied, "Occupied slots should match expected count");
    }


    private VehicleEntity createVehicle(String registrationNumber) {
        return VehicleEntity.builder()
                .vehicleRegistrationNumber(registrationNumber)
                .parkingStartTime(LocalDateTime.now())
                .vehicleType(VehicleType.CAR_MEDIUM)
                .build();
    }
}

