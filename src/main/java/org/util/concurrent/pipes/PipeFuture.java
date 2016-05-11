package org.util.concurrent.pipes;

import org.util.concurrent.futures.Future;

import java.util.function.BiConsumer;

/**
 * @author ahmad
 */
public interface PipeFuture extends Future<Void> {

    Pipe pipe();

    @Override
    PipeFuture whenComplete(BiConsumer<? super Void, ? super Throwable> action);

}
