package org.elevatorsystem.state;

import org.elevatorsystem.constants.Direction;
import org.elevatorsystem.constants.RequestSource;
import org.elevatorsystem.models.Elevator;
import org.elevatorsystem.models.Request;

/**
 * State when the elevator is actively moving downward.
 * <p>
 * Each call to {@code move()} decrements the floor by 1.
 * When the elevator reaches a requested floor, that floor is removed from the queue.
 * Once all down-requests are served, the elevator transitions back to {@link IdleState}.
 */
public class MovingDown implements ElevatorState {

    /**
     * Moves the elevator one floor down per tick.
     * <ul>
     *   <li>If the down-request queue is empty, transition to Idle.</li>
     *   <li>Otherwise, move down by 1 floor.</li>
     *   <li>If the new floor matches the nearest down-request, stop and remove it from the queue.</li>
     *   <li>After serving, if no more down-requests remain, transition to Idle.</li>
     * </ul>
     *
     * @param elevator the elevator being moved
     */
    @Override
    public void move(Elevator elevator) {
        // No more floors to visit going down → become idle
        if (elevator.getDownRequests().isEmpty()) {
            elevator.setCurrentState(new IdleState());
            return;
        }

        // Peek at the nearest (highest) floor we need to reach going down
        // (downRequests is sorted in descending order via the reverse comparator)
        Integer targetFloor = elevator.getDownRequests().getFirst();

        // Move one floor downward
        elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);

        // Check if we've arrived at the target floor
        if (elevator.getCurrentFloor() == targetFloor) {
            elevator.getDownRequests().pollFirst();  // remove the served floor
            System.out.println("Elevator " + elevator.getId() + " stopped at floor " + targetFloor);
        }

        // After serving, if no more down-requests remain, go idle
        if (elevator.getDownRequests().isEmpty()) {
            elevator.setCurrentState(new IdleState());
        }
    }

    /**
     * Queues a new request while the elevator is moving down.
     * <p>
     * <b>INTERNAL requests</b> (cabin button): queued into downRequests if target is below,
     * otherwise into upRequests (will be served after the elevator reverses direction).
     * <p>
     * <b>EXTERNAL requests</b> (hall-call): only picked up on-the-way if the direction
     * matches and the floor is at or below the current floor; otherwise queued for later.
     *
     * @param request  the incoming request
     * @param elevator the elevator receiving the request
     */
    @Override
    public void addRequest(Request request, Elevator elevator) {

        if (request.getRequestSource() == RequestSource.INTERNAL) {
            // Internal: user inside the cabin pressed a floor button
            if (request.getTargetFloor() > elevator.getCurrentFloor()) {
                elevator.getUpRequests().add(request.getTargetFloor());   // serve later when going up
            } else {
                elevator.getDownRequests().add(request.getTargetFloor()); // on-the-way down
            }
        } else if (request.getRequestSource() == RequestSource.EXTERNAL) {
            // External hall-call
            if (request.getDirection() == Direction.DOWN && request.getTargetFloor() <= elevator.getCurrentFloor()) {
                // Same direction & on-the-way → pick it up now
                elevator.getDownRequests().add(request.getTargetFloor());
            } else if (request.getDirection() == Direction.UP) {
                // Opposite direction → serve when we reverse
                elevator.getUpRequests().add(request.getTargetFloor());
            }
        }
    }

    /** @return {@link Direction#DOWN} */
    @Override
    public Direction getDirection() {
        return Direction.DOWN;
    }
}
