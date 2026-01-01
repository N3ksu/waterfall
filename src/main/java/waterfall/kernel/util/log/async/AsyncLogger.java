package waterfall.kernel.util.log.async;

import waterfall.kernel.util.log.Logger;
import waterfall.kernel.util.log.entry.LogEntry;
import waterfall.kernel.util.log.sync.SyncLogger;

import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class AsyncLogger implements Logger {
    private static final String LOG_WORKER_THREAD_NAME_PREFIX = "LOG-WORKER-THREAD";
    private static final AtomicInteger ID = new AtomicInteger();

    private final BlockingQueue<LogEntry> queue;
    private final long shutdownWaitTime;

    private final LogWorker worker;
    private final Thread workerThread;

    private final SyncLogger syncLogger;

    public AsyncLogger(OutputStream out, Config config) {
        queue = new LinkedBlockingQueue<>(config.queueCapacity());
        shutdownWaitTime = config.shutdownWaitTime();

        worker = new LogWorker(queue, out, LogWorker.Config.of(config.timeout(), config.unit()));
        worker.start();

        workerThread = new Thread(worker, "%s-%d".formatted(LOG_WORKER_THREAD_NAME_PREFIX, ID.incrementAndGet()));
        workerThread.setDaemon(true);
        workerThread.start();

        syncLogger = new SyncLogger(out);
    }

    public void stop() {
        worker.stop();
        try {
            workerThread.join(shutdownWaitTime);
        } catch (InterruptedException e) {
            syncLogger.severe(e.getMessage());
        }
    }

    @Override
    public void log(LogEntry entry) {
        boolean added = queue.offer(entry);
        if (!added)
            syncLogger.warning("Log queue full, some messages are lost");
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
    public void severe(String message) {
        log(LogEntry.severe(message));
    }

    public record Config(int queueCapacity, long shutdownWaitTime, long timeout, TimeUnit unit) {
        public static Config of(int queueCapacity, long shutdownWaitTime, long timeout, TimeUnit unit) {
            return new Config(queueCapacity, shutdownWaitTime, timeout, unit);
        }
    }
}
