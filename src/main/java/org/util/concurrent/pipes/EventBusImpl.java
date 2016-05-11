package org.util.concurrent.pipes;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author ahmad
 */
final class EventBusImpl implements EventBus {

    private final ConcurrentMap<String, Registry> registries = new ConcurrentHashMap<>();

    @Override
    public void register(String eventName, Handler<Object> handler) {
        registries.computeIfAbsent(eventName, s -> new Registry()).addHandler(handler);
    }

    @Override
    public void unregister(String eventName, Handler<Object> handler) {
        Registry r = registries.get(eventName);
        if (r != null) {
            r.removeHandler(handler);
        }
    }

    @Override
    public void publish(String eventName, Object message) {
        registries.computeIfAbsent(eventName, s -> new Registry()).publish(message);
    }

    private static final class Registry {

        private final BlockingQueue<Object> topic = new LinkedBlockingQueue<>();
        private final List<Handler<Object>> handlers = new CopyOnWriteArrayList<>();
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock r = lock.readLock();
        private final Lock w = lock.writeLock();

        private void addHandler(Handler<Object> handler) {
            r.lock();
            try {
                handlers.add(handler);
                topic.forEach(handler::handle);
            } finally {
                r.unlock();
            }
        }

        private void removeHandler(Handler<Object> handler) {
            handlers.remove(handler);
        }

        private void publish(Object message) {
            w.lock();
            try {
                topic.offer(message);
                for (Handler<Object> handler : handlers) {
                    handler.handle(message);
                }
            } finally {
                w.unlock();
            }
        }

    }

}
