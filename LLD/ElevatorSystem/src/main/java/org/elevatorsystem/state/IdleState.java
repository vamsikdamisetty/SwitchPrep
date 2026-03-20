package org.elevatorsystem.state;

import org.elevatorsystem.constants.Direction;
import org.elevatorsystem.models.Elevator;
import org.elevatorsystem.models.Request;

/**
 * State when the elevator is stationary and has no pending movement.
 * <p>
 * On each tick ({@code move}), it checks whether any queued requests exist
 * and transitions to MovingUp or MovingDown accordingly.
 */
public class IdleState implements ElevatorState {

    /**
     * Checks queued requests and transitions state if work is available.
     * Priority: UP requests are checked first, then DOWN requests.
     *
     * @param elevator the elevator to evaluate
     */
    @Override
    public void move(Elevator elevator) {
        // If there are pending up-requests, start moving up
        if (!elevator.getUpRequests().isEmpty()) {
            elevator.setCurrentState(new MovingUp());
        }
        // Otherwise, if there are pending down-requests, start moving down
        else if (!elevator.getDownRequests().isEmpty()) {
            elevator.setCurrentState(new MovingDown());
        }
        // If both queues are empty, stay idle (do nothing)
    }

    /**
     * While idle, a new request is routed to the correct queue based on
     * whether the target floor is above or below the current floor.
     * If the target floor equals the current floor, the doors simply open.
     *
     * @param request  the incoming request
     * @param elevator the elevator receiving the request
     */
    @Override
    public void addRequest(Request request, Elevator elevator) {
        if (request.getTargetFloor() > elevator.getCurrentFloor()) {
            // Target is above → queue as an up-request
            elevator.getUpRequests().add(request.getTargetFloor());
        } else if (request.getTargetFloor() < elevator.getCurrentFloor()) {
            // Target is below → queue as a down-request
            elevator.getDownRequests().add(request.getTargetFloor());
        } else {
            // Already at the target floor → just open the doors
            System.out.println("Elevator Doors opening");
        }
    }

    /** @return {@link Direction#IDLE} */
    @Override
    public Direction getDirection() {
        return Direction.IDLE;
    }
}
