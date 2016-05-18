package org.util.concurrent.pipes;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ahmad
 */
final class Seq {

    private static final AtomicLong C = new AtomicLong();

    public static long next() {
        return C.incrementAndGet();
    }

}
