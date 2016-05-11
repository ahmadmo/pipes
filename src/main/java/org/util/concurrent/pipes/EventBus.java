package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
public interface EventBus {

    void register(String eventName, Handler<Object> handler);

    void unregister(String eventName, Handler<Object> handler);

    void publish(String eventName, Object message);

}
