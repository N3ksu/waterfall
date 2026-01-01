package waterfall.kernel.util.log;

import waterfall.kernel.util.log.entry.LogEntry;

public interface Logger {
    void log(LogEntry entry);
    void info(String message);
    void warning(String message);
    void severe(String message);
}
