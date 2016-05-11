package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
@FunctionalInterface
public interface Handler<E> {

    void handle(E event);

}
