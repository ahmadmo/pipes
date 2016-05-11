package org.util.concurrent.pipes;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ahmad
 */
public interface Channel {

    boolean write(Object message);

    boolean write(Object... messages);

    boolean write(List<Object> messages);

    Object read();

    Object readBlocking(long timeout, TimeUnit unit);

    Object readBlocking();

    int size();

    boolean isEmpty();

    void clear();

    OneWayChannel readOnly();

    OneWayChannel writeOnly();

}
