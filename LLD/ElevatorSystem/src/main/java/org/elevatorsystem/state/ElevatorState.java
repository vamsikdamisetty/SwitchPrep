package org.elevatorsystem.state;

import org.elevatorsystem.constants.Direction;
import org.elevatorsystem.models.Elevator;
import org.elevatorsystem.models.Request;

/**
 * State Pattern – defines the contract for every elevator state (Idle, MovingUp, MovingDown).
 * <p>
 * Each concrete state implements:
 * <ul>
 *   <li>{@code move}        – how the elevator moves (or stays still) in this state.</li>
 *   <li>{@code addRequest}  – how a new request is queued while in this state.</li>
 *   <li>{@code getDirection} – the logical direction associated with this state.</li>
 * </ul>
 */
public interface ElevatorState {

    /**
     * Executes one "tick" of movement for the elevator.
     * Called once per second from the elevator's run-loop.
     *
     * @param elevator the elevator to move
     */
    void move(Elevator elevator);

    /**
     * Queues a new request into the appropriate request set (up / down) based on
     * the current state and the request details.
     *
     * @param request  the incoming request
     * @param elevator the elevator receiving the request
     */
    void addRequest(Request request, Elevator elevator);

    /**
     * @return the direction associated with this state (IDLE, UP, or DOWN)
     */
    Direction getDirection();
}
