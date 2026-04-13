package com.parking.strategy.fee;

import com.parking.entity.ParkingTicket;

public class FlatFeeStrategy implements FeeStrategy{

    private static final int RATE_PER_HOUR =40;

    @Override
    public double calculateFee(ParkingTicket ticket) {
        long duration = ticket.getExitTimestamp() - ticket.getEntryTimestamp();
        long hours = (duration / (1000 * 60 * 60)) + 1;
        return hours*RATE_PER_HOUR;
    }
}
