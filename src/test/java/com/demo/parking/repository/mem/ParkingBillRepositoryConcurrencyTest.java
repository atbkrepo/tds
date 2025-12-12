package com.demo.parking.repository.mem;

import com.demo.parking.entity.mem.ParkingBillEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ParkingBillRepository Concurrency Tests")
class ParkingBillRepositoryConcurrencyTest {

    private ParkingBillRepositoryInMemoryImpl repository;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        repository = new ParkingBillRepositoryInMemoryImpl();
        executorService = Executors.newFixedThreadPool(20);
    }

    @RepeatedTest(5)
    @DisplayName("Should handle concurrent bill saves without ID conflicts under high load")
    void testConcurrentSaveBills_NoIdConflicts() throws InterruptedException {
        int numberOfBills = 500;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numberOfBills);


        for (int i = 0; i < numberOfBills; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    ParkingBillEntity bill = createBill("plate-" + index, index);
                    ParkingBillEntity saved = repository.saveBill(bill);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(completionLatch.await(10, TimeUnit.SECONDS), "All saves should complete within timeout");

        //Verify all bills should have unique IDs
        Set<Long> uniqueIds = repository.billsHistory.stream()
                .map(ParkingBillEntity::getBillId)
                        .collect(Collectors.toSet());

        assertEquals(numberOfBills, repository.billsHistory.size(), "All bills should be saved");
        assertEquals(numberOfBills, uniqueIds.size(), "All bill IDs should be unique");
        
        // Verify IDs are sequential from 1 to numberOfBills
        List<Long> sortedIds = new ArrayList<>(uniqueIds);
        Collections.sort(sortedIds);
        for (long i = 1; i <= numberOfBills; i++) {
            assertTrue( sortedIds.contains(i), "IDs should be sequential");
        }
    }

    private ParkingBillEntity createBill(String vehicleNumber, int spaceNumber) {
        return ParkingBillEntity.builder()
                .vehicleRegistrationNumber(vehicleNumber)
                .parkingSpaceNumber(spaceNumber)
                .vehicleCharge(10.0)
                .parkingStartTime(LocalDateTime.now().minusHours(1))
                .parkingEndTime(LocalDateTime.now())
                .build();
    }
}

