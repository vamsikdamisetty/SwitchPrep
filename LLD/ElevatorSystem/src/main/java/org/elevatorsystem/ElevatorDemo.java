package org.elevatorsystem;

import org.elevatorsystem.constants.Direction;
import org.elevatorsystem.service.ElevatorSystem;

/**
 * Entry point for the Elevator System simulation.
 * <p>
 * This demo:
 * <ol>
 *   <li>Creates a building with 2 elevators via the Singleton ElevatorSystem.</li>
 *   <li>Starts the elevator threads (each elevator runs its own move-loop).</li>
 *   <li>Fires a series of external (hall-call) and internal (cabin button) requests.</li>
 *   <li>Lets the simulation run so console-display updates can be observed.</li>
 *   <li>Gracefully shuts down all elevators and the thread pool.</li>
 * </ol>
 */
public class ElevatorDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── Step 1: Initialise the elevator system (Singleton) with 2 elevators ──
        int numElevators = 2;
        // getInstance() creates the elevators and attaches the ElevatorDisplay observer
        ElevatorSystem elevatorSystem = ElevatorSystem.getInstance(numElevators);

        // ── Step 2: Start the elevator threads ──
        elevatorSystem.start();
        System.out.println("Elevator system started. ConsoleDisplay is observing.\n");

        // ═══════════════════════════════════════════
        //              SIMULATION START
        // ═══════════════════════════════════════════

        // ── Request 1 (EXTERNAL): User at floor 5 presses UP button ──
        // The NearestElevatorStrategy will dispatch this to the closest idle elevator.
        elevatorSystem.requestElevator(5, Direction.UP);
        Thread.sleep(10000); // wait for the elevator to start moving toward floor 5

        // ── Request 2 (INTERNAL): User inside Elevator 1 selects floor 10 ──
        // Simulates the user entering the cabin at floor 5 and pressing "10".
        // Note: In a full simulation we'd wait until E1 reaches floor 5; here we
        //       approximate by sending the internal request after a short delay.
        elevatorSystem.selectFloor(1, 10);
        Thread.sleep(200);

        // ── Request 3 (EXTERNAL): User at floor 3 presses DOWN button ──
        // E2 (likely still idle at floor 1) may be dispatched, or E1 if it's closer.
        elevatorSystem.requestElevator(3, Direction.DOWN);
        Thread.sleep(30000); // let elevators process the queued requests

        // ── Request 4 (INTERNAL): User inside Elevator 2 selects floor 1 ──
        elevatorSystem.selectFloor(2, 1);

        // ── Let the simulation run so we can observe display updates ──
        System.out.println("\n--- Letting simulation run for 100 seconds ---");
        Thread.sleep(10000);

        // ── Step 3: Gracefully shut down the elevator system ──
        elevatorSystem.shutDown();
        System.out.println("\n--- SIMULATION END ---");
    }
}
