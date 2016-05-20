/*
 * Copyright 2016 Ahmad Mozafarnia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.util.concurrent.pipes;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author ahmad
 */
final class Do {

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

    static void executeSerial(Runnable runnable) {
        SERIAL_EXECUTOR.execute(runnable);
    }

    static void executeAsync(Runnable runnable) {
        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    static CompletableFuture<Void> runSerial(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, SERIAL_EXECUTOR);
    }

    static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, THREAD_POOL_EXECUTOR);
    }

    public static <V> CompletableFuture<V> supplySerial(Supplier<V> supplier) {
        return CompletableFuture.supplyAsync(supplier, SERIAL_EXECUTOR);
    }

    public static <V> CompletableFuture<V> supplyAsync(Supplier<V> supplier) {
        return CompletableFuture.supplyAsync(supplier, THREAD_POOL_EXECUTOR);
    }

    @SuppressWarnings("unchecked")
    static <V> CompletableFuture<V> combine(List<? extends CompletableFuture<? extends V>> futures,
                                            BiFunction<? super V, ? super V, ? extends V> combiner) {
        if (futures.isEmpty()) {
            return new CompletableFuture<>();
        }
        final CompletableFuture[] future = {null};
        for (CompletableFuture<? extends V> c : futures) {
            future[0] = future[0] == null ? c : future[0].thenCombine(c, combiner);
        }
        for (CompletableFuture<? extends V> c : futures) {
            c.exceptionally(ex -> {
                future[0].completeExceptionally(ex);
                return null;
            });
        }
        return future[0];
    }

    static CompletableFuture<Void> combine(List<? extends CompletableFuture<? extends Void>> futures) {
        final CompletableFuture<Void> future = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        for (CompletableFuture<?> child : futures) {
            child.exceptionally(ex -> {
                future.completeExceptionally(ex);
                return null;
            });
        }
        return future;
    }

}
