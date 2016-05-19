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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author ahmad
 */
final class PromiseImpl<V> implements Promise<V> {

    final CompletableFuture<V> delegate;

    PromiseImpl(CompletableFuture<V> future) {
        this.delegate = future;
    }

    @Override
    public boolean complete(V value) {
        return delegate.complete(value);
    }

    @Override
    public boolean completeExceptionally(Throwable cause) {
        return delegate.completeExceptionally(cause);
    }

    @Override
    public Promise<V> exceptionally(Function<Throwable, ? extends V> function) {
        delegate.exceptionally(function);
        return this;
    }

    @Override
    public Promise<V> whenComplete(BiConsumer<? super V, ? super Throwable> action) {
        delegate.whenComplete(action);
        return this;
    }

    @Override
    public void cancel() {
        delegate.cancel(true);
    }

    @Override
    public V getNow(V valueIfAbsent) {
        return delegate.getNow(valueIfAbsent);
    }

    @Override
    public V join() {
        return delegate.join();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }

}
