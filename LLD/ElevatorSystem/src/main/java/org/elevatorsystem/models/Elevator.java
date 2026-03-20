package org.elevatorsystem.models;

import org.elevatorsystem.observer.ElevatorObserver;
import org.elevatorsystem.state.ElevatorState;
import org.elevatorsystem.state.IdleState;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a single elevator cabin in the building.
 * <p>
 * Key design patterns used:
 * <ul>
 *   <li><b>State Pattern</b> – behaviour (move / addRequest) is delegated to the current {@link ElevatorState}.</li>
 *   <li><b>Observer Pattern</b> – registered {@link ElevatorObserver}s are notified on every floor / state change.</li>
 * </ul>
 * <p>
 * Each Elevator runs on its own thread (implements {@link Runnable}). The main loop
 * repeatedly calls {@code move()} every 1 second, simulating real-time floor transitions.
 */
public class Elevator implements Runnable {

    private Integer id;

    /** Thread-safe current floor counter (used by the elevator's own thread and the dispatch thread) */
    private AtomicInteger currentFloor;

    /** Current behavioural state – IdleState, MovingUp, or MovingDown (State Pattern) */
    private ElevatorState currentState;

    /** Flag to gracefully stop the elevator's run-loop; marked volatile for cross-thread visibility */
    private volatile boolean isRunning = true;

    /**
     * Sorted set of floors the elevator must visit while going UP.
     * Natural ascending order (TreeSet default) ensures the nearest upward floor is served first.
     */
    private final TreeSet<Integer> upRequests;

    /**
     * Sorted set of floors the elevator must visit while going DOWN.
     * Uses a reverse comparator so the nearest downward floor is served first.
     */
    private final TreeSet<Integer> downRequests;

    /** Observer Pattern: list of observers (e.g., displays) subscribed to this elevator's state changes */
    private final List<ElevatorObserver> observers = new ArrayList<>();

    /**
     * Constructs a new Elevator starting at floor 1 in IDLE state.
     *
     * @param id unique elevator identifier
     */
    public Elevator(Integer id) {
        this.id = id;
        currentFloor = new AtomicInteger(1);           // every elevator starts at floor 1
        upRequests = new TreeSet<>();                    // natural order (ascending)
        downRequests = new TreeSet<>((a, b) -> b - a);  // reverse order (descending) for downward travel
        this.currentState = new IdleState();             // initially idle
    }

    /**
     * Registers an observer and immediately pushes the current state to it.
     *
     * @param observer the observer to register (e.g., ElevatorDisplay)
     */
    public void addObserver(ElevatorObserver observer) {
        this.observers.add(observer);
        observer.update(this); // send initial state so the display shows floor 1 / IDLE right away
    }

    /**
     * Accepts a new request and delegates handling to the current state.
     * The state decides which queue (upRequests / downRequests) the floor goes into.
     *
     * @param request the incoming request (internal or external)
     */
    public void addRequest(Request request) {
        System.out.println("Elevator " + id + " processing: " + request);
        this.currentState.addRequest(request, this);
    }

    /** @return the current floor number */
    public int getCurrentFloor() {
        return currentFloor.intValue();
    }

    /** @return the current behavioural state (IdleState / MovingUp / MovingDown) */
    public ElevatorState getCurrentState() {
        return currentState;
    }

    /**
     * Updates the floor counter and notifies all observers about the change.
     *
     * @param floor the new floor number
     */
    public void setCurrentFloor(int floor) {
        this.currentFloor.set(floor);
        notifyObservers(); // push update to displays / monitors
    }

    /**
     * Iterates over all registered observers and sends them the latest elevator state.
     * Called whenever the floor or the state changes.
     */
    private void notifyObservers() {
        this.observers.forEach(observer -> observer.update(this));
    }

    /**
     * Transitions to a new state (e.g., from Idle → MovingUp) and notifies observers.
     *
     * @param currentState the new state to set
     */
    public void setCurrentState(ElevatorState currentState) {
        this.currentState = currentState;
        notifyObservers();
    }

    public TreeSet<Integer> getUpRequests() {
        return upRequests;
    }

    public TreeSet<Integer> getDownRequests() {
        return downRequests;
    }

    public Integer getId() {
        return id;
    }

    /**
     * Main elevator loop – runs on a dedicated thread.
     * Calls {@code move()} every 1 second, simulating real-time movement.
     * The loop exits when {@code isRunning} is set to false via {@link #stopElevator()}.
     */
    @Override
    public void run() {
        while (isRunning) {
            move();                       // delegate movement to the current state
            try {
                Thread.sleep(1000);       // 1-second interval simulates floor-to-floor travel time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isRunning = false;        // gracefully exit on interrupt
            }
        }
    }

    /**
     * Delegates the actual floor-transition logic to the current state's {@code move()} method.
     */
    private void move() {
        this.currentState.move(this);
    }

    /**
     * Signals this elevator's run-loop to stop.
     * Called during system shutdown.
     */
    public void stopElevator() {
        this.isRunning = false;
    }
}
