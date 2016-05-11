package org.util.concurrent.pipes;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ahmad
 */
final class DataBusImpl implements DataBus {

    private final ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();

    @Override
    public Object get(String key) {
        return map.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return type.cast(map.get(key));
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public <T> boolean contains(String key, Class<T> type) {
        return type.isInstance(map.get(key));
    }

    @Override
    public boolean contains(String key, Object value) {
        return Objects.equals(map.get(key), value);
    }

    @Override
    public <T> boolean contains(String key, Class<T> type, T expected) {
        Object value = map.get(key);
        return type.isInstance(value) && Objects.equals(value, expected);
    }

    @Override
    public Object set(String key, Object value) {
        return map.put(key, value);
    }

    @Override
    public boolean compareAndSet(String key, final Object expected, final Object update) {
        return map.replace(key, expected, update);
    }

    @Override
    public int getAndAddInt(String key, int delta) {
        int curVal;
        do {
            curVal = (int) map.get(key);
        } while (!compareAndSet(key, curVal, curVal + delta));
        return curVal;
    }

    @Override
    public int addAndGetInt(String key, int delta) {
        return getAndAddInt(key, delta) + delta;
    }

    @Override
    public int getAndIncrementInt(String key) {
        return getAndAddInt(key, 1);
    }

    @Override
    public int incrementAndGetInt(String key) {
        return addAndGetInt(key, 1);
    }

    @Override
    public int getAndDecrementInt(String key) {
        return getAndAddInt(key, -1);
    }

    @Override
    public int decrementAndGetInt(String key) {
        return addAndGetInt(key, -1);
    }

    @Override
    public long getAndAddLong(String key, long delta) {
        long curVal;
        do {
            curVal = (long) map.get(key);
        } while (!compareAndSet(key, curVal, curVal + delta));
        return curVal;
    }

    @Override
    public long addAndGetLong(String key, long delta) {
        return getAndAddLong(key, delta) + delta;
    }

    @Override
    public long getAndIncrementLong(String key) {
        return getAndAddLong(key, 1L);
    }

    @Override
    public long incrementAndGetLong(String key) {
        return addAndGetLong(key, 1L);
    }

    @Override
    public long getAndDecrementLong(String key) {
        return getAndAddLong(key, -1L);
    }

    @Override
    public long decrementAndGetLong(String key) {
        return addAndGetLong(key, -1L);
    }

    @Override
    public Object remove(String key) {
        return map.remove(key);
    }

}
