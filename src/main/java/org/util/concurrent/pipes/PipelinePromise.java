package org.util.concurrent.pipes;

import org.util.concurrent.futures.Promise;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author ahmad
 */
public interface PipelinePromise extends PipelineFuture, Promise<Void> {

    @Override
    PipePromise pipeAt(int pipeIndex);

    @Override
    PipelinePromise whenComplete(BiConsumer<? super Void, ? super Throwable> action);

    @Override
    PipelinePromise exceptionally(Function<Throwable, ? extends Void> function);

}
