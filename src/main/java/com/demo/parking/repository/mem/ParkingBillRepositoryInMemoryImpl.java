package com.demo.parking.repository.mem;

import com.demo.parking.entity.mem.ParkingBillEntity;
import com.demo.parking.repository.ParkingBillRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class ParkingBillRepositoryInMemoryImpl implements ParkingBillRepository {

    final List<ParkingBillEntity> billsHistory = Collections.synchronizedList(new ArrayList<>());

    @Override
    public ParkingBillEntity saveBill(ParkingBillEntity parkingBill) {
        synchronized (billsHistory) {
            long id = CollectionUtils.isEmpty(this.billsHistory) ? 1 : this.billsHistory.getLast().getBillId() + 1;
            parkingBill.setBillId(id);
            this.billsHistory.add(parkingBill);
        }
        return parkingBill;
    }

    @Override
    public void deleteBillByVehiclePlateAndBillId(String vehicleRegistrationNumber, Long billId) {
        synchronized (billsHistory) {
            this.billsHistory.stream()
                    .filter(bill -> bill.getBillId().equals(billId) && bill.getVehicleRegistrationNumber().equals(vehicleRegistrationNumber))
                    .findFirst()
                    .ifPresent(billsHistory::remove);
        }
    }
}
