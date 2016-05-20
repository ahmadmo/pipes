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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author ahmad
 */
class ReadWriteLockContainer {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.readLock();

    void acquireReadLock(Runnable runnable) {
        r.lock();
        try {
            runnable.run();
        } finally {
            r.unlock();
        }
    }

    <R> R acquireReadLock(Supplier<R> supplier) {
        r.lock();
        try {
            return supplier.get();
        } finally {
            r.unlock();
        }
    }

    <T, R> R acquireReadLock(T t, Function<T, R> function) {
        r.lock();
        try {
            return function.apply(t);
        } finally {
            r.unlock();
        }
    }

    void acquireWriteLock(Runnable runnable) {
        w.lock();
        try {
            runnable.run();
        } finally {
            w.unlock();
        }
    }

    <R> R acquireWriteLock(Supplier<R> supplier) {
        w.lock();
        try {
            return supplier.get();
        } finally {
            w.unlock();
        }
    }

    <T, R> R acquireWriteLock(T t, Function<T, R> function) {
        w.lock();
        try {
            return function.apply(t);
        } finally {
            w.unlock();
        }
    }

}
