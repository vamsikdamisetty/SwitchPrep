package org.example.pool;

/**
 * Custom unchecked exception for connection pool errors.
 */
public class ConnectionPoolException extends RuntimeException {
    public ConnectionPoolException(String message) {
        super(message);
    }

    public ConnectionPoolException(String message, Throwable cause) {
        super(message, cause);
    }
}

