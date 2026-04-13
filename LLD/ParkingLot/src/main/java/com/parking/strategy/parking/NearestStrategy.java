package com.parking.strategy.parking;

import com.parking.entity.ParkingFloor;
import com.parking.entity.ParkingSpot;
import com.parking.entity.vehicle.Vehicle;

import java.util.List;
import java.util.Optional;

public class NearestStrategy implements ParkingStrategy{
    @Override
    public Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle) {
        for(ParkingFloor floor:floors){
            Optional<ParkingSpot> spot = floor.findSpot(vehicle.getVehicleSize());

            if(spot.isPresent()){
                return Optional.of(spot.get());
            }
        }
        return Optional.empty();
    }
}
