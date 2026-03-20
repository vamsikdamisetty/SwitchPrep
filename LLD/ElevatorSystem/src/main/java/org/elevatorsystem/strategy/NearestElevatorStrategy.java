package org.elevatorsystem.strategy;

import org.elevatorsystem.constants.Direction;
import org.elevatorsystem.models.Elevator;
import org.elevatorsystem.models.Request;

import java.util.List;
import java.util.Optional;

/**
 * Concrete Strategy – selects the nearest <b>suitable</b> elevator for an external request.
 * <p>
 * "Suitable" means:
 * <ol>
 *   <li>The elevator is IDLE (available immediately), <b>OR</b></li>
 *   <li>The elevator is already moving in the same direction as the request
 *       and hasn't yet passed the requested floor.</li>
 * </ol>
 * Among all suitable elevators, the one with the smallest distance to the
 * requested floor is chosen.
 */
public class NearestElevatorStrategy implements ElevatorStrategy {

    /**
     * Iterates through all elevators, filters by suitability, and picks the
     * one with the minimum distance to the request's target floor.
     *
     * @param elevators list of all elevators in the system
     * @param request   the external request to dispatch
     * @return the nearest suitable elevator, or {@link Optional#empty()} if none qualifies
     */
    @Override
    public Optional<Elevator> selectElevator(List<Elevator> elevators, Request request) {

        int minDistance = Integer.MAX_VALUE;
        Elevator bestElevator = null;

        for (Elevator elevator : elevators) {
            // Only consider elevators that can serve this request efficiently
            if (isSuitable(elevator, request)) {
                int distance = Math.abs(elevator.getCurrentFloor() - request.getTargetFloor());

                // Track the elevator with the smallest distance
                if (minDistance > distance) {
                    minDistance = distance;
                    bestElevator = elevator;
                }
            }
        }

        return Optional.ofNullable(bestElevator);
    }

    /**
     * Determines whether an elevator can efficiently serve the given request
     * without making a detour.
     * <p>
     * Rules:
     * <ul>
     *   <li>An IDLE elevator is always suitable.</li>
     *   <li>A moving elevator is suitable only if it's heading in the same direction
     *       as the request AND hasn't passed the requested floor yet.</li>
     * </ul>
     *
     * @param elevator the elevator to evaluate
     * @param request  the external request
     * @return {@code true} if the elevator can take this request on-the-way
     */
    private boolean isSuitable(Elevator elevator, Request request) {

        // An idle elevator can serve any request
        if (elevator.getCurrentState().getDirection() == Direction.IDLE) {
            return true;
        }

        // Elevator is moving in the same direction as the request
        if (elevator.getCurrentState().getDirection() == request.getDirection()) {
            // Going UP – suitable only if the elevator is at or below the target floor
            if (request.getDirection() == Direction.UP && elevator.getCurrentFloor() <= request.getTargetFloor())
                return true;
            // Going DOWN – suitable only if the elevator is at or above the target floor
            if (request.getDirection() == Direction.DOWN && elevator.getCurrentFloor() >= request.getTargetFloor())
                return true;
        }

        // Elevator is moving in the opposite direction → not suitable
        return false;
    }
}
