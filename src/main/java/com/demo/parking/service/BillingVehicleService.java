package com.demo.parking.service;

import com.demo.parking.entity.mem.ParkingBillEntity;
import com.demo.parking.entity.mem.VehicleEntity;
import com.demo.parking.repository.ParkingBillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.demo.parking.constants.Constants.ADDITIONAL_CHARGE;
import static com.demo.parking.constants.Constants.ADDITIONAL_CHARGE_PERIOD_MIN;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingVehicleService {
    private final ParkingBillRepository repository;
    private final ModelMapper modelMapper;

    public synchronized ParkingBillEntity billVehicle(VehicleEntity vehicle) {
        ParkingBillEntity parkingBill = this.modelMapper.map(vehicle, ParkingBillEntity.class);
        parkingBill.setParkingEndTime(LocalDateTime.now(Clock.systemUTC()));
        calculateBill(parkingBill, vehicle.getVehicleType().getPricePerHour());
        return this.repository.saveBill(parkingBill);
    }

    public void removeBillVehicle(ParkingBillEntity parkingBill) {
        this.repository.deleteBillByVehiclePlateAndBillId(parkingBill.getVehicleRegistrationNumber(), parkingBill.getBillId());
    }

    private static void calculateBill(ParkingBillEntity parkingBill, Double pricePerMin) {
        long sec = ChronoUnit.SECONDS.between(parkingBill.getParkingStartTime(), parkingBill.getParkingEndTime());
        long minutes = sec / 60;
        log.info("Total parked seconds: {}, => in minutes: {}", sec, minutes);

        long remainingSeconds = sec - minutes * 60;
        if (remainingSeconds > 0) {
            minutes += 1;
        }
        log.info("Total billed minutes (rounded up): {}", minutes);

        double vehicleCharge = minutes * pricePerMin;
        log.info("Vehicle charge: {} £, by £{}/minute", vehicleCharge, pricePerMin);

        long additionalChargeCount = minutes / ADDITIONAL_CHARGE_PERIOD_MIN;
        double additionalCharge = additionalChargeCount * ADDITIONAL_CHARGE;
        log.info("Additional charge of £{} by each {} minutes, ({} times), total additional charge: £{} ", ADDITIONAL_CHARGE, ADDITIONAL_CHARGE_PERIOD_MIN, additionalChargeCount, additionalCharge);

        double total = vehicleCharge + additionalCharge;
        parkingBill.setVehicleCharge(total);
        log.info("Total parking bill: £{}", total);
    }


}
