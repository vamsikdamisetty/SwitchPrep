package org.example.pool;

/**
 * Factory Method pattern — isolates connection creation.
 * Interview: "If we swap to a real DB, only this class changes."
 */
public class ConnectionFactory {

    public PooledConnection create() {
        Connection conn = new Connection();
        return new PooledConnection(conn);
    }
}

