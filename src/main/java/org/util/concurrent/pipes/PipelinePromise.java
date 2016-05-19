package org.util.concurrent.pipes;

import org.util.concurrent.futures.Promise;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author ahmad
 */
public interface PipelinePromise extends PipelineFuture, Promise<Void> {

    @Override
    List<PipePromise> pipeFutures();

    @Override
    PipePromise pipeFutureAt(int pipeIndex);

    @Override
    PipePromise findPipeFuture(String pipeName);

    @Override
    PipelinePromise whenComplete(BiConsumer<? super Void, ? super Throwable> action);

    @Override
    PipelinePromise exceptionally(Function<Throwable, ? extends Void> function);

}
