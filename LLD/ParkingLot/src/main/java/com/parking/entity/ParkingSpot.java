package com.parking.entity;

import com.parking.entity.vehicle.Vehicle;
import com.parking.entity.vehicle.VehicleSize;

import java.util.concurrent.atomic.AtomicBoolean;

public class ParkingSpot {

    private final String spotId;
    private final VehicleSize size;
    private final AtomicBoolean occupied = new AtomicBoolean(false);
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
        return occupied.get();
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }


    public boolean parkVehicle(Vehicle parkedVehicle) {
        if (occupied.compareAndSet(false, true)) {
            this.parkedVehicle = parkedVehicle;
            return true;  // successfully claimed the spot
        }
        return false;  // another thread already claimed it


    }

    public synchronized void unparkVehicle() {
        if(parkedVehicle != null){
            parkedVehicle = null;
            occupied.set(false);
        }
    }

    public boolean canFitVehicle(VehicleSize vehicleSize){
        if(occupied.get()) return false;

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
