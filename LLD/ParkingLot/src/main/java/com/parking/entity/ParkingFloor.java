package com.parking.entity;

import com.parking.entity.vehicle.VehicleSize;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ParkingFloor {
    private final int floorNumber;
    private Map<String,ParkingSpot> spots;

    public ParkingFloor(int floorNumber){
        this.floorNumber = floorNumber;
        this.spots = new ConcurrentHashMap<>();
    }

    public void addSpot(ParkingSpot spot){
        this.spots.put(spot.getSpotId(),spot);
    }

    public Optional<ParkingSpot> findSpot(VehicleSize size){
        return spots.values().stream()
                .filter(e -> !e.isOccupied() && e.canFitVehicle(size))
                .sorted(Comparator.comparing(ParkingSpot::getSize))
                .findFirst();
    }

    public void displayAvailability() {
        System.out.printf("--- Floor %d Availability ---\n", floorNumber);
        Map<VehicleSize,Long> spotAvailability = this.spots.values().stream()
                .filter(e -> !e.isOccupied())
                .collect(Collectors.groupingBy(ParkingSpot::getSize,Collectors.counting()));

        for(VehicleSize size : VehicleSize.values()){
            System.out.println(size+ " Available Spots count:: "+ spotAvailability.getOrDefault(size,0L));
        }
    }
}
