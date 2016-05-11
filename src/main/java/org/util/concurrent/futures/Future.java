package org.util.concurrent.futures;

import java.util.function.BiConsumer;

/**
 * @author ahmad
 */
public interface Future<V> extends java.util.concurrent.Future<V> {

    void cancel();

    V getNow(V valueIfAbsent);

    V join();

    Future<V> whenComplete(BiConsumer<? super V, ? super Throwable> action);

}
