package com.parking.strategy.fee;

import com.parking.entity.ParkingTicket;

public interface FeeStrategy {
    double calculateFee(ParkingTicket ticket);
}
