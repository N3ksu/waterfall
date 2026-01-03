package waterfall.kernel.util.log.sync;

import waterfall.kernel.util.log.entry.LogEntry;

import java.io.OutputStream;
import java.io.PrintStream;

public final class SyncLogger {
    private final OutputStream out;

    public SyncLogger(OutputStream out) {
        this.out = out;
    }

    public void log(LogEntry entry) {
        try (PrintStream printer = new PrintStream(out, true)) {
            printer.printf("[%s] [%s] %s%n", entry.timestamp(), entry.level(), entry.message());
        }
    }

    public void info(String message) {
        log(LogEntry.info(message));
    }

    public void warning(String message) {
        log(LogEntry.warning(message));
    }

    public void severe(String message){
        log(LogEntry.severe(message));
    }
}
