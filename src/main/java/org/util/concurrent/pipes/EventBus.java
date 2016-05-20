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

/**
 * @author ahmad
 */
public interface EventBus extends Controllable {

    void register(String topic, Handler<Object> handler);

    void registerAsync(String topic, Handler<Object> handler);

    void register(String topic, Handler<Object> handler, PublishMode mode);

    void registerAsync(String topic, Handler<Object> handler, PublishMode mode);

    void unregister(String topic, Handler<Object> handler);

    void unregisterHandlers(String topic);

    void unregisterAllHandlers();

    void publish(String topic, Object message);

    void publishAsync(String topic, Object message);

    void publish(String topic, Object message, PublishMode mode);

    void publishAsync(String topic, Object message, PublishMode mode);

    void clearMessages(String topic);

    void clearAllMessages();

    void reset(String topic);

    void resetAll();

    enum PublishMode {ASYNC, SERIAL}

}
