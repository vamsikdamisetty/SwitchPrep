package com.parking.strategy.fee;

import com.parking.entity.ParkingTicket;
import com.parking.entity.vehicle.VehicleSize;

import java.util.Map;

public class VehicleBasedFeeStrategy implements FeeStrategy{
    private static final Map<VehicleSize, Double> HOURLY_RATES = Map.of(
            VehicleSize.SMALL, 20.0,
            VehicleSize.MEDIUM, 40.0,
            VehicleSize.LARGE, 60.0
    );

    @Override
    public double calculateFee(ParkingTicket parkingTicket) {
        long duration = parkingTicket.getExitTimestamp() - parkingTicket.getEntryTimestamp();
        long hours = (duration / (1000 * 60 * 60)) + 1; // Ceiling to next hr
        return hours * HOURLY_RATES.get(parkingTicket.getVehicle().getVehicleSize());
    }
}
