package com.parking.strategy.parking;

import com.parking.entity.ParkingFloor;
import com.parking.entity.ParkingSpot;
import com.parking.entity.vehicle.Vehicle;

import java.util.List;
import java.util.Optional;

public interface ParkingStrategy {
    Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle);
}
