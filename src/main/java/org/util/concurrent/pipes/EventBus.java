package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
public interface EventBus {

    void register(String eventName, Handler<Object> handler);

    void registerAsync(String eventName, Handler<Object> handler);

    void register(String eventName, Handler<Object> handler, PublishMode mode);

    void registerAsync(String eventName, Handler<Object> handler, PublishMode mode);

    void unregister(String eventName, Handler<Object> handler);

    void publish(String eventName, Object message);

    void publishAsync(String eventName, Object message);

    void publish(String eventName, Object message, PublishMode mode);

    void publishAsync(String eventName, Object message, PublishMode mode);

    enum PublishMode {ASYNC, SERIAL}

}
