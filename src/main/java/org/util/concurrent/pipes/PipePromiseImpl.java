package org.util.concurrent.pipes;

import org.util.concurrent.futures.Promise;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author ahmad
 */
final class PipePromiseImpl implements PipePromise {

    private final Pipe pipe;
    private final Promise<Void> delegate;

    PipePromiseImpl(Pipe pipe, Promise<Void> promise) {
        this.pipe = Objects.requireNonNull(pipe);
        this.delegate = Objects.requireNonNull(promise);
    }

    Promise<Void> getDelegate() {
        return delegate;
    }

    @Override
    public void cancel() {
        delegate.cancel();
    }

    @Override
    public Void getNow(Void valueIfAbsent) {
        return delegate.getNow(valueIfAbsent);
    }

    @Override
    public Void join() {
        return delegate.join();
    }

    @Override
    public PipePromise whenComplete(BiConsumer<? super Void, ? super Throwable> action) {
        delegate.whenComplete(action);
        return this;
    }

    @Override
    public boolean complete(Void value) {
        return delegate.complete(value);
    }

    @Override
    public boolean completeExceptionally(Throwable cause) {
        return delegate.completeExceptionally(cause);
    }

    @Override
    public PipePromise exceptionally(Function<Throwable, ? extends Void> function) {
        delegate.exceptionally(function);
        return this;
    }

    @Override
    public Pipe pipe() {
        return pipe;
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
    public Void get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }

}
