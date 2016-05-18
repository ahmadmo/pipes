package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
@FunctionalInterface
public interface Process {

    void start(PipeContext context);

}
