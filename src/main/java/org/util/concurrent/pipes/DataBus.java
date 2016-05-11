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
