package org.util.concurrent.pipes;

import org.util.concurrent.futures.Promise;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author ahmad
 */
public interface PipePromise extends PipeFuture, Promise<Void> {

    @Override
    PipePromise whenComplete(BiConsumer<? super Void, ? super Throwable> action);

    @Override
    PipePromise exceptionally(Function<Throwable, ? extends Void> function);

}
