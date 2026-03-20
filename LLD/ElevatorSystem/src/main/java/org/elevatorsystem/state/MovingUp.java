package org.elevatorsystem.state;

import org.elevatorsystem.constants.Direction;
import org.elevatorsystem.constants.RequestSource;
import org.elevatorsystem.models.Elevator;
import org.elevatorsystem.models.Request;

/**
 * State when the elevator is actively moving upward.
 * <p>
 * Each call to {@code move()} increments the floor by 1.
 * When the elevator reaches a requested floor, that floor is removed from the queue.
 * Once all up-requests are served, the elevator transitions back to {@link IdleState}.
 */
public class MovingUp implements ElevatorState {

    /**
     * Moves the elevator one floor up per tick.
     * <ul>
     *   <li>If the up-request queue is empty, transition to Idle.</li>
     *   <li>Otherwise, move up by 1 floor.</li>
     *   <li>If the new floor matches the nearest up-request, stop and remove it from the queue.</li>
     *   <li>After serving, if no more up-requests remain, transition to Idle (may pick up down-requests next).</li>
     * </ul>
     *
     * @param elevator the elevator being moved
     */
    @Override
    public void move(Elevator elevator) {
        // No more floors to visit going up → become idle
        if (elevator.getUpRequests().isEmpty()) {
            elevator.setCurrentState(new IdleState());
            return;
        }

        // Peek at the nearest (lowest) floor we need to reach going up
        Integer targetFloor = elevator.getUpRequests().getFirst();

        // Move one floor upward
        elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);

        // Check if we've arrived at the target floor
        if (elevator.getCurrentFloor() == targetFloor) {
            elevator.getUpRequests().pollFirst();  // remove the served floor
            System.out.println("Elevator " + elevator.getId() + " stopped at floor " + targetFloor);
        }

        // After serving, if no more up-requests remain, go idle
        if (elevator.getUpRequests().isEmpty()) {
            elevator.setCurrentState(new IdleState());
        }
    }

    /**
     * Queues a new request while the elevator is moving up.
     * <p>
     * <b>INTERNAL requests</b> (cabin button): queued into upRequests if target is above,
     * otherwise into downRequests (will be served after the elevator reverses direction).
     * <p>
     * <b>EXTERNAL requests</b> (hall-call): only picked up on-the-way if the direction
     * matches and the floor is at or above the current floor; otherwise queued for later.
     *
     * @param request  the incoming request
     * @param elevator the elevator receiving the request
     */
    @Override
    public void addRequest(Request request, Elevator elevator) {

        if (request.getRequestSource() == RequestSource.INTERNAL) {
            // Internal: user inside the cabin pressed a floor button
            if (request.getTargetFloor() > elevator.getCurrentFloor()) {
                elevator.getUpRequests().add(request.getTargetFloor());   // on-the-way up
            } else {
                elevator.getDownRequests().add(request.getTargetFloor()); // serve later when going down
            }
        } else if (request.getRequestSource() == RequestSource.EXTERNAL) {
            // External hall-call
            if (request.getDirection() == Direction.UP && request.getTargetFloor() >= elevator.getCurrentFloor()) {
                // Same direction & on-the-way → pick it up now
                elevator.getUpRequests().add(request.getTargetFloor());
            } else if (request.getDirection() == Direction.DOWN) {
                // Opposite direction → serve when we reverse
                elevator.getDownRequests().add(request.getTargetFloor());
            }
        }
    }

    /** @return {@link Direction#UP} */
    @Override
    public Direction getDirection() {
        return Direction.UP;
    }
}
