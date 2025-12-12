package com.demo.parking.service;

import com.demo.parking.dto.VehicleType;
import com.demo.parking.entity.mem.ParkingBillEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("BillingVehicleService Tests")
class BillingVehicleServiceTest {

    private ParkingBillEntity parkingBillEntity;
    private Method calculateBillMethod;

    @BeforeEach
    void setUp(){

        parkingBillEntity = new ParkingBillEntity();
        parkingBillEntity.setVehicleRegistrationNumber("ABC123");
        parkingBillEntity.setParkingSpaceNumber(1);

        calculateBillMethod = ReflectionUtils.findMethod(BillingVehicleService.class, "calculateBill", ParkingBillEntity.class, Double.class).get();
        calculateBillMethod.setAccessible(true);
    }

    @Test
    @DisplayName("Should calculate correct bill for small car parked for exactly 60 sec")
    void testCalculateBill_SmallCar_OneMinute() throws Exception {
        //Small car (0.10/min) parked for exactly 1 minute
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 10, 1, 0);
        
        parkingBillEntity.setParkingStartTime(startTime);
        parkingBillEntity.setParkingEndTime(endTime);

        // when
        calculateBillMethod.invoke(null, parkingBillEntity, VehicleType.CAR_SMALL.getPricePerHour());

        // Then: 1 minute * 0.10, no additional charge because time less < 5 minutes
        assertEquals(0.10, parkingBillEntity.getVehicleCharge(), 0.001);
    }

    @Test
    @DisplayName("Should calculate correct bill for small car parked for exactly 61 sec")
    void testCalculateBill_SmallCar_61sec() throws Exception {
        //small car (0.10/min) parked for exactly 1 minute
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 10, 1, 1);

        parkingBillEntity.setParkingStartTime(startTime);
        parkingBillEntity.setParkingEndTime(endTime);

        // when
        calculateBillMethod.invoke(null, parkingBillEntity, VehicleType.CAR_SMALL.getPricePerHour());

        // then: 2 minute * 0.10, no additional charge because time less < 5 minutes
        assertEquals(0.20, parkingBillEntity.getVehicleCharge(), 0.001);

    }

    @Test
    @DisplayName("Should calculate correct bill for small car parked for exactly 601 sec=> 10 min 1 sec")
    void testCalculateBill_SmallCar_601sec() throws Exception {
        // Small car (0.10/min) parked for exactly 1 minute
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 10, 10, 1);

        parkingBillEntity.setParkingStartTime(startTime);
        parkingBillEntity.setParkingEndTime(endTime);

        // When
        calculateBillMethod.invoke(null, parkingBillEntity, VehicleType.CAR_SMALL.getPricePerHour());

        // Then => 11 minute * 0.10, plus 2 times additional charge by 1
        assertEquals(3.10, parkingBillEntity.getVehicleCharge(), 0.001);
    }


}

