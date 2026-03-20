package org.elevatorsystem.strategy;

import org.elevatorsystem.models.Elevator;
import org.elevatorsystem.models.Request;

import java.util.List;
import java.util.Optional;

/**
 * Strategy Pattern – defines a pluggable algorithm for selecting which
 * elevator should handle an incoming external (hall-call) request.
 * <p>
 * Implementations can be swapped at runtime (e.g., NearestElevator, LeastLoaded, etc.).
 */
public interface ElevatorStrategy {

    /**
     * Selects the most suitable elevator for the given request.
     *
     * @param elevators list of all elevators in the system
     * @param request   the external request to be dispatched
     * @return an {@link Optional} containing the chosen elevator, or empty if none is suitable
     */
    Optional<Elevator> selectElevator(List<Elevator> elevators, Request request);
}
