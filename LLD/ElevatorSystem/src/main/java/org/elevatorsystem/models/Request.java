package org.elevatorsystem.models;

import org.elevatorsystem.constants.Direction;
import org.elevatorsystem.constants.RequestSource;

/**
 * Represents a single elevator request.
 * <p>
 * A request can be:
 * <ul>
 *   <li><b>EXTERNAL</b> – a hall-call from a floor (e.g., user at floor 5 wants to go UP).</li>
 *   <li><b>INTERNAL</b> – a cabin button press (e.g., user inside elevator selects floor 10).</li>
 * </ul>
 */
public class Request {

    /** The direction the user intends to travel (meaningful for EXTERNAL requests; IDLE for INTERNAL) */
    private Direction direction;

    /** Indicates whether the request came from inside the elevator or from a floor hallway */
    private RequestSource requestSource;

    /** The destination / target floor number for this request */
    private Integer targetFloor;

    public Request(Direction direction, RequestSource requestSource, Integer targetFloor) {
        this.direction = direction;
        this.requestSource = requestSource;
        this.targetFloor = targetFloor;
    }


    public Direction getDirection() {
        return direction;
    }

    public RequestSource getRequestSource() {
        return requestSource;
    }

    public Integer getTargetFloor() {
        return targetFloor;
    }

    /**
     * Human-readable representation of the request.
     * For EXTERNAL requests, the direction is appended for clarity.
     */
    @Override
    public String toString() {
        return requestSource + " Request to floor " + targetFloor +
                (requestSource == RequestSource.EXTERNAL ? " going " + direction : "");
    }
}
