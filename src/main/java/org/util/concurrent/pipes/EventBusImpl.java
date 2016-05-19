/*
 * Copyright 2016 Ahmad Mozafarnia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.util.concurrent.pipes;

import org.util.concurrent.futures.Do;
import org.util.concurrent.futures.Promise;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author ahmad
 */
final class EventBusImpl implements EventBus {

    private final ConcurrentMap<String, Registry> registries = new ConcurrentHashMap<>();

    @Override
    public void register(String eventName, Handler<Object> handler) {
        register(eventName, handler, PublishMode.SERIAL);
    }

    @Override
    public void registerAsync(String eventName, Handler<Object> handler) {
        registerAsync(eventName, handler, PublishMode.SERIAL);
    }

    @Override
    public void register(String eventName, Handler<Object> handler, PublishMode mode) {
        getOrAddRegistry(eventName).addHandler(handler, mode, false);
    }

    @Override
    public void registerAsync(String eventName, Handler<Object> handler, PublishMode mode) {
        getOrAddRegistry(eventName).addHandler(handler, mode, true);
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
        publish(eventName, message, PublishMode.ASYNC);
    }

    @Override
    public void publishAsync(String eventName, Object message) {
        publishAsync(eventName, message, PublishMode.ASYNC);
    }

    @Override
    public void publish(String eventName, Object message, PublishMode mode) {
        getOrAddRegistry(eventName).publish(message, mode, false);
    }

    @Override
    public void publishAsync(String eventName, Object message, PublishMode mode) {
        getOrAddRegistry(eventName).publish(message, mode, true);
    }

    private Registry getOrAddRegistry(String eventName) {
        return registries.computeIfAbsent(eventName, s -> new Registry());
    }

    private static final class Registry {

        private final Queue<Object> topic = new ArrayDeque<>();
        private final List<Handler<Object>> handlers = new CopyOnWriteArrayList<>();
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock r = lock.readLock();
        private final Lock w = lock.writeLock();

        private void addHandler(final Handler<Object> handler, final PublishMode mode, boolean async) {
            List<Promise<Void>> promises = async ? null : new ArrayList<>();
            r.lock();
            try {
                if (async) for (Object message : topic) send(handler, message, mode);
                else promises.addAll(topic.stream()
                        .map(message -> sendWithPromise(handler, message, mode))
                        .collect(Collectors.toList())
                );
                handlers.add(handler);
            } finally {
                r.unlock();
            }
            if (!async && !promises.isEmpty()) {
                Do.combine(promises).join();
            }
        }

        private void removeHandler(Handler<Object> handler) {
            handlers.remove(handler);
        }

        private void publish(final Object message, final PublishMode mode, boolean async) {
            List<Promise<Void>> promises = async ? null : new ArrayList<>();
            w.lock();
            try {
                if (async) for (Handler<Object> handler : handlers) send(handler, message, mode);
                else promises.addAll(handlers.stream()
                        .map(handler -> sendWithPromise(handler, message, mode))
                        .collect(Collectors.toList())
                );
                topic.offer(message);
            } finally {
                w.unlock();
            }
            if (!async && !promises.isEmpty()) {
                Do.combine(promises).join();
            }
        }

        private void send(final Handler<Object> handler, final Object message, PublishMode mode) {
            switch (mode) {
                case ASYNC:
                    Do.executeAsync(() -> handler.handle(message));
                    break;
                case SERIAL:
                    Do.executeSerial(() -> handler.handle(message));
                    break;
            }
        }

        private Promise<Void> sendWithPromise(final Handler<Object> handler, final Object message, PublishMode mode) {
            switch (mode) {
                case ASYNC:
                    return Do.runAsync(() -> handler.handle(message));
                case SERIAL:
                default:
                    return Do.runSerial(() -> handler.handle(message));
            }
        }

    }

}
