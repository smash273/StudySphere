package com.studysphere.concurrency;

import com.studysphere.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadPoolManager {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);
    private static volatile ThreadPoolManager instance;

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutor;

    private ThreadPoolManager() {
        int poolSize = DatabaseConfig.getThreadPoolSize();
        AtomicInteger threadCount = new AtomicInteger(1);

        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r);
            thread.setName("StudySphere-Worker-" + threadCount.getAndIncrement());
            thread.setDaemon(true); // Allow JVM to exit cleanly
            return thread;
        };

        this.executorService = new ThreadPoolExecutor(
                poolSize,
                poolSize * 2,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "StudySphere-Scheduler");
            t.setDaemon(true);
            return t;
        });

        // Register JVM shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        logger.info("ThreadPoolManager initialized with core pool size: {}", poolSize);
    }

    public static ThreadPoolManager getInstance() {
        if (instance == null) {
            synchronized (ThreadPoolManager.class) {
                if (instance == null) {
                    instance = new ThreadPoolManager();
                }
            }
        }
        return instance;
    }

    public Future<?> submit(Runnable task) {
        return executorService.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }

    public <T> CompletableFuture<T> supplyAsync(java.util.function.Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executorService);
    }

    public void schedule(Runnable task, long delay, TimeUnit unit) {
        scheduledExecutor.schedule(task, delay, unit);
    }

    public void shutdown() {
        logger.info("Shutting down ThreadPoolManager...");
        executorService.shutdown();
        scheduledExecutor.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
