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
public interface DataBus {

    Object get(String key);

    <T> T get(String key, Class<T> type);

    boolean contains(String key);

    <T> boolean contains(String key, Class<T> type);

    boolean contains(String key, Object value);

    <T> boolean contains(String key, Class<T> type, T value);

    Object set(String key, Object value);

    boolean compareAndSet(String key, Object expected, Object update);

    int getAndAddInt(String key, int delta);

    int addAndGetInt(String key, int delta);

    int getAndIncrementInt(String key);

    int incrementAndGetInt(String key);

    int getAndDecrementInt(String key);

    int decrementAndGetInt(String key);

    long getAndAddLong(String key, long delta);

    long addAndGetLong(String key, long delta);

    long getAndIncrementLong(String key);

    long incrementAndGetLong(String key);

    long getAndDecrementLong(String key);

    long decrementAndGetLong(String key);

    Object remove(String key);

}
