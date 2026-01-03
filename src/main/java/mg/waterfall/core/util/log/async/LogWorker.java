package mg.waterfall.core.util.log.async;

import mg.waterfall.core.util.log.entry.LogEntry;
import mg.waterfall.core.util.log.sync.SyncLogger;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public final class LogWorker implements Runnable {
    private final BlockingQueue<LogEntry> queue;

    private final long timeout;
    private final TimeUnit unit;

    private volatile boolean running;

    private final SyncLogger syncLogger;

    public LogWorker(BlockingQueue<LogEntry> queue, OutputStream out, Config config) {
        this.queue = queue;

        timeout = config.timeout();
        unit = config.unit();

        syncLogger = new SyncLogger(out);
    }

    public void start() {
        running = true;
    }

    @Override
    public void run() {
        try {
            while (running || !queue.isEmpty()) {
                LogEntry entry = queue.poll(timeout, unit);

                if (entry != null)
                    syncLogger.log(entry);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            syncLogger.severe(e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }

    public record Config(long timeout, TimeUnit unit) {
        public static Config of(long timeout, TimeUnit unit) {
            return new Config(timeout, unit);
        }
    }
}
