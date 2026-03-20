package org.elevatorsystem.constants;

/**
 * Enum representing the possible directions an elevator can travel.
 * Also used to indicate the desired direction when a user makes a hall-call (external request).
 */
public enum Direction {
    /** Elevator is stationary / not moving */
    IDLE,
    /** Elevator is moving upward */
    UP,
    /** Elevator is moving downward */
    DOWN
}
