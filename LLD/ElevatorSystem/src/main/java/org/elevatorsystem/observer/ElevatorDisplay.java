package org.elevatorsystem.observer;

import org.elevatorsystem.models.Elevator;

/**
 * Concrete Observer – prints elevator status to the console whenever notified.
 * Simulates the real-world floor indicator display found near elevator doors.
 */
public class ElevatorDisplay implements ElevatorObserver {

    /**
     * Prints the elevator's current floor and direction to standard output.
     *
     * @param elevator the elevator that triggered the update
     */
    @Override
    public void update(Elevator elevator) {
        System.out.println("[DISPLAY] Elevator " + elevator.getId() +
                " | Current Floor: " + elevator.getCurrentFloor() +
                " | Direction: " + elevator.getCurrentState().getDirection());
    }
}
