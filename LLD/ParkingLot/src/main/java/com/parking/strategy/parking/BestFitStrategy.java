package com.parking.strategy.parking;

import com.parking.entity.ParkingFloor;
import com.parking.entity.ParkingSpot;
import com.parking.entity.vehicle.Vehicle;

import java.util.List;
import java.util.Optional;

public class BestFitStrategy implements ParkingStrategy{

    @Override
    public Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle) {

        ParkingSpot bestSpot = null;

        for(ParkingFloor floor:floors){
            Optional<ParkingSpot> spot = floor.findSpot(vehicle.getVehicleSize());

            if(spot.isPresent()){
                if(bestSpot == null){
                    bestSpot = spot.get();
                }else{
                    if(bestSpot.getSize().ordinal() > spot.get().getSize().ordinal()){
                        bestSpot = spot.get();
                    }
                }
            }
        }
        return Optional.ofNullable(bestSpot);
    }
}
