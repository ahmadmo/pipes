package org.util.concurrent.pipes;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author ahmad
 */
final class ConcurrentChannel implements Channel {

    private final BlockingQueue<Object> messages = new LinkedBlockingQueue<>();

    @Override
    public boolean write(Object message) {
        return messages.offer(message);
    }

    @Override
    public boolean write(Object... messages) {
        boolean modified = false;
        for (Object message : messages) {
            modified |= write(message);
        }
        return modified;
    }

    @Override
    public boolean write(List<Object> messages) {
        boolean modified = false;
        for (Object message : messages) {
            modified |= write(message);
        }
        return modified;
    }

    @Override
    public Object read() {
        return messages.poll();
    }

    @Override
    public Object readBlocking(long timeout, TimeUnit unit) {
        try {
            return messages.poll(timeout, unit);
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public Object readBlocking() {
        try {
            return messages.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public Object peek() {
        return messages.peek();
    }

    @Override
    public Iterator<Object> iterator() {
        return messages.iterator();
    }

    @Override
    public int size() {
        return messages.size();
    }

    @Override
    public boolean isEmpty() {
        return messages.isEmpty();
    }

    @Override
    public void clear() {
        messages.clear();
    }

    @Override
    public OneWayChannel readOnly() {
        return new OneWayChannel(this, OneWayChannel.AccessMode.READ_ONLY);
    }

    @Override
    public OneWayChannel writeOnly() {
        return new OneWayChannel(this, OneWayChannel.AccessMode.WRITE_ONLY);
    }


}
