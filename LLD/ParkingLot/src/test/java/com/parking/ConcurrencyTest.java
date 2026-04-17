package com.parking;

import com.parking.entity.ParkingFloor;
import com.parking.entity.ParkingSpot;
import com.parking.entity.ParkingTicket;
import com.parking.entity.vehicle.Car;
import com.parking.entity.vehicle.Vehicle;
import com.parking.entity.vehicle.VehicleSize;
import com.parking.service.ParkingLot;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrency tests to prove that two threads cannot park in the same spot simultaneously.
 *
 * Key concurrency primitives used:
 *   - CyclicBarrier  : ensures all threads start the CAS attempt at the exact same instant
 *   - ExecutorService : manages a fixed thread pool for concurrent execution
 *   - CountDownLatch  : (in stress test) signals when all threads have finished
 *   - Future          : collects results from each thread for assertion
 */
public class ConcurrencyTest {

    // ─────────────────────────────────────────────────────────────────
    // Test 1: Two threads race for the SAME ParkingSpot (low-level CAS)
    // ─────────────────────────────────────────────────────────────────
    @Test
    void twoThreadsCannotParkInSameSpot() throws Exception {
        ParkingSpot spot = new ParkingSpot("S1", VehicleSize.MEDIUM);
        Vehicle car1 = new Car("CAR-001");
        Vehicle car2 = new Car("CAR-002");

        int threadCount = 2;
        // CyclicBarrier ensures both threads call parkVehicle at the exact same moment
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Submit both park attempts — barrier forces them to start simultaneously
        Future<Boolean> result1 = executor.submit(() -> {
            barrier.await();              // wait for the other thread
            return spot.parkVehicle(car1); // CAS attempt
        });

        Future<Boolean> result2 = executor.submit(() -> {
            barrier.await();              // wait for the other thread
            return spot.parkVehicle(car2); // CAS attempt
        });

        boolean parked1 = result1.get(5, TimeUnit.SECONDS);
        boolean parked2 = result2.get(5, TimeUnit.SECONDS);

        executor.shutdown();

        // Exactly ONE thread should succeed, the other must fail
        assertTrue(parked1 ^ parked2, "Exactly one thread should park successfully (XOR)");
        assertTrue(spot.isOccupied(), "Spot must be occupied after parking");
    }

    // ─────────────────────────────────────────────────────────────────
    // Test 2: Two threads race through ParkingLot.parkVehicle()
    //         (end-to-end with strategy, ticket, and retry logic)
    // ─────────────────────────────────────────────────────────────────
    @Test
    void twoThreadsCompeteForLastSpotViaParkingLot() throws Exception {
        // Setup: one floor, one MEDIUM spot — guarantees contention
        ParkingLot lot = ParkingLot.getInstance();
        ParkingFloor floor = new ParkingFloor(1);
        floor.addSpot(new ParkingSpot("ONLY-SPOT", VehicleSize.MEDIUM));
        lot.addFloor(floor);

        Vehicle car1 = new Car("RACE-001");
        Vehicle car2 = new Car("RACE-002");

        int threadCount = 2;
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        Future<Optional<ParkingTicket>> future1 = executor.submit(() -> {
            barrier.await();
            return lot.parkVehicle(car1);
        });

        Future<Optional<ParkingTicket>> future2 = executor.submit(() -> {
            barrier.await();
            return lot.parkVehicle(car2);
        });

        Optional<ParkingTicket> ticket1 = future1.get(5, TimeUnit.SECONDS);
        Optional<ParkingTicket> ticket2 = future2.get(5, TimeUnit.SECONDS);

        executor.shutdown();

        // Exactly one should get a ticket
        assertTrue(ticket1.isPresent() ^ ticket2.isPresent(),
                "Exactly one vehicle should get the last spot");

        // The winner's ticket should reference "ONLY-SPOT"
        Optional<ParkingTicket> winner = ticket1.isPresent() ? ticket1 : ticket2;
        assertEquals("ONLY-SPOT", winner.get().getSpot().getSpotId());
    }

    // ─────────────────────────────────────────────────────────────────
    // Test 3: Stress test — 100 threads compete for 10 spots
    //         Proves no double-allocation under heavy contention
    // ─────────────────────────────────────────────────────────────────
    @Test
    void stressTest_100ThreadsFor10Spots() throws Exception {
        ParkingSpot[] spots = new ParkingSpot[10];
        for (int i = 0; i < 10; i++) {
            spots[i] = new ParkingSpot("SPOT-" + i, VehicleSize.MEDIUM);
        }

        int threadCount = 100;
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Boolean> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            Vehicle car = new Car("STRESS-" + i);
            executor.submit(() -> {
                try {
                    barrier.await();  // all 100 threads start at once

                    // Try every spot until one succeeds or all fail
                    boolean parked = false;
                    for (ParkingSpot spot : spots) {
                        if (spot.parkVehicle(car)) {
                            System.out.println(car.getLicenseNo() + " -- " + spot.getSpotId());
                            parked = true;
                            break;
                        }
                    }
                    results.add(parked);
                } catch (Exception e) {
                    results.add(false);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        long successCount = results.stream().filter(r -> r).count();
        long failCount = results.stream().filter(r -> !r).count();

        // Exactly 10 should succeed (one per spot), 90 should fail
        assertEquals(10, successCount, "Only 10 threads should park (one per spot)");
        assertEquals(90, failCount, "90 threads should fail to park");

        // Every spot should be occupied
        for (ParkingSpot spot : spots) {
            assertTrue(spot.isOccupied(), spot.getSpotId() + " should be occupied");
            System.out.println(spot.getSpotId() + " is occupied");
        }
    }
}

