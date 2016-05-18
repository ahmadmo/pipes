package org.util.concurrent.pipes;

import org.util.concurrent.futures.Do;
import org.util.concurrent.futures.Promise;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ahmad
 */
final class PipelinePromiseImpl implements PipelinePromise {

    private final Pipeline pipeline;
    private final List<PipePromise> pipePromises;
    private final Promise<Void> delegate;

    PipelinePromiseImpl(Pipeline pipeline, List<PipePromise> pipePromises) {
        this.pipeline = pipeline;
        this.pipePromises = pipePromises;
        this.delegate = Do.combine(
                pipePromises.stream()
                        .map(promise -> ((PipePromiseImpl) promise).delegate)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Pipeline pipeline() {
        return pipeline;
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
    public List<PipePromise> pipes() {
        return Collections.unmodifiableList(pipePromises);
    }

    @Override
    public PipePromise pipeAt(int pipeIndex) {
        return pipePromises.get(pipeIndex);
    }

    @Override
    public PipelinePromise whenComplete(BiConsumer<? super Void, ? super Throwable> action) {
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
    public PipelinePromise exceptionally(Function<Throwable, ? extends Void> function) {
        delegate.exceptionally(function);
        return this;
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
