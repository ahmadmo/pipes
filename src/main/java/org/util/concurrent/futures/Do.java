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

    public static void executeSerial(Runnable runnable) {
        SERIAL_EXECUTOR.execute(runnable);
    }

    public static void executeAsync(Runnable runnable) {
        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    public static Promise<Void> runSerial(Runnable runnable) {
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
        if (promises.isEmpty()) {
            return new PromiseImpl<>(new CompletableFuture<>());
        }
        CompletableFuture<V> future = null;
        for (Promise<? extends V> promise : promises) {
            @SuppressWarnings("unchecked")
            CompletableFuture<V> d = ((PromiseImpl) promise).delegate;
            future = future == null ? d : future.thenCombine(d, combiner);
        }
        return new PromiseImpl<>(future);
    }

    public static Promise<Void> combine(List<? extends Promise<? extends Void>> promises) {
        if (promises.isEmpty()) {
            return new PromiseImpl<>(new CompletableFuture<>());
        }
        CompletableFuture[] futures = new CompletableFuture[promises.size()];
        for (int i = 0, n = promises.size(); i < n; i++) {
            futures[i] = ((PromiseImpl) promises.get(i)).delegate;
        }
        return new PromiseImpl<>(CompletableFuture.allOf(futures));
    }

}
