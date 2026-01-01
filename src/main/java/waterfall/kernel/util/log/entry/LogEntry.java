package waterfall.kernel.util.log.entry;

import waterfall.kernel.util.log.level.Level;

import java.time.LocalDateTime;

public record LogEntry(LocalDateTime timestamp, Level level, String message) {
    public LogEntry(Level level, String message) {
        this(LocalDateTime.now(), level, message);
    }

    public static LogEntry of(Level level, String message) {
        return new LogEntry(level, message);
    }

    public static LogEntry info(String message) {
        return LogEntry.of(Level.INFO, message);
    }

    public static LogEntry warning(String message) {
        return LogEntry.of(Level.WARNING, message);
    }

    public static LogEntry severe(String message) {
        return LogEntry.of(Level.SEVERE, message);
    }
}
