package com.parking.entity;

import com.parking.entity.vehicle.Vehicle;
import com.parking.entity.vehicle.VehicleSize;

public class ParkingSpot {

    private final String spotId;
    private final VehicleSize size;
    private boolean occupied;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, VehicleSize size) {
        this.spotId = spotId;
        this.size = size;
    }

    public String getSpotId() {
        return spotId;
    }

    public VehicleSize getSize() {
        return size;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }


    public synchronized void parkVehicle(Vehicle parkedVehicle) {
        this.parkedVehicle = parkedVehicle;
        this.occupied = true;
    }

    public synchronized void unparkVehicle() {
        if(parkedVehicle != null){
            parkedVehicle = null;
            this.occupied = false;
        }
    }

    public boolean canFitVehicle(VehicleSize vehicleSize){
        if(occupied) return false;

        switch (vehicleSize){
            case SMALL -> {
                return this.size == VehicleSize.SMALL ? true:false;
            }
            case MEDIUM -> {
                return  this.size == VehicleSize.MEDIUM || this.size == VehicleSize.LARGE ? true: false;
            }
            case LARGE -> {
                return this.size == VehicleSize.LARGE ? true:false;
            }
            default -> {
                return false;
            }
        }
    }
}
