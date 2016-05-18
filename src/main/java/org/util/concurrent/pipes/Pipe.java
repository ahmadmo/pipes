package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
public final class Pipe {

    final long id = Seq.next();

    private final int index;
    private final Process process;
    private final boolean blocking;

    Pipe(int index, Process process, boolean blocking) {
        this.index = index;
        this.process = process;
        this.blocking = blocking;
    }

    public int index() {
        return index;
    }

    public Process process() {
        return process;
    }

    public boolean isBlocking() {
        return blocking;
    }

}
