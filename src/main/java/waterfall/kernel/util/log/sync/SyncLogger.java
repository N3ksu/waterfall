package waterfall.kernel.util.log.sync;

import waterfall.kernel.util.log.Logger;
import waterfall.kernel.util.log.entry.LogEntry;

import java.io.OutputStream;
import java.io.PrintStream;

public final class SyncLogger implements Logger {
    private final OutputStream out;

    public SyncLogger(OutputStream out) {
        this.out = out;
    }

    @Override
    public void log(LogEntry entry) {
        try (PrintStream printer = new PrintStream(out, true)) {
            printer.printf("[%s] [%s] %s%n", entry.timestamp(), entry.level(), entry.message());
        }
    }

    @Override
    public void info(String message) {
        log(LogEntry.info(message));
    }

    @Override
    public void warning(String message) {
        log(LogEntry.warning(message));
    }

    @Override
    public void severe(String message){
        log(LogEntry.severe(message));
    }
}
