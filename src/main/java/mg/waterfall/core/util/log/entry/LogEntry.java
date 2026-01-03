package mg.waterfall.core.util.log.entry;

import mg.waterfall.core.util.log.level.Level;

import java.time.LocalDateTime;

public record LogEntry(LocalDateTime timestamp, Level level, String message) {
    public LogEntry(Level level, String message) {
        this(LocalDateTime.now(), level, message);
    }

    public static LogEntry info(String message) {
        return new LogEntry(Level.INFO, message);
    }

    public static LogEntry warning(String message) {
        return new LogEntry(Level.WARNING, message);
    }

    public static LogEntry severe(String message) {
        return new LogEntry(Level.SEVERE, message);
    }
}
