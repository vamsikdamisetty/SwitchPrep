package com.parking.entity;

import com.parking.entity.vehicle.Vehicle;

import java.util.UUID;

public class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final long entryTimestamp;
    private long exitTimestamp;

    public ParkingTicket(Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTimestamp = System.currentTimeMillis();
    }

    public long getEntryTimestamp() {
        return entryTimestamp;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public long getExitTimestamp() {
        return exitTimestamp;
    }

    public void closeTicket(){
        this.exitTimestamp = System.currentTimeMillis();
    }

}
