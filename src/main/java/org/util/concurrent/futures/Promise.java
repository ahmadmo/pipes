package org.util.concurrent.futures;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author ahmad
 */
public interface Promise<V> extends Future<V> {

    boolean complete(V value);

    boolean completeExceptionally(Throwable cause);

    Promise<V> exceptionally(Function<Throwable, ? extends V> function);

    @Override
    Promise<V> whenComplete(BiConsumer<? super V, ? super Throwable> action);

}
