package org.util.concurrent.futures;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author ahmad
 */
public final class Do {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final ThreadFactory DEFAULT_THREAD_FACTORY = Executors.defaultThreadFactory();
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CPU_COUNT + 1, CPU_COUNT * 4 + 1,
            15L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            r -> {
                Thread t = DEFAULT_THREAD_FACTORY.newThread(r);
                t.setDaemon(true);
                return t;
            });

    private static final Executor SERIAL_EXECUTOR = new SerialExecutor();

    private static final class SerialExecutor implements Executor {

        private final Queue<Runnable> tasks = new ArrayDeque<>();

        private Runnable activeTask;

        public synchronized void execute(final Runnable r) {
            tasks.offer(() -> {
                try {
                    r.run();
                } finally {
                    scheduleNext();
                }
            });
            if (activeTask == null) {
                scheduleNext();
            }
        }

        private synchronized void scheduleNext() {
            if ((activeTask = tasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(activeTask);
            }
        }

    }

    public static Promise<Void> runSerial(Runnable runnable) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {

            }
        });
        return new PromiseImpl<>(CompletableFuture.runAsync(runnable, SERIAL_EXECUTOR));
    }

    public static Promise<Void> runAsync(Runnable runnable) {
        return new PromiseImpl<>(CompletableFuture.runAsync(runnable, THREAD_POOL_EXECUTOR));
    }

    public static <V> Promise<V> supplySerial(Supplier<V> supplier) {
        return new PromiseImpl<>(CompletableFuture.supplyAsync(supplier, SERIAL_EXECUTOR));
    }

    public static <V> Promise<V> supplyAsync(Supplier<V> supplier) {
        return new PromiseImpl<>(CompletableFuture.supplyAsync(supplier, THREAD_POOL_EXECUTOR));
    }

    public static <V> Promise<V> combine(List<? extends Promise<? extends V>> promises,
                                         BiFunction<? super V, ? super V, ? extends V> combiner) {
        CompletableFuture<V> future = null;
        for (Promise<? extends V> promise : promises) {
            @SuppressWarnings("unchecked")
            CompletableFuture<V> d = ((PromiseImpl) promise).getDelegate();
            future = future == null ? d : future.thenCombine(d, combiner);
        }
        return new PromiseImpl<>(future);
    }

    public static Promise<Void> combine(List<? extends Promise<? extends Void>> promises) {
        CompletableFuture<Void> future = null;
        BiFunction<Void, Void, Void> combiner = (a, b) -> null;
        for (Promise<? extends Void> promise : promises) {
            @SuppressWarnings("unchecked")
            CompletableFuture<Void> d = ((PromiseImpl) promise).getDelegate();
            future = future == null ? d : future.thenCombine(d, combiner);
        }
        return new PromiseImpl<>(future);
    }

}
