package org.util.concurrent.futures;

import java.util.Objects;
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

    private final CompletableFuture<V> delegate;

    PromiseImpl(CompletableFuture<V> future) {
        this.delegate = Objects.requireNonNull(future);
    }

    CompletableFuture<V> getDelegate() {
        return delegate;
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
