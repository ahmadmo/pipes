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

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author ahmad
 */
abstract class Completable<T extends Completable<T>> {

    private final CompletableFuture<Void> future;
    private final T t;

    @SuppressWarnings("unchecked")
    Completable(CompletableFuture<Void> future) {
        this.future = future;
        this.t = (T) this;
    }

    CompletableFuture<Void> future() {
        return future;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }

    public boolean isDone() {
        return future.isDone();
    }

    public void get() throws InterruptedException, ExecutionException {
        future.get();
    }

    public void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        future.get(timeout, unit);
    }

    public void getNow() {
        future.getNow(null);
    }

    public void join() {
        future.join();
    }

    public T exceptionally(Function<Throwable, ? extends Void> fn) {
        future.exceptionally(fn);
        return t;
    }

    public T whenComplete(BiConsumer<? super Void, ? super Throwable> action) {
        future.whenCompleteAsync(action);
        return t;
    }

    public T whenCompleteAsync(BiConsumer<? super Void, ? super Throwable> action) {
        future.whenCompleteAsync(action);
        return t;
    }

    public T whenCompleteAsync(BiConsumer<? super Void, ? super Throwable> action, Executor executor) {
        future.whenCompleteAsync(action, executor);
        return t;
    }

    public boolean complete() {
        return future.complete(null);
    }

    public boolean completeExceptionally(Throwable ex) {
        return future.completeExceptionally(ex);
    }

    public boolean isCompletedExceptionally() {
        return future.isCompletedExceptionally();
    }

}
