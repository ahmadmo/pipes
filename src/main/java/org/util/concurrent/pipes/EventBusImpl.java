package org.util.concurrent.pipes;

import org.util.concurrent.futures.Do;

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
        getOrAddRegistry(eventName).addHandler(handler);
    }

    @Override
    public void registerAsync(String eventName, final Handler<Object> handler) {
        final Registry registry = getOrAddRegistry(eventName);
        if (registry != null) {
            Do.executeAsync(() -> registry.addHandler(handler));
        }
    }

    @Override
    public void unregister(String eventName, Handler<Object> handler) {
        Registry registry = registries.get(eventName);
        if (registry != null) {
            registry.removeHandler(handler);
        }
    }

    @Override
    public void publish(String eventName, Object message) {
        getOrAddRegistry(eventName).publish(message);
    }

    @Override
    public void publishAsync(String eventName, final Object message) {
        final Registry registry = getOrAddRegistry(eventName);
        if (registry != null) {
            Do.executeAsync(() -> registry.publish(message));
        }
    }

    private Registry getOrAddRegistry(String eventName) {
        return registries.computeIfAbsent(eventName, s -> new Registry());
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
