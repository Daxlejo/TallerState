package AgriDrone.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility logger for drone events.
 * Stores log entries with timestamps for both console output and frontend display.
 */
public class DroneLogger {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<String> logEntries;

    public DroneLogger() {
        this.logEntries = new ArrayList<>();
    }

    /**
     * Logs a message with a timestamp.
     * @param message the message in Spanish for the user.
     */
    public void log(String message) {
        String timestamp = LocalTime.now().format(TIME_FORMAT);
        String entry = "[" + timestamp + "] " + message;
        logEntries.add(entry);
        System.out.println(entry);
    }

    /**
     * @return an unmodifiable view of all log entries.
     */
    public List<String> getEntries() {
        return Collections.unmodifiableList(logEntries);
    }

    /**
     * Clears all log entries.
     */
    public void clear() {
        logEntries.clear();
    }
}
