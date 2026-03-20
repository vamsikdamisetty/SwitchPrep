package org.elevatorsystem.constants;

/**
 * Enum representing where a request originated from.
 */
public enum RequestSource {
    /** Request from inside the elevator (user presses a floor button inside the cabin) */
    INTERNAL,
    /** Request from outside the elevator (user presses the up/down hall-call button on a floor) */
    EXTERNAL
}
