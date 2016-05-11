package org.util.concurrent.pipes;

import org.util.concurrent.futures.Future;

import java.util.function.BiConsumer;

/**
 * @author ahmad
 */
public interface PipelineFuture extends Future<Void> {

    Pipeline pipeline();

    @Override
    PipelineFuture whenComplete(BiConsumer<? super Void, ? super Throwable> action);

}
