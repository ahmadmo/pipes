package org.util.concurrent.pipes;

import java.util.Iterator;
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

    Object peek();

    Iterator<Object> iterator();

    int size();

    boolean isEmpty();

    void clear();

    OneWayChannel readOnly();

    OneWayChannel writeOnly();

}
