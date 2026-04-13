package com.parking.service;

import com.parking.entity.ParkingFloor;
import com.parking.entity.ParkingSpot;
import com.parking.entity.ParkingTicket;
import com.parking.entity.vehicle.Vehicle;
import com.parking.strategy.fee.FeeStrategy;
import com.parking.strategy.fee.VehicleBasedFeeStrategy;
import com.parking.strategy.parking.BestFitStrategy;
import com.parking.strategy.parking.ParkingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLot {

    private static ParkingLot instance ;
    private final List<ParkingFloor> parkingFloors = new ArrayList<>();
    private final Map<String, ParkingTicket> activeTickets;
    private ParkingStrategy parkingStrategy;
    private FeeStrategy feeStrategy;


    private ParkingLot(){
        this.feeStrategy = new VehicleBasedFeeStrategy();
        this.parkingStrategy = new BestFitStrategy();
        activeTickets = new ConcurrentHashMap<>();
    }

    public synchronized static ParkingLot getInstance(){
        if(instance == null){
            instance = new ParkingLot();
        }
        return instance;
    }

    public void addFloor(ParkingFloor floor) {
        parkingFloors.add(floor);
    }

    public void setFeeStrategy (FeeStrategy feeStrategy) {
        this.feeStrategy = feeStrategy;
    }

    public void setParkingStrategy(ParkingStrategy parkingStrategy) {
        this.parkingStrategy = parkingStrategy;
    }

    public Optional<ParkingTicket> parkVehicle(Vehicle vehicle) {
        Optional<ParkingSpot> availableSpot = parkingStrategy.findSpot(parkingFloors, vehicle);

        if (availableSpot.isPresent()) {
            ParkingSpot spot = availableSpot.get();
            spot.parkVehicle(vehicle);
            ParkingTicket ticket = new ParkingTicket(vehicle, spot);
            activeTickets.put(vehicle.getLicenseNo(), ticket);
            System.out.printf("%s parked at %s. Ticket: %s\n", vehicle.getLicenseNo(), spot.getSpotId(), ticket.getTicketId());
            return Optional.of(ticket);
        }

        System.out.println("No available spot for " + vehicle.getLicenseNo());
        return Optional.empty();
    }

    public Optional<Double> unparkVehicle(String licenseNumber) {
        ParkingTicket ticket = activeTickets.remove(licenseNumber);
        if (ticket == null) {
            System.out.println("Ticket not found");
            return Optional.empty();
        }

        ticket.closeTicket();
        ticket.getSpot().unparkVehicle();

        Double parkingFee = feeStrategy.calculateFee(ticket);

        return Optional.of(parkingFee);
    }
}
