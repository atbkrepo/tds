package com.demo.parking.repository;

import com.demo.parking.entity.mem.ParkingBillEntity;

public interface ParkingBillRepository {
    ParkingBillEntity saveBill(ParkingBillEntity parkingBill);

    void deleteBillByVehiclePlateAndBillId(String vehicleRegistrationNumber, Long billId);
}
