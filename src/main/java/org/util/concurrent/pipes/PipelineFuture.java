package org.util.concurrent.pipes;

import org.util.concurrent.futures.Future;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author ahmad
 */
public interface PipelineFuture extends Future<Void> {

    Pipeline pipeline();

    List<? extends PipeFuture> pipes();

    PipeFuture pipeAt(int pipeIndex);

    PipeFuture findPipe(String pipeName);

    @Override
    PipelineFuture whenComplete(BiConsumer<? super Void, ? super Throwable> action);

}
