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
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author ahmad
 */
final class ThreadPool {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private final ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CPU_COUNT + 1, CPU_COUNT * 2 + 1,
            15L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            r -> {
                Thread t = threadFactory.newThread(r);
                t.setDaemon(true);
                return t;
            });

    private final Executor serialExecutor = new SerialExecutor();

    ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    Executor getSerialExecutor() {
        return serialExecutor;
    }

    void executeSerial(Runnable runnable) {
        serialExecutor.execute(runnable);
    }

    void executeAsync(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    CompletableFuture<Void> runSerial(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, serialExecutor);
    }

    CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, threadPoolExecutor);
    }

    <V> CompletableFuture<V> supplySerial(Supplier<V> supplier) {
        return CompletableFuture.supplyAsync(supplier, serialExecutor);
    }

    <V> CompletableFuture<V> supplyAsync(Supplier<V> supplier) {
        return CompletableFuture.supplyAsync(supplier, threadPoolExecutor);
    }

    void shutdownGracefully() {
        threadPoolExecutor.shutdown();
        threadPoolExecutor.shutdownNow();
        try {
            threadPoolExecutor.awaitTermination(15L, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    private final class SerialExecutor implements Executor {

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
                threadPoolExecutor.execute(activeTask);
            }
        }

    }

}
