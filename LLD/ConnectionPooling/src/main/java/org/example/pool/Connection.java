package org.example.pool;

/**
 * Dummy Connection — simulates a real DB connection without any JDBC dependency.
 * In interviews, say: "This is a simplified stand-in; in production this would be java.sql.Connection."
 */
public class Connection {
    private static int counter = 0;

    private final int id;
    private boolean open;

    public Connection() {
        this.id = ++counter;
        this.open = true;
        // Simulate the cost of opening a real connection
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        System.out.println("[Connection-" + id + "] Opened");
    }

    public void execute(String query) {
        if (!open) throw new ConnectionPoolException("Connection-" + id + " is closed!");
        System.out.println("[Connection-" + id + "] Executing: " + query);
        // Simulate query execution time
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
    }

    public void close() {
        if (open) {
            open = false;
            System.out.println("[Connection-" + id + "] Closed permanently");
        }
    }

    public boolean isOpen() { return open; }
    public int getId() { return id; }

    @Override
    public String toString() { return "Connection-" + id; }
}

