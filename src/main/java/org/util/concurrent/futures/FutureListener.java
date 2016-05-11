package org.util.concurrent.futures;

/**
 * @author ahmad
 */
@FunctionalInterface
public interface FutureListener<V> {

    void onComplete(Future<V> future);

}
