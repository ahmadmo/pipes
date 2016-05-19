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
import java.util.concurrent.TimeUnit;

/**
 * @author ahmad
 */
final class OneWayChannel implements Channel {

    private final Channel delegate;
    private final AccessMode mode;

    OneWayChannel(Channel delegate, AccessMode mode) {
        this.delegate = delegate;
        this.mode = mode;
    }

    @Override
    public boolean write(Object message) {
        checkAccess(AccessMode.WRITE_ONLY);
        return delegate.write(message);
    }

    @Override
    public boolean write(Object... messages) {
        checkAccess(AccessMode.WRITE_ONLY);
        return delegate.write(messages);
    }

    @Override
    public boolean write(List<Object> messages) {
        checkAccess(AccessMode.WRITE_ONLY);
        return delegate.write(messages);
    }

    @Override
    public Object read() {
        checkAccess(AccessMode.READ_ONLY);
        return delegate.read();
    }

    @Override
    public Object readBlocking(long timeout, TimeUnit unit) {
        checkAccess(AccessMode.READ_ONLY);
        return delegate.readBlocking(timeout, unit);
    }

    @Override
    public Object readBlocking() {
        checkAccess(AccessMode.READ_ONLY);
        return delegate.readBlocking();
    }

    @Override
    public Object peek() {
        checkAccess(AccessMode.READ_ONLY);
        return delegate.peek();
    }

    @Override
    public Iterator<Object> iterator() {
        checkAccess(AccessMode.READ_ONLY);
        return delegate.iterator();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("OneWay Channel");
    }

    @Override
    public OneWayChannel readOnly() {
        throw new UnsupportedOperationException("OneWay Channel");
    }

    @Override
    public OneWayChannel writeOnly() {
        throw new UnsupportedOperationException("OneWay Channel");
    }

    private void checkAccess(AccessMode requested) {
        if (mode != requested) {
            throw new UnsupportedOperationException((mode == AccessMode.READ_ONLY ? "Read" : "Write") + "Only Channel");
        }
    }

    enum AccessMode {READ_ONLY, WRITE_ONLY}

}
