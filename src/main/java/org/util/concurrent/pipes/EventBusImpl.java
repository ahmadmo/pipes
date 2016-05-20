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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author ahmad
 */
final class EventBusImpl extends AbstractControllable implements EventBus {

    private final ConcurrentMap<String, Topic> topics = new ConcurrentHashMap<>();
    private final ThreadPool pool = new ThreadPool();

    EventBusImpl() {
        super("EventBus");
    }

    @Override
    public void register(String topic, Handler<Object> handler) {
        register(topic, handler, PublishMode.SERIAL);
    }

    @Override
    public void registerAsync(String topic, Handler<Object> handler) {
        registerAsync(topic, handler, PublishMode.SERIAL);
    }

    @Override
    public void register(String topic, Handler<Object> handler, PublishMode mode) {
        checkShutdown();
        getOrAddRegistry(topic).addHandler(handler, mode, false);
    }

    @Override
    public void registerAsync(String topic, Handler<Object> handler, PublishMode mode) {
        checkShutdown();
        getOrAddRegistry(topic).addHandler(handler, mode, true);
    }

    @Override
    public void unregister(String topic, Handler<Object> handler) {
        Topic t = topics.get(topic);
        if (t != null) {
            t.removeHandler(handler);
        }
    }

    @Override
    public void unregisterHandlers(String topic) {
        Topic t = topics.get(topic);
        if (t != null) {
            t.removeHandlers();
        }
    }

    @Override
    public void unregisterAllHandlers() {
        topics.forEach((s, topic) -> topic.removeHandlers());
    }

    @Override
    public void publish(String topic, Object message) {
        publish(topic, message, PublishMode.ASYNC);
    }

    @Override
    public void publishAsync(String topic, Object message) {
        publishAsync(topic, message, PublishMode.ASYNC);
    }

    @Override
    public void publish(String topic, Object message, PublishMode mode) {
        checkShutdown();
        checkStopped();
        getOrAddRegistry(topic).publish(message, mode, false);
    }

    @Override
    public void publishAsync(String topic, Object message, PublishMode mode) {
        checkShutdown();
        checkStopped();
        getOrAddRegistry(topic).publish(message, mode, true);
    }

    @Override
    public void clearMessages(String topic) {
        Topic t = topics.get(topic);
        if (t != null) {
            t.clear();
        }
    }

    @Override
    public void clearAllMessages() {
        topics.forEach((s, topic) -> topic.clear());
    }

    @Override
    public void reset(String topic) {
        unregisterHandlers(topic);
        clearMessages(topic);
    }

    @Override
    public void resetAll() {
        unregisterAllHandlers();
        clearAllMessages();
    }

    @Override
    public void start() {
        start(1);
    }

    @Override
    Object doStart(Object... args) {
        return null;
    }

    @Override
    void doShutdown() {
        pool.shutdownGracefully();
    }

    private Topic getOrAddRegistry(String topic) {
        return topics.computeIfAbsent(topic, s -> new Topic());
    }

    private final class Topic extends ReadWriteLockContainer {

        private final Queue<Object> messages = new ArrayDeque<>();
        private final List<Handler<Object>> handlers = new CopyOnWriteArrayList<>();

        private void addHandler(final Handler<Object> handler, final PublishMode mode, boolean async) {
            List<CompletableFuture<Void>> futures = async ? null : new ArrayList<>();
            acquireReadLock(() -> {
                if (!isPaused()) {
                    if (async) for (Object message : messages) send(handler, message, mode);
                    else futures.addAll(messages.stream()
                            .map(message -> sendWithPromise(handler, message, mode))
                            .collect(Collectors.toList())
                    );
                }
                handlers.add(handler);
            });
            if (!async && !futures.isEmpty()) {
                Futures.combine(futures).join();
            }
        }

        private void removeHandler(Handler<Object> handler) {
            handlers.remove(handler);
        }

        private void removeHandlers() {
            handlers.clear();
        }

        private void publish(final Object message, final PublishMode mode, boolean async) {
            List<CompletableFuture<Void>> futures = async ? null : new ArrayList<>();
            acquireWriteLock(() -> {
                if (!isPaused()) {
                    if (async) for (Handler<Object> handler : handlers) send(handler, message, mode);
                    else futures.addAll(handlers.stream()
                            .map(handler -> sendWithPromise(handler, message, mode))
                            .collect(Collectors.toList())
                    );
                }
                messages.offer(message);
            });
            if (!async && !futures.isEmpty()) {
                Futures.combine(futures).join();
            }
        }

        private void send(final Handler<Object> handler, final Object message, PublishMode mode) {
            switch (mode) {
                case ASYNC:
                    pool.executeAsync(() -> handler.handle(message));
                    break;
                case SERIAL:
                    pool.executeSerial(() -> handler.handle(message));
                    break;
            }
        }

        private CompletableFuture<Void> sendWithPromise(final Handler<Object> handler, final Object message, PublishMode mode) {
            switch (mode) {
                case ASYNC:
                    return pool.runAsync(() -> handler.handle(message));
                case SERIAL:
                default:
                    return pool.runSerial(() -> handler.handle(message));
            }
        }

        private void clear() {
            acquireWriteLock(messages::clear);
        }

    }

}
