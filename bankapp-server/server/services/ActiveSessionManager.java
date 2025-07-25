package server.services;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages active user sessions to prevent multiple logins for the same user.
 */
public class ActiveSessionManager {
    // Thread-safe map to store currently logged-in users
    private static final ConcurrentHashMap<String, Object> activeSessions = new ConcurrentHashMap<>();

    /**
     * Checks if a user is currently logged in.
     *
     * @param username The username to check
     * @return true if the user is already logged in, false otherwise
     */
    public static boolean isUserActive(String username) {
        return activeSessions.containsKey(username);
    }

    /**
     * Register a user as logged in.
     *
     * @param username The username to register
     */
    public static void registerSession(String username) {
        activeSessions.put(username, new Object());
    }

    /**
     * Remove a user session when they log out.
     *
     * @param username The username to remove
     */
    public static void removeSession(String username) {
        activeSessions.remove(username);
    }
}