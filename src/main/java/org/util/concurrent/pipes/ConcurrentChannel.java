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
