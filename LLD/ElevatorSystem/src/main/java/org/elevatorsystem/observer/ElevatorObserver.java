package org.elevatorsystem.observer;

import org.elevatorsystem.models.Elevator;

/**
 * Observer Pattern – interface for any component that wants to be
 * notified when an elevator's state or floor changes.
 */
public interface ElevatorObserver {

    /**
     * Called by the elevator whenever its floor or state changes.
     *
     * @param elevator the elevator whose state has been updated
     */
    void update(Elevator elevator);
}
