package org.elevatorsystem.service;

import org.elevatorsystem.constants.Direction;
import org.elevatorsystem.constants.RequestSource;
import org.elevatorsystem.models.Elevator;
import org.elevatorsystem.models.Request;
import org.elevatorsystem.observer.ElevatorDisplay;
import org.elevatorsystem.strategy.ElevatorStrategy;
import org.elevatorsystem.strategy.NearestElevatorStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Central controller for the elevator system.
 * <p>
 * Design patterns used:
 * <ul>
 *   <li><b>Singleton</b> – only one ElevatorSystem instance exists (via {@link #getInstance(int)}).</li>
 *   <li><b>Strategy</b>  – dispatch logic is delegated to a pluggable {@link ElevatorStrategy}.</li>
 * </ul>
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Creating and managing all elevator instances.</li>
 *   <li>Dispatching external (hall-call) requests to the best elevator via the strategy.</li>
 *   <li>Forwarding internal (cabin button) requests to a specific elevator.</li>
 *   <li>Starting and shutting down the elevator threads.</li>
 * </ul>
 */
public class ElevatorSystem {

    /** Singleton instance */
    private static ElevatorSystem INSTANCE;

    /** Map of elevator ID → Elevator object for quick look-up */
    private Map<Integer, Elevator> elevatorMap = new HashMap<>();

    /** Pluggable strategy for dispatching external requests to elevators */
    private ElevatorStrategy elevatorStrategy;

    /** Thread pool that runs each elevator on its own thread */
    private final ExecutorService executorService;

    /**
     * Private constructor – initialises elevators, attaches the display observer,
     * and creates a fixed-size thread pool.
     *
     * @param numElevators the number of elevators to create
     */
    private ElevatorSystem(int numElevators) {
        // Default strategy: pick the nearest suitable elevator
        elevatorStrategy = new NearestElevatorStrategy();
        // One thread per elevator
        this.executorService = Executors.newFixedThreadPool(numElevators);

        // Shared display observer – prints state changes for every elevator
        ElevatorDisplay elevatorDisplay = new ElevatorDisplay();

        // Create each elevator, register the display observer, and store in the map
        for (int i = 1; i <= numElevators; i++) {
            Elevator elevator = new Elevator(i);
            elevator.addObserver(elevatorDisplay);   // Observer Pattern – attach display
            this.elevatorMap.put(i, elevator);
        }
    }

    /**
     * Thread-safe Singleton accessor.
     * The first call creates the instance; subsequent calls return the existing one.
     *
     * @param numElevators number of elevators (only used on first call)
     * @return the singleton ElevatorSystem instance
     */
    public static synchronized ElevatorSystem getInstance(int numElevators) {
        if (INSTANCE == null) {
            INSTANCE = new ElevatorSystem(numElevators);
        }
        return INSTANCE;
    }

    /**
     * Starts all elevators by submitting their {@link Runnable#run()} to the thread pool.
     * Each elevator begins its move-loop on a separate thread.
     */
    public void start() {
        for (Elevator elevator : elevatorMap.values()) {
            executorService.submit(elevator);
        }
    }

    /**
     * Handles an <b>external</b> (hall-call) request.
     * <p>
     * Uses the configured {@link ElevatorStrategy} to pick the best elevator,
     * then forwards the request to it. If no elevator is available, a message is printed.
     *
     * @param targetFloor the floor where the user is waiting
     * @param direction   the direction the user wants to travel (UP or DOWN)
     */
    public void requestElevator(int targetFloor, Direction direction) {
        System.out.println("\n>> EXTERNAL Request: User at floor " + targetFloor + " wants to go " + direction);

        // Wrap the hall-call data into a Request object
        Request request = new Request(direction, RequestSource.EXTERNAL, targetFloor);

        // Delegate elevator selection to the strategy
        Optional<Elevator> selectedElevator = elevatorStrategy.selectElevator(
                elevatorMap.values().stream().toList(), request);

        if (selectedElevator.isPresent()) {
            System.out.println("Elevator Selected by NearestElevatorStrategy : " + selectedElevator.get().getId());
            selectedElevator.get().addRequest(request);  // forward request to the chosen elevator
        } else {
            System.out.println("Elevators are busy please try later");
        }
    }

    /**
     * Handles an <b>internal</b> (cabin button) request.
     * <p>
     * The user is already inside a specific elevator and presses a floor button.
     * Direction is set to IDLE because the elevator's state will decide
     * which queue (up / down) the floor belongs to.
     *
     * @param elevatorId  the ID of the elevator the user is inside
     * @param targetFloor the floor the user selected
     */
    public void selectFloor(Integer elevatorId, int targetFloor) {
        System.out.println("\n>> INTERNAL Request: User in Elevator " + elevatorId + " selected floor " + targetFloor);

        // Direction is IDLE for internal requests; routing is handled by the elevator's current state
        Request request = new Request(Direction.IDLE, RequestSource.INTERNAL, targetFloor);

        if (elevatorMap.containsKey(elevatorId)) {
            elevatorMap.get(elevatorId).addRequest(request);
        } else {
            System.out.println("Invalid Elevator");
        }
    }

    /**
     * Gracefully shuts down the elevator system.
     * Stops all elevator run-loops and terminates the thread pool.
     */
    public void shutDown() {
        System.out.println("Shutting down elevator system...");
        for (Elevator elevator : elevatorMap.values()) {
            elevator.stopElevator();    // signal each elevator to exit its loop
        }
        executorService.shutdown();      // no new tasks; wait for running threads to finish
    }
}
